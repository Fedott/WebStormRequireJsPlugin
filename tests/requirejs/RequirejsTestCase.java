package requirejs;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.testFramework.fixtures.CodeInsightFixtureTestCase;
import requirejs.settings.RequirejsSettingsPage;
import requirejs.settings.Settings;

public abstract class RequirejsTestCase extends CodeInsightFixtureTestCase {
    protected void setWebPathSetting() {
        Settings.getInstance(myFixture.getProject()).webPath = getProject()
                .getBaseDir()
                .getChildren()[0]
                .getName().concat("/public");
    }
}
