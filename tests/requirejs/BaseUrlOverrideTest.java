package requirejs;

import com.intellij.psi.PsiReference;

import java.util.Arrays;
import java.util.List;

public class BaseUrlOverrideTest extends RequirejsTestCase {
    @Override
    public void setUp() throws Exception {
        super.setUp();
        myFixture.configureByFiles(
                "baseUrlOverride/referenceTest.js",
                "baseUrlOverride/other/file.js",
                "baseUrlOverride/sub/block.js"
        );
        setWebPathSetting("baseUrlOverride");
    }

    public void testReference1() {
        setConfigPath("referenceTest.js");
        setBaseUrlOverride("other");

        PsiReference reference;

        reference = getReferenceForHumanPosition(6, 28);
        assertReference(reference, "block", null);
    }

    public void testReference2() {
        setConfigPath("referenceTest.js");
        setBaseUrlOverride("other");

        PsiReference reference;

        reference = getReferenceForHumanPosition(7, 28);
        assertReference(reference, "file", "file.js");
    }

    public void testCompletion1() {
        myFixture.configureByFile("baseUrlOverride/completionTest.js");
        setConfigPath("completionTest.js");
        setBaseUrlOverride("other");

        List<String> strings;

        strings = getCompletionStringsForHumanPosition(6, 29);
        assertEmpty(strings);
    }

    public void testCompletion2() {
        myFixture.configureByFile("baseUrlOverride/completionTest.js");
        setConfigPath("completionTest.js");
        setBaseUrlOverride("other");

        List<String> strings;

        strings = getCompletionStringsForHumanPosition(7, 27);
        assertNull(strings);
        assertCompletionSingle("file");
    }
}
