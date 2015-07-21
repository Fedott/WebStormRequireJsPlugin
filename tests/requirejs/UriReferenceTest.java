package requirejs;

import com.intellij.openapi.paths.WebReference;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.PsiReference;

public class UriReferenceTest extends RequirejsTestCase {
    @Override
    public void setUp() throws Exception {
        super.setUp();
        myFixture.configureByFiles(
                "public/fileForUriReferenceTest.js",
                "public/config/configUriReferenceTest.js"
        );
        setWebPathSetting();
        setConfigPath("config/configUriReferenceTest.js");
    }

    protected void assertUrlReference(PsiReference reference, String expectedUrl) {
        PsiElement referenceElement = reference.resolve();
        assertTrue(referenceElement instanceof PsiNamedElement);
        assertEquals(expectedUrl, ((PsiNamedElement) referenceElement).getName());
    }

    public void testReference1() {
        PsiReference reference = getReferenceForHumanPosition(2, 42);
        assertUrlReference(reference, "https://cdn.google.com/jquery.js");
    }

    public void testReference2() {
        PsiReference reference = getReferenceForHumanPosition(3, 42);
        assertUrlReference(reference, "http://cdn.google.com/jquery.2.js");
    }

    public void testReference3() {
        PsiReference reference = getReferenceForHumanPosition(4, 42);
        assertUrlReference(reference, "https://google.com/jquery.js");
    }

    public void testReference4() {
        PsiReference reference = getReferenceForHumanPosition(5, 42);
        assertUrlReference(reference, "http://google.com/jquery.2.js");
    }
}
