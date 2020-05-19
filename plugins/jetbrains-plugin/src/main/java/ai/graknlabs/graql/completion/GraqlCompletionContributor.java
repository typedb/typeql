package ai.graknlabs.graql.completion;

import ai.graknlabs.graql.GraqlFileType;
import ai.graknlabs.graql.GraqlLanguage;
import ai.graknlabs.graql.GraqlParser;
import ai.graknlabs.graql.psi.property.PsiHasTypeProperty;
import ai.graknlabs.graql.psi.property.PsiPlaysTypeProperty;
import ai.graknlabs.graql.psi.property.PsiRelatesTypeProperty;
import ai.graknlabs.graql.psi.property.PsiSubTypeProperty;
import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;
import org.antlr.intellij.adaptor.parser.SyntaxError;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

import static ai.graknlabs.graql.GraqlParserDefinition.RULE_ELEMENT_TYPES;
import static ai.graknlabs.graql.GraqlParserDefinition.TOKEN_ELEMENT_TYPES;
import static ai.graknlabs.graql.psi.GraqlPsiUtils.*;
import static java.util.Objects.requireNonNull;

/**
 * @author <a href="mailto:bfergerson@apache.org">Brandon Fergerson</a>
 */
public class GraqlCompletionContributor extends CompletionContributor {

    public GraqlCompletionContributor() {
        extend(CompletionType.BASIC, PlatformPatterns.psiElement(),
                new CompletionProvider<CompletionParameters>() {
                    public void addCompletions(@NotNull CompletionParameters parameters,
                                               @NotNull ProcessingContext context,
                                               @NotNull CompletionResultSet resultSet) {
                        boolean includeKeywords = true;
                        PsiElement ruleType = findParentByType(parameters.getPosition(),
                                RULE_ELEMENT_TYPES.get(GraqlParser.RULE_type_property));
                        if (ruleType != null) {
                            if (ruleType instanceof PsiHasTypeProperty) {
                                //has, include all attributes
                                includeAttributeTypes(resultSet, ruleType);
                            } else if (ruleType instanceof PsiSubTypeProperty) {
                                //sub, include all declarations (except current) & base types
                                includeAllTypes(resultSet, ruleType);
                                includeBaseTypes(resultSet);
                            } else if (ruleType instanceof PsiRelatesTypeProperty) {
                                //relates, include all plays (roles)
                                includePlayRoles(parameters, resultSet);
                            } else if (ruleType instanceof PsiPlaysTypeProperty) {
                                //plays, include all relates (roles)
                                includeRelateRoles(parameters, resultSet);
                            }

                            //if looking for TYPE_NAME_ don't include keywords
                            if (parameters.getPosition() instanceof LeafPsiElement) {
                                if (((LeafPsiElement) parameters.getPosition()).getElementType() ==
                                        TOKEN_ELEMENT_TYPES.get(GraqlParser.TYPE_NAME_)) {
                                    includeKeywords = false;
                                }
                            }
                        }

                        if (includeKeywords) {
                            SyntaxError syntaxError = GraqlCompletionErrorListener.tokenToErrorMap.get(parameters.getOffset());
                            if (syntaxError != null && syntaxError.getException() != null
                                    && syntaxError.getException().getExpectedTokens() != null) {
                                getActualKeywords(syntaxError).forEach(keyword -> {
                                    resultSet.addElement(LookupElementBuilder.create(keyword)
                                            .withIcon(GraqlFileType.INSTANCE.getIcon())
                                            .withTypeText(keyword)
                                            .withBoldness(true));
                                });
                            } else {
                                //no errors or suggested keywords; fallback to query types
                                includeQueryTypes(resultSet);
                            }
                        }
                    }
                }
        );
    }

    private void includeAttributeTypes(@NotNull CompletionResultSet resultSet, PsiElement ruleType) {
        getDeclarationsByType(ruleType.getProject(), "attribute").forEach(identifier -> {
            String declarationType = determineDeclarationType(identifier);
            resultSet.addElement(LookupElementBuilder.create(identifier)
                    .withIcon(GraqlFileType.INSTANCE.getIcon())
                    .withTypeText(declarationType != null ? declarationType : "unknown")
                    .withStrikeoutness(declarationType == null)
            );
        });
    }

    private void includeAllTypes(@NotNull CompletionResultSet resultSet, PsiElement ruleType) {
        getAllDeclarations(ruleType.getProject()).forEach(identifier -> {
            String declarationType = determineDeclarationType(identifier);
            if (declarationType != null) {
                resultSet.addElement(LookupElementBuilder.create(identifier)
                        .withIcon(GraqlFileType.INSTANCE.getIcon())
                        .withTypeText(declarationType)
                );
            }
        });
    }

    private void includeBaseTypes(@NotNull CompletionResultSet resultSet) {
        GraqlLanguage.GRAQL_TYPES.forEach(it -> resultSet.addElement(LookupElementBuilder.create(it)
                .withIcon(GraqlFileType.INSTANCE.getIcon())
                .withTypeText(it)
                .withBoldness(true)
        ));
    }

    private void includePlayRoles(@NotNull CompletionParameters parameters, @NotNull CompletionResultSet resultSet) {
        PsiTreeUtil.collectElementsOfType(parameters.getOriginalFile(), PsiPlaysTypeProperty.class)
                .forEach(it -> resultSet.addElement(LookupElementBuilder.create(it.getPlaysType())
                        .withIcon(GraqlFileType.INSTANCE.getIcon())
                        .withTypeText("role")
                ));
    }

    private void includeRelateRoles(@NotNull CompletionParameters parameters, @NotNull CompletionResultSet resultSet) {
        PsiTreeUtil.collectElementsOfType(parameters.getOriginalFile(), PsiRelatesTypeProperty.class)
                .forEach(it -> resultSet.addElement(LookupElementBuilder.create(requireNonNull(it.getName()))
                        .withIcon(GraqlFileType.INSTANCE.getIcon())
                        .withTypeText("role")
                ));
    }

    private void includeQueryTypes(@NotNull CompletionResultSet resultSet) {
        resultSet.addElement(LookupElementBuilder.create("define")
                .withIcon(GraqlFileType.INSTANCE.getIcon())
                .withTypeText("define")
                .withBoldness(true));
        resultSet.addElement(LookupElementBuilder.create("compute")
                .withIcon(GraqlFileType.INSTANCE.getIcon())
                .withTypeText("compute")
                .withBoldness(true));
        resultSet.addElement(LookupElementBuilder.create("insert")
                .withIcon(GraqlFileType.INSTANCE.getIcon())
                .withTypeText("insert")
                .withBoldness(true));
        resultSet.addElement(LookupElementBuilder.create("match")
                .withIcon(GraqlFileType.INSTANCE.getIcon())
                .withTypeText("match")
                .withBoldness(true));
        resultSet.addElement(LookupElementBuilder.create("undefine")
                .withIcon(GraqlFileType.INSTANCE.getIcon())
                .withTypeText("undefine")
                .withBoldness(true));
    }

    private static List<String> getActualKeywords(SyntaxError syntaxError) {
        return syntaxError.getException().getExpectedTokens().toList().stream()
                .map(it -> TOKEN_ELEMENT_TYPES.get(it).toString().replace("'", ""))
                .map(s -> {
                    switch (s) {
                        case "SUB_":
                            return Arrays.asList("sub", "sub!");
                        case "ID_":
                            return new ArrayList<String>(); //todo: return IDs found
                        case "VAR_":
                        case "TYPE_NAME_":
                        case "TYPE_IMPLICIT_":
                            return new ArrayList<String>();
                    }
                    return Collections.singletonList(s);
                })
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }
}
