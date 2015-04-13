package requirejs;

import com.intellij.psi.PsiElement;

public class Path {
    protected PsiElement element;
    protected String originValue;
    protected String path;
    protected String module = null;

    public Path(PsiElement element) {
        this.element = element;

        parse(element.getText());
    }

    public void parse(String rawValue) {
        originValue = rawValue.replace("\"", "").replace("'", "");

        if (originValue.contains("!")) {
            String[] exclamationMarkSplit = originValue.split("!");
            if (exclamationMarkSplit.length == 2) {
                module = exclamationMarkSplit[0];
                path = exclamationMarkSplit[1];
            } else {
                module = exclamationMarkSplit[0];
                path = "";
            }
        } else {
            path = originValue;
        }
    }

    public String getOriginValue() {
        return originValue;
    }

    public String getPath() {
        return path;
    }

    public String getModule() {
        return module;
    }

    public boolean isAbsolutePath() {
        return path.startsWith("/");
    }

    public boolean isRelativePath() {
        return path.startsWith(".");
    }

    public boolean isBuildInVariable() {
        return originValue.equals("exports") || originValue.equals("module") || originValue.equals("require");
    }
}
