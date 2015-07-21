package requirejs;

import com.intellij.openapi.paths.WebReference;
import com.intellij.psi.PsiElement;

public class UriReference extends WebReference {
    public UriReference(PsiElement psiElement) {
        super(psiElement);
    }

    @Override
    protected String getUrl() {
        String value = this.getValue();
        if (value.startsWith("//")) {
            return "http:".concat(value);
        }

        return value;
    }
}
