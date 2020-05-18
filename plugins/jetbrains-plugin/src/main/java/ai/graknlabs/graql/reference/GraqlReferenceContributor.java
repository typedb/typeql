package ai.graknlabs.graql.reference;

import ai.graknlabs.graql.psi.property.PsiHasTypeProperty;
import ai.graknlabs.graql.psi.property.PsiPlaysTypeProperty;
import ai.graknlabs.graql.psi.property.PsiRelatesSuperRoleTypeProperty;
import ai.graknlabs.graql.psi.property.PsiSubTypeProperty;
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
        registrar.registerReferenceProvider(PlatformPatterns.psiElement(PsiHasTypeProperty.class), new PsiReferenceProvider() {
            @NotNull
            @Override
            public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
                PsiHasTypeProperty hasElement = (PsiHasTypeProperty) element;
                return new PsiReference[]{
                        new GraqlReference(hasElement, hasElement.getHasTypeTextRange(), hasElement.getHasType())
                };
            }
        });
        registrar.registerReferenceProvider(PlatformPatterns.psiElement(PsiPlaysTypeProperty.class), new PsiReferenceProvider() {
            @NotNull
            @Override
            public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
                PsiPlaysTypeProperty playsElement = (PsiPlaysTypeProperty) element;
                return new PsiReference[]{
                        new GraqlReference(playsElement, playsElement.getPlaysTypeTextRange(),
                                playsElement.getPlaysType())
                };
            }
        });
        registrar.registerReferenceProvider(PlatformPatterns.psiElement(PsiRelatesSuperRoleTypeProperty.class), new PsiReferenceProvider() {
            @NotNull
            @Override
            public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
                PsiRelatesSuperRoleTypeProperty relatesSuperRoleElement = (PsiRelatesSuperRoleTypeProperty) element;
                return new PsiReference[]{
                        new GraqlReference(relatesSuperRoleElement,
                                new TextRange(0, relatesSuperRoleElement.getText().length()),
                                relatesSuperRoleElement.getText())
                };
            }
        });
        registrar.registerReferenceProvider(PlatformPatterns.psiElement(PsiSubTypeProperty.class), new PsiReferenceProvider() {
            @NotNull
            @Override
            public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
                PsiSubTypeProperty subTypeElement = (PsiSubTypeProperty) element;
                return new PsiReference[]{
                        new GraqlReference(subTypeElement, subTypeElement.getSubTypeTextRange(),
                                subTypeElement.getSubType())
                };
            }
        });
    }
}

