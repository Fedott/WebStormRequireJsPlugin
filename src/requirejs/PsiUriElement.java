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
        if (this.originalUri.startsWith("/")) {
            return "http:".concat(this.originalUri);
        }

        return this.originalUri;
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
