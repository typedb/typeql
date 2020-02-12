package ai.graknlabs.graql.inspection.unusedDeclaration;

import ai.graknlabs.graql.psi.GraqlPsiUtils;
import ai.graknlabs.graql.psi.PsiGraqlElement;
import ai.graknlabs.graql.psi.PsiGraqlNamedElement;
import ai.graknlabs.graql.psi.property.PsiTypeProperty;
import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author <a href="mailto:brandon@srcpl.us">Brandon Fergerson</a>
 */
public class GraqlUnusedRoleDeclarationInspection extends LocalInspectionTool {

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, boolean isOnTheFly) {
        return new PsiElementVisitor() {
            @Override
            public void visitElement(PsiElement identifier) {
                if (identifier instanceof PsiGraqlNamedElement) {
                    PsiGraqlNamedElement declaration = (PsiGraqlNamedElement) identifier;
                    String type = GraqlPsiUtils.determineDeclarationType(declaration);
                    if ("role".equals(type)) {
                        List<PsiGraqlElement> usages = GraqlPsiUtils.findUsages(
                                declaration.getProject(), declaration, declaration.getName());
                        if (usages.isEmpty()) {
                            if (identifier instanceof PsiTypeProperty) {
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
