package ai.graknlabs.graql.reference;

import ai.graknlabs.graql.psi.GraqlPsiUtils;
import ai.graknlabs.graql.psi.PsiGraqlElement;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:brandon@srcpl.us">Brandon Fergerson</a>
 */
public class GraqlReference extends PsiReferenceBase<PsiGraqlElement> implements PsiPolyVariantReference {

    private final String identifier;

    public GraqlReference(@NotNull PsiGraqlElement element, @NotNull TextRange textRange,
                          @NotNull String identifier) {
        super(element, textRange);
        this.identifier = identifier;
    }

    @NotNull
    @Override
    public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException {
        return GraqlPsiUtils.setName(myElement, newElementName);
    }

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean incompleteCode) {
        Project project = myElement.getProject();
        List<ResolveResult> results = new ArrayList<>();
        for (PsiGraqlElement identifier : GraqlPsiUtils.findUsages(project, getElement(), identifier)) {
            results.add(new PsiElementResolveResult(identifier));
        }
        return results.toArray(new ResolveResult[0]);
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        return GraqlPsiUtils.findDeclaration(myElement.getProject(), identifier);
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        return new Object[0];
    }
}
