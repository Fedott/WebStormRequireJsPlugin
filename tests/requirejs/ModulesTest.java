package requirejs;

import com.intellij.psi.PsiReference;

public class ModulesTest extends RequirejsTestCase {
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        myFixture.configureByFiles(
                "public/fileForTestModules.js",
                "public/config/configWithModules.js",
//                "public/data/awsum.jpg",
                "public/data/bar.php",
                "public/data/foo.json",
                "public/data/lorem_ipsum.md",
                "public/data/template.hbs"
        );
        setWebPathSetting();
        setConfigPath("config/configWithModules.js");
    }

//    public void testReferenceImage() {
//        PsiReference reference;
//
//        reference = getReferenceForHumanPosition(2, 22);
//        assertReference(reference, "image!data/awsum.jpg", "awsum.jpg");
//    }

    public void testReferenceJson() {
        PsiReference reference;

        reference = getReferenceForHumanPosition(3, 22);
        assertReference(reference, "json!data/foo.json", "foo.json");
    }

    public void testReferenceNoext() {
        PsiReference reference;

        reference = getReferenceForHumanPosition(4, 22);
        assertReference(reference, "noext!data/bar.php", "bar.php");
    }

    public void testReferenceMdown() {
        PsiReference reference;

        reference = getReferenceForHumanPosition(5, 22);
        assertReference(reference, "mdown!data/lorem_ipsum.md", "lorem_ipsum.md");
    }

    public void testReferenceHbs() {
        PsiReference reference;

        reference = getReferenceForHumanPosition(10, 22);
        assertReference(reference, "hbs!data/template", "template.hbs");
    }
}
