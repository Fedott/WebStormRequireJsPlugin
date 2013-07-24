package requirejs.properties;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class RequirejsSettingsPage implements Configurable {
    Project project;
    protected JTextField webPathTextField;

    @Nls
    @Override
    public String getDisplayName() {
        return "Requirejs";
    }

    @Nullable
    @Override
    public String getHelpTopic() {
        return null;
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

        webPathTextField = new JTextField(50);
        JLabel label = new JLabel("requirejs web path");
        panel.add(label);
        label.setLabelFor(webPathTextField);
        panel.add(webPathTextField);
        panel.add(Box.createHorizontalGlue());

        PropertiesComponent properties = PropertiesComponent.getInstance(project);
        webPathTextField.setText(properties.getValue("web_dir", "webfront/web"));

        return panel;
    }

    @Override
    public boolean isModified() {
        return true;
    }

    @Override
    public void apply() throws ConfigurationException {
        PropertiesComponent properties = PropertiesComponent.getInstance(project);
        properties.setValue("web_dir", webPathTextField.getText());
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
