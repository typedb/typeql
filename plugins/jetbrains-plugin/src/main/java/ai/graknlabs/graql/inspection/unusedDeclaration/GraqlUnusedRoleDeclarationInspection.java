package ai.graknlabs.graql.inspection.unusedDeclaration;

import ai.graknlabs.graql.psi.GraqlPsiUtils;
import ai.graknlabs.graql.psi.PsiGraqlElement;
import ai.graknlabs.graql.psi.PsiGraqlNamedElement;
import ai.graknlabs.graql.psi.constraint.PsiTypeConstraint;
import ai.graknlabs.graql.psi.statement.PsiStatementType;
import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static ai.graknlabs.graql.psi.GraqlPsiUtils.ensureGraqlElementsUpToDate;

/**
 * @author <a href="mailto:bfergerson@apache.org">Brandon Fergerson</a>
 */
public class GraqlUnusedRoleDeclarationInspection extends LocalInspectionTool {

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, boolean isOnTheFly) {
        return new PsiElementVisitor() {
            @Override
            public void visitElement(PsiElement element) {
                if (element instanceof PsiStatementType) {
                    ensureGraqlElementsUpToDate(element.getContainingFile());

                    for (PsiGraqlNamedElement declaration : ((PsiStatementType) element).findRelatesTypeProperties()) {
                        if (StringUtils.isEmpty(declaration.getName())) {
                            return; //user still typing
                        }

                        String type = GraqlPsiUtils.determineDeclarationType(declaration);
                        if ("role".equals(type)) {
                            List<PsiGraqlElement> usages = GraqlPsiUtils.findUsages(declaration);
                            if (usages.isEmpty()) {
                                if (declaration instanceof PsiTypeConstraint) {
                                    holder.registerProblem(declaration.getFirstChild(),
                                            "Role <code>#ref</code> is never used");
                                } else {
                                    holder.registerProblem(declaration.getFirstChild().getNextSibling().getNextSibling(),
                                            "Role <code>#ref</code> is never used");
                                }
                            }
                        }
                    }
                }
            }
        };
    }

    @NotNull
    public String getDisplayName() {
        return "Unused role declaration";
    }

    @NotNull
    public String getGroupDisplayName() {
        return "Graql";
    }

    @NotNull
    public String getShortName() {
        return "UnusedRoleDeclaration";
    }

    @Override
    public boolean isEnabledByDefault() {
        return true;
    }
}
