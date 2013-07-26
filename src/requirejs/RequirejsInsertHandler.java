package requirejs;

import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.lang.javascript.completion.JavaScriptCompletionData;
import icons.JavaScriptLanguageIcons;

public class RequirejsInsertHandler extends JavaScriptCompletionData.JSInsertHandler {
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
