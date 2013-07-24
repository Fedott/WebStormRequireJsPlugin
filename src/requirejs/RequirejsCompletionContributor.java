package requirejs;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.lang.Language;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.newvfs.impl.VirtualDirectoryImpl;
import com.intellij.openapi.vfs.newvfs.impl.VirtualFileImpl;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class RequirejsCompletionContributor extends CompletionContributor {
    VirtualFile webDir;
    public RequirejsCompletionContributor() {

        extend(CompletionType.BASIC,
                PlatformPatterns.psiFile().withLanguage(Language.findLanguageByID("JavaScript")),
                new CompletionProvider<CompletionParameters>() {
                    @Override
                    public void addCompletions(@NotNull CompletionParameters completionParameters,
                                               ProcessingContext processingContext,
                                               @NotNull CompletionResultSet completionResultSet
                    ) {
                        if (webDir == null) {
                            PropertiesComponent properties = PropertiesComponent.getInstance();
                            String webDirPrefString = properties.getValue("web_dir", "webfront/web");
                            VirtualFile webDir = completionParameters
                                    .getOriginalFile()
                                    .getProject()
                                    .getBaseDir()
                                    .findFileByRelativePath(webDirPrefString);
                        }

                        ArrayList<String> files = getAllFilesInDirectory(webDir);

                        for (int i = 0; i < files.size(); i++) {
                            completionResultSet.addElement(LookupElementBuilder.create(files.get(i)));
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
                    files.addAll(getAllFilesInDirectory((VirtualDirectoryImpl) childrens[i]));
                } else if (childrens[i] instanceof VirtualFileImpl) {
                    files.add(childrens[i].getPath().replace(webDir.getPath() + "/", ""));
                }
            }
        }

        return files;
    }
}
