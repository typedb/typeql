package ai.graknlabs.graql.inspection.undefinedDeclaration;

import ai.graknlabs.graql.psi.GraqlPsiUtils;
import ai.graknlabs.graql.psi.PsiGraqlElement;
import ai.graknlabs.graql.psi.PsiGraqlNamedElement;
import ai.graknlabs.graql.psi.statement.PsiStatementType;
import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.NotNull;

import static ai.graknlabs.graql.GraqlLanguage.GRAQL_TYPES;

/**
 * @author <a href="mailto:brandon@srcpl.us">Brandon Fergerson</a>
 */
public class GraqlUndefinedDeclarationInspection extends LocalInspectionTool {

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, boolean isOnTheFly) {
        return new PsiElementVisitor() {
            @Override
            public void visitElement(PsiElement identifier) {
                if (identifier instanceof PsiGraqlElement
                        && !(identifier instanceof PsiGraqlNamedElement)
                        && !(identifier instanceof PsiStatementType)) {
                    PsiGraqlElement identifierElement = ((PsiGraqlElement) identifier);
                    if (identifierElement.getName() == null) {
                        return;
                    }

                    PsiGraqlNamedElement declaration = GraqlPsiUtils.findDeclaration(
                            identifier.getProject(), identifierElement.getName());
                    if (declaration == null) {
                        PsiElement undefinedConcept = identifier.getFirstChild().getNextSibling().getNextSibling();
                        if ("".equals(undefinedConcept.getText())) {
                            return; //user still typing
                        } else if (GRAQL_TYPES.contains(undefinedConcept.getText())) {
                            return; //defined by language
                        }

                        holder.registerProblem(undefinedConcept,
                                "Concept <code>#ref</code> has not been defined",
                                ProblemHighlightType.GENERIC_ERROR);
                    }
                }
            }
        };
    }

    @NotNull
    public String getDisplayName() {
        return "Undefined declaration";
    }

    @NotNull
    public String getGroupDisplayName() {
        return "Graql";
    }

    @NotNull
    public String getShortName() {
        return "UndefinedDeclaration";
    }

    @Override
    public boolean isEnabledByDefault() {
        return true;
    }
}
