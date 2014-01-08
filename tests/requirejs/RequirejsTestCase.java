package requirejs;

import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.openapi.editor.LogicalPosition;
import com.intellij.testFramework.fixtures.CodeInsightFixtureTestCase;
import requirejs.settings.Settings;

import java.util.List;

public abstract class RequirejsTestCase extends CodeInsightFixtureTestCase {
    protected void setWebPathSetting() {
        Settings.getInstance(myFixture.getProject()).publicPath = getProject()
                .getBaseDir()
                .getChildren()[0]
                .getName().concat("/public");
    }

    protected void assertCompletionList(List<String> expected, List<String> actual) {
        assertCompletionList(expected, expected.size(), actual);
    }

    protected void assertCompletionList(List<String> expected, int expectedSize, List<String> actual) {
        assert actual != null;
        assertContainsElements(actual, expected);
        assertEquals(expectedSize, actual.size());
    }

    protected List<String> getCompletionStrings(int line, int column) {
        List<String> strings;
        myFixture.getEditor().getCaretModel().moveToLogicalPosition(new LogicalPosition(line, column));
        myFixture.complete(CompletionType.BASIC, 1);
        strings = myFixture.getLookupElementStrings();
        return strings;
    }

    protected List<String> getCompletionStringsForHumanPosition(int line, int column) {
        return getCompletionStrings(line - 1, column - 1);
    }
}
