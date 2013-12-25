package requirejs;

import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import requirejs.settings.Settings;

public class RequirejsProjectComponent implements ProjectComponent
{
    private Project project;

    final private static Logger LOG = Logger.getInstance("Requirejs-Plugin");

    public RequirejsProjectComponent(Project project) {
        this.project = project;
    }

    @Override
    public void projectOpened() {

    }

    @Override
    public void projectClosed() {

    }

    @Override
    public void initComponent() {

    }

    @Override
    public void disposeComponent() {

    }

    @NotNull
    @Override
    public String getComponentName() {
        return "RequirejsProjectComponent";
    }

    public static Logger getLogger() {
        return LOG;
    }

    public boolean isEnabled() {
        return Settings.getInstance(project).pluginEnabled;
    }
}
