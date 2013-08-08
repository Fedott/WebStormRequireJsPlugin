package requirejs;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.lang.javascript.JSElementTypes;
import com.intellij.lang.javascript.JSTokenTypes;
import com.intellij.lang.javascript.psi.JSCallExpression;
import com.intellij.lang.javascript.psi.JSFile;
import com.intellij.lang.javascript.psi.impl.JSFileImpl;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.tree.CompositeElement;
import com.intellij.psi.impl.source.tree.TreeElement;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
import requirejs.properties.RequirejsSettingsPage;

import java.util.ArrayList;

public class RequirejsPsiReferenceProvider extends PsiReferenceProvider {

    protected Project project;
    PropertiesComponent propertiesComponent;
    public static ArrayList<String> requirejsPaths;

    @NotNull
    @Override
    public PsiReference[] getReferencesByElement(@NotNull PsiElement psiElement, @NotNull ProcessingContext processingContext) {
        if (project == null) {
            project = psiElement.getProject();
            propertiesComponent = PropertiesComponent.getInstance(project);
        }

        PropertiesComponent properties = PropertiesComponent.getInstance(project);
        String webDirPrefString = properties.getValue(RequirejsSettingsPage.WEB_PATH_PROPERTY_NAME, RequirejsSettingsPage.DEFAULT_WEB_PATH);
        VirtualFile webDir = project.getBaseDir().findFileByRelativePath(webDirPrefString);

        if (webDir == null) {
            return PsiReference.EMPTY_ARRAY;
        }

        if (null == requirejsPaths) {
            PsiFile mainJs = PsiManager
                    .getInstance(project)
                    .findFile(
                            webDir
                                    .findFileByRelativePath(
                                            properties.getValue(
                                                    RequirejsSettingsPage.REQUIREJS_MAIN_JS_FILE_PATH,
                                                    RequirejsSettingsPage.DEFAULT_REQUIREJS_MAIN_JS_FILE_PATH
                                            )
                                    )
                    );
            if (mainJs instanceof JSFileImpl) {
                if (((JSFileImpl) mainJs).getTreeElement() == null) {
                    requirejsPaths = parseMainJsFile(((JSFileImpl) mainJs).calcTreeElement());
                } else {
                    requirejsPaths = parseMainJsFile(((JSFileImpl) mainJs).getTreeElement());
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
            try {
                if (prevEl.getChildren().length > 1) {
                    String requireFunctionName = PropertiesComponent
                            .getInstance(element.getProject())
                            .getValue(RequirejsSettingsPage.REQUIREJS_FUNCTION_NAME_PROPERTY_NAME, RequirejsSettingsPage.DEFAULT_REQUIREJS_FUNCTION_NAME);
                    if (prevEl.getChildren()[0].getText().toLowerCase().equals(requireFunctionName)) {
                        return true;
                    }
                }
            } catch (Exception ignored) {}
        }

        return false;
    }

    public ArrayList<String> parseMainJsFile(TreeElement node) {
        ArrayList<String> list = new ArrayList<String>();

        TreeElement firstChild = node.getFirstChildNode();
        if (firstChild != null) {
            list.addAll(parseMainJsFile(firstChild));
        }

        TreeElement nextNode = node.getTreeNext();
        if (nextNode != null) {
            list.addAll(parseMainJsFile(nextNode));
        }

        if (node.getElementType() == JSTokenTypes.IDENTIFIER) {
            try {
                String requirejsFunctionName = PropertiesComponent
                        .getInstance(project)
                        .getValue(RequirejsSettingsPage.REQUIREJS_FUNCTION_NAME_PROPERTY_NAME, RequirejsSettingsPage.DEFAULT_REQUIREJS_FUNCTION_NAME);
                if (node.getText().equals(requirejsFunctionName)) {
                    list.addAll(
                            parseRequirejsConfig(
                                    (TreeElement) node
                                            .getTreeParent()
                                            .getTreeNext()
                                            .findChildByType(JSElementTypes.OBJECT_LITERAL_EXPRESSION)
                                            .getFirstChildNode()
                            )
                    );
                }
            } catch (NullPointerException ignored) {}
        }

        return list;
    }

    public ArrayList<String> parseRequirejsConfig(TreeElement node) {
        ArrayList<String> list = new ArrayList<String>();
        if (null == node) {
            return list;
        }

        TreeElement next = node.getTreeNext();
        if (null != next) {
            list.addAll(parseRequirejsConfig(next));
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
                                .findChildByType(JSElementTypes.STRING_LITERAL_TYPE)
                                .getText();

                        list.add(
                                propertiesComponent
                                        .getValue(RequirejsSettingsPage.WEB_PATH_PROPERTY_NAME, RequirejsSettingsPage.DEFAULT_WEB_PATH)
                                        .concat(baseUrl)
                        );
                    }
                    if (identifierName.equals("paths")) {
                        list.addAll(
                                parseMainJsPaths(
                                        (TreeElement) node
                                                .findChildByType(JSElementTypes.OBJECT_LITERAL_EXPRESSION)
                                                .getFirstChildNode()
                                )
                        );
                    }
                }
            }
        } catch (NullPointerException ignored) {}

        return list;
    }

    protected ArrayList<String> parseMainJsPaths(TreeElement node) {
        ArrayList<String> list = new ArrayList<String>();
        if (null == node) {
            return list;
        }

        TreeElement next = node.getTreeNext();
        if (null != next) {
            list.addAll(parseMainJsPaths(next));
        }

        if (node.getElementType() == JSElementTypes.PROPERTY) {
            TreeElement path = (TreeElement) node.findChildByType(JSElementTypes.LITERAL_EXPRESSION);
            if (null != path) {
                list.add(path.getText().replace("\"","").replace("'", ""));
            }
        }

        return list;
    }
}
