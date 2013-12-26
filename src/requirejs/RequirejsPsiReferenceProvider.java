package requirejs;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.lang.javascript.JSElementTypes;
import com.intellij.lang.javascript.JSTokenTypes;
import com.intellij.lang.javascript.psi.JSCallExpression;
import com.intellij.lang.javascript.psi.impl.JSFileImpl;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.tree.TreeElement;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
import requirejs.settings.RequirejsSettingsPage;
import requirejs.settings.Settings;

import java.util.HashMap;

public class RequirejsPsiReferenceProvider extends PsiReferenceProvider {

    protected Project project;
    public Settings settings;

    @NotNull
    @Override
    public PsiReference[] getReferencesByElement(@NotNull PsiElement psiElement, @NotNull ProcessingContext processingContext) {
        RequirejsProjectComponent projectComponent = psiElement.getProject().getComponent(RequirejsProjectComponent.class);

        if (!projectComponent.isEnabled()) {
            return PsiReference.EMPTY_ARRAY;
        }

        try {
            String path = psiElement.getText();
            if (isRequireCall(psiElement)) {
                PsiReference ref = new RequirejsReference(psiElement, new TextRange(1, path.length() - 1));
                return new PsiReference[] {ref};
            }
        } catch (Exception ignored) {}

        return new PsiReference[0];
    }

    public static boolean isRequireCall(PsiElement element) {
        PsiElement prevEl = element.getParent();
        if (prevEl != null) {
            prevEl = prevEl.getParent();
        }

        if (prevEl instanceof JSCallExpression) {
            if (prevEl.getChildren().length > 1) {
                String requireFunctionName = Settings
                        .getInstance(element.getProject())
                        .requireFunctionName;
                if (prevEl.getChildren()[0].getText().toLowerCase().equals(requireFunctionName)) {
                    return true;
                }
            }
        }

        return false;
    }

}
