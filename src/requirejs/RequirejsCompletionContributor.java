package requirejs;

import com.intellij.codeInsight.completion.*;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.lang.javascript.JavascriptLanguage;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.newvfs.impl.VirtualDirectoryImpl;
import com.intellij.openapi.vfs.newvfs.impl.VirtualFileImpl;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
import requirejs.properties.RequirejsSettingsPage;

import java.util.ArrayList;

public class RequirejsCompletionContributor extends CompletionContributor {
    public VirtualFile webDir;

    public RequirejsCompletionContributor() {

        extend(CompletionType.BASIC,
                PlatformPatterns
                        .psiElement()
                        .withLanguage(JavascriptLanguage.INSTANCE),
                new CompletionProvider<CompletionParameters>() {
                    @Override
                    public void addCompletions(@NotNull CompletionParameters completionParameters,
                                               ProcessingContext processingContext,
                                               @NotNull CompletionResultSet completionResultSet
                    ) {
                        if (webDir == null) {
                            PropertiesComponent properties = PropertiesComponent.getInstance();
                            String webDirPrefString = properties.getValue(RequirejsSettingsPage.WEB_PATH_PROPERTY_NAME, RequirejsSettingsPage.DEFAULT_WEB_PATH);
                            webDir = completionParameters
                                    .getOriginalFile()
                                    .getProject()
                                    .getBaseDir()
                                    .findFileByRelativePath(webDirPrefString);
                        }

                        ArrayList<String> files = filterFiles(completionParameters.getPosition());

                        for (int i = 0; i < files.size(); i++) {
                            completionResultSet.addElement(
                                    new RequirejsLookupElement(
                                            files.get(i),
                                            RequirejsInsertHandler.getInstance(),
                                            completionParameters.getPosition()
                                    )
                            );
                        }
                    }
                });
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

        return trueFiles;
    }
}
