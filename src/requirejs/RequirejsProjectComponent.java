package requirejs;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.intellij.lang.ASTNode;
import com.intellij.lang.javascript.JSElementTypes;
import com.intellij.lang.javascript.JSTokenTypes;
import com.intellij.lang.javascript.psi.impl.JSFileImpl;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileAdapter;
import com.intellij.openapi.vfs.VirtualFileEvent;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.impl.source.PsiFileImpl;
import com.intellij.psi.impl.source.tree.TreeElement;
import com.intellij.psi.impl.source.xml.XmlFileImpl;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;
import requirejs.settings.Settings;

import java.util.*;

public class RequirejsProjectComponent implements ProjectComponent {
    protected Project project;
    protected Settings settings;
    protected boolean settingValidStatus;
    protected String settingValidVersion;
    protected String settingVersionLastShowNotification;

    protected final Logger LOG = Logger.getInstance("Requirejs-Plugin");

    protected String requirejsBaseUrl;

    protected RequirePaths requirePaths;
    protected RequireMap requireMap = new RequireMap();

    private RequireConfigVfsListener vfsListener;
    public PackageConfig packageConfig;

    public RequirejsProjectComponent(Project project) {
        this.project = project;
        settings = Settings.getInstance(project);
        requirePaths = new RequirePaths(this);
        packageConfig = new PackageConfig(this);
    }

    @Override
    public void projectOpened() {
        if (isEnabled()) {
            validateSettings();
        }
    }

    public void watchConfigFile() {
        // Add the Virtual File listener
        vfsListener = new RequireConfigVfsListener();
        VirtualFileManager.getInstance().addVirtualFileListener(vfsListener, project);
    }

    public void stopWatchConfigFile() {
        if (vfsListener == null) {
            return;
        }
        VirtualFileManager.getInstance().removeVirtualFileListener(vfsListener);
    }

    @Override
    public void projectClosed() {
    }

    @Override
    public void initComponent() {
        if (isEnabled()) {
            validateSettings();
        }
    }

    @Override
    public void disposeComponent() {
    }

    @NotNull
    @Override
    public String getComponentName() {
        return "RequirejsProjectComponent";
    }

    public Logger getLogger() {
        return LOG;
    }

    public boolean isEnabled() {
        return Settings.getInstance(project).pluginEnabled;
    }

    public boolean isSettingsValid() {
        if (!settings.getVersion().equals(settingValidVersion)) {
            validateSettings();
            settingValidVersion = settings.getVersion();
        }
        return settingValidStatus;
    }

    public boolean validateSettings() {
        if (null == getWebDir()) {
            showErrorConfigNotification(
                    "Public directory not found. Path " +
                            getContentRoot().getPath() + '/' + settings.publicPath +
                            " not found in project"
            );
            LOG.debug("Public directory not found");
            settingValidStatus = false;
            return false;
        }
        if (isEnabled()) {
            watchConfigFile();
        } else {
            stopWatchConfigFile();
        }

        settingValidStatus = true;
        return true;
    }

    public void clearParse() {
        requirejsBaseUrl = null;
        requirePaths.clear();
        requireMap.clear();
        packageConfig.clear();
    }

    protected void showErrorConfigNotification(String content) {
        if (!settings.getVersion().equals(settingVersionLastShowNotification)) {
            settingVersionLastShowNotification = settings.getVersion();
            showInfoNotification(content, NotificationType.ERROR);
        }
    }

    public VirtualFile getWebDir(VirtualFile elementFile) {
        if (null != elementFile) {
            if (settings.publicPath.isEmpty()) {
                return getContentRoot(elementFile);
            }
        }
        VirtualFile vfWebDir = findPathInContentRoot(settings.publicPath);
        if (null != vfWebDir) {
            return vfWebDir;
        } else {
            return null;
        }
    }

    public VirtualFile getWebDir() {
        return getWebDir(null);
    }

    protected VirtualFile findPathInWebDir(String path) {
        if (settings.publicPath.isEmpty()) {
            return findPathInContentRoot(path);
        }
        VirtualFile vfWebDir = getWebDir();
        if (null != vfWebDir) {
            return vfWebDir.findFileByRelativePath(path);
        } else {
            return null;
        }
    }

    public VirtualFile getContentRoot() {
        VirtualFile[] contentRoots = ProjectRootManager.getInstance(project).getContentRoots();
        if (contentRoots.length > 0) {
            return contentRoots[0];
        } else {
            return project.getBaseDir();
        }
    }

    public VirtualFile getContentRoot(VirtualFile file) {
        return ProjectRootManager.getInstance(project).getFileIndex().getContentRootForFile(file);
    }

    protected VirtualFile findPathInContentRoot(String path) {
        VirtualFile[] contentRoots = ProjectRootManager.getInstance(project).getContentRoots();
        if (contentRoots.length > 0) {
            for(VirtualFile contentRoot : contentRoots) {
                VirtualFile vfPath = contentRoot.findFileByRelativePath(path);
                if (null != vfPath) {
                    return vfPath;
                }
            }

            return null;
        } else {
            return project.getBaseDir().findFileByRelativePath(path);
        }
    }

    public void showInfoNotification(String content, NotificationType type) {
        Notification errorNotification = new Notification("Require.js plugin", "Require.js plugin", content, type);
        Notifications.Bus.notify(errorNotification, this.project);
    }

    public List<String> getModulesNames() {
        List<String> modules = new ArrayList<String>();
        if (requirePaths.isEmpty()) {
            if (!parseRequirejsConfig()) {
                return modules;
            }
        }
        modules.addAll(requirePaths.getAliasToFiles());
        Collection<Package> filteredPackages = Collections2.filter(packageConfig.packages, new Predicate<Package>() {
            @Override
            public boolean apply(Package aPackage) {
                return aPackage != null && aPackage.mainExists;
            }
        });
        Collection<String> ret = Collections2.transform(filteredPackages, new Function<Package, String>() {
            @Override
            public String apply(Package aPackage) {
                return aPackage.name;
            }
        });
        modules.addAll(ret);
        return modules;
    }

    public String getBaseUrl() {
        if (null == requirejsBaseUrl) {
            VirtualFile baseUrlPath = getBaseUrlPath(true);
            if (null != baseUrlPath) {
                requirejsBaseUrl = baseUrlPath.getPath().replace(getWebDir().getPath(), "");
                requirejsBaseUrl = StringUtil.trimEnd(requirejsBaseUrl, "/");
                if (requirejsBaseUrl.startsWith("/")) {
                    requirejsBaseUrl = requirejsBaseUrl.substring(1);
                }
            } else {
                requirejsBaseUrl = "";
            }
        }

        return requirejsBaseUrl;
    }

    public VirtualFile getBaseUrlPath(boolean parseConfig) {
        if (null == requirejsBaseUrl) {
            if (parseConfig) {
                parseRequirejsConfig();
            }
            if (null == requirejsBaseUrl) {
                return getConfigFileDir();
            }
        }

        return findPathInWebDir(requirejsBaseUrl);
    }

    protected VirtualFile getConfigFileDir() {
        VirtualFile mainJsVirtualFile = findPathInWebDir(settings.configFilePath);
        if (null != mainJsVirtualFile) {
            return mainJsVirtualFile.getParent();
        } else {
            return null;
        }
    }

//    private Date lastParse;

    public boolean parseRequirejsConfig() {
        VirtualFile mainJsVirtualFile = findPathInWebDir(settings.configFilePath);
        if (null == mainJsVirtualFile) {
            this.showErrorConfigNotification("Config file not found. File " + settings.publicPath + '/' + settings.configFilePath + " not found in project");
            LOG.debug("Config not found");
            return false;
        } else {
            PsiFile mainJs = PsiManager.getInstance(project).findFile(mainJsVirtualFile);
            if (mainJs instanceof JSFileImpl || mainJs instanceof XmlFileImpl) {
                Map<String, VirtualFile> allConfigPaths;
                packageConfig.clear();
                requireMap.clear();
                requirePaths.clear();
                if (((PsiFileImpl) mainJs).getTreeElement() == null) {
                    parseMainJsFile(((PsiFileImpl) mainJs).calcTreeElement());
                } else {
                    parseMainJsFile(((PsiFileImpl) mainJs).getTreeElement());
                }
            } else {
                this.showErrorConfigNotification("Config file wrong format");
                LOG.debug("Config file wrong format");
                return false;
            }
        }

        return true;
    }

    public void parseMainJsFile(TreeElement node) {

        TreeElement firstChild = node.getFirstChildNode();
        if (firstChild != null) {
            parseMainJsFile(firstChild);
        }

        TreeElement nextNode = node.getTreeNext();
        if (nextNode != null) {
            parseMainJsFile(nextNode);
        }

        if (node.getElementType() == JSTokenTypes.IDENTIFIER) {
            if (node.getText().equals("requirejs") || node.getText().equals("require")) {
                TreeElement treeParent = node.getTreeParent();

                if (null != treeParent) {
                    ASTNode firstTreeChild = treeParent.findChildByType(JSElementTypes.OBJECT_LITERAL_EXPRESSION);
                    TreeElement nextTreeElement = treeParent.getTreeNext();
                    if (null != firstTreeChild) {
                        parseRequirejsConfig((TreeElement) firstTreeChild
                            .getFirstChildNode()
                        );
                    } else if (null != nextTreeElement && nextTreeElement.getElementType() == JSTokenTypes.DOT) {
                        nextTreeElement = nextTreeElement.getTreeNext();
                        if (null != nextTreeElement && nextTreeElement.getText().equals("config")) {
                            treeParent = nextTreeElement.getTreeParent();
                            findAndParseConfig(treeParent);
                        }
                    } else {
                        findAndParseConfig(treeParent);
                    }
                }
            }
        }
    }

    protected void findAndParseConfig(TreeElement treeParent) {
        TreeElement nextTreeElement;
        if (null != treeParent) {
            nextTreeElement = treeParent.getTreeNext();
            if (null != nextTreeElement) {
                ASTNode nextChild = nextTreeElement.findChildByType(JSElementTypes.OBJECT_LITERAL_EXPRESSION);
                if (null != nextChild) {
                    parseRequirejsConfig(
                            (TreeElement) nextChild.getFirstChildNode()
                    );
                }
            }
        }
    }

    public static String dequote(String text) {
        return text.replace("\"", "").replace("'", "");
    }

    public static String dequoteAll(String text) {
        return text.replaceAll("\"", "").replaceAll("'", "");
    }

    public void parseRequirejsConfig(TreeElement node) {
        if (null == node) {
            return ;
        }

        try {
            if (node.getElementType() == JSElementTypes.PROPERTY) {
                TreeElement identifier = (TreeElement) node.findChildByType(JSTokenTypes.IDENTIFIER);
                String identifierName = null;
                if (null != identifier) {
                    identifierName = identifier.getText();
                } else {
                    TreeElement identifierString = (TreeElement) node.findChildByType(JSTokenTypes.STRING_LITERAL);
                    if (null != identifierString) {
                        identifierName = dequote(identifierString.getText());
                    }
                }
                if (null != identifierName) {
                    if (identifierName.equals("baseUrl")) {
                        String baseUrl = null;
                        if (!settings.overrideBaseUrl) {
                            ASTNode baseUrlNode = node
                                    .findChildByType(JSElementTypes.LITERAL_EXPRESSION);
                            if (null != baseUrlNode) {
                                baseUrl = dequote(baseUrlNode.getText());
                            }
                        } else {
                            LOG.info("baseUrl override is enabled, overriding with '" + settings.baseUrl + "'");
                            baseUrl = settings.baseUrl;
                        }
                        if (null != baseUrl) {
                            LOG.info("Setting baseUrl to '" + baseUrl + "'");
                            setBaseUrl(baseUrl);
                            packageConfig.baseUrl = baseUrl;
                        } else {
                            LOG.debug("BaseUrl not set");
                        }
                    } else if (identifierName.equals("paths")) {
                        ASTNode pathsNode = node
                                .findChildByType(JSElementTypes.OBJECT_LITERAL_EXPRESSION);
                        if (null != pathsNode) {
                            parseRequireJsPaths(
                                    (TreeElement) pathsNode.getFirstChildNode()
                            );
                        }
                    } else if (identifierName.equals("packages")) {
                        TreeElement packages = (TreeElement) node.findChildByType(JSElementTypes.ARRAY_LITERAL_EXPRESSION);
                        LOG.debug("parsing packages");
                        parsePackages(packages);
                        LOG.debug("parsing packages done, found " + packageConfig.packages.size() + " packages");
                    } else if (identifierName.equals("map")) {
                        TreeElement mapElement = (TreeElement) node.findChildByType(JSElementTypes.OBJECT_LITERAL_EXPRESSION);
                        parseMapsConfig(mapElement);
                    }
                }
            }
        } catch (NullPointerException exception) {
            LOG.error(exception.getMessage(), exception);
        }

        TreeElement next = node.getTreeNext();
        if (null != next) {
            parseRequirejsConfig(next);
        }
    }

    protected void parseMapsConfig(TreeElement mapElement) {
        TreeElement firstMapConfigElement = (TreeElement) mapElement.findChildByType(JSElementTypes.PROPERTY);
        parseMapConfigElement(firstMapConfigElement);
    }

    protected void parseMapConfigElement(TreeElement mapConfigElement) {
        if (null == mapConfigElement) {
            return;
        }

        if (mapConfigElement.getElementType() == JSElementTypes.PROPERTY) {
            String module = getJSPropertyName(mapConfigElement);

            TreeElement mapAliasesObject = (TreeElement) mapConfigElement
                    .findChildByType(JSElementTypes.OBJECT_LITERAL_EXPRESSION);

            if (null != mapAliasesObject) {
                RequireMapModule requireMapModule = new RequireMapModule();

                requireMapModule.module = module;
                TreeElement mapAliasProperty = (TreeElement) mapAliasesObject.findChildByType(JSElementTypes.PROPERTY);
                parseMapAliasProperty(requireMapModule, mapAliasProperty);

                requireMap.addModule(requireMapModule);
            }
        }

        parseMapConfigElement(mapConfigElement.getTreeNext());
    }

    protected void parseMapAliasProperty(RequireMapModule requireMapModule, TreeElement mapAliasProperty) {
        if (null == mapAliasProperty) {
            return;
        }

        if (mapAliasProperty.getElementType() == JSElementTypes.PROPERTY) {
            RequirePathAlias alias = new RequirePathAlias();
            alias.alias = getJSPropertyName(mapAliasProperty);
            alias.path = getJSPropertyLiteralValue(mapAliasProperty);

            if (null != alias.alias && alias.path != null) {
                requireMapModule.addAlias(alias);
            } else {
                LOG.debug("Error parse require js path", alias);
            }
        }

        parseMapAliasProperty(requireMapModule, mapAliasProperty.getTreeNext());
    }

    protected String getJSPropertyName(TreeElement jsProperty) {
        TreeElement identifier = (TreeElement) jsProperty.findChildByType(
                TokenSet.create(JSTokenTypes.IDENTIFIER, JSTokenTypes.STRING_LITERAL, JSTokenTypes.PUBLIC_KEYWORD)
        );
        String identifierName = null;
        if (null != identifier) {
            identifierName = dequote(identifier.getText());
        }

        return identifierName;
    }

    private void parsePackages(TreeElement node) {
        TokenSet tokenSet = TokenSet.create(
                JSElementTypes.OBJECT_LITERAL_EXPRESSION,
                JSElementTypes.LITERAL_EXPRESSION);
        TreeElement packageNode = (TreeElement) node.findChildByType(tokenSet);
        parsePackage(packageNode);
    }

    private void parsePackage(TreeElement node) {
        if (null == node) {
            return;
        }
        if (node.getElementType() == JSElementTypes.OBJECT_LITERAL_EXPRESSION
            || node.getElementType() == JSElementTypes.LITERAL_EXPRESSION
        ) {
            // TODO: Not adding not resolve package
            Package p = new Package();
            packageConfig.packages.add(p);
            if (node.getElementType() == JSElementTypes.OBJECT_LITERAL_EXPRESSION) {
                TreeElement prop = (TreeElement) node.findChildByType(JSElementTypes.PROPERTY);
                parsePackageObject(prop, p);
            } else {
                p.name = dequote(node.getText());
            }
            normalizeParsedPackage(p);
            validatePackage(p);
        }
        TreeElement next = node.getTreeNext();
        parsePackage(next);
    }

    private void normalizeParsedPackage(Package p) {
        if (null == p.location) {
            p.location = p.name;
        }
        if (null == p.main) {
            p.main = Package.DEFAULT_MAIN;
        }
    }

    private void validatePackage(Package p) {
        if (null == getConfigFileDir().findFileByRelativePath(p.location + '/' + p.main + ".js")) {
            p.mainExists = false;
        } else {
            p.mainExists = true;
        }
    }

    private static void parsePackageObject(TreeElement node, Package p) {
        if (null == node) {
            return;
        }

        if (node.getElementType() == JSElementTypes.PROPERTY) {
            TreeElement identifier = (TreeElement) node.findChildByType(JSTokenTypes.IDENTIFIER);
            String identifierName = null;
            if (null != identifier) {
                identifierName = identifier.getText();
            } else {
                TreeElement identifierString = (TreeElement) node.findChildByType(JSTokenTypes.STRING_LITERAL);
                if (null != identifierString) {
                    identifierName = dequote(identifierString.getText());
                }
            }
            if (null != identifierName) {
                if (identifierName.equals("name")) {
                    p.name = getJSPropertyLiteralValue(node);
                } else if (identifierName.equals("location")) {
                    p.location = getJSPropertyLiteralValue(node);
                } else if (identifierName.equals("main")) {
                    p.main = getJSPropertyLiteralValue(node);
                }
            }
        }

        TreeElement next = node.getTreeNext();
        parsePackageObject(next, p);
    }

    private static String getJSPropertyLiteralValue(TreeElement jsProperty) {
        return dequote(jsProperty
                .findChildByType(JSElementTypes.LITERAL_EXPRESSION)
                .getText());
    }

    protected void setBaseUrl(String baseUrl) {
        if (baseUrl.startsWith("/")) {
            baseUrl = baseUrl.substring(1);
        }
        if (baseUrl.endsWith("/")) {
            baseUrl = StringUtil.trimEnd(baseUrl, "/");
        }
        requirejsBaseUrl = baseUrl;
    }

    protected void parseRequireJsPaths(TreeElement node) {
        if (null == node) {
            return ;
        }

        if (node.getElementType() == JSElementTypes.PROPERTY) {
            RequirePathAlias pathAlias = new RequirePathAlias();
            pathAlias.alias = getJSPropertyName(node);
            pathAlias.path = getJSPropertyLiteralValue(node);
            requirePaths.addPath(pathAlias);
        }

        TreeElement next = node.getTreeNext();
        if (null != next) {
            parseRequireJsPaths(next);
        }
    }

    public VirtualFile resolvePath(String path) {
        VirtualFile rootDirectory;
        if (path.startsWith(".")) {
            rootDirectory = getBaseUrlPath(false);
        } else if (path.startsWith("/")) {
            // TODO: Check work on multi modules idea project and empty web path
            rootDirectory = getWebDir();
        } else {
            rootDirectory = getBaseUrlPath(false);
        }

        if (null != rootDirectory) {
            VirtualFile directoryVF = rootDirectory.findFileByRelativePath(path);
            if (null != directoryVF) {
                return directoryVF;
            } else {
                VirtualFile fileVF = rootDirectory.findFileByRelativePath(path + ".js");
                if (null != fileVF) {
                    return fileVF;
                }
            }
        }

        return null;
    }

    public PsiElement requireResolve(PsiElement element) {
        Path path = new Path(element, this);

        return path.resolve();
    }

    public List<String> getCompletion(PsiElement element) {
        List<String> completions = new ArrayList<String>();
        String value = element.getText().replace("'", "").replace("\"", "").replace("IntellijIdeaRulezzz ", "");
        String valuePath = value;
        boolean exclamationMark = value.contains("!");
        String plugin = "";
        int doubleDotCount = 0;
        boolean notEndSlash = false;
        String pathOnDots = "";
        String dotString = "";
        VirtualFile elementFile = element
                .getContainingFile()
                .getOriginalFile()
                .getVirtualFile();

        if (exclamationMark) {
            String[] exclamationMarkSplit = valuePath.split("!");
            plugin = exclamationMarkSplit[0];
            if (exclamationMarkSplit.length == 2) {
                valuePath = exclamationMarkSplit[1];
            } else {
                valuePath = "";
            }
        }

        if (exclamationMark) {
            for (String moduleName : getModulesNames()) {
                completions.add(plugin + '!' + moduleName);
            }
        } else {
            completions.addAll(getModulesNames());
            // expand current package
        }


        PsiDirectory fileDirectory = element
                .getContainingFile()
                .getOriginalFile()
                .getContainingDirectory();
        if (null == fileDirectory) {
            return completions;
        }
        String filePath = fileDirectory
                .getVirtualFile()
                .getPath()
                .replace(getWebDir(elementFile).getPath(), "");
        if (filePath.startsWith("/")) {
            filePath = filePath.substring(1);
        }

        boolean startSlash = valuePath.startsWith("/");
        if (startSlash) {
            valuePath = valuePath.substring(1);
        }

        boolean oneDot = valuePath.startsWith("./");
        if (oneDot) {
            if (filePath.isEmpty()) {
                valuePath = valuePath.substring(2);
            } else {
                valuePath = valuePath.replaceFirst(".", filePath);
            }
        }

        if (valuePath.startsWith("..")) {
            doubleDotCount = FileUtils.getDoubleDotCount(valuePath);
            String[] pathsOfPath = filePath.split("/");
            if (pathsOfPath.length > 0) {
                if (doubleDotCount > 0) {
                    if (doubleDotCount > pathsOfPath.length || filePath.isEmpty()) {
                        return new ArrayList<String>();
                    }
                    pathOnDots = FileUtils.getNormalizedPath(doubleDotCount, pathsOfPath);
                    dotString = StringUtil.repeat("../", doubleDotCount);
                    if (valuePath.endsWith("..")) {
                        notEndSlash = true;
                    }
                    if (valuePath.endsWith("..") || !StringUtil.isEmpty(pathOnDots)) {
                        dotString = dotString.substring(0, dotString.length() - 1);
                    }
                    valuePath = valuePath.replaceFirst(dotString, pathOnDots);
                }
            }
        }

        List<String> allFiles = FileUtils.getAllFilesInDirectory(
                getWebDir(elementFile),
                getWebDir(elementFile).getPath() + '/',
                ""
        );
        List<String> aliasFiles = requirePaths.getAllFilesOnPaths();
        aliasFiles.addAll(packageConfig.getAllFilesOnPackages());

        String requireMapModule = FileUtils.removeExt(element
                .getContainingFile()
                .getOriginalFile()
                .getVirtualFile()
                .getPath()
                .replace(
                    getWebDir(elementFile).getPath() + '/',
                    ""
                ),
            ".js"
        );

        completions.addAll(requireMap.getCompletionByModule(requireMapModule));

        String valuePathForAlias = valuePath;
        if (!oneDot && 0 == doubleDotCount && !startSlash && !getBaseUrl().isEmpty()) {
            valuePath = FileUtils.join(getBaseUrl(), valuePath);
        }

        for (String file : allFiles) {
            if (file.startsWith(valuePath)) {
                // Prepare file path
                if (oneDot) {
                    if (filePath.isEmpty()) {
                        file = "./" + file;
                    } else {
                        file = file.replaceFirst(filePath, ".");
                    }
                }

                if (doubleDotCount > 0) {
                    if (!StringUtil.isEmpty(valuePath)) {
                        file = file.replace(pathOnDots, "");
                    }
                    if (notEndSlash) {
                        file = '/' + file;
                    }
                    file = dotString + file;
                }

                if (!oneDot && 0 == doubleDotCount && !startSlash && !getBaseUrl().isEmpty()) {
                    file = file.substring(getBaseUrl().length() + 1);
                }

                if (startSlash) {
                    file = '/' + file;
                }

                addToCompletion(completions, file, exclamationMark, plugin);
            }
        }

        for (String file : aliasFiles) {
            if (file.startsWith(valuePathForAlias)) {
                addToCompletion(completions, file, exclamationMark, plugin);
            }
        }

        return completions;
    }

    private boolean isModuleAccessible(Package pkg, String file, String moduleId) {
        return file.endsWith(".js") && !file.equals(moduleId) && !file.equals(pkg.name + '/' + pkg.main + ".js");
//        return (restrictAccessToPackage && !file.equals(pkg.name + '/' + pkg.main + ".js")) && file.endsWith(".js") && !file.equals(moduleId);
    }

    private static void addToCompletion(List<String> completions, String file, boolean exclamationMark, String plugin) {
        if (exclamationMark) {
            file = FileUtils.removeExt(file, ".js");
            completions.add(plugin + '!' + file);
        } else if (file.endsWith(".js")) {
            completions.add(file.replace(".js", ""));
        }
    }

    // -------------------------------------------------------------------------
    // RequireConfigVfsListener
    // -------------------------------------------------------------------------
    private class RequireConfigVfsListener extends VirtualFileAdapter {
        public void contentsChanged(@NotNull VirtualFileEvent event) {
            VirtualFile confFile = findPathInWebDir(settings.configFilePath);
            if (confFile == null || !confFile.exists() || !event.getFile().equals(confFile)) {
                return;
            }
            LOG.debug("RequireConfigVfsListener contentsChanged");
//            RequirejsProjectComponent.this.project.getComponent(RequirejsProjectComponent.class).parseRequirejsConfig();
            RequirejsProjectComponent.this.parseRequirejsConfig();
        }
    }
}
