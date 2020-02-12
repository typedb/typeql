package ai.graknlabs.graql.psi;

import ai.graknlabs.graql.psi.property.PsiHasTypeProperty;
import ai.graknlabs.graql.psi.property.PsiPlaysTypeProperty;
import ai.graknlabs.graql.psi.property.PsiSubTypeProperty;
import com.intellij.lang.ASTNode;
import org.antlr.intellij.adaptor.psi.ANTLRPsiNode;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:brandon@srcpl.us">Brandon Fergerson</a>
 */
public abstract class PsiGraqlElement extends ANTLRPsiNode {

    public PsiGraqlElement(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public String getName() {
        if (this instanceof PsiSubTypeProperty) {
            return ((PsiSubTypeProperty) this).getSubType();
        } else if (this instanceof PsiPlaysTypeProperty) {
            return ((PsiPlaysTypeProperty) this).getPlaysType();
        } else if (this instanceof PsiHasTypeProperty) {
            return ((PsiHasTypeProperty) this).getHasType();
        } else {
            return super.getName();
        }
    }

    @Override
    public String toString() {
        return getNode().getElementType() + " - " + getText() + " - Location: " + getTextRange();
    }
}
