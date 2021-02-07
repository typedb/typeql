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
import com.intellij.psi.PsiErrorElement;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static ai.graknlabs.graql.GraqlLanguage.GRAQL_TYPES;
import static ai.graknlabs.graql.psi.GraqlPsiUtils.ensureGraqlElementsUpToDate;

/**
 * @author <a href="mailto:bfergerson@apache.org">Brandon Fergerson</a>
 */
public class GraqlUndefinedDeclarationInspection extends LocalInspectionTool {

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, boolean isOnTheFly) {
        return new PsiElementVisitor() {
            @Override
            public void visitElement(@NotNull PsiElement element) {
                if (element instanceof PsiStatementType) {
                    ensureGraqlElementsUpToDate(element.getContainingFile());

                    List<PsiGraqlElement> identifiers = new ArrayList<>();
                    identifiers.addAll(((PsiStatementType) element).findOwnsTypeProperties());
                    identifiers.addAll(((PsiStatementType) element).findPlaysTypeProperties());
                    identifiers.addAll(((PsiStatementType) element).findSubTypeProperties());

                    for (PsiGraqlElement identifier : identifiers) {
                        if (StringUtils.isEmpty(identifier.getName())) {
                            return; //user still typing
                        }

                        PsiGraqlNamedElement declaration = GraqlPsiUtils.findDeclaration(
                                identifier.getProject(), identifier);
                        if (declaration == null) {
                            PsiElement undefinedConcept;
                            if (identifier.getFirstChild() != null && identifier.getFirstChild().getNextSibling() != null
                                    && identifier.getFirstChild().getNextSibling().getNextSibling() != null) {
                                undefinedConcept = identifier.getFirstChild().getNextSibling().getNextSibling();
                            } else {
                                return; //user still typing
                            }
                            if (GRAQL_TYPES.contains(undefinedConcept.getText())) {
                                return; //defined by language
                            }

                            if (!(undefinedConcept.getFirstChild() instanceof PsiErrorElement)) {
                                holder.registerProblem(undefinedConcept,
                                        "Concept <code>#ref</code> has not been defined",
                                        ProblemHighlightType.GENERIC_ERROR);
                            }
                        }
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
