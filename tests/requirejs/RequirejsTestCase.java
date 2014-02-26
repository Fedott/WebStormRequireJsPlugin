package requirejs;

import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.lang.javascript.psi.JSFile;
import com.intellij.openapi.editor.LogicalPosition;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.impl.source.resolve.reference.impl.PsiMultiReference;
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
        assertNotNull(actual);
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

    protected PsiReference getReferenceForHumanPosition(int line, int column) {
        return getReferenceForPosition(line - 1, column - 1);
    }

    protected PsiReference getReferenceForPosition(int line, int column) {
        myFixture
                .getEditor()
                .getCaretModel()
                .moveToLogicalPosition(new LogicalPosition(line, column));
        return myFixture.getReferenceAtCaretPosition();
    }

    protected void assertReference(PsiReference reference, String expectedText, String expectedFileName) {
        assertNotNull(reference);
        if (reference instanceof PsiMultiReference) {
            for (PsiReference ref : ((PsiMultiReference)reference).getReferences()) {
                if (ref instanceof RequirejsReference) {
                    reference = ref;
                    break;
                }
            }
        }

        if (!expectedText.startsWith("'")) {
            expectedText = "'".concat(expectedText).concat("'");
        }

        assertInstanceOf(reference, RequirejsReference.class);
        assertEquals(expectedText, reference.getCanonicalText());
        PsiElement referenceElement = reference.resolve();
        if (null == expectedFileName) {
            assertNull(referenceElement);
        } else {
            assertNotNull(referenceElement);
            assertInstanceOf(referenceElement, JSFile.class);
            assertEquals(expectedFileName, ((JSFile) referenceElement).getName());
        }
    }
}
