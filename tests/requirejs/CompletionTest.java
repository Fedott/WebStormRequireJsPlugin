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
import org.junit.Before;
import org.junit.BeforeClass;
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
                                "blocks/childBlocks/childBlock"
                        )
                )
        );
        assertEquals(3, strings.size());
    }

    public void testCompletionWithOneDot() {
        // WithOneDot
        myFixture.getEditor().getCaretModel().moveToLogicalPosition(new LogicalPosition(4, 37));
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> strings = myFixture.getLookupElementStrings();
        assert strings != null;
        assertTrue(
                strings.isEmpty()
        );
        assertEquals(0, strings.size());

        // WithOneDotAndSlash
        myFixture.getEditor().getCaretModel().moveToLogicalPosition(new LogicalPosition(5, 46));
        myFixture.complete(CompletionType.BASIC, 1);
        strings = myFixture.getLookupElementStrings();
        assertTrue(
                strings.containsAll(
                        Arrays.asList(
                                "./block",
                                "./childWebPathFile",
                                "./childBlocks/childBlock"
                        )
                )
        );
        assertEquals(3, strings.size());

        // WithOneDotAndSlashTwoChars
        myFixture.getEditor().getCaretModel().moveToLogicalPosition(new LogicalPosition(6, 56));
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
        myFixture.getEditor().getCaretModel().moveToLogicalPosition(new LogicalPosition(7, 61));
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
        myFixture.getEditor().getCaretModel().moveToLogicalPosition(new LogicalPosition(8, 70));
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
        myFixture.getEditor().getCaretModel().moveToLogicalPosition(new LogicalPosition(9, 80));
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

    public void testCompletionWithTwoDot() {
        // WithTwoDot
        myFixture.getEditor().getCaretModel().moveToLogicalPosition(new LogicalPosition(11, 38));
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
                                "../blocks/childBlocks/childBlock"
                        )
                )
        );
        assertEquals(5, strings.size());

        // WithTwoDotAndSlash
        myFixture.getEditor().getCaretModel().moveToLogicalPosition(new LogicalPosition(12, 47));
        myFixture.complete(CompletionType.BASIC, 1);
        strings = myFixture.getLookupElementStrings();
        assertTrue(
                strings.containsAll(
                        Arrays.asList(
                                "../main",
                                "../rootWebPathFile",
                                "../blocks/block",
                                "../blocks/childWebPathFile",
                                "../blocks/childBlocks/childBlock"
                        )
                )
        );
        assertEquals(5, strings.size());

        // WithTwoDotAndSlashTwoChars
        myFixture.getEditor().getCaretModel().moveToLogicalPosition(new LogicalPosition(13, 57));
        myFixture.complete(CompletionType.BASIC, 1);
        strings = myFixture.getLookupElementStrings();
        assertTrue(
                strings.containsAll(
                        Arrays.asList(
                                "../blocks/block",
                                "../blocks/childWebPathFile",
                                "../blocks/childBlocks/childBlock"
                        )
                )
        );
        assertEquals(3, strings.size());

        // WithTwoDotAndDirectory
        myFixture.getEditor().getCaretModel().moveToLogicalPosition(new LogicalPosition(14, 57));
        myFixture.complete(CompletionType.BASIC, 1);
        strings = myFixture.getLookupElementStrings();
        assertTrue(
                strings.containsAll(
                        Arrays.asList(
                                "../blocks/block",
                                "../blocks/childWebPathFile",
                                "../blocks/childBlocks/childBlock"
                        )
                )
        );
        assertEquals(3, strings.size());

        // WithTwoDotAndDirectoryAndSlash
        myFixture.getEditor().getCaretModel().moveToLogicalPosition(new LogicalPosition(15, 66));
        myFixture.complete(CompletionType.BASIC, 1);
        strings = myFixture.getLookupElementStrings();
        assertTrue(
                strings.containsAll(
                        Arrays.asList(
                                "../blocks/block",
                                "../blocks/childWebPathFile",
                                "../blocks/childBlocks/childBlock"
                        )
                )
        );
        assertEquals(3, strings.size());

        // WithTwoDotAndTwoDirectories
        myFixture.getEditor().getCaretModel().moveToLogicalPosition(new LogicalPosition(16, 74));
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
        myFixture.getEditor().getCaretModel().moveToLogicalPosition(new LogicalPosition(17, 83));
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
        myFixture.getEditor().getCaretModel().moveToLogicalPosition(new LogicalPosition(18, 77));
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
}
