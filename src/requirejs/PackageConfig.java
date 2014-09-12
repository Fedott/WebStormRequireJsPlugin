package requirejs;

import com.intellij.openapi.vfs.VirtualFile;

import java.util.ArrayList;
import java.util.List;

public class PackageConfig {
    public String baseUrl;
    RequirejsProjectComponent component;
    public List<Package> packages = new ArrayList<Package>();

    public PackageConfig(RequirejsProjectComponent requirejsProjectComponent) {
        component = requirejsProjectComponent;
    }

    public void clear() {
        baseUrl = null;
        packages.clear();
    }

    public List<String> getAllFilesOnPackages() {
        List<String> files = new ArrayList<String>();
        for(Package pack: packages) {
            VirtualFile directory = component.resolvePath(pack.location);
            if (directory != null && directory.isDirectory()) {
                List<String> packageFiles = new ArrayList<String>();
                packageFiles.addAll(
                        FileUtils.getAllFilesInDirectory(
                                directory,
                                directory.getPath(),
                                pack.name
                        )
                );
                packageFiles.remove(pack.name + '/' + pack.main + ".js");
                files.addAll(packageFiles);
            }
        }

        return files;
    }
}

class Package {
    public final static String DEFAULT_MAIN = "main";

    public String name;
    public String location;
    public String main;

    public boolean mainExists = false;
}
