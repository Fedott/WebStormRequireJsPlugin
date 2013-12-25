package requirejs;

import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.openapi.editor.LogicalPosition;
import com.intellij.psi.PsiElement;

import java.util.Arrays;
import java.util.List;

public class CompletionTest extends RequirejsTestCase
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
        setWebPathSetting();
    }

    public void testCompletion() {
        // NotFound
        myFixture.getEditor().getCaretModel().moveToLogicalPosition(new LogicalPosition(1, 40));
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> strings = myFixture.getLookupElementStrings();
        assert strings != null;
        assertTrue(
                strings.isEmpty()
        );
        assertEquals(0, strings.size());

        // True
        myFixture.getEditor().getCaretModel().moveToLogicalPosition(new LogicalPosition(2, 37));
        myFixture.complete(CompletionType.BASIC, 1);
        strings = myFixture.getLookupElementStrings();
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

        // WithoutSlash
        myFixture.getEditor().getCaretModel().moveToLogicalPosition(new LogicalPosition(3, 44));
        myFixture.complete(CompletionType.BASIC, 1);
        strings = myFixture.getLookupElementStrings();
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

        // TwoChars
        myFixture.getEditor().getCaretModel().moveToLogicalPosition(new LogicalPosition(4, 36));
        myFixture.complete(CompletionType.BASIC, 1);
        strings = myFixture.getLookupElementStrings();
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

        // TwoDirectory
        myFixture.getEditor().getCaretModel().moveToLogicalPosition(new LogicalPosition(5, 56));
        myFixture.complete(CompletionType.BASIC, 1);
        strings = myFixture.getLookupElementStrings();
        assert strings != null;
        assertTrue(
                strings.containsAll(
                        Arrays.asList(
                                "blocks/childBlocks/childBlock"
                        )
                )
        );
        assertEquals(1, strings.size());

        // TwoDirectoryWithSlash
        myFixture.getEditor().getCaretModel().moveToLogicalPosition(new LogicalPosition(6, 66));
        myFixture.complete(CompletionType.BASIC, 1);
        strings = myFixture.getLookupElementStrings();
        assert strings != null;
        assertTrue(
                strings.containsAll(
                        Arrays.asList(
                                "blocks/childBlocks/childBlock"
                        )
                )
        );
        assertEquals(1, strings.size());

        // RootFound
        myFixture.getEditor().getCaretModel().moveToLogicalPosition(new LogicalPosition(7, 39));
        myFixture.complete(CompletionType.BASIC, 1);
        strings = myFixture.getLookupElementStrings();
        assert strings == null;
        PsiElement element = myFixture.getFile().findElementAt(myFixture.getCaretOffset());
        assert element != null;
        assertEquals("'rootWebPathFile'", element.getText());
    }

    public void testCompletionInRootWebPathFile()
    {
        myFixture.configureByFile("public/rootWebPathFile.js");

        // NotFound
        myFixture.getEditor().getCaretModel().moveToLogicalPosition(new LogicalPosition(1, 40));
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> strings = myFixture.getLookupElementStrings();
        assert strings != null;
        assertTrue(
                strings.isEmpty()
        );
        assertEquals(0, strings.size());

        // True
        myFixture.getEditor().getCaretModel().moveToLogicalPosition(new LogicalPosition(2, 37));
        myFixture.complete(CompletionType.BASIC, 1);
        strings = myFixture.getLookupElementStrings();
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

        // WithoutSlash
        myFixture.getEditor().getCaretModel().moveToLogicalPosition(new LogicalPosition(3, 44));
        myFixture.complete(CompletionType.BASIC, 1);
        strings = myFixture.getLookupElementStrings();
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

        // TwoChars
        myFixture.getEditor().getCaretModel().moveToLogicalPosition(new LogicalPosition(4, 36));
        myFixture.complete(CompletionType.BASIC, 1);
        strings = myFixture.getLookupElementStrings();
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

        // TwoDirectory
        myFixture.getEditor().getCaretModel().moveToLogicalPosition(new LogicalPosition(5, 56));
        myFixture.complete(CompletionType.BASIC, 1);
        strings = myFixture.getLookupElementStrings();
        assert strings != null;
        assertTrue(
                strings.containsAll(
                        Arrays.asList(
                                "blocks/childBlocks/childBlock"
                        )
                )
        );
        assertEquals(1, strings.size());

        // TwoDirectoryWithSlash
        myFixture.getEditor().getCaretModel().moveToLogicalPosition(new LogicalPosition(6, 66));
        myFixture.complete(CompletionType.BASIC, 1);
        strings = myFixture.getLookupElementStrings();
        assert strings != null;
        assertTrue(
                strings.containsAll(
                        Arrays.asList(
                                "blocks/childBlocks/childBlock"
                        )
                )
        );
        assertEquals(1, strings.size());

        // RootFound
        myFixture.getEditor().getCaretModel().moveToLogicalPosition(new LogicalPosition(7, 39));
        myFixture.complete(CompletionType.BASIC, 1);
        strings = myFixture.getLookupElementStrings();
        assert strings == null;
        PsiElement element = myFixture.getFile().findElementAt(myFixture.getCaretOffset());
        assert element != null;
        assertEquals("'rootWebPathFile'", element.getText());
    }
}
