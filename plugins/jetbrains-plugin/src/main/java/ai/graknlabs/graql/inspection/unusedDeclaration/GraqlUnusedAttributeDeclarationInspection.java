package ai.graknlabs.graql.inspection.unusedDeclaration;

import ai.graknlabs.graql.psi.GraqlPsiUtils;
import ai.graknlabs.graql.psi.PsiGraqlElement;
import ai.graknlabs.graql.psi.PsiGraqlNamedElement;
import ai.graknlabs.graql.psi.statement.PsiStatementType;
import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static ai.graknlabs.graql.psi.GraqlPsiUtils.ensureGraqlElementsUpToDate;

/**
 * @author <a href="mailto:bfergerson@apache.org">Brandon Fergerson</a>
 */
public class GraqlUnusedAttributeDeclarationInspection extends LocalInspectionTool {

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, boolean isOnTheFly) {
        return new PsiElementVisitor() {
            @Override
            public void visitElement(PsiElement element) {
                if (element instanceof PsiStatementType) {
                    ensureGraqlElementsUpToDate(element.getContainingFile());

                    for (PsiGraqlNamedElement declaration : ((PsiStatementType) element).findTypeProperties()) {
                        String type = GraqlPsiUtils.determineDeclarationType(declaration);
                        if ("attribute".equals(type)) {
                            List<PsiGraqlElement> usages = GraqlPsiUtils.findUsages(declaration);
                            if (usages.isEmpty()) {
                                holder.registerProblem(declaration, "Attribute <code>#ref</code> is never used");
                            }
                        }
                    }
                }
            }
        };
    }

    @NotNull
    public String getDisplayName() {
        return "Unused attribute declaration";
    }

    @NotNull
    public String getGroupDisplayName() {
        return "Graql";
    }

    @NotNull
    public String getShortName() {
        return "UnusedAttributeDeclaration";
    }

    @Override
    public boolean isEnabledByDefault() {
        return true;
    }
}
