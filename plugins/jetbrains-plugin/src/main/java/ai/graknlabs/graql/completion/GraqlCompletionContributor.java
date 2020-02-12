package ai.graknlabs.graql.completion;

import ai.graknlabs.graql.GraqlFileType;
import ai.graknlabs.graql.psi.GraqlPsiUtils;
import ai.graknlabs.graql.refactor.GraqlNamesValidator;
import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href="mailto:brandon@srcpl.us">Brandon Fergerson</a>
 */
public class GraqlCompletionContributor extends CompletionContributor {

    public GraqlCompletionContributor() {
        extend(CompletionType.BASIC, PlatformPatterns.psiElement(),
                new CompletionProvider<CompletionParameters>() {
                    public void addCompletions(@NotNull CompletionParameters parameters,
                                               @NotNull ProcessingContext context,
                                               @NotNull CompletionResultSet resultSet) {
                        //identifiers
                        Set<String> distinctIdentifiers = new HashSet<>();
                        GraqlPsiUtils.getIdentifiers(parameters.getEditor().getProject())
                                .forEach(identifier -> {
                                    if (!distinctIdentifiers.contains(identifier.getName())) {
                                        distinctIdentifiers.add(identifier.getName());

                                        String declarationType = GraqlPsiUtils.determineDeclarationType(identifier);
                                        resultSet.addElement(LookupElementBuilder.create(identifier)
                                                .withIcon(GraqlFileType.INSTANCE.getIcon())
                                                .withTypeText(declarationType != null ? declarationType : "unknown")
                                                .withStrikeoutness(declarationType == null)
                                                .withBoldness(declarationType != null)
                                        );
                                    }
                                });
                        //keywords
                        for (String keyword : GraqlNamesValidator.GRAQL_KEYWORDS) {
                            resultSet.addElement(LookupElementBuilder.create(keyword.replace("'", ""))
                                    .withIcon(GraqlFileType.INSTANCE.getIcon())
                                    .withTypeText("keyword")
                            );
                        }
                    }
                }
        );
    }
}
