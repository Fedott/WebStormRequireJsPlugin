package requirejs;

import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.newvfs.impl.VirtualDirectoryImpl;
import com.intellij.openapi.vfs.newvfs.impl.VirtualFileImpl;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.PsiNonJavaFileReferenceProcessor;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class RequirejsReference implements PsiReference {
    PsiElement element;
    TextRange textRange;
    Project project;
    VirtualFile webDir;

    public RequirejsReference(PsiElement element, TextRange textRange, Project project, VirtualFile webDir) {
        this.element = element;
        this.textRange = textRange;
        this.project = project;
        this.webDir = webDir;
    }

    @Override
    public PsiElement getElement() {
        return this.element;
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        String path = element.getText();
        path = path.replace("'", "").replace("\"", "");
        if (path.startsWith("tpl!")) {
            path = path.replace("tpl!", "");
        } else {
            path = path.concat(".js");
        }
        VirtualFile targetFile = webDir.findFileByRelativePath(path);

        if (targetFile != null) {
            return PsiManager.getInstance(project).findFile(targetFile);
        }

        return null;
    }

    @Override
    public String toString() {
        return getCanonicalText();
    }

    @Override
    public boolean isSoft() {
        return false;
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        String value = element.getText().replace("'", "").replace("\"", "").replace("IntellijIdeaRulezzz ", "");
        Boolean tpl = value.startsWith("tpl!");
        String valuePath = value.replaceFirst("tpl!", "");

        ArrayList<String> allFiles = getAllFilesInDirectory(webDir);
        ArrayList<String> trueFiles = new ArrayList<String>();

        String file;

        for (int i = 0; i < allFiles.size(); i++) {
            file = allFiles.get(i);
            if (file.startsWith(valuePath)) {
                if (tpl && file.endsWith(".html")) {
                    trueFiles.add("tpl!" + file);
                } else if (file.endsWith(".js")) {
                    trueFiles.add(file.replace(".js", ""));
                }
            }
        }

        return trueFiles.toArray();
    }

    protected ArrayList<String> getAllFilesInDirectory(VirtualFile directory) {
        ArrayList<String> files = new ArrayList<String>();

        VirtualFile[] childrens = directory.getChildren();
        if (childrens.length != 0) {
            for (int i = 0; i < childrens.length; i++) {
                if (childrens[i] instanceof VirtualDirectoryImpl) {
                    files.addAll(getAllFilesInDirectory(childrens[i]));
                } else if (childrens[i] instanceof VirtualFileImpl) {
                    files.add(childrens[i].getPath().replace(webDir.getPath() + "/", ""));
                }
            }
        }

        return files;
    }

    @Override
    public boolean isReferenceTo(PsiElement psiElement) {
        return false;
    }

    @Override
    public PsiElement bindToElement(@NotNull PsiElement psiElement) throws IncorrectOperationException {
        throw new IncorrectOperationException();
    }

    @Override
    public PsiElement handleElementRename(String s) throws IncorrectOperationException {
        throw new IncorrectOperationException();
    }

    @Override
    public TextRange getRangeInElement() {
        return textRange;
    }

    @NotNull
    @Override
    public String getCanonicalText() {
        return element.getText();
    }
}
