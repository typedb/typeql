package ai.graknlabs.graql.psi.property;

import ai.graknlabs.graql.psi.PsiGraqlElement;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiReference;
import com.intellij.psi.impl.source.resolve.reference.ReferenceProvidersRegistry;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * @author <a href="mailto:brandon@srcpl.us">Brandon Fergerson</a>
 */
public class PsiPlaysTypeProperty extends PsiGraqlElement {

    public PsiPlaysTypeProperty(@NotNull ASTNode node) {
        super(node);
    }

    public TextRange getPlaysTypeTextRange() {
        return new TextRange(6, 6 + getPlaysType().length());
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
