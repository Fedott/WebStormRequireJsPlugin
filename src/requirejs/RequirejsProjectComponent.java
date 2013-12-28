package requirejs;

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
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.newvfs.impl.VirtualDirectoryImpl;
import com.intellij.openapi.vfs.newvfs.impl.VirtualFileImpl;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.impl.source.tree.TreeElement;
import org.jetbrains.annotations.NotNull;
import requirejs.settings.Settings;

import java.util.ArrayList;
import java.util.HashMap;

public class RequirejsProjectComponent implements ProjectComponent
{
    protected Project project;
    protected Settings settings;
    protected boolean settingValidStatus;
    protected String settingValidVersion;

    final protected static Logger LOG = Logger.getInstance("Requirejs-Plugin");
    private VirtualFile requirejsBaseUrlPath;
    private String requirejsBaseUrl;

    protected HashMap<String, VirtualFile> requirejsConfigModules;

    public RequirejsProjectComponent(Project project) {
        this.project = project;
        settings = Settings.getInstance(project);
    }

    @Override
    public void projectOpened() {
        validateSettings();
    }

    @Override
    public void projectClosed() {

    }

    @Override
    public void initComponent() {
        validateSettings();
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

    public boolean isSettingsValid(){
        if (!settings.getVersion().equals(settingValidVersion)) {
            validateSettings();
        }
        return settingValidStatus;
    }

    public boolean validateSettings()
    {
        if (null == getWebDir()) {
            showErrorConfigNotification("Web path not found");
            getLogger().debug("Web path not found");
            settingValidStatus = false;
            return false;
        }

        settingValidStatus = true;
        return true;
    }

    protected void showErrorConfigNotification(String content) {
        if (!settings.getVersion().equals(settingValidVersion)) {
            showInfoNotification(content, NotificationType.ERROR);
        }
    }

    public VirtualFile getWebDir() {
        if (settings.webPath.equals("")) {
            return project.getBaseDir();
        }
        return project.getBaseDir().findFileByRelativePath(settings.webPath);
    }

    public void showInfoNotification(String content) {
        this.showInfoNotification(content, NotificationType.WARNING);
    }

    public void showInfoNotification(String content, NotificationType type) {
        Notification errorNotification = new Notification("Require.js plugin", "Require.js plugin", content, type);
        Notifications.Bus.notify(errorNotification, this.project);
    }

    public ArrayList<String> getModulesNames()
    {
        ArrayList<String> modules = new ArrayList<String>();
        if (requirejsConfigModules == null) {
            if (!parseRequirejsConfig()) {
                return modules;
            }
        }

        modules.addAll(requirejsConfigModules.keySet());

        return modules;
    }

    public VirtualFile getModuleVFile(String alias)
    {
        if (requirejsConfigModules == null) {
            if (!parseRequirejsConfig()) {
                return null;
            }
        }

        return requirejsConfigModules.get(alias);
    }

    public String getBaseUrl()
    {
        if (null == requirejsBaseUrl) {
            if (!parseRequirejsConfig() || null == requirejsBaseUrl) {
                requirejsBaseUrl = "";
            }
        }

        return requirejsBaseUrl;
    }

    public VirtualFile getBaseUrlPath()
    {
        if (null == requirejsBaseUrlPath) {
            if (!parseRequirejsConfig() || null == requirejsBaseUrlPath) {
                requirejsBaseUrlPath = getWebDir();
            }
        }

        return requirejsBaseUrlPath;
    }

    protected boolean parseRequirejsConfig()
    {
        VirtualFile mainJsVirtualFile = getWebDir()
                .findFileByRelativePath(
                        settings.mainJsPath
                );
        if (null == mainJsVirtualFile) {
            this.showErrorConfigNotification("Config file not found");
            getLogger().debug("Config not found");
            return false;
        } else {
            PsiFile mainJs = PsiManager
                    .getInstance(project)
                    .findFile(
                            mainJsVirtualFile
                    );
            if (mainJs instanceof JSFileImpl) {
                if (((JSFileImpl) mainJs).getTreeElement() == null) {
                    requirejsConfigModules = parseMainJsFile(((JSFileImpl) mainJs).calcTreeElement());
                } else {
                    requirejsConfigModules = parseMainJsFile(((JSFileImpl) mainJs).getTreeElement());
                }
            } else {
                this.showErrorConfigNotification("Config file wrong format");
                getLogger().debug("Config file wrong format");
                return false;
            }
        }

        return true;
    }

    public HashMap<String, VirtualFile> parseMainJsFile(TreeElement node) {
        HashMap<String, VirtualFile> list = new HashMap<String, VirtualFile>();

        TreeElement firstChild = node.getFirstChildNode();
        if (firstChild != null) {
            list.putAll(parseMainJsFile(firstChild));
        }

        TreeElement nextNode = node.getTreeNext();
        if (nextNode != null) {
            list.putAll(parseMainJsFile(nextNode));
        }

        if (node.getElementType() == JSTokenTypes.IDENTIFIER) {
            String requirejsFunctionName = Settings
                    .getInstance(project)
                    .requireFunctionName;
            if (node.getText().equals(requirejsFunctionName)) {
                TreeElement treeParent = node.getTreeParent();
                findAndParseConfig(list, treeParent);
            }
            if (node.getText().equals("requirejs")) {
                TreeElement treeParent = node.getTreeParent();
                if (null != treeParent) {
                    TreeElement nextTreeElement = treeParent.getTreeNext();
                    if (null != nextTreeElement && nextTreeElement.getElementType() == JSTokenTypes.DOT) {
                        nextTreeElement = nextTreeElement.getTreeNext();
                        if (null != nextTreeElement && nextTreeElement.getText().equals("config")) {
                            treeParent = nextTreeElement.getTreeParent();
                            findAndParseConfig(list, treeParent);
                        }
                    }
                }
            }
        }

        return list;
    }

    protected void findAndParseConfig(HashMap<String, VirtualFile> list, TreeElement treeParent) {
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

    public HashMap<String, VirtualFile> parseRequirejsConfig(TreeElement node) {
        HashMap<String, VirtualFile> list = new HashMap<String, VirtualFile>();
        if (null == node) {
            return list;
        }

        try {
            if (node.getElementType() == JSElementTypes.PROPERTY) {
                TreeElement identifier = (TreeElement) node.findChildByType(JSTokenTypes.IDENTIFIER);
                if (null != identifier) {
                    String identifierName = identifier.getText();
                    if (identifierName.equals("baseUrl")) {
                        String baseUrl;

                        baseUrl = node
                                .findChildByType(JSElementTypes.LITERAL_EXPRESSION)
                                .getText().replace("\"", "").replace("'","");
                        setBaseUrl(baseUrl);
                    }
                    if (identifierName.equals("paths")) {
                        list.putAll(
                                parseRequireJsPaths(
                                        (TreeElement) node
                                                .findChildByType(JSElementTypes.OBJECT_LITERAL_EXPRESSION)
                                                .getFirstChildNode()
                                )
                        );
                    }
                }
            }
        } catch (NullPointerException exception) {
            getLogger().error(exception.getMessage());
        }

        TreeElement next = node.getTreeNext();
        if (null != next) {
            list.putAll(parseRequirejsConfig(next));
        }

        return list;
    }

    protected void setBaseUrl(String baseUrl) {
        if (baseUrl.startsWith("/")) {
            baseUrl = baseUrl.substring(1);
        }
        if (baseUrl.endsWith("/")) {
            baseUrl = StringUtil.trimEnd(baseUrl, "/");
        }
        requirejsBaseUrl = baseUrl;
        baseUrl = settings
                .webPath
                .concat("/")
                .concat(baseUrl);
        requirejsBaseUrlPath = project
                .getBaseDir()
                .findFileByRelativePath(baseUrl);
    }

    protected HashMap<String, VirtualFile> parseRequireJsPaths(TreeElement node) {
        HashMap<String, VirtualFile> list = new HashMap<String, VirtualFile>();
        if (null == node) {
            return list;
        }

        if (node.getElementType() == JSElementTypes.PROPERTY) {
            TreeElement path = (TreeElement) node.findChildByType(JSElementTypes.LITERAL_EXPRESSION);
            TreeElement alias = node.getFirstChildNode();
            if (null != path && null != alias) {
                String pathString = path.getText().replace("\"","").replace("'", "").concat(".js");
                String aliasString = alias.getText().replace("\"","").replace("'", "");

                VirtualFile pathVF = getWebDir().findFileByRelativePath(pathString);
                if (null != pathVF) {
                    list.put(aliasString, pathVF);
                } else {
                    pathVF = getBaseUrlPath().findFileByRelativePath(pathString);
                    if (null != pathVF) {
                        list.put(aliasString, pathVF);
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

    public PsiElement requireResolve(PsiElement element)
    {
        String path;
        String pathOriginal;
        VirtualFile targetFile;

        pathOriginal = element.getText().replace("'", "").replace("\"", "");
        path = pathOriginal;
        if (path.startsWith("tpl!")) {
            path = path.replace("tpl!", "");
        } else if (!path.endsWith(".js")) {
            path = path.concat(".js");
        }

        if (path.startsWith("./") || path.startsWith("..")) {
            PsiDirectory fileDirectory = element.getContainingFile().getContainingDirectory();
            if (null != fileDirectory) {
                targetFile = fileDirectory
                        .getVirtualFile()
                        .findFileByRelativePath(path);
                if (null != targetFile) {
                    return PsiManager.getInstance(element.getProject()).findFile(targetFile);
                }
            }
        }

        VirtualFile webDir = getWebDir();

        targetFile = webDir.findFileByRelativePath(path);

        if (targetFile != null) {
            return PsiManager.getInstance(element.getProject()).findFile(targetFile);
        }

        VirtualFile module = getModuleVFile(pathOriginal);
        if (null != module) {
            return PsiManager
                    .getInstance(element.getProject())
                    .findFile(module);
        }

        return null;
    }

    public ArrayList<String> getCompletion(PsiElement element)
    {
        ArrayList<String> completions = new ArrayList<String>();
        String value = element.getText().replace("'", "").replace("\"", "").replace("IntellijIdeaRulezzz ", "");
        Boolean tpl = value.startsWith("tpl!");
        String valuePath = value.replaceFirst("tpl!", "");
        Boolean oneDot;
        Integer doubleDotCount = 0;
        Boolean startSlash;
        Boolean notEndSlash = false;
        String pathOnDots = "";
        String dotString = "";

        completions.addAll(getModulesNames());

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

        startSlash = valuePath.startsWith("/");
        if (startSlash) {
            valuePath = valuePath.substring(1);
        }

        oneDot = valuePath.startsWith("./");
        if (oneDot) {
            if (filePath.equals("")) {
                valuePath = valuePath.substring(2);
            } else {
                try {
                    valuePath = valuePath
                            .replaceFirst(
                                    ".",
                                    filePath
                            );
                } catch (NullPointerException ignored) {}
            }
        }

        if (valuePath.startsWith("..")) {
            doubleDotCount = getDoubleDotCount(valuePath);
            String[] pathsOfPath = filePath.split("/");
            if ( pathsOfPath.length > 0) {
                if (doubleDotCount > 0) {
                    if (doubleDotCount > pathsOfPath.length || filePath.equals("")) {
                        return new ArrayList<String>();
                    }
                    pathOnDots = getNormalizedPath(doubleDotCount, pathsOfPath);
                    dotString = StringUtil.repeat("../", doubleDotCount);
                    if (valuePath.endsWith("..")) {
                        notEndSlash = true;
                    }
                    if (valuePath.endsWith("..") || !StringUtil.isEmpty(pathOnDots)) {
                        dotString = dotString.substring(0, dotString.length() - 1);
                    }
                    valuePath = valuePath.replace(dotString, pathOnDots);
                }
            }
        }

        ArrayList<String> allFiles = getAllFilesInDirectory(getWebDir());

        if (!oneDot && 0 == doubleDotCount && !startSlash && !getBaseUrl().equals("")) {
            valuePath = getBaseUrl().concat("/").concat(valuePath);
        }

        for (String file : allFiles) {
            if (file.startsWith(valuePath)) {
                // Prepare file path
                if (oneDot) {
                    if (filePath.equals("")) {
                        file = "./".concat(file);
                    } else {
                        file = file.replaceFirst(filePath, ".");
                    }
                }

                if (doubleDotCount > 0) {
                    if (!StringUtil.isEmpty(valuePath)) {
                        file = file.replace(pathOnDots, "");
                    }
                    if (notEndSlash) {
                        file = "/".concat(file);
                    }
                    file = dotString.concat(file);
                }

                if (!oneDot && 0 == doubleDotCount && !startSlash && !getBaseUrl().equals("")) {
                    file = file.substring(getBaseUrl().length() + 1);
                }

                if (startSlash) {
                    file = "/".concat(file);
                }

                if (tpl && file.endsWith(".html")) {
                    completions.add("tpl!" + file);
                } else if (file.endsWith(".js")) {
                    completions.add(file.replace(".js", ""));
                }
            }
        }

        return completions;
    }

    protected String getNormalizedPath(Integer doubleDotCount, String[] pathsOfPath) {
        StringBuilder newValuePath = new StringBuilder();
        for (int i = 0; i < pathsOfPath.length - doubleDotCount; i++) {
            if (0 != i) {
                newValuePath.append("/");
            }
            newValuePath.append(pathsOfPath[i]);
        }
        return newValuePath.toString();
    }

    protected Integer getDoubleDotCount(String valuePath) {
        Integer doubleDotCount = (valuePath.length() - valuePath.replaceAll("\\.\\.", "").length()) / 2;

        Boolean doubleDotCountTrues = false;

        while (!doubleDotCountTrues && 0 != doubleDotCount) {
            if (valuePath.startsWith(StringUtil.repeat("../", doubleDotCount))) {
                doubleDotCountTrues = true;
            } else if (valuePath.startsWith(StringUtil.repeat("../", doubleDotCount - 1) + "..")) {
                doubleDotCountTrues = true;
            } else {
                doubleDotCount--;
            }
        }
        return doubleDotCount;
    }

    protected ArrayList<String> getAllFilesInDirectory(VirtualFile directory) {
        ArrayList<String> files = new ArrayList<String>();

        VirtualFile[] childrens = directory.getChildren();
        if (childrens.length != 0) {
            for (VirtualFile children : childrens) {
                if (children instanceof VirtualDirectoryImpl) {
                    files.addAll(getAllFilesInDirectory(children));
                } else if (children instanceof VirtualFileImpl) {
                    files.add(children.getPath().replace(getWebDir().getPath() + "/", ""));
                }
            }
        }

        return files;
    }
}
