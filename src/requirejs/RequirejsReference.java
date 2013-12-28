package requirejs;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class RequirejsReference implements PsiReference {
    PsiElement element;
    TextRange textRange;

    public RequirejsReference(PsiElement element, TextRange textRange) {
        this.element = element;
        this.textRange = textRange;
    }

    @Override
    public PsiElement getElement() {
        return this.element;
    }

    protected boolean isSettingsValid() {
        return element.getProject().getComponent(RequirejsProjectComponent.class).isSettingsValid();
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        if (!isSettingsValid()) {
            return null;
        }

        return element.getProject().getComponent(RequirejsProjectComponent.class).requireResolve(element);
    }

    @Override
    public String toString() {
        return getCanonicalText();
    }

    @Override
    public boolean isSoft() {
        return false;
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        ArrayList<LookupElement> completionResultSet = new ArrayList<LookupElement>();

        if (! isSettingsValid()) {
            return completionResultSet.toArray();
        }

        ArrayList<String> files = element
                .getProject()
                .getComponent(RequirejsProjectComponent.class)
                .getCompletion(element);

        for (String file : files) {
            completionResultSet.add(
                    LookupElementBuilder
                            .create(element, file)
                            .withInsertHandler(
                                    RequirejsInsertHandler.getInstance()
                            )
            );
        }

        return completionResultSet.toArray();
    }

    @Override
    public boolean isReferenceTo(PsiElement psiElement) {
        return false;
    }

    @Override
    public PsiElement bindToElement(@NotNull PsiElement psiElement) throws IncorrectOperationException {
        throw new IncorrectOperationException();
    }

    @Override
    public PsiElement handleElementRename(String s) throws IncorrectOperationException {
        throw new IncorrectOperationException();
    }

    @Override
    public TextRange getRangeInElement() {
        return textRange;
    }

    @NotNull
    @Override
    public String getCanonicalText() {
        return element.getText();
    }
}
