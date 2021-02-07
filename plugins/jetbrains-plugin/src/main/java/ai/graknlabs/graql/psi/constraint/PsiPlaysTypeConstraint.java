package ai.graknlabs.graql.psi.constraint;

import ai.graknlabs.graql.psi.PsiGraqlElement;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiReference;
import com.intellij.psi.impl.source.resolve.reference.ReferenceProvidersRegistry;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * @author <a href="mailto:bfergerson@apache.org">Brandon Fergerson</a>
 */
public class PsiPlaysTypeConstraint extends PsiGraqlElement {

    public PsiPlaysTypeConstraint(@NotNull ASTNode node) {
        super(node);
    }

    public TextRange getPlaysTypeTextRange() {
        int scopeIndex = getText().indexOf(":") + 1;
        return new TextRange(scopeIndex, 6 + getPlaysType().length());
    }

    public String getPlaysType() {
        if (getLastChild() != null) {
            return getLastChild().getText();
        }
        return null;
    }

    public boolean isAbstractType() {
        return Objects.equals(getPlaysType(), "abstract");
    }

    @Override
    public PsiReference getReference() {
        if (getPlaysType() == null || isAbstractType()) {
            return null;
        }
        return ReferenceProvidersRegistry.getReferencesFromProviders(this)[0];
    }
}
