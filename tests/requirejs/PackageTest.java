package requirejs;

import com.intellij.psi.PsiReference;

import java.util.Arrays;
import java.util.List;

public class PackageTest extends RequirejsTestCase {
    @Override
    public void setUp() throws Exception {
        super.setUp();
        myFixture.configureByFiles(
                "public/packages/filePackagesCompletionTest.js",
                "public/packages/filePackagesResolveTest.js",
                "public/packages/packageLocation/main.js",
                "public/packages/packageLocation2/packageFile.js",
                "public/packages/packageSimple/main.js",
                "public/packages/packageWithMain/packageFile.js",
                "public/packages/packageWithName/main.js",
                "public/packages/packageSimple/otherFile.js",
                "public/packages/packageWithMainNotExists/otherFile.js",
                "public/sub/packages/locationMainPackage/location.js",
                "public/sub/packages/locationMainPackage/otherFile.js",
                "public/sub/packages/locationPackage/main.js",
                "public/sub/packages/locationPackage/otherFile.js",
                "public/packages/configWithPackages.js"
        );
        setWebPathSetting();
        setConfigPath("packages/configWithPackages.js");
    }

    public void testCompletion1() {
        List<String> strings = getCompletionStringsForHumanPosition(2, 32);
        assertCompletionList(Arrays.asList(
                "packageSimple",
                "packageSimple/otherFile",
                "packageWithName",
                "packageWithName/main",
                "packageWithMain",
                "packageWithLocation",
                "packageWithLocationAndMain",
                "packageWithMainNotExists/otherFile",
                "locationMainPackage",
                "locationPackage"
        ), 14, strings);
    }

    public void testCompletion2() {
        List<String> strings = getCompletionStringsForHumanPosition(3, 36);
        assertCompletionList(Arrays.asList(
                "packageSimple",
                "packageSimple/main",
                "packageSimple/otherFile"
        ), strings);
    }

    public void testCompletion3() {
        List<String> strings = getCompletionStringsForHumanPosition(4, 42);
        assertCompletionList(Arrays.asList(
                "packageSimple/main",
                "packageSimple/otherFile"
        ), strings);
    }

    public void testCompletion4() {
        List<String> strings = getCompletionStringsForHumanPosition(5, 39);
        assertEmpty(strings);
    }

    public void testCompletion5() {
        List<String> strings = getCompletionStringsForHumanPosition(6, 48);
        assertEmpty(strings);
    }

    public void testCompletion6() {
        List<String> strings = getCompletionStringsForHumanPosition(7, 36);
        assertCompletionList(Arrays.asList(
                "locationPackage",
                "locationPackage/otherFile",
                "locationMainPackage",
                "locationMainPackage/otherFile",
                "packageWithLocation",
                "packageWithLocationAndMain"
        ), strings);
    }

    public void testCompletion7() {
        List<String> strings = getCompletionStringsForHumanPosition(8, 48);
        assertNull(strings);
        assertCompletionSingle("locationMainPackage/otherFile");
    }

    public void testReference1()
    {
        myFixture.configureByFile("public/packages/filePackagesResolveTest.js");
        PsiReference reference;

        // 1
        reference = getReferenceForHumanPosition(2, 35);
        assertReference(reference, "'packageSimple'", "main.js");
    }

    public void testReference2()
    {
        myFixture.configureByFile("public/packages/filePackagesResolveTest.js");
        PsiReference reference;

        // 1
        reference = getReferenceForHumanPosition(3, 35);
        assertReference(reference, "'packageSimple/otherFile'", "otherFile.js");
    }

    public void testReference3()
    {
        myFixture.configureByFile("public/packages/filePackagesResolveTest.js");
        PsiReference reference;

        // 1
        reference = getReferenceForHumanPosition(4, 35);
        assertReference(reference, "'packageWithMain'", "packageFile.js");
    }

    public void testReference4()
    {
        myFixture.configureByFile("public/packages/filePackagesResolveTest.js");
        PsiReference reference;

        // 1
        reference = getReferenceForHumanPosition(5, 35);
        assertReference(reference, "'packageWithLocationAndMain'", "packageFile.js");
    }

    public void testReference5()
    {
        myFixture.configureByFile("public/packages/filePackagesResolveTest.js");
        PsiReference reference;

        // 1
        reference = getReferenceForHumanPosition(6, 35);
        assertReference(reference, "'packageDirNotExists'", null);
    }

    public void testReference6()
    {
        myFixture.configureByFile("public/packages/filePackagesResolveTest.js");
        PsiReference reference;

        // 1
        reference = getReferenceForHumanPosition(7, 35);
        assertReference(reference, "'packageWithMainNotExists'", null);
    }

    public void testReference7()
    {
        myFixture.configureByFile("public/packages/filePackagesResolveTest.js");
        PsiReference reference;

        // 1
        reference = getReferenceForHumanPosition(8, 35);
        assertReference(reference, "'packageWithMainNotExists/otherFile'", "otherFile.js");
    }

    public void testReference8()
    {
        myFixture.configureByFile("public/packages/filePackagesResolveTest.js");
        PsiReference reference;

        // 1
        reference = getReferenceForHumanPosition(9, 35);
        assertReference(reference, "'packageSimple/main'", "main.js");
    }

    public void testReference9()
    {
        myFixture.configureByFile("public/packages/filePackagesResolveTest.js");
        PsiReference reference;

        // 1
        reference = getReferenceForHumanPosition(10, 35);
        assertReference(reference, "'packageWithLocation'", "main.js");
    }

    public void testReference10()
    {
        myFixture.configureByFile("public/packages/filePackagesResolveTest.js");
        PsiReference reference;

        // 1
        reference = getReferenceForHumanPosition(11, 35);
        assertReference(reference, "'locationMainPackage'", "location.js");
    }

    public void testReference11()
    {
        myFixture.configureByFile("public/packages/filePackagesResolveTest.js");
        PsiReference reference;

        // 1
        reference = getReferenceForHumanPosition(12, 35);
        assertReference(reference, "'locationPackage'", "main.js");
    }
}
