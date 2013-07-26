package requirejs;

import com.intellij.lang.Language;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class RequirejsType extends IElementType {
    public static final RequirejsType DEFAULT_NAME = new RequirejsType("REQUIRE_NAME_TOKEN");

    public RequirejsType(@NotNull @NonNls String debugName) {
        super(debugName, Language.ANY);
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
