package requirejs;

import com.intellij.lang.javascript.psi.JSFile;
import com.intellij.openapi.editor.LogicalPosition;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.impl.source.resolve.reference.impl.PsiMultiReference;
import requirejs.settings.Settings;

public class ReferenceTest extends RequirejsTestCase {
    @Override
    public void setUp() throws Exception {
        super.setUp();
        myFixture.configureByFiles(
                "public/blocks/referenceTest.js",
                "public/blocks/fileWithTwoDotPath.js",
                "public/blocks/childWebPathFile.js",
                "public/blocks/fileWithDotPath.js",
                "public/main.js",
                "public/blocks/block.js",
                "public/blocks/childBlocks/childBlock.js",
                "public/rootWebPathFile.js",
                "public/blocks/childBlocks/templates/index.html"

        );
        setWebPathSetting();
    }

    public void testReference()
    {
        PsiReference reference;
        PsiElement referenceElement;

        // referenceNotFound
        myFixture.getEditor().getCaretModel().moveToLogicalPosition(new LogicalPosition(1, 40));
        reference = myFixture.getReferenceAtCaretPosition();
        assertTrue(reference instanceof RequirejsReference);
        assertEquals("'app/as'", reference.getCanonicalText());
        referenceElement = reference.resolve();
        assertNull(referenceElement);

        // referenceTrue
        myFixture
                .getEditor()
                .getCaretModel()
                .moveToLogicalPosition(new LogicalPosition(2, 40));
        reference = myFixture.getReferenceAtCaretPosition();
        assertTrue(reference instanceof RequirejsReference);
        assertEquals("'blocks/block'", reference.getCanonicalText());
        referenceElement = reference.resolve();
        assertTrue(referenceElement instanceof JSFile);
        assertEquals("block.js", ((JSFile) referenceElement).getName());

        // referenceNotFound2
        myFixture
                .getEditor()
                .getCaretModel()
                .moveToLogicalPosition(new LogicalPosition(3, 40));
        reference = myFixture.getReferenceAtCaretPosition();
        assertTrue(reference instanceof PsiMultiReference);
        reference = ((PsiMultiReference) reference).getReferences()[1];
        assertEquals("'bl'", reference.getCanonicalText());
        referenceElement = reference.resolve();
        assertNull(referenceElement);

        // referenceDirectory
        myFixture
                .getEditor()
                .getCaretModel()
                .moveToLogicalPosition(new LogicalPosition(4, 40));
        reference = myFixture.getReferenceAtCaretPosition();
        assertTrue(reference instanceof RequirejsReference);
        assertEquals("'blocks/childBlocks'", reference.getCanonicalText());
        referenceElement = reference.resolve();
        assertNull(referenceElement);

        // referenceWithTwoDot
        myFixture
                .getEditor()
                .getCaretModel()
                .moveToLogicalPosition(new LogicalPosition(5, 46));
        reference = myFixture.getReferenceAtCaretPosition();
        assertTrue(reference instanceof RequirejsReference);
        assertEquals("'../blocks/block'", reference.getCanonicalText());
        referenceElement = reference.resolve();
        assertTrue(referenceElement instanceof JSFile);
        assertEquals("block.js", ((JSFile) referenceElement).getName());

        // referenceWithTwoDotTwoDir
        myFixture
                .getEditor()
                .getCaretModel()
                .moveToLogicalPosition(new LogicalPosition(6, 49));
        reference = myFixture.getReferenceAtCaretPosition();
        assertTrue(reference instanceof RequirejsReference);
        assertEquals("'../blocks/childBlocks/childBlock'", reference.getCanonicalText());
        referenceElement = reference.resolve();
        assertTrue(referenceElement instanceof JSFile);
        assertEquals("childBlock.js", ((JSFile) referenceElement).getName());

        // referenceWithOneDot
        myFixture
                .getEditor()
                .getCaretModel()
                .moveToLogicalPosition(new LogicalPosition(7, 44));
        reference = myFixture.getReferenceAtCaretPosition();
        assertTrue(reference instanceof RequirejsReference);
        assertEquals("'./block'", reference.getCanonicalText());
        referenceElement = reference.resolve();
        assertTrue(referenceElement instanceof JSFile);
        assertEquals("block.js", ((JSFile) referenceElement).getName());

        // referenceWithOneDotTwoDir
        myFixture
                .getEditor()
                .getCaretModel()
                .moveToLogicalPosition(new LogicalPosition(8, 50));
        reference = myFixture.getReferenceAtCaretPosition();
        assertTrue(reference instanceof RequirejsReference);
        assertEquals("'./childBlocks/childBlock'", reference.getCanonicalText());
        referenceElement = reference.resolve();
        assertTrue(referenceElement instanceof JSFile);
        assertEquals("childBlock.js", ((JSFile) referenceElement).getName());
    }

    public void testReferenceWithBaseUrl()
    {
        PsiReference reference;
        PsiElement referenceElement;

        myFixture.configureByFiles("public/fileForTestBaseUrlReference.js", "public/mainWithBaseUrl.js");
        Settings.getInstance(getProject()).configFilePath = "mainWithBaseUrl.js";

        // referenceNotFound
        myFixture.getEditor().getCaretModel().moveToLogicalPosition(new LogicalPosition(1, 40));
        reference = myFixture.getReferenceAtCaretPosition();
        assertTrue(reference instanceof RequirejsReference);
        assertEquals("'app/as'", reference.getCanonicalText());
        referenceElement = reference.resolve();
        assertNull(referenceElement);

        // 1
        myFixture
                .getEditor()
                .getCaretModel()
                .moveToLogicalPosition(new LogicalPosition(2, 33));
        reference = myFixture.getReferenceAtCaretPosition();
        assertTrue(reference instanceof RequirejsReference);
        assertEquals("'blocks/block'", reference.getCanonicalText());
        referenceElement = reference.resolve();
        assertNull(referenceElement);

        // 2
        myFixture
                .getEditor()
                .getCaretModel()
                .moveToLogicalPosition(new LogicalPosition(3, 33));
        reference = myFixture.getReferenceAtCaretPosition();
        assert null != reference;
        reference = ((PsiMultiReference)reference).getReferences()[1];
        assertTrue(reference instanceof RequirejsReference);
        assertEquals("'block'", reference.getCanonicalText());
        referenceElement = reference.resolve();
        assertTrue(referenceElement instanceof JSFile);
        assertEquals("block.js", ((JSFile) referenceElement).getName());

        // 3
        myFixture
                .getEditor()
                .getCaretModel()
                .moveToLogicalPosition(new LogicalPosition(4, 33));
        reference = myFixture.getReferenceAtCaretPosition();
        assertTrue(reference instanceof RequirejsReference);
        assertEquals("'childBlocks/childBlock'", reference.getCanonicalText());
        referenceElement = reference.resolve();
        assertTrue(referenceElement instanceof JSFile);
        assertEquals("childBlock.js", ((JSFile) referenceElement).getName());

        // 4
        myFixture
                .getEditor()
                .getCaretModel()
                .moveToLogicalPosition(new LogicalPosition(5, 33));
        reference = myFixture.getReferenceAtCaretPosition();
        assert null != reference;
        reference = ((PsiMultiReference)reference).getReferences()[1];
        assertTrue(reference instanceof RequirejsReference);
        assertEquals("'childBlocks'", reference.getCanonicalText());
        referenceElement = reference.resolve();
        assertNull(referenceElement);

        // 5
        myFixture
                .getEditor()
                .getCaretModel()
                .moveToLogicalPosition(new LogicalPosition(6, 33));
        reference = myFixture.getReferenceAtCaretPosition();
        assertTrue(reference instanceof RequirejsReference);
        assertEquals("'/block'", reference.getCanonicalText());
        referenceElement = reference.resolve();
        assertNull(referenceElement);

        // 6
        myFixture
                .getEditor()
                .getCaretModel()
                .moveToLogicalPosition(new LogicalPosition(7, 33));
        reference = myFixture.getReferenceAtCaretPosition();
        assertTrue(reference instanceof RequirejsReference);
        assertEquals("'/blocks/block'", reference.getCanonicalText());
        referenceElement = reference.resolve();
        assertTrue(referenceElement instanceof JSFile);
        assertEquals("block.js", ((JSFile) referenceElement).getName());

        // 7
        myFixture
                .getEditor()
                .getCaretModel()
                .moveToLogicalPosition(new LogicalPosition(8, 33));
        reference = myFixture.getReferenceAtCaretPosition();
        assertTrue(reference instanceof RequirejsReference);
        assertEquals("'/childBlocks/childBlock'", reference.getCanonicalText());
        referenceElement = reference.resolve();
        assertNull(referenceElement);

        // 8
        myFixture
                .getEditor()
                .getCaretModel()
                .moveToLogicalPosition(new LogicalPosition(9, 33));
        reference = myFixture.getReferenceAtCaretPosition();
        assertTrue(reference instanceof RequirejsReference);
        assertEquals("'./blocks/block'", reference.getCanonicalText());
        referenceElement = reference.resolve();
        assertTrue(referenceElement instanceof JSFile);
        assertEquals("block.js", ((JSFile) referenceElement).getName());

        // 9
        // TODO: Add check for parent root web directory
//        myFixture
//                .getEditor()
//                .getCaretModel()
//                .moveToLogicalPosition(new LogicalPosition(10, 33));
//        reference = myFixture.getReferenceAtCaretPosition();
//        assertTrue(reference instanceof RequirejsReference);
//        assertEquals("'../public/blocks/block'", reference.getCanonicalText());
//        referenceElement = reference.resolve();
//        assertNull(referenceElement);
    }

    public void testCompletionInDefine()
    {
        myFixture.configureByFiles("public/blocks/fileWithDefine.js");

        PsiReference reference;
        PsiElement referenceElement;

        // 1
        myFixture
                .getEditor()
                .getCaretModel()
                .moveToLogicalPosition(new LogicalPosition(5, 18));
        reference = myFixture.getReferenceAtCaretPosition();
        assert null != reference;
        reference = ((PsiMultiReference)reference).getReferences()[1];
        assertTrue(reference instanceof RequirejsReference);
        assertEquals("'blocks/block'", reference.getCanonicalText());
        referenceElement = reference.resolve();
        assertTrue(referenceElement instanceof JSFile);
        assertEquals("block.js", ((JSFile) referenceElement).getName());

        // 2
        myFixture
                .getEditor()
                .getCaretModel()
                .moveToLogicalPosition(new LogicalPosition(5, 33));
        reference = myFixture.getReferenceAtCaretPosition();
        assert null != reference;
        reference = ((PsiMultiReference)reference).getReferences()[1];
        assertTrue(reference instanceof RequirejsReference);
        assertEquals("'/blocks/block'", reference.getCanonicalText());
        referenceElement = reference.resolve();
        assertTrue(referenceElement instanceof JSFile);
        assertEquals("block.js", ((JSFile) referenceElement).getName());

        // 3
        myFixture
                .getEditor()
                .getCaretModel()
                .moveToLogicalPosition(new LogicalPosition(5, 51));
        reference = myFixture.getReferenceAtCaretPosition();
        assert null != reference;
        reference = ((PsiMultiReference)reference).getReferences()[1];
        assertTrue(reference instanceof RequirejsReference);
        assertEquals("'./blocks/block'", reference.getCanonicalText());
        referenceElement = reference.resolve();
        assertTrue(referenceElement instanceof JSFile);
        assertEquals("block.js", ((JSFile) referenceElement).getName());
    }

    public void testCompletionWithConfigWithRelativePath()
    {
        myFixture.configureByFiles(
                "public/fileForTestConfigWithRelativePathReference.js",
                "public/sub/mainWithRelativePath.js",
                "public/sub/kits/kit.js"
        );

        Settings.getInstance(getProject()).configFilePath = "sub/mainWithRelativePath.js";

        PsiReference reference;

        // 1
        reference = getReferenceForHumanPosition(2, 35);
        assertReference(reference, "'pathForBlock'", "block.js");

        // 2
        reference = getReferenceForHumanPosition(3, 35);
        assertReference(reference, "'pathForDirectoryTwoDot/block'", "block.js");

        // 3
        reference = getReferenceForHumanPosition(4, 35);
        assertReference(reference, "'pathForDirectoryOneDot/kits/kit'", "kit.js");

        // 4
        reference = getReferenceForHumanPosition(5, 35);
        assertReference(reference, "'pathForKit'", "kit.js");

        // 5
        reference = getReferenceForHumanPosition(6, 35);
        assertReference(reference, "'pathForDirOneDotWithDir/kit'", "kit.js");

        // 6
        reference = getReferenceForHumanPosition(6, 35);
        assertReference(reference, "'pathForNotFound'", null);
    }
}
