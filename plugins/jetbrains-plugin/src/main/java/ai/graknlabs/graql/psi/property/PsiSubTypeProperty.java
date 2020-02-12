package ai.graknlabs.graql.psi.property;

import ai.graknlabs.graql.psi.PsiGraqlElement;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiReference;
import com.intellij.psi.impl.source.resolve.reference.ReferenceProvidersRegistry;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:brandon@srcpl.us">Brandon Fergerson</a>
 */
public class PsiSubTypeProperty extends PsiGraqlElement {

    public PsiSubTypeProperty(@NotNull ASTNode node) {
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
        return ReferenceProvidersRegistry.getReferencesFromProviders(this)[0];
    }
}
