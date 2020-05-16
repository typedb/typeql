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
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

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
            public void visitElement(PsiElement element) {
                List<PsiGraqlElement> identifiers = new ArrayList<>();
                if (element instanceof PsiStatementType) {
                    identifiers.addAll(((PsiStatementType) element).findHasTypeProperties());
                    identifiers.addAll(((PsiStatementType) element).findPlaysTypeProperties());
                    identifiers.addAll(((PsiStatementType) element).findSubTypeProperties());
                }

                for (PsiGraqlElement identifier : identifiers) {
                    if (StringUtils.isEmpty(identifier.getName())) {
                        return; //user still typing
                    }

                    PsiGraqlNamedElement declaration = GraqlPsiUtils.findDeclaration(
                            identifier.getProject(), identifier.getName());
                    if (declaration == null) {
                        PsiElement undefinedConcept = identifier.getFirstChild().getNextSibling().getNextSibling();
                        if (GRAQL_TYPES.contains(undefinedConcept.getText())) {
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
