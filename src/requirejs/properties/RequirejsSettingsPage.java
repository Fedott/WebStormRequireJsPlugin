package requirejs.properties;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class RequirejsSettingsPage implements Configurable {

    public static final String WEB_PATH_PROPERTY_NAME = "web_dir";
    public static final String REQUIREJS_FUNCTION_NAME_PROPERTY_NAME = "requirejs_function_name";
    public static final String REQUIREJS_MAIN_JS_FILE_PATH = "requirejs_main_js_file_path";
    public static final String DEFAULT_REQUIREJS_FUNCTION_NAME = "require";
    public static final String DEFAULT_WEB_PATH = "webfront/web";
    public static final String DEFAULT_REQUIREJS_MAIN_JS_FILE_PATH = "main.js";


    public Project project;
    protected JTextField webPathTextField;
    protected JTextField requirejsFunctionNameField;
    protected JTextField requirejsMainFileField;

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

        webPathTextField = new JTextField(50);
        JLabel webPathLabel = new JLabel("Require.js web path", JLabel.TRAILING);
        webPathLabel.setLabelFor(webPathTextField);

        webPathTextField.setText(properties.getValue(WEB_PATH_PROPERTY_NAME, DEFAULT_WEB_PATH));

        requirejsFunctionNameField = new JTextField(50);
        JLabel requirejsFunctionNameLabel = new JLabel("Require.js function name", JLabel.TRAILING);
        requirejsFunctionNameLabel.setLabelFor(requirejsFunctionNameField);

        requirejsFunctionNameField.setText(properties.getValue(REQUIREJS_FUNCTION_NAME_PROPERTY_NAME, DEFAULT_REQUIREJS_FUNCTION_NAME));

        requirejsMainFileField = new JTextField(50);
        JLabel requirejsMainFileLabel = new JLabel("Require.js config file path", JLabel.TRAILING);
        requirejsMainFileLabel.setLabelFor(requirejsMainFileField);

        requirejsMainFileField.setText(properties.getValue(REQUIREJS_MAIN_JS_FILE_PATH, DEFAULT_REQUIREJS_MAIN_JS_FILE_PATH));



        JPanel newSettingsPage = new JPanel();
        newSettingsPage.setLayout(new GridBagLayout());
        newSettingsPage.add(
                requirejsFunctionNameLabel,
                new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 0, 5, 0), 0, 0));
        newSettingsPage.add(
                requirejsFunctionNameField,
                new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 0, 5, 5), 0, 0));
        newSettingsPage.add(
                webPathLabel,
                new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 0, 5, 0), 0, 0));
        newSettingsPage.add(
                webPathTextField,
                new GridBagConstraints(1, 1, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 0, 5, 5), 0, 0));
        newSettingsPage.add(
                requirejsMainFileLabel,
                new GridBagConstraints(0, 2, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 0, 5, 0), 0, 0));
        newSettingsPage.add(
                requirejsMainFileField,
                new GridBagConstraints(1, 2, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 0, 5, 5), 0, 0));



        return newSettingsPage;
    }

    @Override
    public boolean isModified() {
        return true;
    }

    @Override
    public void apply() throws ConfigurationException {
        PropertiesComponent properties = PropertiesComponent.getInstance(project);
        properties.setValue(WEB_PATH_PROPERTY_NAME, webPathTextField.getText());
        properties.setValue(REQUIREJS_FUNCTION_NAME_PROPERTY_NAME, requirejsFunctionNameField.getText());
        properties.setValue(REQUIREJS_MAIN_JS_FILE_PATH, requirejsMainFileField.getText());
    }

    @Override
    public void reset() {

    }

    @Override
    public void disposeUIResources() {

    }

    public RequirejsSettingsPage(Project project) {
        this.project = project;
    }


}
