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

import javax.annotation.Nullable;
import java.util.*;

public class RequirejsProjectComponent implements ProjectComponent {
    protected Project project;
    protected Settings settings;
    protected boolean settingValidStatus;
    protected String settingValidVersion;
    protected String settingVersionLastShowNotification;

    private static final Logger LOG = Logger.getInstance("Requirejs-Plugin");
    private VirtualFile requirejsBaseUrlPath;
    private String requirejsBaseUrl;

    protected Map<String, VirtualFile> requirejsConfigModules;
    protected Map<String, VirtualFile> requirejsConfigPaths;

    private RequireConfigVfsListener vfsListener;
    public PackageConfig packageConfig = new PackageConfig();

    public RequirejsProjectComponent(Project project) {
        this.project = project;
        settings = Settings.getInstance(project);
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

    public static Logger getLogger() {
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

    protected void showErrorConfigNotification(String content) {
        if (!settings.getVersion().equals(settingVersionLastShowNotification)) {
            settingVersionLastShowNotification = settings.getVersion();
            showInfoNotification(content, NotificationType.ERROR);
        }
    }

    public VirtualFile getWebDir() {
        VirtualFile contentRoot = getContentRoot();
        if (settings.publicPath.isEmpty()) {
            return contentRoot;
        }
        return contentRoot.findFileByRelativePath(settings.publicPath);
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

    public void showInfoNotification(String content, NotificationType type) {
        Notification errorNotification = new Notification("Require.js plugin", "Require.js plugin", content, type);
        Notifications.Bus.notify(errorNotification, this.project);
    }

    public Map<String, VirtualFile> getConfigPaths() {
        if (requirejsConfigPaths == null) {
            if (!parseRequirejsConfig()) {
                return requirejsConfigPaths;
            }
        }

        return requirejsConfigPaths;
    }

    public List<String> getModulesNames() {
        List<String> modules = new ArrayList<String>();
        if (requirejsConfigModules == null) {
            if (!parseRequirejsConfig()) {
                return modules;
            }
        }
        modules.addAll(requirejsConfigModules.keySet());
        Collection<Package> filteredPackages = Collections2.filter(packageConfig.packages, new Predicate<Package>() {
            @Override
            public boolean apply(@Nullable Package aPackage) {
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

    public VirtualFile getModuleVFile(String alias) {
        if (requirejsConfigModules == null) {
            if (!parseRequirejsConfig()) {
                return null;
            }
        }
        return requirejsConfigModules.get(alias);
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
        if (null == requirejsBaseUrlPath) {
            if (parseConfig) {
                parseRequirejsConfig();
            }
            if (null == requirejsBaseUrlPath) {
                requirejsBaseUrlPath = getConfigFileDir();
            }
        }

        return requirejsBaseUrlPath;
    }

    protected VirtualFile getConfigFileDir() {
        VirtualFile mainJsVirtualFile = getWebDir()
                .findFileByRelativePath(
                        settings.configFilePath
                );
        if (null != mainJsVirtualFile) {
            return mainJsVirtualFile.getParent();
        } else {
            return null;
        }
    }

//    private Date lastParse;

    public boolean parseRequirejsConfig() {
        VirtualFile mainJsVirtualFile = getWebDir().findFileByRelativePath(settings.configFilePath);
        if (null == mainJsVirtualFile) {
            this.showErrorConfigNotification("Config file not found. File " + settings.publicPath + '/' + settings.configFilePath + " not found in project");
            LOG.debug("Config not found");
            return false;
        } else {
            PsiFile mainJs = PsiManager.getInstance(project).findFile(mainJsVirtualFile);
            if (mainJs instanceof JSFileImpl || mainJs instanceof XmlFileImpl) {
                Map<String, VirtualFile> allConfigPaths;
                packageConfig.clear();
                if (((PsiFileImpl) mainJs).getTreeElement() == null) {
                    allConfigPaths = parseMainJsFile(((PsiFileImpl) mainJs).calcTreeElement());
                } else {
                    allConfigPaths = parseMainJsFile(((PsiFileImpl) mainJs).getTreeElement());
                }
                requirejsConfigModules = new HashMap<String, VirtualFile>();
                requirejsConfigPaths = new HashMap<String, VirtualFile>();
                for (Map.Entry<String, VirtualFile> entry : allConfigPaths.entrySet()) {
                    if (entry.getValue().isDirectory()) {
                        requirejsConfigPaths.put(entry.getKey(), entry.getValue());
                    } else {
                        requirejsConfigModules.put(entry.getKey(), entry.getValue());
                    }
                }
            } else {
                this.showErrorConfigNotification("Config file wrong format");
                LOG.debug("Config file wrong format");
                return false;
            }
        }

        return true;
    }

    public Map<String, VirtualFile> parseMainJsFile(TreeElement node) {
        Map<String, VirtualFile> list = new HashMap<String, VirtualFile>();

        TreeElement firstChild = node.getFirstChildNode();
        if (firstChild != null) {
            list.putAll(parseMainJsFile(firstChild));
        }

        TreeElement nextNode = node.getTreeNext();
        if (nextNode != null) {
            list.putAll(parseMainJsFile(nextNode));
        }

        if (node.getElementType() == JSTokenTypes.IDENTIFIER) {
            if (node.getText().equals("requirejs") || node.getText().equals("require")) {
                TreeElement treeParent = node.getTreeParent();

                if (null != treeParent) {
                    ASTNode firstTreeChild = treeParent.findChildByType(JSElementTypes.OBJECT_LITERAL_EXPRESSION);
                    TreeElement nextTreeElement = treeParent.getTreeNext();
                    if (null != firstTreeChild) {
                        list.putAll(
                            parseRequirejsConfig((TreeElement) firstTreeChild
                                .getFirstChildNode()
                            )
                        );
                    } else if (null != nextTreeElement && nextTreeElement.getElementType() == JSTokenTypes.DOT) {
                        nextTreeElement = nextTreeElement.getTreeNext();
                        if (null != nextTreeElement && nextTreeElement.getText().equals("config")) {
                            treeParent = nextTreeElement.getTreeParent();
                            findAndParseConfig(list, treeParent);
                        }
                    } else {
                        findAndParseConfig(list, treeParent);
                    }
                }
            }
        }

        return list;
    }

    protected void findAndParseConfig(Map<String, VirtualFile> list, TreeElement treeParent) {
        TreeElement nextTreeElement;
        if (null != treeParent) {
            nextTreeElement = treeParent.getTreeNext();
            if (null != nextTreeElement) {
                ASTNode nextChild = nextTreeElement.findChildByType(JSElementTypes.OBJECT_LITERAL_EXPRESSION);
                if (null != nextChild) {
                    list.putAll(
                            parseRequirejsConfig(
                                    (TreeElement) nextChild.getFirstChildNode()
                            )
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

    public Map<String, VirtualFile> parseRequirejsConfig(TreeElement node) {
        Map<String, VirtualFile> list = new HashMap<String, VirtualFile>();
        if (null == node) {
            return list;
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
                        String baseUrl = dequote(node
                                .findChildByType(JSElementTypes.LITERAL_EXPRESSION)
                                .getText());
                        setBaseUrl(baseUrl);
                        packageConfig.baseUrl = baseUrl;
                    } else if (identifierName.equals("paths")) {
                        list.putAll(
                                parseRequireJsPaths(
                                        (TreeElement) node
                                                .findChildByType(JSElementTypes.OBJECT_LITERAL_EXPRESSION)
                                                .getFirstChildNode()
                                )
                        );
                    } else if (identifierName.equals("packages")) {
                        TreeElement packages = (TreeElement) node.findChildByType(JSElementTypes.ARRAY_LITERAL_EXPRESSION);
                        LOG.debug("parsing packages");
                        parsePackages(packages);
                        LOG.debug("parsing packages done, found " + packageConfig.packages.size() + " packages");
                    }
                }
            }
        } catch (NullPointerException exception) {
            LOG.error(exception.getMessage(), exception);
        }

        TreeElement next = node.getTreeNext();
        if (null != next) {
            list.putAll(parseRequirejsConfig(next));
        }

        return list;
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
                    p.name = getLiteralValue(node);
                } else if (identifierName.equals("location")) {
                    p.location = getLiteralValue(node);
                } else if (identifierName.equals("main")) {
                    p.main = getLiteralValue(node);
                }
            }
        }

        TreeElement next = node.getTreeNext();
        parsePackageObject(next, p);
    }

    private static String getLiteralValue(TreeElement node) {
        return dequote(node
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
        baseUrl = settings.publicPath + '/' + baseUrl;

        VirtualFile firstContentRoot = getContentRoot();
        requirejsBaseUrlPath = firstContentRoot.findFileByRelativePath(baseUrl);
    }

    protected Map<String, VirtualFile> parseRequireJsPaths(TreeElement node) {
        Map<String, VirtualFile> list = new HashMap<String, VirtualFile>();
        if (null == node) {
            return list;
        }

        if (node.getElementType() == JSElementTypes.PROPERTY) {
            TreeElement path = (TreeElement) node.findChildByType(JSElementTypes.LITERAL_EXPRESSION);
            TreeElement alias = node.getFirstChildNode();
            if (null != path && null != alias) {
                String pathString = dequote(path.getText());
                String aliasString = dequote(alias.getText());

                VirtualFile rootDirectory;
                if (pathString.startsWith(".")) {
                    rootDirectory = getBaseUrlPath(false);
                } else if (pathString.startsWith("/")) {
                    rootDirectory = getWebDir();
                } else {
                    rootDirectory = getBaseUrlPath(false);
                }

                if (null != rootDirectory) {
                    VirtualFile directoryVF = rootDirectory.findFileByRelativePath(pathString);
                    if (null != directoryVF) {
                        list.put(aliasString, directoryVF);
                    } else {
                        VirtualFile fileVF = rootDirectory.findFileByRelativePath(pathString + ".js");
                        if (null != fileVF) {
                            list.put(aliasString, fileVF);
                        }
                    }
                }
            }
        }

        TreeElement next = node.getTreeNext();
        if (null != next) {
            list.putAll(parseRequireJsPaths(next));
        }

        return list;
    }

    public PsiElement requireResolve(PsiElement element) {
        VirtualFile targetFile;

        String value = dequote(element.getText());
        String valuePath = value;
        if (valuePath.contains("!")) {
            String[] exclamationMarkSplit = valuePath.split("!");
            if (exclamationMarkSplit.length == 2) {
                valuePath = exclamationMarkSplit[1];
            } else {
                valuePath = "";
            }
        }

        if (valuePath.startsWith("/")) {
            targetFile = FileUtils.findFileByPath(getWebDir(), valuePath);
            if (null != targetFile) {
                return PsiManager.getInstance(element.getProject()).findFile(targetFile);
            } else {
                return null;
            }
        } else if (valuePath.startsWith(".")) {
            PsiDirectory fileDirectory = element.getContainingFile().getContainingDirectory();
            if (null != fileDirectory) {
                targetFile = FileUtils.findFileByPath(fileDirectory.getVirtualFile(), valuePath);
                if (null != targetFile) {
                    return PsiManager.getInstance(element.getProject()).findFile(targetFile);
                }
            }
        }

        targetFile = FileUtils.findFileByPath(getBaseUrlPath(true), valuePath);

        if (targetFile != null) {
            return PsiManager.getInstance(element.getProject()).findFile(targetFile);
        }

        VirtualFile module = getModuleVFile(valuePath);
        if (null != module) {
            return PsiManager
                    .getInstance(element.getProject())
                    .findFile(module);
        }

        if (null != getConfigPaths()) {
            for (Map.Entry<String, VirtualFile> entry : getConfigPaths().entrySet()) {
                if (valuePath.startsWith(entry.getKey())) {
                    targetFile = FileUtils.findFileByPath(entry.getValue(), valuePath.replaceFirst(entry.getKey(), ""));
                    if (null != targetFile) {
                        return PsiManager.getInstance(element.getProject()).findFile(targetFile);
                    }
                }
            }
        }

        // check for packages
        String packageName;
        String moduleId = null;
        if (valuePath.indexOf('/') == -1) {
            packageName = valuePath;
        } else {
            packageName = valuePath.substring(0, valuePath.indexOf('/'));
            moduleId = valuePath.substring(valuePath.indexOf('/') + 1);
        }
        for (Package pkg : packageConfig.packages) {
            if (pkg.name.equals(packageName)) {
                if (moduleId == null) {
                    moduleId = pkg.main;
                }
                targetFile = getBaseUrlPath(false)
                        .findFileByRelativePath(pkg.location + '/' + moduleId + ".js");
                if (null != targetFile) {
                    return PsiManager.getInstance(element.getProject()).findFile(targetFile);
                }
            }
        }

        LOG.debug("Could not resolve reference for " + value);
        return null;
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
                .replace(getWebDir().getPath(), "");
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

        List<String> allFiles = FileUtils.getAllFilesInDirectory(getWebDir(), getWebDir().getPath() + '/', "");
        List<String> aliasFiles = getAllFilesForConfigPaths();

        // get project relative path
        VirtualFile contentRoot = getContentRoot(fileDirectory.getVirtualFile());
        String relativePath;
        if (fileDirectory.getVirtualFile().getPath().equals(contentRoot.getPath())) {
            relativePath = "";
        } else {
            relativePath = fileDirectory.getVirtualFile().getPath().substring(contentRoot.getPath().length() + 1);
        }
        String relativeFilePath = element.getContainingFile().getOriginalFile().getVirtualFile().getPath().substring(contentRoot.getPath().length() + 1);

        for (Package pkg : packageConfig.packages) {
            if (relativePath.startsWith(pkg.location)) {
                VirtualFile pkgLocation = getConfigFileDir().findFileByRelativePath(pkg.location);
                if (null != pkgLocation) {
                    List<String> packageFiles = FileUtils.getAllFilesInDirectory(pkgLocation, pkgLocation.getPath(), pkg.name);
                    for (String file : packageFiles) {
                        // filter out entry
                        // TODO filter out current file
                        String moduleId = pkg.name + '/' + relativeFilePath.substring(pkg.location.length() + 1);
                        if (isModuleAccessible(pkg, file, moduleId)) {
                            aliasFiles.add(file);
                        }
                    }
                }
            }
        }

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

    protected List<String> getAllFilesForConfigPaths() {
        List<String> strings = new ArrayList<String>();

        Map<String, VirtualFile> configPaths = getConfigPaths();
        if (null != configPaths) {
            for (Map.Entry<String, VirtualFile> entry : configPaths.entrySet()) {
                strings.addAll(
                        FileUtils.getAllFilesInDirectory(
                                entry.getValue(),
                                entry.getValue().getPath(),
                                entry.getKey()
                        )
                );
            }
        }

        return strings;
    }

    // -------------------------------------------------------------------------
    // RequireConfigVfsListener
    // -------------------------------------------------------------------------
    private class RequireConfigVfsListener extends VirtualFileAdapter {
        public void contentsChanged(@NotNull VirtualFileEvent event) {
            VirtualFile publicPath = getContentRoot().findFileByRelativePath(settings.publicPath);
            if (publicPath == null || !publicPath.exists()) {
                return;
            }
            VirtualFile confFile = publicPath.findChild(settings.configFilePath);
            if (confFile == null || !confFile.exists() || !event.getFile().equals(confFile)) {
                return;
            }
            LOG.debug("RequireConfigVfsListener contentsChanged");
//            RequirejsProjectComponent.this.project.getComponent(RequirejsProjectComponent.class).parseRequirejsConfig();
            RequirejsProjectComponent.this.parseRequirejsConfig();
        }
    }
}
