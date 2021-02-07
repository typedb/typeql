package ai.graknlabs.graql.psi.constraint;

import ai.graknlabs.graql.psi.GraqlPsiUtils;
import ai.graknlabs.graql.psi.PsiGraqlNamedElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="mailto:bfergerson@apache.org">Brandon Fergerson</a>
 */
public class PsiTypeConstraint extends PsiGraqlNamedElement {

    public PsiTypeConstraint(@NotNull ASTNode node) {
        super(node);
    }

    @Nullable
    @Override
    public PsiElement getNameIdentifier() {
        //todo: seems wrong
        return getNode().getFirstChildNode().getPsi();
    }

    @Override
    public PsiElement setName(@NotNull String name) throws IncorrectOperationException {
        return GraqlPsiUtils.setName(this, name);
    }
}
