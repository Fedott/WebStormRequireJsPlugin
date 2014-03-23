package requirejs;

import com.intellij.psi.PsiReference;
import requirejs.settings.Settings;

public class FilenameEqualsDirectoryNameTest extends RequirejsTestCase {
    @Override
    public void setUp() throws Exception {
        super.setUp();
        myFixture.configureByFiles(
                "public/fileForTestFilenameEqualsDirectoryName.js",
                "public/js/core/tools/toString.js",
                "public/js/core/tools.js"
        );
        setWebPathSetting();

        Settings.getInstance(getProject()).configFilePath = "fileForTestFilenameEqualsDirectoryName.js";
    }

    public void testReference() {
        myFixture.configureByFile("public/fileForTestFilenameEqualsDirectoryName.js");

        PsiReference reference = getReferenceForHumanPosition(5, 33);
        assertReference(reference, "core/tools", "tools.js");
    }
}
