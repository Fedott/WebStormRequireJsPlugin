package requirejs;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class RequireMapModule {
    public String module;
    public Map<String, RequirePathAlias> aliases = new HashMap<String, RequirePathAlias>();

    public void addAlias(RequirePathAlias alias) {
        aliases.put(alias.alias, alias);
    }

    public RequirePathAlias getAlias(String aliasString) {
        return aliases.get(aliasString);
    }

    public Set<String> getAliases() {
        return aliases.keySet();
    }
}
