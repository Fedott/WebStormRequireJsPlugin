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
    }

    protected void assertUrlReference(PsiReference reference, String expectedUrl) {
        PsiElement referenceElement = reference.resolve();
        assertTrue(referenceElement instanceof PsiNamedElement);
        assertEquals(((PsiNamedElement) referenceElement).getName(), expectedUrl);
    }

    public void testReference1() {
        PsiReference reference = getReferenceForHumanPosition(2, 42);
        assertUrlReference(reference, "https://cdn.google.com/jquery.js");
    }

    public void testReference2() {
        PsiReference reference = getReferenceForHumanPosition(3, 42);
        assertUrlReference(reference, "http://cdn.google.com/jquery.2.js");
    }
}
