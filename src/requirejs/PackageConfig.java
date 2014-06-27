package requirejs;

import java.util.ArrayList;
import java.util.List;

public class PackageConfig {
    public String baseUrl;
    public List<Package> packages = new ArrayList<Package>();
}

class Package {
    public String name;
    public String location;
    public String main;
}
