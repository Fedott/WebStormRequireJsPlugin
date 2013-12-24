package requirejs;

import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.editor.LogicalPosition;
import com.intellij.testFramework.fixtures.CodeInsightFixtureTestCase;
import requirejs.properties.RequirejsSettings;

import java.util.Arrays;
import java.util.List;

public class CompletionTest extends CodeInsightFixtureTestCase
{
    @Override
    public void setUp() throws Exception {
        super.setUp();
        myFixture.configureByFiles(
                "public/blocks/childWebPathFile.js",
                "public/blocks/fileWithDotPath.js",
                "public/blocks/fileWithTwoDotPath.js",
                "public/main.js",
                "public/blocks/block.js",
                "public/blocks/childBlocks/childBlock.js",
                "public/rootWebPathFile.js",
                "public/blocks/childBlocks/templates/index.html"

        );
        PropertiesComponent props = PropertiesComponent.getInstance(myFixture.getProject());
        props.setValue(RequirejsSettings.REQUIREJS_WEB_PATH_PROPERTY_NAME, getProject().getBaseDir().getChildren()[0].getName() + "/public");
    }

    public void testCompletion() {
        myFixture.getEditor().getCaretModel().moveToLogicalPosition(new LogicalPosition(2, 37));
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> strings = myFixture.getLookupElementStrings();
        assert strings != null;
        assertTrue(
                strings.containsAll(
                        Arrays.asList(
                                "blocks/block",
                                "blocks/childWebPathFile",
                                "blocks/fileWithDotPath",
                                "blocks/fileWithTwoDotPath",
                                "blocks/childBlocks/childBlock"
                        )
                )
        );
        assertEquals(5, strings.size());
    }
}
