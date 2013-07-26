package requirejs;

import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

public class RequirejsLookupElement extends LookupElement {
    String path;
    PsiElement element;
    private InsertHandler<LookupElement> insertHandler = null;

    public RequirejsLookupElement(String path, InsertHandler<LookupElement> insertHandler, PsiElement element) {
        this.path = path;
        this.insertHandler = insertHandler;
        this.element = element;
    }

    public void handleInsert(InsertionContext context) {
        if (this.insertHandler != null) {
            this.insertHandler.handleInsert(context, this);
        }
    }

    @NotNull
    @Override
    public String getLookupString() {
        return path;
    }
}
