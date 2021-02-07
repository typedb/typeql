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
public class PsiOwnsTypeConstraint extends PsiGraqlElement {

    public PsiOwnsTypeConstraint(@NotNull ASTNode node) {
        super(node);
    }

    public TextRange getOwnsTypeTextRange() {
        return new TextRange(4, 4 + getOwnsType().length());
    }

    public String getOwnsType() {
        if (getLastChild() != null) {
            return getLastChild().getText();
        }
        return null;
    }

    public boolean isAbstractType() {
        return Objects.equals(getOwnsType(), "abstract");
    }

    public boolean isKey() {
        return getFirstChild() != null && Objects.equals(getFirstChild().getText(), "key");
    }

    @Override
    public PsiReference getReference() {
        if (getOwnsType() == null || isAbstractType()) {
            return null;
        }
        return ReferenceProvidersRegistry.getReferencesFromProviders(this)[0];
    }
}
