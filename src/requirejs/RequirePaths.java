package requirejs;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RequirePaths {
    public RequirejsProjectComponent component;

    public Map<String, RequirePathAlias> paths = new ConcurrentHashMap<String, RequirePathAlias>();

    public RequirePaths(RequirejsProjectComponent requirejsProjectComponent) {
        component = requirejsProjectComponent;
    }

    public void clear() {
        paths.clear();
    }

    public boolean isEmpty() {
        return paths.isEmpty();
    }

    public void addPath(RequirePathAlias pathAlias) {
        paths.put(pathAlias.alias, pathAlias);
    }

    public PsiElement resolve(Path path) {
        RequirePathAlias fileAlias = paths.get(path.getPath());
        if (null != fileAlias) {
            path.setPath(fileAlias.path);
            return path.resolve();
        }

        PsiElement result;
        for (RequirePathAlias pathAlias : paths.values()) {
            if (path.getPath().startsWith(pathAlias.alias)) {
                path.setPath(path.getPath().replaceFirst(pathAlias.alias, pathAlias.path));
                result = path.resolve();
                if (null != result) {
                    return result;
                }
            }
        }

        return null;
    }

    public List<String> getAllFilesOnPaths() {
        List<String> files = new ArrayList<String>();
        for (RequirePathAlias pathAlias : paths.values()) {
            VirtualFile directory = component.resolvePath(pathAlias.path);
            if (null != directory && directory.isDirectory()) {
                files.addAll(
                        FileUtils.getAllFilesInDirectory(
                                directory,
                                directory.getPath(),
                                pathAlias.alias
                        )
                );
            }
        }

        return files;
    }

    public List<String> getAliasToFiles() {
        List<String> aliases = new ArrayList<String>();
        for (RequirePathAlias pathAlias : paths.values()) {
            VirtualFile directory = component.resolvePath(pathAlias.path);
            if (null != directory && !directory.isDirectory()) {
                aliases.add(pathAlias.alias);
            }
        }

        return aliases;
    }
}
