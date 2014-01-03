package requirejs;

import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.openapi.editor.LogicalPosition;
import requirejs.settings.Settings;

import java.util.Arrays;
import java.util.List;

public class EmptyWebPathTest extends RequirejsTestCase
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
    }

    public void testEmptyWebPathCompletion()
    {
        Settings.getInstance(getProject()).publicPath = "";
        List<String> strings;

        myFixture.configureByFile("parentWebPathFile.js");

        myFixture.getEditor().getCaretModel().moveToLogicalPosition(new LogicalPosition(2, 36));
        myFixture.complete(CompletionType.BASIC, 1);
        strings = myFixture.getLookupElementStrings();
        assert strings != null;
        assertTrue(
                strings.containsAll(
                        Arrays.asList(
                                "./public/blocks/block",
                                "./public/blocks/childWebPathFile",
                                "./public/blocks/fileWithDotPath",
                                "./public/blocks/fileWithTwoDotPath",
                                "./public/blocks/childBlocks/childBlock",
                                "./parentWebPathFile",
                                "./public/main",
                                "./public/rootWebPathFile"
                        )
                )
        );
        assertEquals(8, strings.size());
    }
}
