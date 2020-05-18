package ai.graknlabs.graql.psi.property;

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
public class PsiHasTypeProperty extends PsiGraqlElement {

    public PsiHasTypeProperty(@NotNull ASTNode node) {
        super(node);
    }

    public TextRange getHasTypeTextRange() {
        return new TextRange(4, 4 + getHasType().length());
    }

    public String getHasType() {
        if (getLastChild() != null) {
            return getLastChild().getText();
        }
        return null;
    }

    public boolean isAbstractType() {
        return Objects.equals(getHasType(), "abstract");
    }

    public boolean isKey() {
        return getFirstChild() != null && Objects.equals(getFirstChild().getText(), "key");
    }

    @Override
    public PsiReference getReference() {
        if (getHasType() == null || isAbstractType()) {
            return null;
        }
        return ReferenceProvidersRegistry.getReferencesFromProviders(this)[0];
    }
}
