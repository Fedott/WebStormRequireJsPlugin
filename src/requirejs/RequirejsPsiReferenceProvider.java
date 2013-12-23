package requirejs;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.lang.javascript.JSElementTypes;
import com.intellij.lang.javascript.JSTokenTypes;
import com.intellij.lang.javascript.psi.JSCallExpression;
import com.intellij.lang.javascript.psi.impl.JSFileImpl;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.tree.TreeElement;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
import requirejs.properties.RequirejsSettings;

import java.util.HashMap;

public class RequirejsPsiReferenceProvider extends PsiReferenceProvider {

    protected Project project;
    PropertiesComponent propertiesComponent;
    public static VirtualFile requirejsBasePath;
    public static HashMap<String, VirtualFile> requirejsConfigAliasesMap = new HashMap<String, VirtualFile>();

    public long lastShowNotification = 0;

    @NotNull
    @Override
    public PsiReference[] getReferencesByElement(@NotNull PsiElement psiElement, @NotNull ProcessingContext processingContext) {
        if (project == null || project.isDisposed()) {
            project = psiElement.getProject();
            propertiesComponent = PropertiesComponent.getInstance(project);
        }

        PropertiesComponent properties = PropertiesComponent.getInstance(project);
        String webDirPrefString = properties.getValue(RequirejsSettings.REQUIREJS_WEB_PATH_PROPERTY_NAME, RequirejsSettings.DEFAULT_WEB_PATH);
        VirtualFile webDir = project.getBaseDir().findFileByRelativePath(webDirPrefString);

        if (webDir == null) {
            this.showInfoNotification("Web path not found");
            return PsiReference.EMPTY_ARRAY;
        }

        if (requirejsConfigAliasesMap.size() == 0) {
            VirtualFile mainJsVirtualFile = webDir
                    .findFileByRelativePath(
                            properties.getValue(
                                    RequirejsSettings.REQUIREJS_MAIN_JS_FILE_PATH_PROPERTY_NAME,
                                    RequirejsSettings.DEFAULT_REQUIREJS_MAIN_JS_FILE_PATH
                            )
                    );
            if (null == mainJsVirtualFile) {
                this.showInfoNotification("Config file not found");
            } else {
                PsiFile mainJs = PsiManager
                        .getInstance(project)
                        .findFile(
                                mainJsVirtualFile
                        );
                if (mainJs instanceof JSFileImpl) {
                    if (((JSFileImpl) mainJs).getTreeElement() == null) {
                        requirejsConfigAliasesMap = parseMainJsFile(((JSFileImpl) mainJs).calcTreeElement(), webDir);
                    } else {
                        requirejsConfigAliasesMap = parseMainJsFile(((JSFileImpl) mainJs).getTreeElement(), webDir);
                    }
                } else {
                    this.showInfoNotification("Config file wrong format");
                }
            }
        }

        try {
            String path = psiElement.getText();
            if (isRequireCall(psiElement)) {
                PsiReference ref = new RequirejsReference(psiElement, new TextRange(1, path.length() - 1), project, webDir);
                return new PsiReference[] {ref};
            }
        } catch (Exception ignored) {}

        return new PsiReference[0];
    }

    public static boolean isRequireCall(PsiElement element) {
        PsiElement prevEl = element.getParent();
        if (prevEl != null) {
            prevEl = prevEl.getParent();
        }

        if (prevEl instanceof JSCallExpression) {
            if (prevEl.getChildren().length > 1) {
                String requireFunctionName = PropertiesComponent
                        .getInstance(element.getProject())
                        .getValue(RequirejsSettings.REQUIREJS_FUNCTION_NAME_PROPERTY_NAME, RequirejsSettings.DEFAULT_REQUIREJS_FUNCTION_NAME);
                if (prevEl.getChildren()[0].getText().toLowerCase().equals(requireFunctionName)) {
                    return true;
                }
            }
        }

        return false;
    }

    public HashMap<String, VirtualFile> parseMainJsFile(TreeElement node, VirtualFile webDir) {
        HashMap<String, VirtualFile> list = new HashMap<String, VirtualFile>();

        TreeElement firstChild = node.getFirstChildNode();
        if (firstChild != null) {
            list.putAll(parseMainJsFile(firstChild, webDir));
        }

        TreeElement nextNode = node.getTreeNext();
        if (nextNode != null) {
            list.putAll(parseMainJsFile(nextNode, webDir));
        }

        if (node.getElementType() == JSTokenTypes.IDENTIFIER) {
            try {
                String requirejsFunctionName = PropertiesComponent
                        .getInstance(project)
                        .getValue(RequirejsSettings.REQUIREJS_FUNCTION_NAME_PROPERTY_NAME, RequirejsSettings.DEFAULT_REQUIREJS_FUNCTION_NAME);
                if (node.getText().equals(requirejsFunctionName)) {
                    list.putAll(
                            parseRequirejsConfig(
                                    (TreeElement) node
                                            .getTreeParent()
                                            .getTreeNext()
                                            .findChildByType(JSElementTypes.OBJECT_LITERAL_EXPRESSION)
                                            .getFirstChildNode(),
                                    webDir
                            )
                    );
                }
            } catch (NullPointerException ignored) {}
        }

        return list;
    }

    public HashMap<String, VirtualFile> parseRequirejsConfig(TreeElement node, VirtualFile webDir) {
        HashMap<String, VirtualFile> list = new HashMap<String, VirtualFile>();
        if (null == node) {
            return list;
        }

        TreeElement next = node.getTreeNext();
        if (null != next) {
            list.putAll(parseRequirejsConfig(next, webDir));
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
                        baseUrl = propertiesComponent
                                .getValue(RequirejsSettings.REQUIREJS_WEB_PATH_PROPERTY_NAME, RequirejsSettings.DEFAULT_WEB_PATH)
                                .concat(baseUrl);
                        requirejsBasePath = project
                                .getBaseDir()
                                .findFileByRelativePath(baseUrl);
                    }
                    if (identifierName.equals("paths")) {
                        list.putAll(
                                parseRequireJsPaths(
                                        (TreeElement) node
                                                .findChildByType(JSElementTypes.OBJECT_LITERAL_EXPRESSION)
                                                .getFirstChildNode(),
                                        webDir
                                )
                        );
                    }
                }
            }
        } catch (NullPointerException ignored) {}

        return list;
    }

    protected HashMap<String, VirtualFile> parseRequireJsPaths(TreeElement node, VirtualFile webDir) {
        HashMap<String, VirtualFile> list = new HashMap<String, VirtualFile>();
        if (null == node) {
            return list;
        }

        TreeElement next = node.getTreeNext();
        if (null != next) {
            list.putAll(parseRequireJsPaths(next, webDir));
        }

        if (node.getElementType() == JSElementTypes.PROPERTY) {
            TreeElement path = (TreeElement) node.findChildByType(JSElementTypes.LITERAL_EXPRESSION);
            TreeElement alias = (TreeElement) node.getFirstChildNode();
            if (null != path && null != alias) {
                String pathString = path.getText().replace("\"","").replace("'", "").concat(".js");
                String aliasString = alias.getText().replace("\"","").replace("'", "").concat(".js");

                VirtualFile pathVF = webDir.findFileByRelativePath(pathString);
                if (null != pathVF) {
                    list.put(aliasString, pathVF);
                } else {
                    pathVF = requirejsBasePath.findFileByRelativePath(pathString);
                    if (null != pathVF) {
                        list.put(aliasString, pathVF);
                    }
                }
            }
        }

        return list;
    }

    public void showInfoNotification(String content) {
        this.showInfoNotification(content, NotificationType.WARNING);
    }

    public void showInfoNotification(String content, NotificationType type) {
        long currentTimestamp = System.currentTimeMillis();
        if (currentTimestamp - lastShowNotification < 60000) {
            return;
        }
        lastShowNotification = currentTimestamp;
        Notification errorNotification = new Notification("Require.js plugin", "Require.js plugin", content, type);
        Notifications.Bus.notify(errorNotification, this.project);
    }

}
