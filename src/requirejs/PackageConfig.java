package requirejs;

import java.util.ArrayList;
import java.util.List;

public class PackageConfig {
    public String baseUrl;
    public List<Package> packages = new ArrayList<Package>();

    public void clear() {
        baseUrl = null;
        packages.clear();
    }
}

class Package {
    public final static String DEFAULT_MAIN = "main";

    public String name;
    public String location;
    public String main;

    public boolean mainExists = false;
}
