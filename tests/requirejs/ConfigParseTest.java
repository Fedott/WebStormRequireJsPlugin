package requirejs;

import com.intellij.lang.javascript.psi.JSFile;
import com.intellij.openapi.editor.LogicalPosition;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.impl.source.resolve.reference.impl.PsiMultiReference;
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
        Settings.getInstance(getProject()).configFilePath = "mainRequireJs.js";

        // moduleDepend
        List<String> strings = getCompletionStrings(1, 38);
        assertCompletionList(Arrays.asList(
                "moduleRelativeBaseUrlPath",
                "moduleAbsolutePath",
                "moduleRelativeOneDotPath",
                "moduleRelativeTwoDotPath"
        ), strings);

        // moduleDepend2
        strings = getCompletionStrings(2, 39);
        assertCompletionList(Arrays.asList(
                "moduleRelativeBaseUrlPath",
                "moduleRelativeOneDotPath",
                "moduleRelativeTwoDotPath"
        ), strings);
    }

    public void testReference()
    {
        Settings.getInstance(getProject()).configFilePath = "mainRequireJs.js";

        PsiReference reference;
        PsiElement referenceElement;

        myFixture
                .getEditor()
                .getCaretModel()
                .moveToLogicalPosition(new LogicalPosition(1, 38));
        reference = myFixture.getReferenceAtCaretPosition();
        assert (reference) != null;
        reference = ((PsiMultiReference)reference).getReferences()[1];
        assertTrue(reference instanceof RequirejsReference);
        assertEquals("'module'", reference.getCanonicalText());
        referenceElement = reference.resolve();
        assertNull(referenceElement);

        myFixture
                .getEditor()
                .getCaretModel()
                .moveToLogicalPosition(new LogicalPosition(3, 51));
        reference = myFixture.getReferenceAtCaretPosition();
        assert (reference) != null;
        reference = ((PsiMultiReference)reference).getReferences()[1];
        assertTrue(reference instanceof RequirejsReference);
        assertEquals("'moduleRelativeBaseUrlPath'", reference.getCanonicalText());
        referenceElement = reference.resolve();
        assertTrue(referenceElement instanceof JSFile);
        assertEquals("childBlock.js", ((JSFile) referenceElement).getName());

        myFixture
                .getEditor()
                .getCaretModel()
                .moveToLogicalPosition(new LogicalPosition(4, 51));
        reference = myFixture.getReferenceAtCaretPosition();
        assert (reference) != null;
        reference = ((PsiMultiReference)reference).getReferences()[1];
        assertTrue(reference instanceof RequirejsReference);
        assertEquals("'moduleAbsolutePath'", reference.getCanonicalText());
        referenceElement = reference.resolve();
        assertTrue(referenceElement instanceof JSFile);
        assertEquals("block.js", ((JSFile) referenceElement).getName());

        myFixture
                .getEditor()
                .getCaretModel()
                .moveToLogicalPosition(new LogicalPosition(5, 51));
        reference = myFixture.getReferenceAtCaretPosition();
        assert (reference) != null;
        reference = ((PsiMultiReference)reference).getReferences()[1];
        assertTrue(reference instanceof RequirejsReference);
        assertEquals("'moduleRelativeOneDotPath'", reference.getCanonicalText());
        referenceElement = reference.resolve();
        assertTrue(referenceElement instanceof JSFile);
        assertEquals("block.js", ((JSFile) referenceElement).getName());

        myFixture
                .getEditor()
                .getCaretModel()
                .moveToLogicalPosition(new LogicalPosition(6, 51));
        reference = myFixture.getReferenceAtCaretPosition();
        assert (reference) != null;
        reference = ((PsiMultiReference)reference).getReferences()[1];
        assertTrue(reference instanceof RequirejsReference);
        assertEquals("'moduleRelativeTwoDotPAth'", reference.getCanonicalText());
        referenceElement = reference.resolve();
        assertNull(referenceElement);
    }

    protected void testCompletionOtherConfigFile(String configPath)
    {
        Settings.getInstance(getProject()).configFilePath = configPath;

        // moduleDepend
        List<String> strings = getCompletionStrings(1, 38);
        assertCompletionList(Arrays.asList(
                "moduleRelativeBaseUrlPath",
                "moduleAbsolutePath",
                "moduleRelativeTwoDotPAth"
        ), strings);

        // moduleDepend2
        strings = getCompletionStrings(2, 39);
        assertCompletionList(Arrays.asList(
                "moduleRelativeBaseUrlPath",
                "moduleRelativeTwoDotPAth"
        ), strings);
    }

    public void testCompletionConfigRequire() {
        myFixture.configureByFiles(
                "public/rootWebPathConfigTest.js",
                "public/config/configWithRequire.js"
        );

        testCompletionOtherConfigFile("config/configWithRequire.js");
    }

    public void testCompletionConfigRequireJs() {
        myFixture.configureByFiles(
                "public/rootWebPathConfigTest.js",
                "public/config/configWithRequireJs.js"
        );

        testCompletionOtherConfigFile("config/configWithRequireJs.js");
    }

    public void testCompletionConfigRequireFirstObject() {
        myFixture.configureByFiles(
                "public/rootWebPathConfigTest.js",
                "public/config/configWithRequireFirstObject.js"
        );

        testCompletionOtherConfigFile("config/configWithRequireFirstObject.js");
    }

    public void testCompletionConfigRequireJsFirstObject() {
        myFixture.configureByFiles(
                "public/rootWebPathConfigTest.js",
                "public/config/configWithRequireJsFirstObject.js"
        );

        testCompletionOtherConfigFile("config/configWithRequireJsFirstObject.js");
    }

    public void testCompletionConfigWithStringLiteral() {
        myFixture.configureByFiles(
                "public/rootWebPathConfigTest.js",
                "public/config/configWithStringLiteral.js"
        );

        testCompletionOtherConfigFile("config/configWithStringLiteral.js");
    }

    public void testCompletionConfigWithGlobalRequireObject() {
        myFixture.configureByFiles(
                "public/rootWebPathConfigTest.js",
                "public/config/configWithGlobalRequireObject.js"
        );

        testCompletionOtherConfigFile("config/configWithGlobalRequireObject.js");
    }

    public void testCompletionConfigWithGlobalRequirejsObject() {
        myFixture.configureByFiles(
                "public/rootWebPathConfigTest.js",
                "public/config/configWithGlobalRequirejsObject.js"
        );

        testCompletionOtherConfigFile("config/configWithGlobalRequirejsObject.js");
    }

    public void testConfigWithBaseUrlWithoutStartSlash()
    {
        List<String> strings;
        PsiReference reference;

        myFixture.configureByFile("public/config/configWithBaseUrlWithoutStartSlash.js");

        Settings.getInstance(getProject()).configFilePath = "config/configWithBaseUrlWithoutStartSlash.js";


        // Completion 1
        strings = getCompletionStringsForHumanPosition(10, 11);
        assertCompletionList(Arrays.asList(
                "aliasChildBlock",
                "aliasRelativePath/fileWithDotPath",
                "aliasRelativePath/childWebPathFile",
                "aliasRelativePath/fileWithTwoDotPath",
                "aliasRelativePath/block",
                "aliasRelativePath/childBlocks/childBlock"
        ), strings);

        // Completion 2
        strings = getCompletionStringsForHumanPosition(11, 29);
        assertCompletionList(Arrays.asList(
                "aliasRelativePath/childWebPathFile",
                "aliasRelativePath/childBlocks/childBlock"
        ), strings);


        // Reference 1
        reference = getReferenceForHumanPosition(13, 16);
        assertReference(reference, "'aliasChildBlock'", "childBlock.js");

        // Reference 2
        reference = getReferenceForHumanPosition(14,16);
        assertReference(reference, "'aliasRelativePath/block'", "block.js");
    }

    public void testConfigWithRelativePathReference()
    {
        PsiReference reference;

        initForTestConfigWithRelativePath();

        // 1
        reference = getReferenceForHumanPosition(11, 12);
        assertReference(reference, "'moduleOne'", "kit.js");
    }

    public void testConfigWithRelativePathReference2()
    {
        PsiReference reference;

        initForTestConfigWithRelativePath();

        // 2
        reference = getReferenceForHumanPosition(12, 12);
        assertReference(reference, "'moduleTwo'", "mainWithRelativePath.js");
    }

    public void testConfigWithRelativePathReference3()
    {
        PsiReference reference;

        initForTestConfigWithRelativePath();

        // 3
        reference = getReferenceForHumanPosition(13, 12);
        assertReference(reference, "'moduleThree'", "main.js");
    }

    protected void initForTestConfigWithRelativePath() {
        myFixture.configureByFiles(
                "public/config/configWithRelativePathTest.js",
                "public/sub/kits/kit.js",
                "public/sub/mainWithRelativePath.js"
        );

        Settings.getInstance(getProject()).configFilePath = "config/configWithRelativePathTest.js";
    }

    protected void initForTestsConfigWithoutBaseUrlWithRelativePathReference()
    {
        myFixture.configureByFiles(
                "public/blocks/fileForReferenceTestConfigWithoutBaseUrlWithRelativePath.js",
                "public/sub/kits/kit.js",
                "public/sub/kits/configWithoutBaseUrlWithRelativePath.js"
        );

        Settings.getInstance(getProject()).configFilePath = "sub/kits/configWithoutBaseUrlWithRelativePath.js";
    }

    public void testConfigWithoutBaseUrlWithRelativePathReference1()
    {
        PsiReference reference;
        initForTestsConfigWithoutBaseUrlWithRelativePathReference();

        // 1
        reference = getReferenceForHumanPosition(2, 30);
        assertReference(reference, "pathForBlock", "block.js");
    }

    public void testConfigWithoutBaseUrlWithRelativePathReference2()
    {
        PsiReference reference;
        initForTestsConfigWithoutBaseUrlWithRelativePathReference();

        // 2
        reference = getReferenceForHumanPosition(3, 30);
        assertReference(reference, "pathForDirectoryTwoDot/block", "block.js");
    }

    public void testConfigWithoutBaseUrlWithRelativePathReference3()
    {
        PsiReference reference;
        initForTestsConfigWithoutBaseUrlWithRelativePathReference();

        // 3
        reference = getReferenceForHumanPosition(4, 30);
        assertReference(reference, "pathForDirectoryOneDot/kit", "kit.js");
    }

    public void testConfigWithoutBaseUrlWithRelativePathReference4()
    {
        PsiReference reference;
        initForTestsConfigWithoutBaseUrlWithRelativePathReference();

        // 4
        reference = getReferenceForHumanPosition(5, 30);
        assertReference(reference, "pathForKit", "kit.js");
    }

    public void testConfigWithoutBaseUrlWithRelativePathReference5()
    {
        PsiReference reference;
        initForTestsConfigWithoutBaseUrlWithRelativePathReference();

        // 5
        reference = getReferenceForHumanPosition(6, 30);
        assertReference(reference, "pathForNotFound", null);
    }

    public void testConfigWithoutBaseUrlWithRelativePathReference6()
    {
        PsiReference reference;
        initForTestsConfigWithoutBaseUrlWithRelativePathReference();

        // 6
        reference = getReferenceForHumanPosition(7, 30);
        assertReference(reference, "kit", "kit.js");
    }

    public void testConfigWithoutBaseUrlWithRelativePathReference7()
    {
        PsiReference reference;
        initForTestsConfigWithoutBaseUrlWithRelativePathReference();

        // 7
        reference = getReferenceForHumanPosition(8, 30);
        assertReference(reference, "./block", "block.js");
    }

    protected void initForTestsConfigWithoutBaseUrlWithRelativePathCompletion()
    {
        myFixture.configureByFiles(
                "public/blocks/fileForCompletionTestConfigWithoutBaseUrlWithRelativePath.js",
                "public/sub/kits/kit.js",
                "public/sub/kits/configWithoutBaseUrlWithRelativePath.js"
        );

        Settings.getInstance(getProject()).configFilePath = "sub/kits/configWithoutBaseUrlWithRelativePath.js";
    }

    public void testConfigWithoutBaseUrlWithRelativePathCompletion1()
    {
        List<String> strings;

        initForTestsConfigWithoutBaseUrlWithRelativePathCompletion();

        // 1
        strings = getCompletionStringsForHumanPosition(2, 35);
        assertCompletionList(Arrays.asList(
                "pathForBlock",
                "pathForDirectoryTwoDot/fileWithDotPath",
                "pathForDirectoryTwoDot/childWebPathFile",
                "pathForDirectoryTwoDot/fileWithTwoDotPath",
                "pathForDirectoryTwoDot/block",
                "pathForDirectoryTwoDot/childBlocks/childBlock",
                "pathForDirectoryTwoDot/fileForCompletionTestConfigWithoutBaseUrlWithRelativePath",
                "pathForDirectoryOneDot/kit",
                "pathForDirectoryOneDot/configWithoutBaseUrlWithRelativePath",
                "pathForKit"
        ), strings);
    }

    public void testConfigWithoutBaseUrlWithRelativePathCompletion2()
    {
        List<String> strings;

        initForTestsConfigWithoutBaseUrlWithRelativePathCompletion();

        // 2
        strings = getCompletionStringsForHumanPosition(3, 51);
        assertCompletionList(Arrays.asList(
                "pathForDirectoryTwoDot/fileWithDotPath",
                "pathForDirectoryTwoDot/childWebPathFile",
                "pathForDirectoryTwoDot/fileWithTwoDotPath",
                "pathForDirectoryTwoDot/block",
                "pathForDirectoryTwoDot/childBlocks/childBlock",
                "pathForDirectoryTwoDot/fileForCompletionTestConfigWithoutBaseUrlWithRelativePath"
        ), strings);
    }

    public void testConfigWithoutBaseUrlWithRelativePathCompletion3()
    {
        List<String> strings;

        initForTestsConfigWithoutBaseUrlWithRelativePathCompletion();

        // 3
        strings = getCompletionStringsForHumanPosition(4, 51);
        assertCompletionList(Arrays.asList(
                "pathForDirectoryOneDot/kit",
                "pathForDirectoryOneDot/configWithoutBaseUrlWithRelativePath"
        ), strings);
    }

    public void testConfigWithoutBaseUrlWithRelativePathCompletion4()
    {
        List<String> strings;

        initForTestsConfigWithoutBaseUrlWithRelativePathCompletion();

        // 4
        strings = getCompletionStringsForHumanPosition(5, 28);
        assertCompletionList(Arrays.asList(
                "kit",
                "configWithoutBaseUrlWithRelativePath",
                "pathForBlock",
                "pathForDirectoryTwoDot/fileWithDotPath",
                "pathForDirectoryTwoDot/childWebPathFile",
                "pathForDirectoryTwoDot/fileWithTwoDotPath",
                "pathForDirectoryTwoDot/block",
                "pathForDirectoryTwoDot/childBlocks/childBlock",
                "pathForDirectoryTwoDot/fileForCompletionTestConfigWithoutBaseUrlWithRelativePath",
                "pathForDirectoryOneDot/kit",
                "pathForDirectoryOneDot/configWithoutBaseUrlWithRelativePath",
                "pathForKit"
        ), strings);
    }

    public void testConfigWithoutBaseUrlWithRelativePathCompletion5()
    {
        List<String> strings;

        initForTestsConfigWithoutBaseUrlWithRelativePathCompletion();

        // 5
        strings = getCompletionStringsForHumanPosition(6, 30);
        assertCompletionList(Arrays.asList(
                "./fileWithDotPath",
                "./childWebPathFile",
                "./fileWithTwoDotPath",
                "./block",
                "./childBlocks/childBlock",
                "./fileForCompletionTestConfigWithoutBaseUrlWithRelativePath"
        ), strings);
    }
}
