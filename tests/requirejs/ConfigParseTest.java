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
                "moduleRelativeOneDotPath"
        ), strings);

        // moduleDepend2
        strings = getCompletionStrings(2, 39);
        assertCompletionList(Arrays.asList(
                "moduleRelativeBaseUrlPath",
                "moduleRelativeOneDotPath"
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
}
