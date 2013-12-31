package requirejs;

import com.intellij.lang.javascript.psi.JSArgumentList;
import com.intellij.lang.javascript.psi.JSArrayLiteralExpression;
import com.intellij.lang.javascript.psi.JSCallExpression;
import com.intellij.lang.javascript.psi.JSReferenceExpression;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
import requirejs.settings.Settings;

public class RequirejsPsiReferenceProvider extends PsiReferenceProvider {

    protected Project project;
    public Settings settings;

    @NotNull
    @Override
    public PsiReference[] getReferencesByElement(@NotNull PsiElement psiElement, @NotNull ProcessingContext processingContext) {
        RequirejsProjectComponent projectComponent = psiElement.getProject().getComponent(RequirejsProjectComponent.class);

        if (!projectComponent.isEnabled()) {
            return PsiReference.EMPTY_ARRAY;
        }

        String path = psiElement.getText();
        if (isRequireCall(psiElement) || isDefineFirstCollection(psiElement)) {
            PsiReference ref = new RequirejsReference(psiElement, new TextRange(1, path.length() - 1));
            return new PsiReference[] {ref};
        }

        return new PsiReference[0];
    }

    public boolean isRequireCall(PsiElement element) {
        PsiElement prevEl = element.getParent();
        if (prevEl != null) {
            prevEl = prevEl.getParent();
        }

        if (prevEl instanceof JSCallExpression) {
            if (prevEl.getChildren().length > 1) {
                String requireFunctionName = Settings
                        .getInstance(element.getProject())
                        .requireFunctionName;
                if (prevEl.getChildren()[0].getText().toLowerCase().equals(requireFunctionName)) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean isDefineFirstCollection(PsiElement element) {
        PsiElement jsArrayLiteral = element.getParent();
        if (null != jsArrayLiteral && jsArrayLiteral instanceof JSArrayLiteralExpression) {
            PsiElement jsArgumentList = jsArrayLiteral.getParent();
            if (null != jsArgumentList && jsArgumentList instanceof JSArgumentList) {
                PsiElement jsReferenceExpression = jsArgumentList.getPrevSibling();
                if (null != jsReferenceExpression && jsReferenceExpression instanceof JSReferenceExpression) {
                    if (jsReferenceExpression.getText().equals("define")) {
                        return true;
                    }
                }
            }
        }

        return false;
    }
}
