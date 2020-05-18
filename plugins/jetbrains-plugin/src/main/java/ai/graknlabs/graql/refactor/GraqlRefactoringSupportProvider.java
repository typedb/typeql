package ai.graknlabs.graql.refactor;

import ai.graknlabs.graql.psi.PsiGraqlNamedElement;
import com.intellij.lang.refactoring.RefactoringSupportProvider;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:bfergerson@apache.org">Brandon Fergerson</a>
 */
public class GraqlRefactoringSupportProvider extends RefactoringSupportProvider {

    @Override
    public boolean isAvailable(@NotNull PsiElement context) {
        return context instanceof PsiGraqlNamedElement;
    }

    @Override
    public boolean isMemberInplaceRenameAvailable(@NotNull PsiElement element, PsiElement context) {
        return element instanceof PsiGraqlNamedElement;
    }
}