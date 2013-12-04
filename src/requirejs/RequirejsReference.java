package requirejs;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.newvfs.impl.VirtualDirectoryImpl;
import com.intellij.openapi.vfs.newvfs.impl.VirtualFileImpl;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiReference;
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
        if (path.startsWith("./")) {
            path = path.replaceFirst(
                    ".",
                    element
                            .getContainingFile()
                            .getVirtualFile()
                            .getParent()
                            .getPath()
                            .replace(webDir.getPath(), "")
            );
        }
        VirtualFile targetFile = webDir.findFileByRelativePath(path);

        if (targetFile != null) {
            return PsiManager.getInstance(project).findFile(targetFile);
        }

        if (RequirejsPsiReferenceProvider.requirejsConfigAliasesMap.containsKey(path)) {
            return PsiManager
                    .getInstance(project)
                    .findFile(RequirejsPsiReferenceProvider.requirejsConfigAliasesMap.get(path));
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
        ArrayList<String> files = filterFiles(element);

        ArrayList<LookupElement> completionResultSet = new ArrayList<LookupElement>();

        for (int i = 0; i < files.size(); i++) {
            completionResultSet.add(
                    LookupElementBuilder
                            .create(element, files.get(i))
                            .withInsertHandler(
                                    RequirejsInsertHandler.getInstance()
                            )
            );
        }

        return completionResultSet.toArray();
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

    protected ArrayList<String> filterFiles (PsiElement element) {
        String value = element.getText().replace("'", "").replace("\"", "").replace("IntellijIdeaRulezzz ", "");
        Boolean tpl = value.startsWith("tpl!");
        String valuePath = value.replaceFirst("tpl!", "");
        Boolean oneDot = false;
        Integer doubleDotCount = 0;
        String filePath = element
                .getContainingFile()
                .getOriginalFile()
                .getVirtualFile()
                .getParent()
                .getPath()
                .replace(webDir.getPath().concat("/"), "");
        
        if (valuePath.startsWith("./")) {
            oneDot = true;
            try {
                valuePath = valuePath
                        .replaceFirst(
                                ".",
                                filePath
                        );
            } catch (NullPointerException ignored) {}
        }

        if (valuePath.startsWith("..")) {
            doubleDotCount = (valuePath.length() - valuePath.replaceAll("\\.\\.", "").length()) / 2;
            String[] pathsOfPath = filePath.split("/");
            if (pathsOfPath.length > 0) {
                Boolean doubleDotCountTrues = false;

                while (!doubleDotCountTrues && 0 != doubleDotCount) {
                    if (valuePath.startsWith(StringUtil.repeat("../", doubleDotCount))) {
                        doubleDotCountTrues = true;
                    } else if (valuePath.startsWith(StringUtil.repeat("../", doubleDotCount - 1) + "..")) {
                        doubleDotCountTrues = true;
                    } else {
                        doubleDotCount--;
                    }
                }

                if (doubleDotCount > 0) {
                    if (doubleDotCount > pathsOfPath.length) {
                        return new ArrayList<String>();
                    }
                    StringBuilder newValuePath = new StringBuilder();
                    for (int i = 0; i < pathsOfPath.length - doubleDotCount; i++) {
                        if (0 != i) {
                            newValuePath.append("/");
                        }
                        newValuePath.append(pathsOfPath[i]);
                    }
                    valuePath = newValuePath.toString();
                }
            }
        }

        ArrayList<String> allFiles = getAllFilesInDirectory(webDir);
        ArrayList<String> trueFiles = new ArrayList<String>();

        String file;

        for (int i = 0; i < allFiles.size(); i++) {
            file = allFiles.get(i);

            if (file.startsWith(valuePath)) {
                // Prepare file path
                if (oneDot) {
                    file = file.replace(valuePath, "./");
                }

                if (doubleDotCount > 0) {
                    if (!StringUtil.isEmpty(valuePath)) {
                        file = file.replace(valuePath + "/", "");
                    }
                    file = StringUtil.repeat("../", doubleDotCount) + file;
                }

                if (tpl && file.endsWith(".html")) {
                    trueFiles.add("tpl!" + file);
                } else if (file.endsWith(".js")) {
                    trueFiles.add(file.replace(".js", ""));
                }
            }
        }

        return trueFiles;
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
