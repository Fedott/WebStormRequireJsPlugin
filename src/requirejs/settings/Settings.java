package requirejs.settings;

import com.intellij.openapi.components.*;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.Nullable;

@State(
        name = "RequirejsProjectComponent",
        storages = {
                @Storage(id = "default", file = StoragePathMacros.PROJECT_FILE),
                @Storage(id = "dir", file = StoragePathMacros.PROJECT_CONFIG_DIR + "/requirejsPlugin.xml", scheme = StorageScheme.DIRECTORY_BASED)
        }
)

public class Settings implements PersistentStateComponent<Settings>
{
    public static final String REQUIREJS_REQUIRE_FUNCTION_NAME = "require";
    public static final String REQUIREJS_DEFINE_FUNCTION_NAME = "define";
    public static final String DEFAULT_PUBLIC_PATH = "public";
    public static final String DEFAULT_CONFIG_FILE_PATH = "main.js";
    public static final Boolean DEFAULT_PLUGIN_ENABLED = false;
    public String publicPath = DEFAULT_PUBLIC_PATH;
    public String configFilePath = DEFAULT_CONFIG_FILE_PATH;
    public boolean pluginEnabled = DEFAULT_PLUGIN_ENABLED;

    protected Project project;

    public static Settings getInstance(Project project)
    {
        Settings settings = ServiceManager.getService(project, Settings.class);

        settings.project = project;

        return settings;
    }

    @Nullable
    @Override
    public Settings getState() {
        return this;
    }

    @Override
    public void loadState(Settings state) {
        XmlSerializerUtil.copyBean(state, this);
    }

    public String getVersion() {
        return publicPath.concat(configFilePath);
    }
}
