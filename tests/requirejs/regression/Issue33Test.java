package requirejs.regression;


import com.intellij.psi.PsiReference;
import org.junit.Ignore;
import org.junit.Test;
import requirejs.RequirejsTestCase;

public class Issue33Test extends RequirejsTestCase {
    @Override
    public void setUp() throws Exception {
        super.setUp();
        myFixture.configureByFiles(
                "issue33/js/require-config.js",
                "issue33/plugins/designer/scripts/app/designer.js",
                "issue33/plugins/designer/scripts/lib/file.js"
        );
        setWebPathSetting("issue33");
        setConfigPath("js/require-config.js");
    }

//    public void testReference() {
//        PsiReference reference = getReferenceForHumanPosition(118, 36);
//        assertReference(reference, "'designer/designer'", "designer.js");
//    }

    public void testReferenceNotFound() {
        PsiReference reference = getReferenceForHumanPosition(119, 44);
        assertReference(reference, "constraints/notFound", null);
    }
}
