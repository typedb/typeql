package ai.graknlabs.graql.inspection.invalidTypeUsage;

import ai.graknlabs.graql.psi.GraqlPsiUtils;
import ai.graknlabs.graql.psi.PsiGraqlElement;
import ai.graknlabs.graql.psi.PsiGraqlNamedElement;
import ai.graknlabs.graql.psi.constraint.PsiOwnsTypeConstraint;
import ai.graknlabs.graql.psi.constraint.PsiPlaysTypeConstraint;
import ai.graknlabs.graql.psi.constraint.PsiSubTypeConstraint;
import ai.graknlabs.graql.psi.statement.PsiStatementType;
import com.google.common.collect.Sets;
import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static ai.graknlabs.graql.psi.GraqlPsiUtils.ensureGraqlElementsUpToDate;

/**
 * @author <a href="mailto:bfergerson@apache.org">Brandon Fergerson</a>
 */
public class GraqlInvalidTypeUsageInspection extends LocalInspectionTool {

    private final Map<String, Set<String>> invalidTypeUsages;

    public GraqlInvalidTypeUsageInspection() {
        invalidTypeUsages = new HashMap<>();
        invalidTypeUsages.put("entity", Sets.newHashSet("relates", "plays", "owns"));
        invalidTypeUsages.put("role", Sets.newHashSet("owns"));
        invalidTypeUsages.put("attribute", Sets.newHashSet("plays"));
        invalidTypeUsages.put("relation", Sets.newHashSet("plays", "owns"));
    }

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

                    PsiGraqlNamedElement declaration = GraqlPsiUtils.findDeclaration(
                            identifier.getProject(), ((PsiGraqlElement) identifier));
                    if (declaration != null) {
                        String declarationType = GraqlPsiUtils.determineDeclarationType(declaration);
                        String usageType;
                        if (identifier instanceof PsiPlaysTypeConstraint) {
                            usageType = "plays";
                        } else if (identifier instanceof PsiOwnsTypeConstraint) {
                            usageType = "owns";
                        } else if (identifier instanceof PsiSubTypeConstraint) {
                            return; //all types can be sub-typed
                        } else {
                            throw new UnsupportedOperationException();
                        }
                        if (declarationType != null && invalidTypeUsages.get(declarationType).contains(usageType)) {
                            holder.registerProblem(identifier.getFirstChild().getNextSibling().getNextSibling(),
                                    String.format("%s <code>#ref</code> cannot be used as '%s'",
                                            StringUtils.capitalize(declarationType), usageType),
                                    ProblemHighlightType.GENERIC_ERROR);
                        }
                    }
                }
            }
        };
    }

    @NotNull
    public String getDisplayName() {
        return "Invalid type usage";
    }

    @NotNull
    public String getGroupDisplayName() {
        return "Graql";
    }

    @NotNull
    public String getShortName() {
        return "InvalidTypeUsage";
    }

    @Override
    public boolean isEnabledByDefault() {
        return true;
    }
}
