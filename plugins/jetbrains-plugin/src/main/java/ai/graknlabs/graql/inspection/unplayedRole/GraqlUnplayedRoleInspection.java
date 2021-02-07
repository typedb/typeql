package ai.graknlabs.graql.inspection.unplayedRole;

import ai.graknlabs.graql.psi.GraqlPsiUtils;
import ai.graknlabs.graql.psi.PsiGraqlElement;
import ai.graknlabs.graql.psi.PsiGraqlNamedElement;
import ai.graknlabs.graql.psi.constraint.PsiPlaysTypeConstraint;
import ai.graknlabs.graql.psi.statement.PsiStatementType;
import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static ai.graknlabs.graql.psi.GraqlPsiUtils.ensureGraqlElementsUpToDate;

/**
 * @author <a href="mailto:bfergerson@apache.org">Brandon Fergerson</a>
 */
public class GraqlUnplayedRoleInspection extends LocalInspectionTool {

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, boolean isOnTheFly) {
        return new PsiElementVisitor() {
            @Override
            public void visitElement(PsiElement identifier) {
                if (identifier instanceof PsiGraqlElement
                        && !(identifier instanceof PsiGraqlNamedElement)
                        && !(identifier instanceof PsiStatementType)) {
                    ensureGraqlElementsUpToDate(identifier.getContainingFile());

                    PsiGraqlElement identifierElement = (PsiGraqlElement) identifier;
                    PsiGraqlNamedElement declaration = GraqlPsiUtils.findDeclaration(
                            identifier.getProject(), identifierElement);
                    if (declaration != null) {
                        String type = GraqlPsiUtils.determineDeclarationType(declaration);
                        if ("role".equals(type)) {
                            boolean isPlayed = false;
                            List<PsiGraqlElement> usages = GraqlPsiUtils.findUsages(identifierElement);
                            for (PsiGraqlElement usage : usages) {
                                if (usage instanceof PsiPlaysTypeConstraint) {
                                    isPlayed = true;
                                    break;
                                }
                            }

                            if (!isPlayed) {
                                boolean problemAlreadyRegistered = false;
                                for (ProblemDescriptor problem : holder.getResults()) {
                                    if (problem.getPsiElement() == declaration
                                            && getDescriptionTemplate().equals(problem.getDescriptionTemplate())) {
                                        problemAlreadyRegistered = true;
                                        break;
                                    }
                                }

                                if (!problemAlreadyRegistered
                                        && declaration.getContainingFile().isEquivalentTo(identifier.getContainingFile())) {
                                    holder.registerProblem(declaration.getFirstChild().getNextSibling().getNextSibling(), getDescriptionTemplate());
                                }
                            }
                        }
                    }
                }
            }
        };
    }

    @NotNull
    private String getDescriptionTemplate() {
        return "Role <code>#ref</code> is never played";
    }

    @NotNull
    public String getDisplayName() {
        return "Unplayed role";
    }

    @NotNull
    public String getGroupDisplayName() {
        return "Graql";
    }

    @NotNull
    public String getShortName() {
        return "UnplayedRole";
    }

    @Override
    public boolean isEnabledByDefault() {
        return true;
    }
}
