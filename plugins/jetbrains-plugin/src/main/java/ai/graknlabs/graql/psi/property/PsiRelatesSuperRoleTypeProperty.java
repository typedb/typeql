package ai.graknlabs.graql.psi.property;

import ai.graknlabs.graql.psi.PsiGraqlElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiReference;
import com.intellij.psi.impl.source.resolve.reference.ReferenceProvidersRegistry;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:brandon@srcpl.us">Brandon Fergerson</a>
 */
public class PsiRelatesSuperRoleTypeProperty extends PsiGraqlElement {

    public PsiRelatesSuperRoleTypeProperty(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public PsiReference getReference() {
        return ReferenceProvidersRegistry.getReferencesFromProviders(this)[0];
    }
}
