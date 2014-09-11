package requirejs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequireMap {
    public Map<String, RequireMapModule> modules = new HashMap<String, RequireMapModule>();

    public void clear() {
        modules.clear();
    }

    public void addModule(RequireMapModule module) {
        this.modules.put(module.module, module);
    }

    public RequirePathAlias getAliasByModule(String moduleName, String aliasString) {
        RequirePathAlias alias = null;
        if (modules.containsKey(moduleName)) {
            alias = modules.get(moduleName).getAlias(aliasString);
        }
        if (null == alias && modules.containsKey("*")) {
            alias = modules.get("*").getAlias(aliasString);
        }

        return alias;
    }

    public List<String> getCompletionByModule(String moduleName) {
        List<String> completions = new ArrayList<String>();

        if (modules.containsKey(moduleName)) {
            completions.addAll(modules.get(moduleName).getAliases());
        }

        if (modules.containsKey("*")) {
            completions.addAll(modules.get("*").getAliases());
        }

        return completions;
    }
}
