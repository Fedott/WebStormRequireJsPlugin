package requirejs;

import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.openapi.editor.LogicalPosition;
import requirejs.settings.Settings;

import java.util.Arrays;
import java.util.List;

public class ConfigParseTest extends RequirejsTestCase
{
    @Override
    public void setUp() throws Exception {
        super.setUp();
        myFixture.configureByFiles(
                "public/rootWebPathConfigTest.js",
                "public/blocks/fileWithDotPath.js",
                "public/blocks/childWebPathFile.js",
                "public/blocks/fileWithTwoDotPath.js",
                "public/main.js",
                "public/blocks/block.js",
                "public/blocks/childBlocks/childBlock.js",
                "public/rootWebPathFile.js",
                "public/blocks/childBlocks/templates/index.html",
                "public/mainRequireJs.js",
                "public/mainRequire.js"

        );
        setWebPathSetting();
    }

    public void testCompletion()
    {
        Settings.getInstance(getProject()).mainJsPath = "mainRequireJs.js";

        // moduleDepend
        myFixture.getEditor().getCaretModel().moveToLogicalPosition(new LogicalPosition(1, 38));
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> strings = myFixture.getLookupElementStrings();
        assert strings != null;
        assertTrue(
                strings.containsAll(
                        Arrays.asList(
                                "moduleRelativeBaseUrlPath",
                                "moduleAbsolutePath",
                                "moduleRelativeOneDotPath",
                                "moduleRelativeTwoDotPAth"
                        )
                )
        );
        assertEquals(4, strings.size());

        // moduleDepend
        myFixture.getEditor().getCaretModel().moveToLogicalPosition(new LogicalPosition(2, 39));
        myFixture.complete(CompletionType.BASIC, 1);
        strings = myFixture.getLookupElementStrings();
        assert strings != null;
        assertTrue(
                strings.containsAll(
                        Arrays.asList(
                                "moduleRelativeBaseUrlPath",
                                "moduleRelativeOneDotPath",
                                "moduleRelativeTwoDotPAth"
                        )
                )
        );
        assertEquals(3, strings.size());
    }

    public void testCompletionOtherConfigFile()
    {
        Settings.getInstance(getProject()).mainJsPath = "mainRequire.js";

        // moduleDepend
        myFixture.getEditor().getCaretModel().moveToLogicalPosition(new LogicalPosition(1, 38));
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> strings = myFixture.getLookupElementStrings();
        assert strings != null;
        assertTrue(
                strings.containsAll(
                        Arrays.asList(
                                "moduleRelativeBaseUrlPath",
                                "moduleAbsolutePath",
                                "moduleRelativeOneDotPath",
                                "moduleRelativeTwoDotPAth"
                        )
                )
        );
        assertEquals(4, strings.size());

        // moduleDepend
        myFixture.getEditor().getCaretModel().moveToLogicalPosition(new LogicalPosition(2, 39));
        myFixture.complete(CompletionType.BASIC, 1);
        strings = myFixture.getLookupElementStrings();
        assert strings != null;
        assertTrue(
                strings.containsAll(
                        Arrays.asList(
                                "moduleRelativeBaseUrlPath",
                                "moduleRelativeOneDotPath",
                                "moduleRelativeTwoDotPAth"
                        )
                )
        );
        assertEquals(3, strings.size());
    }
}
