package requirejs.settings;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class RequirejsSettingsPage implements Configurable {
    public static final String REQUIREJS_WEB_PATH_PROPERTY_NAME = "web_dir";
    public static final String REQUIREJS_FUNCTION_NAME_PROPERTY_NAME = "requirejs_function_name";
    public static final String REQUIREJS_MAIN_JS_FILE_PATH_PROPERTY_NAME = "requirejs_main_js_file_path";
    public static final String REQUIREJS_ENABLE_PLUGIN_PROPERTY_NAME = "requirejs_enable_plugin";
    public static final String DEFAULT_REQUIREJS_FUNCTION_NAME = "require";
    public static final String DEFAULT_WEB_PATH = "webfront/web";
    public static final String DEFAULT_REQUIREJS_MAIN_JS_FILE_PATH = "main.js";
    public static final Boolean DEFAULT_REQUIREJS_ENABLE = true;

    protected Project project;
    
    private JCheckBox enablePlugin;
    private JTextField requirejsFunctionNameField;
    private JTextField webPathField;
    private JTextField requirejsMainFileField;
    private JPanel panel;

    public RequirejsSettingsPage(@NotNull final Project project) {
        this.project = project;
    }

    @Nls
    @Override
    public String getDisplayName() {
        return "Require.js Plugin";
    }

    @Nullable
    @Override
    public String getHelpTopic() {
        return null;
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        enablePlugin.setSelected(getSettings().pluginEnabled);
        webPathField.setText(getSettings().webPath);
        requirejsFunctionNameField.setText(getSettings().requireFunctionName);
        requirejsMainFileField.setText(getSettings().mainJsPath);

        return panel;
    }

    @Override
    public boolean isModified() {
        return
                !enablePlugin.isSelected() == getSettings().pluginEnabled
                || !webPathField.getText().equals(getSettings().webPath)
                || !requirejsFunctionNameField.getText().equals(getSettings().requireFunctionName)
                || !requirejsMainFileField.getText().equals(getSettings().mainJsPath);
    }

    @Override
    public void apply() throws ConfigurationException {
        loadSettings();
    }

    protected void loadSettings() {
        getSettings().pluginEnabled = enablePlugin.isSelected();
        getSettings().webPath = webPathField.getText();
        getSettings().requireFunctionName = requirejsFunctionNameField.getText();
        getSettings().mainJsPath = requirejsMainFileField.getText();
    }

    @Override
    public void reset() {
        loadSettings();
    }

    @Override
    public void disposeUIResources() {

    }

    protected Settings getSettings() {
        return Settings.getInstance(project);
    }
}
