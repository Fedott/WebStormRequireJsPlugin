package requirejs;

import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.lookup.LookupElement;

public class RequirejsInsertHandler implements InsertHandler {
    private static final RequirejsInsertHandler instance = new RequirejsInsertHandler();

    @Override
    public void handleInsert(InsertionContext insertionContext, LookupElement lookupElement) {
        insertionContext.getDocument().replaceString(
                lookupElement.getPsiElement().getTextOffset() + 1,
                insertionContext.getTailOffset(),
                lookupElement.getLookupString()
        );
    }

    public static RequirejsInsertHandler getInstance() {
        return instance;
    }
}
