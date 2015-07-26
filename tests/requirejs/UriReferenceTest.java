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

    public void testReference5() {
        PsiReference reference = getReferenceForHumanPosition(7, 42);
        assertUrlReference(reference, "https://google.com/jquery.5.js");
    }

    public void testReference6() {
        PsiReference reference = getReferenceForHumanPosition(8, 42);
        assertUrlReference(reference, "http://google.com/jquery.6.js");
    }

    public void testReference7() {
        PsiReference reference = getReferenceForHumanPosition(9, 42);
        assertUrlReference(reference, "https://google.com/jquery.7.js");
    }

    public void testReference8() {
        PsiReference reference = getReferenceForHumanPosition(10, 42);
        assertUrlReference(reference, "http://google.com/jquery.8.js");
    }
}
