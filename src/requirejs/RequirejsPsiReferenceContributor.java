package requirejs;

import com.intellij.patterns.StandardPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceContributor;
import com.intellij.psi.PsiReferenceRegistrar;

public class RequirejsPsiReferenceContributor extends PsiReferenceContributor {
    @Override
    public void registerReferenceProviders(PsiReferenceRegistrar psiReferenceRegistrar) {
        RequirejsPsiReferenceProvider provider = new RequirejsPsiReferenceProvider();

        psiReferenceRegistrar.registerReferenceProvider(StandardPatterns.instanceOf(PsiElement.class), provider);
    }
}
