package requirejs;

import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.openapi.editor.LogicalPosition;

import java.util.Arrays;
import java.util.List;

public class CompletionPathWithDotTest extends RequirejsTestCase
{
    @Override
    public void setUp() throws Exception {
        super.setUp();
        myFixture.configureByFiles(
                "public/blocks/fileWithDotPath.js",
                "public/blocks/childWebPathFile.js",
                "public/blocks/fileWithTwoDotPath.js",
                "public/main.js",
                "public/blocks/block.js",
                "public/blocks/childBlocks/childBlock.js",
                "public/rootWebPathFile.js",
                "public/blocks/childBlocks/templates/index.html"

        );
        setWebPathSetting();
    }

    public void testCompletionWithOneDot() {
        // WithOneDot
        myFixture.getEditor().getCaretModel().moveToLogicalPosition(new LogicalPosition(1, 37));
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> strings = myFixture.getLookupElementStrings();
        assert strings != null;
        assertTrue(
                strings.isEmpty()
        );
        assertEquals(0, strings.size());

        // WithOneDotAndSlash
        myFixture.getEditor().getCaretModel().moveToLogicalPosition(new LogicalPosition(2, 46));
        myFixture.complete(CompletionType.BASIC, 1);
        strings = myFixture.getLookupElementStrings();
        assertTrue(
                strings.containsAll(
                        Arrays.asList(
                                "./block",
                                "./childWebPathFile",
                                "./fileWithDotPath",
                                "./fileWithTwoDotPath",
                                "./childBlocks/childBlock"
                        )
                )
        );
        assertEquals(5, strings.size());

        // WithOneDotAndSlashTwoChars
        myFixture.getEditor().getCaretModel().moveToLogicalPosition(new LogicalPosition(3, 56));
        myFixture.complete(CompletionType.BASIC, 1);
        strings = myFixture.getLookupElementStrings();
        assertTrue(
                strings.containsAll(
                        Arrays.asList(
                                "./block"
                        )
                )
        );
        assertEquals(1, strings.size());

        // WithOneDotAndDirectory
        myFixture.getEditor().getCaretModel().moveToLogicalPosition(new LogicalPosition(4, 61));
        myFixture.complete(CompletionType.BASIC, 1);
        strings = myFixture.getLookupElementStrings();
        assertTrue(
                strings.containsAll(
                        Arrays.asList(
                                "./childBlocks/childBlock"
                        )
                )
        );
        assertEquals(1, strings.size());

        // WithOneDotAndDirectoryAndSlash
        myFixture.getEditor().getCaretModel().moveToLogicalPosition(new LogicalPosition(5, 70));
        myFixture.complete(CompletionType.BASIC, 1);
        strings = myFixture.getLookupElementStrings();
        assertTrue(
                strings.containsAll(
                        Arrays.asList(
                                "./childBlocks/childBlock"
                        )
                )
        );
        assertEquals(1, strings.size());

        // WithOneDotAndDirectoryAndSlashTwoChars
        myFixture.getEditor().getCaretModel().moveToLogicalPosition(new LogicalPosition(6, 80));
        myFixture.complete(CompletionType.BASIC, 1);
        strings = myFixture.getLookupElementStrings();
        assertTrue(
                strings.containsAll(
                        Arrays.asList(
                                "./childBlocks/childBlock"
                        )
                )
        );
        assertEquals(1, strings.size());
    }
}
