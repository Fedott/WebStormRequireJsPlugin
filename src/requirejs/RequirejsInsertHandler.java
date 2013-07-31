package requirejs;

import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.lookup.LookupElement;

public class RequirejsInsertHandler implements InsertHandler {
    private static final RequirejsInsertHandler instance = new RequirejsInsertHandler();

    @Override
    public void handleInsert(InsertionContext insertionContext, LookupElement lookupElement) {
        if (lookupElement instanceof RequirejsLookupElement) {
            insertionContext.getDocument().replaceString(
                    ((RequirejsLookupElement) lookupElement).element.getTextOffset() + 1,
                    insertionContext.getTailOffset(),
                    ((RequirejsLookupElement) lookupElement).path
            );
        }
    }

    public static RequirejsInsertHandler getInstance() {
        return instance;
    }
}
