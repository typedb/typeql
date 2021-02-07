package ai.graknlabs.graql.usage;

import ai.graknlabs.graql.GraqlLanguage;
import ai.graknlabs.graql.GraqlLexer;
import ai.graknlabs.graql.GraqlParserDefinition;
import ai.graknlabs.graql.psi.GraqlPsiUtils;
import ai.graknlabs.graql.psi.PsiGraqlNamedElement;
import ai.graknlabs.graql.psi.constraint.PsiRelatesTypeConstraint;
import ai.graknlabs.graql.psi.constraint.PsiSubTypeConstraint;
import ai.graknlabs.graql.psi.constraint.PsiTypeConstraint;
import com.intellij.lang.cacheBuilder.DefaultWordsScanner;
import com.intellij.lang.cacheBuilder.WordsScanner;
import com.intellij.lang.findUsages.FindUsagesProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.TokenSet;
import org.antlr.intellij.adaptor.lexer.ANTLRLexerAdaptor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="mailto:bfergerson@apache.org">Brandon Fergerson</a>
 */
public class GraqlFindUsagesProvider implements FindUsagesProvider {

    @Nullable
    @Override
    public WordsScanner getWordsScanner() {
        GraqlLexer lexer = new GraqlLexer(null);
        return new DefaultWordsScanner(new ANTLRLexerAdaptor(GraqlLanguage.INSTANCE, lexer),
                GraqlParserDefinition.IDS,
                GraqlParserDefinition.COMMENTS,
                TokenSet.EMPTY);
    }

    @Override
    public boolean canFindUsagesFor(@NotNull PsiElement psiElement) {
        return psiElement instanceof PsiTypeConstraint
                || psiElement instanceof PsiSubTypeConstraint
                || psiElement instanceof PsiRelatesTypeConstraint;
    }

    @Nullable
    @Override
    public String getHelpId(@NotNull PsiElement psiElement) {
        return null;
    }

    @NotNull
    @Override
    public String getType(@NotNull PsiElement element) {
        if (element instanceof PsiGraqlNamedElement) {
            String declarationType = GraqlPsiUtils.determineDeclarationType((PsiGraqlNamedElement) element);
            if (declarationType != null) {
                return "Graql " + declarationType;
            } else {
                return "Graql element";
            }
        } else {
            //todo: can this happen?
            return "";
        }
    }

    @NotNull
    @Override
    public String getDescriptiveName(@NotNull PsiElement element) {
        return element.getText();
    }

    @NotNull
    @Override
    public String getNodeText(@NotNull PsiElement element, boolean useFullName) {
        PsiGraqlNamedElement namedElement = ((PsiGraqlNamedElement) element);
        return namedElement.getName();
    }
}