package requirejs.settings;

import com.intellij.openapi.components.*;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.Nullable;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@State(
        name = "Settings",
        storages = {
                @Storage(id = "default", file = StoragePathMacros.PROJECT_FILE),
                @Storage(id = "dir", file = StoragePathMacros.PROJECT_CONFIG_DIR + "/requirejsPlugin.xml", scheme = StorageScheme.DIRECTORY_BASED)
        }
)

public class Settings implements PersistentStateComponent<Settings>
{
    public static final String DEFAULT_REQUIREJS_FUNCTION_NAME = "require";
    public static final String DEFAULT_WEB_PATH = "webfront/web";
    public static final String DEFAULT_REQUIREJS_MAIN_JS_FILE_PATH = "main.js";
    public static final Boolean DEFAULT_REQUIREJS_ENABLE = true;

    public String requireFunctionName = DEFAULT_REQUIREJS_FUNCTION_NAME;
    public String webPath = DEFAULT_WEB_PATH;
    public String mainJsPath = DEFAULT_REQUIREJS_MAIN_JS_FILE_PATH;
    public boolean pluginEnabled = DEFAULT_REQUIREJS_ENABLE;

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
        return webPath.concat(mainJsPath);
    }
}
