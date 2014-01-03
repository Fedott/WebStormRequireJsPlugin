package requirejs;

import com.intellij.testFramework.fixtures.CodeInsightFixtureTestCase;
import requirejs.settings.Settings;

public abstract class RequirejsTestCase extends CodeInsightFixtureTestCase {
    protected void setWebPathSetting() {
        Settings.getInstance(myFixture.getProject()).publicPath = getProject()
                .getBaseDir()
                .getChildren()[0]
                .getName().concat("/public");
    }
}
