package requirejs;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.lang.javascript.psi.JSCallExpression;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
import requirejs.properties.RequirejsSettingsPage;

public class RequirejsPsiReferenceProvider extends PsiReferenceProvider {

    @NotNull
    @Override
    public PsiReference[] getReferencesByElement(@NotNull PsiElement psiElement, @NotNull ProcessingContext processingContext) {
        Project project = psiElement.getProject();

        PropertiesComponent properties = PropertiesComponent.getInstance(project);
        String webDirPrefString = properties.getValue(RequirejsSettingsPage.WEB_PATH_PROPERTY_NAME, RequirejsSettingsPage.DEFAULT_WEB_PATH);
        VirtualFile webDir = project.getBaseDir().findFileByRelativePath(webDirPrefString);

        if (webDir == null) {
            return PsiReference.EMPTY_ARRAY;
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

        if (prevEl != null) {
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
        }
        return false;
    }
}
