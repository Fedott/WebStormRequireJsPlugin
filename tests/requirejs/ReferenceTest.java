package requirejs;

import com.intellij.lang.javascript.psi.JSFile;
import com.intellij.openapi.editor.LogicalPosition;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.impl.source.resolve.reference.impl.PsiMultiReference;

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
}
