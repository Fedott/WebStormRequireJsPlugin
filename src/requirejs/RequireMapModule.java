package requirejs;

import java.util.*;

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
