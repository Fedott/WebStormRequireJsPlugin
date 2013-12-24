package requirejs;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.testFramework.fixtures.CodeInsightFixtureTestCase;
import requirejs.properties.RequirejsSettings;

public abstract class RequirejsTestCase extends CodeInsightFixtureTestCase {
    protected void setWebPathSetting() {
        PropertiesComponent props = PropertiesComponent.getInstance(myFixture.getProject());
        props.setValue(RequirejsSettings.REQUIREJS_WEB_PATH_PROPERTY_NAME, getProject().getBaseDir().getChildren()[0].getName() + "/public");
    }
}
