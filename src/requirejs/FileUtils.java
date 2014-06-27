package requirejs;

import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.newvfs.impl.VirtualDirectoryImpl;
import com.intellij.openapi.vfs.newvfs.impl.VirtualFileImpl;

import java.util.ArrayList;
import java.util.List;

public final class FileUtils {
    private FileUtils() {
    }

    public static String join(String file, String ext) {
        return file + '/' + ext;
    }

    public static String removeExt(String file, String ext) {
        if (file.endsWith(ext)) {
            return file.replace(ext, "");
        }
        return file;
    }

    public static String relativePath(VirtualFile root, VirtualFile file) {
        // get project relative path
        return file.getPath().substring(root.getPath().length() + 1);
    }

    public static VirtualFile findFileByPath(VirtualFile path, String valuePath) {
        VirtualFile file = path.findFileByRelativePath(valuePath);
        if (null == file || file.isDirectory()) {
            file = path.findFileByRelativePath(valuePath + ".js");
        }
        return file;
    }

    public static String getNormalizedPath(int doubleDotCount, String[] pathsOfPath) {
        StringBuilder newValuePath = new StringBuilder();
        for (int i = 0; i < pathsOfPath.length - doubleDotCount; i++) {
            if (0 != i) {
                newValuePath.append('/');
            }
            newValuePath.append(pathsOfPath[i]);
        }
        return newValuePath.toString();
    }

    public static int getDoubleDotCount(String valuePath) {
        int doubleDotCount = (valuePath.length() - valuePath.replaceAll("\\.\\.", "").length()) / 2;
        boolean doubleDotCountTrues = false;

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

    public static List<String> getAllFilesInDirectory(VirtualFile directory, String target, String replacement) {
        List<String> files = new ArrayList<String>();
        VirtualFile[] children = directory.getChildren();
        for (VirtualFile child : children) {
            if (child instanceof VirtualDirectoryImpl) {
                files.addAll(getAllFilesInDirectory(child, target, replacement));
            } else if (child instanceof VirtualFileImpl) {
                files.add(child.getPath().replace(target, replacement));
            }
        }
        return files;
    }
}
