package requirejs;

import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.openapi.editor.LogicalPosition;
import com.intellij.psi.PsiElement;
import requirejs.settings.Settings;

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

    public void testCompletionWithBaseUrl()
    {
        List<String> strings;
        PsiElement element;

        myFixture.configureByFiles("public/fileForTestBaseUrl.js", "public/mainWithBaseUrl.js");

        Settings.getInstance(getProject()).configFilePath = "mainWithBaseUrl.js";

        // NotFound
        myFixture.getEditor().getCaretModel().moveToLogicalPosition(new LogicalPosition(1, 40));
        myFixture.complete(CompletionType.BASIC, 1);
        strings = myFixture.getLookupElementStrings();
        assert strings != null;
        assertTrue(
                strings.isEmpty()
        );
        assertEquals(0, strings.size());

        // 1
        myFixture.getEditor().getCaretModel().moveToLogicalPosition(new LogicalPosition(2, 29));
        myFixture.complete(CompletionType.BASIC, 1);
        strings = myFixture.getLookupElementStrings();
        assert strings == null;
        element = myFixture.getFile().findElementAt(myFixture.getCaretOffset());
        assert element != null;
        assertEquals("'block'", element.getText());

        // 2
        myFixture.getEditor().getCaretModel().moveToLogicalPosition(new LogicalPosition(3, 34));
        myFixture.complete(CompletionType.BASIC, 1);
        strings = myFixture.getLookupElementStrings();
        assert strings == null;
        element = myFixture.getFile().findElementAt(myFixture.getCaretOffset());
        assert element != null;
        assertEquals("'childBlocks/childBlock'", element.getText());

        // 3
        myFixture.getEditor().getCaretModel().moveToLogicalPosition(new LogicalPosition(4, 39));
        myFixture.complete(CompletionType.BASIC, 1);
        strings = myFixture.getLookupElementStrings();
        assert strings != null;
        assertTrue(
                strings.containsAll(
                        Arrays.asList(
                                "childBlocks/childBlock"
                        )
                )
        );
        assertEquals(1, strings.size());

        // 4
        myFixture.getEditor().getCaretModel().moveToLogicalPosition(new LogicalPosition(5, 41));
        myFixture.complete(CompletionType.BASIC, 1);
        strings = myFixture.getLookupElementStrings();
        assert strings == null;
        element = myFixture.getFile().findElementAt(myFixture.getCaretOffset());
        assert element != null;
        assertEquals("'childBlocks/childBlock'", element.getText());

        // 5
        myFixture.getEditor().getCaretModel().moveToLogicalPosition(new LogicalPosition(6, 33));
        myFixture.complete(CompletionType.BASIC, 1);
        strings = myFixture.getLookupElementStrings();
        assert strings != null;
        assertTrue(
                strings.containsAll(
                        Arrays.asList(
                                "/blocks/block",
                                "/blocks/childWebPathFile",
                                "/blocks/fileWithDotPath",
                                "/blocks/fileWithTwoDotPath",
                                "/blocks/childBlocks/childBlock"
                        )
                )
        );
        assertEquals(5, strings.size());

        // 6
        myFixture.getEditor().getCaretModel().moveToLogicalPosition(new LogicalPosition(7, 37));
        myFixture.complete(CompletionType.BASIC, 1);
        strings = myFixture.getLookupElementStrings();
        assert strings != null;
        assertTrue(
                strings.containsAll(
                        Arrays.asList(
                                "/blocks/block"
                        )
                )
        );
        assertEquals(1, strings.size());

        // 7
        myFixture.getEditor().getCaretModel().moveToLogicalPosition(new LogicalPosition(8, 50));
        myFixture.complete(CompletionType.BASIC, 1);
        strings = myFixture.getLookupElementStrings();
        assert strings != null;
        assertTrue(
                strings.isEmpty()
        );
        assertEquals(0, strings.size());

        // 8
        myFixture.getEditor().getCaretModel().moveToLogicalPosition(new LogicalPosition(9, 38));
        myFixture.complete(CompletionType.BASIC, 1);
        strings = myFixture.getLookupElementStrings();
        assert strings != null;
        assertTrue(
                strings.containsAll(
                        Arrays.asList(
                                "./blocks/block"
                        )
                )
        );
        assertEquals(1, strings.size());

        // 9
        myFixture.getEditor().getCaretModel().moveToLogicalPosition(new LogicalPosition(10, 30));
        myFixture.complete(CompletionType.BASIC, 1);
        strings = myFixture.getLookupElementStrings();
        assert strings != null;
        assertTrue(
                strings.isEmpty()
        );
        assertEquals(0, strings.size());
    }

    public void testCompletionInDefine()
    {
        List<String> strings;

        myFixture.configureByFiles("public/blocks/fileWithDefine.js");

        // One
        myFixture.getEditor().getCaretModel().moveToLogicalPosition(new LogicalPosition(0, 11));
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
                                "blocks/fileWithDefine",
                                "blocks/childBlocks/childBlock"
                        )
                )
        );
        assertEquals(6, strings.size());

        // Two
        myFixture.getEditor().getCaretModel().moveToLogicalPosition(new LogicalPosition(0, 25));
        myFixture.complete(CompletionType.BASIC, 1);
        strings = myFixture.getLookupElementStrings();
        assert strings != null;
        assertTrue(
                strings.containsAll(
                        Arrays.asList(
                                "/blocks/block"
                        )
                )
        );
        assertEquals(1, strings.size());

        // Tree
        myFixture.getEditor().getCaretModel().moveToLogicalPosition(new LogicalPosition(0, 37));
        myFixture.complete(CompletionType.BASIC, 1);
        strings = myFixture.getLookupElementStrings();
        assert strings != null;
        assertTrue(
                strings.containsAll(
                        Arrays.asList(
                                "blocks/block"
                        )
                )
        );
        assertEquals(1, strings.size());
    }
}
