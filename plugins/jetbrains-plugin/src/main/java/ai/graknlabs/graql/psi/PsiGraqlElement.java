package ai.graknlabs.graql.psi;

import ai.graknlabs.graql.GraqlParserDefinition;
import ai.graknlabs.graql.psi.constraint.PsiOwnsTypeConstraint;
import ai.graknlabs.graql.psi.constraint.PsiPlaysTypeConstraint;
import ai.graknlabs.graql.psi.constraint.PsiRelatesTypeConstraint;
import ai.graknlabs.graql.psi.constraint.PsiSubTypeConstraint;
import ai.graknlabs.graql.psi.statement.PsiStatementType;
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
        if (this instanceof PsiSubTypeConstraint) {
            return ((PsiSubTypeConstraint) this).getSubType();
        } else if (this instanceof PsiPlaysTypeConstraint) {
            return ((PsiPlaysTypeConstraint) this).getPlaysType();
        } else if (this instanceof PsiOwnsTypeConstraint) {
            return ((PsiOwnsTypeConstraint) this).getOwnsType();
        } else {
            return super.getName();
        }
    }

    public String getScopedName() {
        if (this instanceof PsiRelatesTypeConstraint) {
            PsiStatementType statementType = (PsiStatementType) getParent();
            return statementType.getName() + ":" + getName();
        } else {
            return getName();
        }
    }

    @Override
    public String toString() {
        return String.format("%s(%s) - Location: %s",
                getClass().getSimpleName(), getNode().getElementType(), getTextRange());
    }

    @Override
    public void subtreeChanged() {
        CompositeElement composite = getNode();
        PsiGraqlElement updatedElement = (PsiGraqlElement) GraqlParserDefinition.INSTANCE.createElement(composite);
        GraqlParserDefinition.updateWrappedTypeIfNecessary(getNode(), updatedElement);
    }
}
