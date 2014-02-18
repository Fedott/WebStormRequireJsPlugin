package requirejs;

import requirejs.settings.Settings;

import java.util.Arrays;
import java.util.List;

public class ExclamationMarkTest extends RequirejsTestCase {
    @Override
    public void setUp() throws Exception {
        super.setUp();
        myFixture.configureByFiles(
                "public/fileWithExclamationMarkTest.js",
                "public/blocks/childWebPathFile.js",
                "public/blocks/fileWithDotPath.js",
                "public/blocks/fileWithTwoDotPath.js",
                "public/main.js",
                "public/blocks/block.js",
                "public/blocks/childBlocks/childBlock.js",
                "public/rootWebPathFile.js",
                "public/blocks/childBlocks/templates/index.html",
                "public/config/configWithExclamationMarkTest.js"

        );
        setWebPathSetting();

        Settings.getInstance(getProject()).configFilePath = "config/configWithExclamationMarkTest.js";
    }

    public void testCompletion() {
        List<String> strings;

        // 1
        strings = getCompletionStringsForHumanPosition(2, 38);
        assertCompletionList(Arrays.asList(
                "moduleOne!moduleOne",
                "moduleOne!moduleTwo",
                "moduleOne!moduleThree",
                "moduleOne!childWebPathFile",
                "moduleOne!fileWithDotPath",
                "moduleOne!fileWithTwoDotPath",
                "moduleOne!block",
                "moduleOne!childBlocks/childBlock",
                "moduleOne!childBlocks/templates/index.html"
        ), strings);
    }

    public void testCompletion2() {
        List<String> strings;

        // 2
        strings = getCompletionStringsForHumanPosition(3, 44);
        assertCompletionList(Arrays.asList(
                "moduleTwo!moduleOne",
                "moduleTwo!moduleTwo",
                "moduleTwo!moduleThree"
        ), strings);

    }

    public void testCompletion3() {
        List<String> strings;

        // 3
        strings = getCompletionStringsForHumanPosition(4, 43);
        assertCompletionList(Arrays.asList(
                "moduleOne!block"
        ), strings);
        // assertNull(strings);
    }

    public void testCompletion4() {
        List<String> strings;

        // 4
        strings = getCompletionStringsForHumanPosition(5, 44);
        assertCompletionList(Arrays.asList(
                "moduleOne!./blocks/childWebPathFile",
                "moduleOne!./blocks/fileWithDotPath",
                "moduleOne!./blocks/fileWithTwoDotPath",
                "moduleOne!./blocks/block",
                "moduleOne!./blocks/childBlocks/childBlock",
                "moduleOne!./blocks/childBlocks/templates/index.html"
        ), strings);
    }

    public void testCompletion5() {
        List<String> strings;

        // 5
        strings = getCompletionStringsForHumanPosition(6, 44);
        assertCompletionList(Arrays.asList(
                "moduleOne!./fileWithExclamationMarkTest"
        ), strings);
    }

    public void testCompletion6() {
        List<String> strings;

        // 6
        strings = getCompletionStringsForHumanPosition(7, 40);
        assertCompletionList(Arrays.asList(
                "moduleOne!./config/configWithExclamationMarkTest",
                "moduleOne!./blocks/childWebPathFile",
                "moduleOne!./blocks/fileWithDotPath",
                "moduleOne!./blocks/fileWithTwoDotPath",
                "moduleOne!./main",
                "moduleOne!./blocks/block",
                "moduleOne!./blocks/childBlocks/childBlock",
                "moduleOne!./rootWebPathFile",
                "moduleOne!./fileWithExclamationMarkTest",
                "moduleOne!./blocks/childBlocks/templates/index.html"
        ), strings);
    }
}
