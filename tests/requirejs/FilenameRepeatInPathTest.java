package requirejs;

import com.intellij.psi.PsiReference;
import requirejs.settings.Settings;

public class FilenameRepeatInPathTest extends RequirejsTestCase {
    @Override
    public void setUp() throws Exception {
        super.setUp();
        myFixture.configureByFiles(
                "public/fileForTestFilenameRepeatInPath.js",
                "public/vendor/mout/src/lang/file.js"
        );
        setWebPathSetting();

        Settings.getInstance(getProject()).configFilePath = "fileForTestFilenameRepeatInPath.js";
    }

    public void testCompletion() {

    }

    public void testReference() {
        myFixture.configureByFile("public/fileForTestFilenameRepeatInPath.js");

        PsiReference reference = getReferenceForHumanPosition(10, 33);
        assertReference(reference, "mout/lang/file", "file.js");
    }

    public void testReferenceWithRelativePathConfig() {
        myFixture.configureByFile("public/sub/fileForTestFilenameRepeatInPathWithRelativePathConfig.js");

        PsiReference reference = getReferenceForHumanPosition(10, 33);
        assertReference(reference, "mout/lang/file", "file.js");
    }


}
