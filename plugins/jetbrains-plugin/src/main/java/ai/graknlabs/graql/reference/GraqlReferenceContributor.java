package ai.graknlabs.graql.reference;

import ai.graknlabs.graql.psi.constraint.PsiOwnsTypeConstraint;
import ai.graknlabs.graql.psi.constraint.PsiPlaysTypeConstraint;
import ai.graknlabs.graql.psi.constraint.PsiRelatesSuperRoleTypeConstraint;
import ai.graknlabs.graql.psi.constraint.PsiSubTypeConstraint;
import com.intellij.openapi.util.TextRange;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.*;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:bfergerson@apache.org">Brandon Fergerson</a>
 */
public class GraqlReferenceContributor extends PsiReferenceContributor {

    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
        registrar.registerReferenceProvider(PlatformPatterns.psiElement(PsiOwnsTypeConstraint.class), new PsiReferenceProvider() {
            @NotNull
            @Override
            public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
                PsiOwnsTypeConstraint ownsElement = (PsiOwnsTypeConstraint) element;
                return new PsiReference[]{
                        new GraqlReference(ownsElement, ownsElement.getOwnsTypeTextRange())
                };
            }
        });
        registrar.registerReferenceProvider(PlatformPatterns.psiElement(PsiPlaysTypeConstraint.class), new PsiReferenceProvider() {
            @NotNull
            @Override
            public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
                PsiPlaysTypeConstraint playsElement = (PsiPlaysTypeConstraint) element;
                return new PsiReference[]{
                        new GraqlReference(playsElement, playsElement.getPlaysTypeTextRange())
                };
            }
        });
        registrar.registerReferenceProvider(PlatformPatterns.psiElement(PsiRelatesSuperRoleTypeConstraint.class), new PsiReferenceProvider() {
            @NotNull
            @Override
            public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
                PsiRelatesSuperRoleTypeConstraint relatesSuperRoleElement = (PsiRelatesSuperRoleTypeConstraint) element;
                return new PsiReference[]{
                        new GraqlReference(relatesSuperRoleElement,
                                new TextRange(0, relatesSuperRoleElement.getText().length()))
                };
            }
        });
        registrar.registerReferenceProvider(PlatformPatterns.psiElement(PsiSubTypeConstraint.class), new PsiReferenceProvider() {
            @NotNull
            @Override
            public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
                PsiSubTypeConstraint subTypeElement = (PsiSubTypeConstraint) element;
                return new PsiReference[]{
                        new GraqlReference(subTypeElement, subTypeElement.getSubTypeTextRange())
                };
            }
        });
    }
}

