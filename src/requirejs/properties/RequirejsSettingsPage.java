package requirejs.properties;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class RequirejsSettingsPage implements Configurable {

    public static final String WEB_PATH_PROPERTY_NAME = "web_dir";
    public static final String REQUIREJS_FUNCTION_NAME_PROPERTY_NAME = "requirejs_function_name";
    public static final String DEFAULT_REQUIREJS_FUNCTION_NAME = "require";
    public static final String DEFAULT_WEB_PATH = "webfront/web";

    public Project project;
    protected JTextField webPathTextField;
    protected JTextField requirejsFunctionNameField;

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

        JPanel webPathLine = new JPanel();
        webPathLine.setLayout(new BoxLayout(webPathLine, BoxLayout.X_AXIS));

        webPathTextField = new JTextField(100);
        JLabel webPathLabel = new JLabel("Require.js web path");
        webPathLine.add(webPathLabel);
        webPathLabel.setLabelFor(webPathTextField);
        webPathLine.add(webPathTextField);
        webPathLine.add(Box.createHorizontalGlue());

        webPathTextField.setText(properties.getValue(WEB_PATH_PROPERTY_NAME, DEFAULT_WEB_PATH));

        requirejsFunctionNameField = new JTextField(50);
        JLabel requirejsFunctionNameLabel = new JLabel("require function name");
        requirejsFunctionNameLabel.setLabelFor(requirejsFunctionNameField);
        JPanel requirejsFunctionNameLine = new JPanel();
        requirejsFunctionNameLine.setLayout(new BoxLayout(requirejsFunctionNameLine, BoxLayout.X_AXIS));
        requirejsFunctionNameLine.add(requirejsFunctionNameLabel);
        requirejsFunctionNameLine.add(requirejsFunctionNameField);
        requirejsFunctionNameLine.add(Box.createHorizontalGlue());

        requirejsFunctionNameField.setText(properties.getValue(REQUIREJS_FUNCTION_NAME_PROPERTY_NAME, DEFAULT_REQUIREJS_FUNCTION_NAME));

        JPanel settingsPage = new JPanel();
        settingsPage.setLayout(new BoxLayout(settingsPage, BoxLayout.Y_AXIS));
        settingsPage.add(requirejsFunctionNameLine);
        settingsPage.add(Box.createVerticalStrut(8));
        settingsPage.add(webPathLine);
        settingsPage.add(Box.createVerticalGlue());

        return settingsPage;
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
