package requirejs.properties;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class RequirejsSettings implements Configurable {
    public static final String REQUIREJS_WEB_PATH_PROPERTY_NAME = "web_dir";
    public static final String REQUIREJS_FUNCTION_NAME_PROPERTY_NAME = "requirejs_function_name";
    public static final String REQUIREJS_MAIN_JS_FILE_PATH_PROPERTY_NAME = "requirejs_main_js_file_path";
    public static final String REQUIREJS_ENABLE_PLUGIN_PROPERTY_NAME = "requirejs_enable_plugin";
    public static final String DEFAULT_REQUIREJS_FUNCTION_NAME = "require";
    public static final String DEFAULT_WEB_PATH = "webfront/web";
    public static final String DEFAULT_REQUIREJS_MAIN_JS_FILE_PATH = "main.js";
    public static final Boolean DEFAULT_REQUIREJS_ENABLE = true;

    public Project project;
    
    private JCheckBox enablePlugin;
    private JTextField requirejsFunctionNameField;
    private JTextField webPathField;
    private JTextField requirejsMainFileField;
    private JPanel panel;

    public RequirejsSettings(Project project) {
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
        PropertiesComponent properties = PropertiesComponent.getInstance(project);

        enablePlugin.setSelected(properties.getBoolean(REQUIREJS_ENABLE_PLUGIN_PROPERTY_NAME, DEFAULT_REQUIREJS_ENABLE));
        webPathField.setText(properties.getValue(REQUIREJS_WEB_PATH_PROPERTY_NAME, DEFAULT_WEB_PATH));
        requirejsFunctionNameField.setText(properties.getValue(REQUIREJS_FUNCTION_NAME_PROPERTY_NAME, DEFAULT_REQUIREJS_FUNCTION_NAME));
        requirejsMainFileField.setText(properties.getValue(REQUIREJS_MAIN_JS_FILE_PATH_PROPERTY_NAME, DEFAULT_REQUIREJS_MAIN_JS_FILE_PATH));

        return panel;
    }

    @Override
    public boolean isModified() {
        return true;
    }

    @Override
    public void apply() throws ConfigurationException {
        PropertiesComponent properties = PropertiesComponent.getInstance(project);
        properties.setValue(REQUIREJS_ENABLE_PLUGIN_PROPERTY_NAME, Boolean.toString(enablePlugin.isSelected()));
        properties.setValue(REQUIREJS_WEB_PATH_PROPERTY_NAME, webPathField.getText());
        properties.setValue(REQUIREJS_FUNCTION_NAME_PROPERTY_NAME, requirejsFunctionNameField.getText());
        properties.setValue(REQUIREJS_MAIN_JS_FILE_PATH_PROPERTY_NAME, requirejsMainFileField.getText());
    }

    @Override
    public void reset() {

    }

    @Override
    public void disposeUIResources() {

    }
}
