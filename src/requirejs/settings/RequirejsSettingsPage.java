package requirejs.settings;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceContributor;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import requirejs.RequirejsProjectComponent;

import javax.swing.*;

public class RequirejsSettingsPage implements Configurable {
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
        loadSettings();

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
        saveSettings();
        PsiManager.getInstance(project).dropResolveCaches();
    }

    protected void saveSettings() {
        getSettings().pluginEnabled = enablePlugin.isSelected();
        getSettings().webPath = webPathField.getText();
        getSettings().requireFunctionName = requirejsFunctionNameField.getText();
        getSettings().mainJsPath = requirejsMainFileField.getText();

        project.getComponent(RequirejsProjectComponent.class).validateSettings();
    }

    protected void loadSettings() {
        enablePlugin.setSelected(getSettings().pluginEnabled);
        webPathField.setText(getSettings().webPath);
        requirejsFunctionNameField.setText(getSettings().requireFunctionName);
        requirejsMainFileField.setText(getSettings().mainJsPath);
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
