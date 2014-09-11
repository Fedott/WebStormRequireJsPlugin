package requirejs;

import com.intellij.openapi.vfs.VirtualFile;

import java.util.HashMap;
import java.util.Map;

public class RequirePaths {
    public RequirejsProjectComponent component;

    public Map<String, RequirePathAlias> paths = new HashMap<String, RequirePathAlias>();

    public RequirePaths(RequirejsProjectComponent requirejsProjectComponent) {
        component = requirejsProjectComponent;
    }

    public void clear() {
        paths.clear();
    }

    public void addPath(RequirePathAlias pathAlias) {
        paths.put(pathAlias.alias, pathAlias);
    }

    public VirtualFile resolve(String path) {
        RequirePathAlias fileAlias = paths.get(path);
        if (null != fileAlias) {
            VirtualFile file = component.resolvePath(fileAlias.path);
            if (null != file && !file.isDirectory()) {
                return file;
            } else {
                return null;
            }
        }

        for (RequirePathAlias pathAlias : paths.values()) {
            if (path.startsWith(pathAlias.alias)) {
                VirtualFile directory = component.resolvePath(pathAlias.path);
                if (directory.isDirectory()) {
                    VirtualFile targetFile = FileUtils.findFileByPath(directory, path.replaceFirst(pathAlias.alias, ""));
                    if (null != targetFile) {
                        return targetFile;
                    }
                }
            }
        }

        return null;
    }
}
