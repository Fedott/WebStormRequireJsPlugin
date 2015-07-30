package requirejs.regression;

import com.intellij.psi.PsiReference;
import requirejs.RequirejsTestCase;

public class Issue48ConfigWithVarsTest extends RequirejsTestCase {
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        myFixture.configureByFiles(
                "issue48/app/file.js",
                "issue48/assets/js/libs/respond.min.js",
                "issue48/config/requirejs.config.js"

        );
        setWebPathSetting("issue48");
        setConfigPath("config/requirejs.config.js");
    }

    public void testReferenceRespond() {
        PsiReference reference;

        reference = getReferenceForHumanPosition(2, 30);
        assertReference(reference, "respond", "respond.min.js");
    }
}
