package ai.graknlabs.graql.psi.constraint;

import ai.graknlabs.graql.psi.PsiGraqlElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiReference;
import com.intellij.psi.impl.source.resolve.reference.ReferenceProvidersRegistry;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:bfergerson@apache.org">Brandon Fergerson</a>
 */
public class PsiRelatesSuperRoleTypeConstraint extends PsiGraqlElement {

    public PsiRelatesSuperRoleTypeConstraint(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public PsiReference getReference() {
        return ReferenceProvidersRegistry.getReferencesFromProviders(this)[0];
    }
}
