package requirejs;

import com.intellij.lang.javascript.psi.JSLiteralExpression;
import com.intellij.patterns.StandardPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceContributor;
import com.intellij.psi.PsiReferenceRegistrar;

public class RequirejsPsiReferenceContributor extends PsiReferenceContributor {
    @Override
    public void registerReferenceProviders(PsiReferenceRegistrar psiReferenceRegistrar) {
        RequirejsPsiReferenceProvider provider = new RequirejsPsiReferenceProvider();

        psiReferenceRegistrar.registerReferenceProvider(StandardPatterns.instanceOf(JSLiteralExpression.class), provider);
    }
}
