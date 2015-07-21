package requirejs;

import com.intellij.ide.BrowserUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.FakePsiElement;

public class PsiUriElement extends FakePsiElement {
    protected String originalUri;
    protected PsiElement parent;

    public PsiUriElement(PsiElement parent, String uri) {
        this.originalUri = uri;
        this.parent = parent;
    }

    protected String getNormalizedUri() {
        String normalizedUri = this.originalUri;

        if (normalizedUri.startsWith("/")) {
            normalizedUri = "http:".concat(this.originalUri);
        }

        if (!normalizedUri.endsWith(".js")) {
            normalizedUri = normalizedUri.concat(".js");
        }

        return normalizedUri;
    }

    @Override
    public PsiElement getParent() {
        return this.parent;
    }

    @Override
    public String getName() {
        return this.getNormalizedUri();
    }

    @Override
    public void navigate(boolean b) {
        BrowserUtil.browse(this.getNormalizedUri());
    }

    @Override
    public String getPresentableText() {
        return this.getNormalizedUri();
    }
}
