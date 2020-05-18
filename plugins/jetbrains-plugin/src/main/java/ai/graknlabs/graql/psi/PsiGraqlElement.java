package ai.graknlabs.graql.psi;

import ai.graknlabs.graql.GraqlParserDefinition;
import ai.graknlabs.graql.psi.property.PsiHasTypeProperty;
import ai.graknlabs.graql.psi.property.PsiPlaysTypeProperty;
import ai.graknlabs.graql.psi.property.PsiSubTypeProperty;
import com.intellij.lang.ASTNode;
import com.intellij.psi.impl.source.tree.CompositeElement;
import org.antlr.intellij.adaptor.psi.ANTLRPsiNode;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:bfergerson@apache.org">Brandon Fergerson</a>
 */
public class PsiGraqlElement extends ANTLRPsiNode {

    public PsiGraqlElement(@NotNull ASTNode node) {
        super(node);
    }

    @NotNull
    @Override
    public CompositeElement getNode() {
        return (CompositeElement) super.getNode();
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
        return String.format("%s(%s) - Location: %s",
                getClass().getSimpleName(), getNode().getElementType(), getTextRange());
    }

    @Override
    public void subtreeChanged() {
        PsiGraqlElement updatedElement = (PsiGraqlElement) GraqlParserDefinition.INSTANCE.createElement(getNode());
        if (updatedElement.getClass() != getClass()) {
            getNode().setPsi(updatedElement);
        }
    }
}
