package requirejs;

import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.lang.Language;
import com.intellij.lang.javascript.psi.JSLiteralExpression;
import com.intellij.openapi.editor.LogicalPosition;
import com.intellij.patterns.StandardPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.resolve.reference.ReferenceProvidersRegistry;
import com.intellij.testFramework.fixtures.CodeInsightFixtureTestCase;
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase;
import requirejs.properties.RequirejsSettings;

import java.util.Arrays;
import java.util.List;

public class CompletionTest extends CodeInsightFixtureTestCase
{
    public void testCompletion() {
        myFixture.configureByFiles("public/blocks/CompletionTest.js", "public/blocks/block.js", "public/main.js");
        PropertiesComponent props = PropertiesComponent.getInstance(myFixture.getProject());
        props.setValue(RequirejsSettings.REQUIREJS_WEB_PATH_PROPERTY_NAME, getProject().getBaseDir().getChildren()[0].getName() + "/public");
        myFixture.getEditor().getCaretModel().moveToLogicalPosition(new LogicalPosition(1, 30));
//        PsiElement element = myFixture.getFile().findElementAt(myFixture.getCaretOffset());
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> strings = myFixture.getLookupElementStrings();
        assertTrue(strings.containsAll(Arrays.asList("key\\ with\\ spaces", "language", "message", "tab", "website")));
        assertEquals(5, strings.size());
    }
}
