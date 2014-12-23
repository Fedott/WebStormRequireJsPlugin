package requirejs.settings;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiManager;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import requirejs.RequirejsProjectComponent;

import javax.swing.*;

public class RequirejsSettingsPage implements Configurable {
    protected Project project;
    
    private JCheckBox pluginEnabledCheckbox;
    private JTextField publicPathField;
    private JTextField configFilePathField;
    private JPanel panel;
    private JCheckBox overrideBaseUrlCheckbox;
    private JTextField baseUrlField;

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
        loadSettings();

        return panel;
    }

    @Override
    public boolean isModified() {
        return
                !pluginEnabledCheckbox.isSelected() == getSettings().pluginEnabled
                || !publicPathField.getText().equals(getSettings().publicPath)
                || !configFilePathField.getText().equals(getSettings().configFilePath)
                || !overrideBaseUrlCheckbox.isSelected() == getSettings().overrideBaseUrl
                || !baseUrlField.getText().equals(getSettings().baseUrl);
    }

    @Override
    public void apply() throws ConfigurationException {
        project.getComponent(RequirejsProjectComponent.class).clearParse();
        saveSettings();

        PsiManager.getInstance(project).dropResolveCaches();
    }

    protected void saveSettings() {
        getSettings().pluginEnabled = pluginEnabledCheckbox.isSelected();
        getSettings().publicPath = publicPathField.getText();
        getSettings().configFilePath = configFilePathField.getText();
        getSettings().overrideBaseUrl = overrideBaseUrlCheckbox.isSelected();
        if (getSettings().overrideBaseUrl) {
            getSettings().baseUrl = baseUrlField.getText();
        }

        project.getComponent(RequirejsProjectComponent.class).validateSettings();
    }

    protected void loadSettings() {
        pluginEnabledCheckbox.setSelected(getSettings().pluginEnabled);
        publicPathField.setText(getSettings().publicPath);
        configFilePathField.setText(getSettings().configFilePath);
        overrideBaseUrlCheckbox.setSelected(getSettings().overrideBaseUrl);
        baseUrlField.setText(getSettings().baseUrl);
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
