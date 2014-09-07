package requirejs;

import com.intellij.psi.PsiReference;

public class MapTest extends RequirejsTestCase {
    @Override
    public void setUp() throws Exception {
        super.setUp();
        myFixture.configureByFiles(
                "mapPublic/some/oldModule.js",
                "mapPublic/some/newModule.js",
                "mapPublic/some/otherModule.js",
                "mapPublic/main.js",
                "mapPublic/foo1.0.js",
                "mapPublic/foo1.2.js"
        );
        setWebPathSetting("mapPublic");
    }

    public void testReferenceOld() {
        PsiReference reference;

        myFixture.configureByFile("mapPublic/some/oldModule.js");
        reference = getReferenceForHumanPosition(2, 27);
        assertReference(reference, "'foo'", "foo1.0.js");
    }

    public void testReferenceNewModule() {
        PsiReference reference;

        myFixture.configureByFile("mapPublic/some/newModule.js");
        reference = getReferenceForHumanPosition(2, 27);
        assertReference(reference, "'foo'", "foo1.2.js");
    }

    public void testReferenceOtherModule() {
        PsiReference reference;

        myFixture.configureByFile("mapPublic/some/otherModule.js");
        reference = getReferenceForHumanPosition(2, 27);
        assertReference(reference, "'foo'", "foo.js");
    }
}
