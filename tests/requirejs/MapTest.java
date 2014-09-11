package requirejs;

import com.intellij.psi.PsiReference;

import java.util.Arrays;
import java.util.List;

public class MapTest extends RequirejsTestCase {
    @Override
    public void setUp() throws Exception {
        super.setUp();
        myFixture.configureByFiles(
                "mapPublic/some/oldModule.js",
                "mapPublic/some/newModule.js",
                "mapPublic/some/otherModule.js",
                "mapPublic/main.js",
                "mapPublic/foo1.3.js",
                "mapPublic/foo1.0.js",
                "mapPublic/foo1.2.js",
                "mapPublic/bar2.0r1.js"
        );
        setWebPathSetting("mapPublic");
    }

    public void testReferenceOldModule() {
        PsiReference reference;

        myFixture.configureByFile("mapPublic/some/oldModule.js");
        reference = getReferenceForHumanPosition(2, 26);
        assertReference(reference, "'foo'", "foo1.0.js");

        reference = getReferenceForHumanPosition(3, 26);
        assertReference(reference, "'bar'", "bar2.0r1.js");
    }

    public void testReferenceNewModule() {
        PsiReference reference;

        myFixture.configureByFile("mapPublic/some/newModule.js");
        reference = getReferenceForHumanPosition(2, 26);
        assertReference(reference, "'foo'", "foo1.2.js");

        reference = getReferenceForHumanPosition(3, 26);
        assertReference(reference, "'bar'", "bar2.0r1.js");
    }

    public void testReferenceOtherModule() {
        PsiReference reference;

        myFixture.configureByFile("mapPublic/some/otherModule.js");
        reference = getReferenceForHumanPosition(2, 26);
        assertReference(reference, "'foo'", "foo1.3.js");

        reference = getReferenceForHumanPosition(3, 26);
        assertReference(reference, "'bar'", "bar2.0r1.js");
    }

    public void testCompletionNewModule() {
        List<String> strings;

        myFixture.configureByFile("mapPublic/some/newModule.js");

        // foo
        strings = getCompletionStringsForHumanPosition(5, 25);
        assertCompletionList(Arrays.asList(
                "foo",
                "foo1.0",
                "foo1.2",
                "foo1.3"
        ), strings);

        // bar
        strings = getCompletionStringsForHumanPosition(6, 25);
        assertCompletionList(Arrays.asList(
                "bar",
                "bar2.0r1"
        ), strings);

        // only in new module
        strings = getCompletionStringsForHumanPosition(7, 38);
        assertNull(strings);
        assertCompletionSingle("onlyInNewModule");
    }

    public void testCompletionOldModule() {
        List<String> strings;

        myFixture.configureByFile("mapPublic/some/oldModule.js");

        // foo
        strings = getCompletionStringsForHumanPosition(5, 25);
        assertCompletionList(Arrays.asList(
                "foo",
                "foo1.0",
                "foo1.2",
                "foo1.3"
        ), strings);

        // bar
        strings = getCompletionStringsForHumanPosition(6, 25);
        assertCompletionList(Arrays.asList(
                "bar",
                "bar2.0r1"
        ), strings);

        // only in new module
        strings = getCompletionStringsForHumanPosition(7, 38);
        assertNull(strings);
        assertCompletionSingle("onlyInOldModule");
    }

    public void testCompletionOtherModule() {
        List<String> strings;

        myFixture.configureByFile("mapPublic/some/newModule.js");

        // foo
        strings = getCompletionStringsForHumanPosition(5, 25);
        assertCompletionList(Arrays.asList(
                "foo",
                "foo1.0",
                "foo1.2",
                "foo1.3"
        ), strings);

        // bar
        strings = getCompletionStringsForHumanPosition(6, 25);
        assertCompletionList(Arrays.asList(
                "bar",
                "bar2.0r1"
        ), strings);

        // only in new module
        strings = getCompletionStringsForHumanPosition(7, 31);
        assertFalse(strings.contains("onlyInNewModule"));
        assertFalse(strings.contains("onlyInOldModule"));
    }
}
