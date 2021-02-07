package ai.graknlabs.graql.psi.constraint;

import ai.graknlabs.graql.psi.PsiGraqlElement;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiReference;
import com.intellij.psi.impl.source.resolve.reference.ReferenceProvidersRegistry;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:bfergerson@apache.org">Brandon Fergerson</a>
 */
public class PsiSubTypeConstraint extends PsiGraqlElement {

    public PsiSubTypeConstraint(@NotNull ASTNode node) {
        super(node);
    }

    public TextRange getSubTypeTextRange() {
        return new TextRange(4, 4 + getSubType().length());
    }

    public String getSubType() {
        if (getLastChild() != null) {
            return getLastChild().getText();
        }
        return null;
    }

    @Override
    public PsiReference getReference() {
        if (getSubType() == null) {
            return null;
        }
        PsiReference[] references = ReferenceProvidersRegistry.getReferencesFromProviders(this);
        return references.length > 0 ? references[0] : null;
    }
}
