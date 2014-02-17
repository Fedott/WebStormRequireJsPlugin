package requirejs;

public class exclamationMarkTest extends RequirejsTestCase {
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

    }
}
