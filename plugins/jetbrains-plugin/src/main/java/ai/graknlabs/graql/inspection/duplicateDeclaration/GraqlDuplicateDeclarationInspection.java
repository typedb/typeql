package ai.graknlabs.graql.inspection.duplicateDeclaration;

import ai.graknlabs.graql.psi.GraqlPsiUtils;
import ai.graknlabs.graql.psi.PsiGraqlNamedElement;
import ai.graknlabs.graql.psi.property.PsiRelatesTypeProperty;
import ai.graknlabs.graql.psi.property.PsiTypeProperty;
import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

import static ai.graknlabs.graql.psi.GraqlPsiUtils.ensureGraqlElementsUpToDate;

/**
 * @author <a href="mailto:bfergerson@apache.org">Brandon Fergerson</a>
 */
public class GraqlDuplicateDeclarationInspection extends LocalInspectionTool {

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, boolean isOnTheFly) {
        return new PsiElementVisitor() {
            @Override
            public void visitElement(PsiElement identifier) {
                if (identifier instanceof PsiGraqlNamedElement) {
                    ensureGraqlElementsUpToDate(identifier.getContainingFile());

                    PsiGraqlNamedElement namedElement = (PsiGraqlNamedElement) identifier;
                    List<String> declarationTypes = GraqlPsiUtils.findDeclarations(
                            identifier.getProject(), namedElement.getName()).stream()
                            .map(it -> it instanceof PsiRelatesTypeProperty ? "relation" : GraqlPsiUtils.determineDeclarationType(it))
                            .distinct().collect(Collectors.toList());
                    if (declarationTypes.size() > 1) {
                        if (identifier instanceof PsiTypeProperty) {
                            holder.registerProblem(identifier.getFirstChild(),
                                    "Concept <code>#ref</code> has been defined more than once",
                                    ProblemHighlightType.GENERIC_ERROR);
                        } else {
                            holder.registerProblem(identifier.getFirstChild().getNextSibling().getNextSibling(),
                                    "Concept <code>#ref</code> has been defined more than once",
                                    ProblemHighlightType.GENERIC_ERROR);
                        }
                    }
                }
            }
        };
    }

    @NotNull
    public String getDisplayName() {
        return "Duplicate declaration";
    }

    @NotNull
    public String getGroupDisplayName() {
        return "Graql";
    }

    @NotNull
    public String getShortName() {
        return "DuplicateDeclaration";
    }

    @Override
    public boolean isEnabledByDefault() {
        return true;
    }
}
