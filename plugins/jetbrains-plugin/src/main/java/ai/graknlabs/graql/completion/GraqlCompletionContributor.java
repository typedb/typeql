package ai.graknlabs.graql.completion;

import ai.graknlabs.graql.GraqlFileType;
import ai.graknlabs.graql.GraqlLanguage;
import ai.graknlabs.graql.GraqlParser;
import ai.graknlabs.graql.psi.PsiGraqlElement;
import ai.graknlabs.graql.psi.constraint.PsiOwnsTypeConstraint;
import ai.graknlabs.graql.psi.constraint.PsiPlaysTypeConstraint;
import ai.graknlabs.graql.psi.constraint.PsiRelatesTypeConstraint;
import ai.graknlabs.graql.psi.constraint.PsiSubTypeConstraint;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.ide.scratch.ScratchUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;
import org.antlr.intellij.adaptor.parser.SyntaxError;
import org.antlr.v4.runtime.misc.IntervalSet;
import org.jetbrains.annotations.NonNls;
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

    public static @NonNls
    final String DUMMY_IDENTIFIER = "IntellijIdeaRulezzz";

    private static final Set<String> TYPE_PROPERTY_MODIFIERS = ImmutableSet.copyOf(
            new String[]{"abstract", "sub", "key", "owns", "plays", "relates"}
    );

    public GraqlCompletionContributor() {
        extend(CompletionType.BASIC, PlatformPatterns.psiElement(),
                new CompletionProvider<CompletionParameters>() {
                    public void addCompletions(@NotNull CompletionParameters parameters,
                                               @NotNull ProcessingContext context,
                                               @NotNull CompletionResultSet resultSet) {
                        boolean includeKeywords = true;
                        PsiGraqlElement ruleType = findParentByType(parameters.getPosition(),
                                RULE_ELEMENT_TYPES.get(GraqlParser.RULE_type_constraint));
                        if (ruleType != null) {
                            if (ruleType instanceof PsiOwnsTypeConstraint) {
                                //owns, include all attributes
                                PsiGraqlElement statementType = findParentByType(parameters.getPosition(),
                                        RULE_ELEMENT_TYPES.get(GraqlParser.RULE_variable_type));
                                includeAttributeTypes(resultSet, ruleType, parameters.getOriginalFile().getVirtualFile(),
                                        requireNonNull(statementType).getName());
                            } else if (ruleType instanceof PsiSubTypeConstraint) {
                                //sub, include all declarations & base types
                                PsiGraqlElement statementType = findParentByType(parameters.getPosition(),
                                        RULE_ELEMENT_TYPES.get(GraqlParser.RULE_variable_type));
                                includeAllTypes(resultSet, ruleType, parameters.getOriginalFile().getVirtualFile(),
                                        statementType.getName());
                                includeBaseTypes(resultSet);
                            } else if (ruleType instanceof PsiRelatesTypeConstraint) {
                                //relates, include all plays (roles)
                                includePlayRoles(parameters, resultSet);
                            } else if (ruleType instanceof PsiPlaysTypeConstraint) {
                                //plays, include all relates (roles)
                                includeRelateRoles(parameters, resultSet);
                            } else {
                                //add type property modifiers
                                TYPE_PROPERTY_MODIFIERS.forEach(keyword ->
                                        resultSet.addElement(LookupElementBuilder.create(keyword)
                                                .withIcon(GraqlFileType.INSTANCE.getIcon())
                                                .withTypeText(keyword)
                                                .withBoldness(true)));
                            }

                            //if looking for TYPE_NAME_ don't include keywords
                            if (parameters.getPosition() instanceof LeafPsiElement) {
                                if (((LeafPsiElement) parameters.getPosition()).getElementType() ==
                                        TOKEN_ELEMENT_TYPES.get(GraqlParser.LABEL_)) {
                                    includeKeywords = false;
                                }
                            }
                        }

                        if (includeKeywords) {
                            Map<Integer, SyntaxError> tokenToErrorMap = GraqlCompletionErrorListener.getTokenToErrorMap();
                            SyntaxError syntaxError = tokenToErrorMap.get(parameters.getOffset());
                            if (syntaxError == null) {
                                String currentText = parameters.getPosition().getText();
                                if (currentText.contains(DUMMY_IDENTIFIER)) {
                                    //currently typing; suggest same as before typing started
                                    syntaxError = tokenToErrorMap.get(
                                            parameters.getOffset() - (currentText.length() - DUMMY_IDENTIFIER.length()));
                                }
                            }
                            if (syntaxError != null && syntaxError.getException() != null
                                    && syntaxError.getException().getExpectedTokens() != null) {
                                getActualKeywords(syntaxError.getException().getExpectedTokens()).forEach(keyword -> {
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

    private void includeAttributeTypes(@NotNull CompletionResultSet resultSet, @NotNull PsiElement ruleType,
                                       @NotNull VirtualFile containingFile, String... excludedNames) {
        Set<String> excludedNameSet = Sets.newHashSet(excludedNames);
        Collection<VirtualFile> searchScope;
        if (ScratchUtil.isScratch(containingFile)) {
            searchScope = Collections.singletonList(containingFile);
        } else {
            searchScope = FileTypeIndex.getFiles(GraqlFileType.INSTANCE,
                    GlobalSearchScope.allScope(ruleType.getProject()));
        }

        getDeclarationsByType(ruleType.getProject(), searchScope, "attribute").stream()
                .filter(it -> !excludedNameSet.contains(it.getName())).forEach(it -> {
            String declarationType = determineDeclarationType(it);
            resultSet.addElement(LookupElementBuilder.create(it)
                    .withIcon(GraqlFileType.INSTANCE.getIcon())
                    .withTypeText(declarationType != null ? declarationType : "unknown")
                    .withStrikeoutness(declarationType == null)
            );
        });
    }

    private void includeAllTypes(@NotNull CompletionResultSet resultSet, @NotNull PsiElement ruleType,
                                 @NotNull VirtualFile containingFile, String... excludedNames) {
        Set<String> excludedNameSet = Sets.newHashSet(excludedNames);
        Collection<VirtualFile> searchScope;
        if (ScratchUtil.isScratch(containingFile)) {
            searchScope = Collections.singletonList(containingFile);
        } else {
            searchScope = FileTypeIndex.getFiles(GraqlFileType.INSTANCE,
                    GlobalSearchScope.allScope(ruleType.getProject()));
        }

        getAllDeclarations(ruleType.getProject(), searchScope).stream()
                .filter(it -> !excludedNameSet.contains(it.getName())).forEach(it -> {
            String declarationType = determineDeclarationType(it);
            if (declarationType != null) {
                resultSet.addElement(LookupElementBuilder.create(it)
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
        PsiTreeUtil.collectElementsOfType(parameters.getOriginalFile(), PsiPlaysTypeConstraint.class)
                .forEach(it -> resultSet.addElement(LookupElementBuilder.create(it.getPlaysType())
                        .withIcon(GraqlFileType.INSTANCE.getIcon())
                        .withTypeText("role")
                ));
    }

    private void includeRelateRoles(@NotNull CompletionParameters parameters, @NotNull CompletionResultSet resultSet) {
        PsiTreeUtil.collectElementsOfType(parameters.getOriginalFile(), PsiRelatesTypeConstraint.class)
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

    private static List<String> getActualKeywords(@NotNull IntervalSet keywordSet) {
        return keywordSet.toList().stream()
                .map(it -> TOKEN_ELEMENT_TYPES.get(it).toString().replace("'", ""))
                .map(s -> {
                    switch (s) {
                        case "SUB_":
                            return Arrays.asList("sub", "sub!");
                        case "IDD_":
                            return new ArrayList<String>(); //todo: return IDs found
                        case "VAR_":
                        case "LABEL_":
                        case "TYPE_IMPLICIT_":
                            return new ArrayList<String>();
                    }
                    return Collections.singletonList(s);
                })
                .flatMap(Collection::stream)
                .distinct().collect(Collectors.toList());
    }
}
