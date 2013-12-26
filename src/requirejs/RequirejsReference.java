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

    public RequirejsReference(PsiElement element, TextRange textRange) {
        this.element = element;
        this.textRange = textRange;
    }

    @Override
    public PsiElement getElement() {
        return this.element;
    }

    protected VirtualFile getWebDir() {
        return element.getProject().getComponent(RequirejsProjectComponent.class).getWebDir();
    }

    protected boolean isSettingsValid() {
        return element.getProject().getComponent(RequirejsProjectComponent.class).isSettingsValid();
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        if (!isSettingsValid()) {
            return null;
        }

        String path = element.getText();
        path = path.replace("'", "").replace("\"", "");
        if (path.startsWith("tpl!")) {
            path = path.replace("tpl!", "");
        } else {
            path = path.concat(".js");
        }

        VirtualFile webDir = getWebDir();

        String filePath = element
                .getContainingFile()
                .getVirtualFile()
                .getParent()
                .getPath()
                .replace(webDir.getPath().concat("/"), "");

        if (path.startsWith("./")) {
            path = path.replaceFirst(
                    ".",
                    filePath
            );
        }
        VirtualFile targetFile = webDir.findFileByRelativePath(path);

        if (targetFile != null) {
            return PsiManager.getInstance(element.getProject()).findFile(targetFile);
        }

        if (path.startsWith("..")) {
            Integer doubleDotCount = getDoubleDotCount(path);
            String[] pathsOfPath = filePath.split("/");
            if (pathsOfPath.length > 0) {
                if (doubleDotCount > 0) {
                    if (doubleDotCount <= pathsOfPath.length) {
                        String pathOnDots = getNormalizedPath(doubleDotCount, pathsOfPath);
                        targetFile = webDir.findFileByRelativePath(
                                path.replace(StringUtil.repeat("../", doubleDotCount), pathOnDots.concat("/"))
                        );

                        if (targetFile != null) {
                            return PsiManager.getInstance(element.getProject()).findFile(targetFile);
                        }
                    }
                }
            }
        }

//        if (RequirejsPsiReferenceProvider.requirejsConfigAliasesMap.containsKey(path)) {
//            return PsiManager
//                    .getInstance(element.getProject())
//                    .findFile(RequirejsPsiReferenceProvider.requirejsConfigAliasesMap.get(path));
//        }

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
        ArrayList<LookupElement> completionResultSet = new ArrayList<LookupElement>();

        if (! isSettingsValid()) {
            return completionResultSet.toArray();
        }

        ArrayList<String> files = filterFiles(element);

        for (String file : files) {
            completionResultSet.add(
                    LookupElementBuilder
                            .create(element, file)
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
            for (VirtualFile children : childrens) {
                if (children instanceof VirtualDirectoryImpl) {
                    files.addAll(getAllFilesInDirectory(children));
                } else if (children instanceof VirtualFileImpl) {
                    files.add(children.getPath().replace(getWebDir().getPath() + "/", ""));
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
        Boolean notEndSlash = false;
        String pathOnDots = "";
        String dotString = "";
        String filePath = element
                .getContainingFile()
                .getOriginalFile()
                .getVirtualFile()
                .getParent()
                .getPath()
                .replace(getWebDir().getPath().concat("/"), "");
        
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
            doubleDotCount = getDoubleDotCount(valuePath);
            String[] pathsOfPath = filePath.split("/");
            if (pathsOfPath.length > 0) {
                if (doubleDotCount > 0) {
                    if (doubleDotCount > pathsOfPath.length) {
                        return new ArrayList<String>();
                    }
                    pathOnDots = getNormalizedPath(doubleDotCount, pathsOfPath);
                    dotString = StringUtil.repeat("../", doubleDotCount);
                    if (valuePath.endsWith("..")) {
                        notEndSlash = true;
                    }
                    if (valuePath.endsWith("..") || !StringUtil.isEmpty(pathOnDots)) {
                        dotString = dotString.substring(0, dotString.length() - 1);
                    }
                    valuePath = valuePath.replace(dotString, pathOnDots);
                }
            }
        }

        ArrayList<String> allFiles = getAllFilesInDirectory(getWebDir());
        ArrayList<String> trueFiles = new ArrayList<String>();

        String file;

        for (String allFile : allFiles) {
            file = allFile;

            if (file.startsWith(valuePath)) {
                // Prepare file path
                if (oneDot) {
                    file = file.replaceFirst(filePath, ".");
                }

                if (doubleDotCount > 0) {
                    if (!StringUtil.isEmpty(valuePath)) {
                        file = file.replace(pathOnDots, "");
                    }
                    if (notEndSlash) {
                        file = "/".concat(file);
                    }
                    file = dotString.concat(file);
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

    protected String getNormalizedPath(Integer doubleDotCount, String[] pathsOfPath) {
        StringBuilder newValuePath = new StringBuilder();
        for (int i = 0; i < pathsOfPath.length - doubleDotCount; i++) {
            if (0 != i) {
                newValuePath.append("/");
            }
            newValuePath.append(pathsOfPath[i]);
        }
        return newValuePath.toString();
    }

    protected Integer getDoubleDotCount(String valuePath) {
        Integer doubleDotCount = (valuePath.length() - valuePath.replaceAll("\\.\\.", "").length()) / 2;

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
        return doubleDotCount;
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
