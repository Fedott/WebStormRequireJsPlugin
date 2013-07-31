package requirejs;

import com.intellij.lang.javascript.JavascriptLanguage;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public final class RequirejsType extends IElementType {
    public static final RequirejsType DEFAULT_NAME = new RequirejsType("REQUIREJS_PATH_TO_FILE");

    public RequirejsType(@NotNull @NonNls String debugName) {
        super(debugName, JavascriptLanguage.INSTANCE);
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
