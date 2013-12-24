package requirejs;

import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.editor.LogicalPosition;
import com.intellij.openapi.editor.ex.util.EditorUtil;
import com.intellij.testFramework.fixtures.CodeInsightFixtureTestCase;
import requirejs.properties.RequirejsSettings;

import java.util.Arrays;
import java.util.List;

public class CompletionPathWithTwoDotTest extends CodeInsightFixtureTestCase
{
    @Override
    public void setUp() throws Exception {
        super.setUp();
        myFixture.configureByFiles(
                "public/blocks/fileWithTwoDotPath.js",
                "public/blocks/childWebPathFile.js",
                "public/blocks/fileWithDotPath.js",
                "public/main.js",
                "public/blocks/block.js",
                "public/blocks/childBlocks/childBlock.js",
                "public/rootWebPathFile.js",
                "public/blocks/childBlocks/templates/index.html"

        );
        PropertiesComponent props = PropertiesComponent.getInstance(myFixture.getProject());
        props.setValue(RequirejsSettings.REQUIREJS_WEB_PATH_PROPERTY_NAME, getProject().getBaseDir().getChildren()[0].getName() + "/public");
    }

    public void testCompletionWithTwoDot() {
        // WithTwoDot
        myFixture.getEditor().getCaretModel().moveToLogicalPosition(new LogicalPosition(1, 38));
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> strings = myFixture.getLookupElementStrings();
        assert strings != null;
        assertTrue(
                strings.containsAll(
                        Arrays.asList(
                                "../main",
                                "../rootWebPathFile",
                                "../blocks/block",
                                "../blocks/childWebPathFile",
                                "../blocks/fileWithDotPath",
                                "../blocks/fileWithTwoDotPath",
                                "../blocks/childBlocks/childBlock"
                        )
                )
        );
        assertEquals(7, strings.size());

        // WithTwoDotAndSlash
        myFixture.getEditor().getCaretModel().moveToLogicalPosition(new LogicalPosition(2, 47));
        myFixture.complete(CompletionType.BASIC, 1);
        strings = myFixture.getLookupElementStrings();
        assertTrue(
                strings.containsAll(
                        Arrays.asList(
                                "../main",
                                "../rootWebPathFile",
                                "../blocks/block",
                                "../blocks/childWebPathFile",
                                "../blocks/fileWithDotPath",
                                "../blocks/fileWithTwoDotPath",
                                "../blocks/childBlocks/childBlock"
                        )
                )
        );
        assertEquals(7, strings.size());

        // WithTwoDotAndSlashTwoChars
        myFixture.getEditor().getCaretModel().moveToLogicalPosition(new LogicalPosition(3, 57));
        myFixture.complete(CompletionType.BASIC, 1);
        strings = myFixture.getLookupElementStrings();
        assertTrue(
                strings.containsAll(
                        Arrays.asList(
                                "../blocks/block",
                                "../blocks/childWebPathFile",
                                "../blocks/fileWithDotPath",
                                "../blocks/fileWithTwoDotPath",
                                "../blocks/childBlocks/childBlock"
                        )
                )
        );
        assertEquals(5, strings.size());

        // WithTwoDotAndDirectory
        myFixture.getEditor().getCaretModel().moveToLogicalPosition(new LogicalPosition(4, 57));
        myFixture.complete(CompletionType.BASIC, 1);
        strings = myFixture.getLookupElementStrings();
        assertTrue(
                strings.containsAll(
                        Arrays.asList(
                                "../blocks/block",
                                "../blocks/childWebPathFile",
                                "../blocks/fileWithDotPath",
                                "../blocks/fileWithTwoDotPath",
                                "../blocks/childBlocks/childBlock"
                        )
                )
        );
        assertEquals(5, strings.size());

        // WithTwoDotAndDirectoryAndSlash
        myFixture.getEditor().getCaretModel().moveToLogicalPosition(new LogicalPosition(5, 66));
        myFixture.complete(CompletionType.BASIC, 1);
        strings = myFixture.getLookupElementStrings();
        assertTrue(
                strings.containsAll(
                        Arrays.asList(
                                "../blocks/block",
                                "../blocks/childWebPathFile",
                                "../blocks/fileWithDotPath",
                                "../blocks/fileWithTwoDotPath",
                                "../blocks/childBlocks/childBlock"
                        )
                )
        );
        assertEquals(5, strings.size());

        // WithTwoDotAndTwoDirectories
        myFixture.getEditor().getCaretModel().moveToLogicalPosition(new LogicalPosition(6, 74));
        myFixture.complete(CompletionType.BASIC, 1);
        strings = myFixture.getLookupElementStrings();
        assertTrue(
                strings.containsAll(
                        Arrays.asList(
                                "../blocks/childBlocks/childBlock"
                        )
                )
        );
        assertEquals(1, strings.size());

        // WithTwoDotAndTwoDirectoriesAndSlash
        myFixture.getEditor().getCaretModel().moveToLogicalPosition(new LogicalPosition(7, 83));
        myFixture.complete(CompletionType.BASIC, 1);
        strings = myFixture.getLookupElementStrings();
        assertTrue(
                strings.containsAll(
                        Arrays.asList(
                                "../blocks/childBlocks/childBlock"
                        )
                )
        );
        assertEquals(1, strings.size());

        // WithTwoDotAndDirectoryAndSlashTwoChars
        myFixture.getEditor().getCaretModel().moveToLogicalPosition(new LogicalPosition(8, 77));
        myFixture.complete(CompletionType.BASIC, 1);
        strings = myFixture.getLookupElementStrings();
        assertTrue(
                strings.containsAll(
                        Arrays.asList(
                                "../blocks/block"
                        )
                )
        );
        assertEquals(1, strings.size());
    }

    public void testCompletionParentWebPath()
    {
        // WithTwoDotAndDirectoryAndSlashTwoChars
        myFixture.getEditor().getCaretModel().moveToLogicalPosition(new LogicalPosition(10, 60));
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> strings = myFixture.getLookupElementStrings();
        assert strings != null;
        assertTrue(
                strings.isEmpty()
        );
        assertEquals(0, strings.size());
    }

    public void testCompletionTwoTwoDotPath()
    {
        myFixture.configureByFile("public/blocks/childBlocks/fileWithTwoDotPathChild.js");

        myFixture.getEditor().getCaretModel().moveToLogicalPosition(new LogicalPosition(1, 39));
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> strings = myFixture.getLookupElementStrings();
        assert strings != null;
        assertTrue(
                strings.containsAll(
                        Arrays.asList(
                                "../../blocks/block",
                                "../../blocks/childWebPathFile",
                                "../../blocks/fileWithDotPath",
                                "../../blocks/fileWithTwoDotPath",
                                "../../blocks/childBlocks/childBlock",
                                "../../blocks/childBlocks/fileWithTwoDotPathChild"
                        )
                )
        );
        assertEquals(6, strings.size());

        myFixture.getEditor().getCaretModel().moveToLogicalPosition(new LogicalPosition(2, 50));
        myFixture.complete(CompletionType.BASIC, 1);
        strings = myFixture.getLookupElementStrings();
        assert strings != null;
        assertTrue(
                strings.isEmpty()
        );
        assertEquals(0, strings.size());
    }
}
