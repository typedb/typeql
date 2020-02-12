package ai.graknlabs.graql.psi.statement;

import ai.graknlabs.graql.psi.PsiGraqlElement;
import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:brandon@srcpl.us">Brandon Fergerson</a>
 */
public class PsiStatementType extends PsiGraqlElement {

    public PsiStatementType(@NotNull ASTNode node) {
        super(node);
    }

    public String getSubType() {
        //todo: seems wrong
        if (getNode().getFirstChildNode() == null || getNode().getFirstChildNode().getTreeNext() == null
                || getNode().getFirstChildNode().getTreeNext().getTreeNext() == null
                || getNode().getFirstChildNode().getTreeNext().getTreeNext().getLastChildNode() == null) {
            return null;
        }
        return getNode().getFirstChildNode().getTreeNext().getTreeNext().getLastChildNode().getText();
    }
}