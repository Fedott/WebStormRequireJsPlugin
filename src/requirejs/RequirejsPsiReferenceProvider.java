package requirejs;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

public class RequirejsPsiReferenceProvider extends PsiReferenceProvider {
    @NotNull
    @Override
    public PsiReference[] getReferencesByElement(@NotNull PsiElement psiElement, @NotNull ProcessingContext processingContext) {
        Project project = psiElement.getProject();

        PropertiesComponent properties = PropertiesComponent.getInstance(project);
        String webDirPrefString = properties.getValue("web_dir", "webfront/web");
        VirtualFile webDir = project.getBaseDir().findFileByRelativePath(webDirPrefString);

        if (webDir == null) {
            return PsiReference.EMPTY_ARRAY;
        }

        Class elementClass = psiElement.getClass();
        String className = elementClass.getName();

        if (className.endsWith("JSLiteralExpressionImpl")) {
            try {
                String adress = psiElement.getText();
                if (isRequireCall(psiElement)) {
                    PsiReference ref = new RequirejsReference(psiElement, new TextRange(1, adress.length() - 1), project, webDir);
                    return new PsiReference[] {ref};
                }
            } catch (Exception e) {}
        }

        return new PsiReference[0];
    }

    public static boolean isRequireCall(PsiElement element) {
        PsiElement prevEl = element.getParent();

        String elClassName;
        if (prevEl != null) {
            elClassName = prevEl.getClass().getName();
        }
        prevEl = prevEl.getParent();
        if (prevEl != null) {
            elClassName = prevEl.getClass().getName();
            if (elClassName.endsWith("JSCallExpressionImpl")) {
                try {
                    if (prevEl.getChildren().length > 1) {
                        if (prevEl.getChildren()[0].getText().toLowerCase().equals("require")) {
                            return true;
                        }
                    }
                } catch (Exception ignored) {}
            }
        }
        return false;
    }
}
