package requirejs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequireMapModule {
    public String module;
    public Map<String, RequirePathAlias> aliases = new HashMap<String, RequirePathAlias>();

    public void addAlias(RequirePathAlias alias) {
        aliases.put(alias.alias, alias);
    }

    public RequirePathAlias getAlias(String aliasString) {
        return aliases.get(aliasString);
    }
}
