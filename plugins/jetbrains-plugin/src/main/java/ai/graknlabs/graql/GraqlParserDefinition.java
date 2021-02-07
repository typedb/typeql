package ai.graknlabs.graql;

import ai.graknlabs.graql.completion.GraqlCompletionErrorListener;
import ai.graknlabs.graql.psi.PsiGraqlElement;
import ai.graknlabs.graql.psi.PsiGraqlFileBase;
import ai.graknlabs.graql.psi.constraint.*;
import ai.graknlabs.graql.psi.statement.PsiStatementType;
import com.intellij.lang.ASTNode;
import com.intellij.lang.ParserDefinition;
import com.intellij.lang.PsiParser;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.source.tree.CompositeElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.TokenSet;
import org.antlr.intellij.adaptor.lexer.ANTLRLexerAdaptor;
import org.antlr.intellij.adaptor.lexer.PSIElementTypeFactory;
import org.antlr.intellij.adaptor.lexer.RuleIElementType;
import org.antlr.intellij.adaptor.lexer.TokenIElementType;
import org.antlr.intellij.adaptor.parser.ANTLRParserAdaptor;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.tree.ParseTree;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static ai.graknlabs.graql.GraqlLanguage.GRAQL_TYPES;

/**
 * @author <a href="mailto:bfergerson@apache.org">Brandon Fergerson</a>
 */
public class GraqlParserDefinition implements ParserDefinition {

    public static Key<Boolean> WRAPPER_SET = new Key<>("graql.wrapper");
    public static GraqlParserDefinition INSTANCE;

    static {
        PSIElementTypeFactory.defineLanguageIElementTypes(
                GraqlLanguage.INSTANCE, GraqlParser.tokenNames, GraqlParser.ruleNames);
    }

    public static final IFileElementType FILE = new IFileElementType(GraqlLanguage.INSTANCE);
    public static final TokenSet IDS =
            PSIElementTypeFactory.createTokenSet(GraqlLanguage.INSTANCE, GraqlParser.LABEL_);
    public static final TokenSet COMMENTS =
            PSIElementTypeFactory.createTokenSet(GraqlLanguage.INSTANCE, GraqlLexer.COMMENT);
    public static final TokenSet WHITESPACE =
            PSIElementTypeFactory.createTokenSet(GraqlLanguage.INSTANCE, GraqlLexer.WS);
    public static final TokenSet STRING =
            PSIElementTypeFactory.createTokenSet(GraqlLanguage.INSTANCE, GraqlLexer.STRING_);
    public static final List<TokenIElementType> TOKEN_ELEMENT_TYPES =
            PSIElementTypeFactory.getTokenIElementTypes(GraqlLanguage.INSTANCE);
    public static final List<RuleIElementType> RULE_ELEMENT_TYPES =
            PSIElementTypeFactory.getRuleIElementTypes(GraqlLanguage.INSTANCE);

    public GraqlParserDefinition() {
        INSTANCE = this;
    }

    @NotNull
    @Override
    public Lexer createLexer(Project project) {
        GraqlLexer lexer = new GraqlLexer(null);
        return new ANTLRLexerAdaptor(GraqlLanguage.INSTANCE, lexer);
    }

    @NotNull
    public TokenSet getWhitespaceTokens() {
        return WHITESPACE;
    }

    @NotNull
    public TokenSet getCommentTokens() {
        return COMMENTS;
    }

    @NotNull
    public TokenSet getStringLiteralElements() {
        return STRING;
    }

    @NotNull
    public PsiParser createParser(final Project project) {
        GraqlParser parser = new GraqlParser(null);
        GraqlCompletionErrorListener completionErrorListener = new GraqlCompletionErrorListener();
        return new ANTLRParserAdaptor(GraqlLanguage.INSTANCE, parser) {
            @Override
            protected ParseTree parse(Parser parser, IElementType root) {
                parser.addErrorListener(completionErrorListener);
                if (root instanceof IFileElementType) {
                    return ((GraqlParser) parser).eof_queries();
                }
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    public IFileElementType getFileNodeType() {
        return FILE;
    }

    public PsiFile createFile(FileViewProvider viewProvider) {
        return new PsiGraqlFileBase(viewProvider);
    }

    @NotNull
    public PsiElement createElement(ASTNode node) {
        RuleIElementType ruleElType = (RuleIElementType) node.getElementType();
        switch (ruleElType.getRuleIndex()) {
            case GraqlParser.RULE_variable_type:
                return updateWrappedTypeIfNecessary(node, new PsiStatementType(node));
            case GraqlParser.RULE_type_constraint:
                PsiGraqlElement ruleTypePropertyElement = getRuleTypePropertyElement(node);
                if (ruleTypePropertyElement != null) {
                    return updateWrappedTypeIfNecessary(node, ruleTypePropertyElement);
                }
            case GraqlParser.RULE_type:
                PsiGraqlElement ruleTypeElement = getRuleTypeElement(node);
                if (ruleTypeElement != null) {
                    return updateWrappedTypeIfNecessary(node, ruleTypeElement);
                }
            default:
                return updateWrappedTypeIfNecessary(node, new PsiGraqlElement(node));
        }
    }

    @Nullable
    public static PsiGraqlElement getRuleTypeElement(ASTNode node) {
        if (node.getTreePrev() != null && node.getTreePrev().getTreePrev() != null
                && node.getTreePrev().getTreePrev().getText().equals("as")) {
            return new PsiRelatesSuperRoleTypeConstraint(node);
        } else if (node.getTreeParent() != null && node.getTreeParent().getTreeNext() != null
                && node.getTreeParent().getTreeNext().getTreeNext() != null
                && node.getTreeParent().getTreeNext().getTreeNext().getFirstChildNode() != null
                && node.getTreeParent().getTreeNext().getTreeNext().getFirstChildNode().getText().equals("sub")
                && node.getFirstChildNode() != null
                && node.getFirstChildNode().getElementType() == RULE_ELEMENT_TYPES.get(GraqlParser.RULE_label)) {
            return new PsiTypeConstraint(node);
        }
        return null;
    }

    @Nullable
    public static PsiGraqlElement getRuleTypePropertyElement(ASTNode node) {
        if (node.getFirstChildNode() != null && (node.getFirstChildNode().getText().equals("owns")
                || node.getFirstChildNode().getText().equals("key"))) {
            String ownsTo = node.getLastChildNode().getText();
            if (!ownsTo.isEmpty()) {
                return new PsiOwnsTypeConstraint(node);
            }
        } else if (node.getFirstChildNode() != null && node.getFirstChildNode().getText().equals("plays")) {
            String playsTo = node.getLastChildNode().getText();
            if (!playsTo.isEmpty()) {
                return new PsiPlaysTypeConstraint(node);
            }
        } else if (node.getFirstChildNode() != null && node.getFirstChildNode().getText().equals("relates")) {
            String relatesTo = node.getLastChildNode().getText();
            if (!relatesTo.isEmpty()) {
                return new PsiRelatesTypeConstraint(node);
            }
        } else if (node.getFirstChildNode() != null && node.getFirstChildNode().getText().equals("sub")) {
            String subsTo = node.getLastChildNode().getText();
            if (!subsTo.isEmpty() && !GRAQL_TYPES.contains(subsTo)) {
                return new PsiSubTypeConstraint(node);
            }
        }
        return null;
    }

    public static PsiGraqlElement updateWrappedTypeIfNecessary(ASTNode node, PsiGraqlElement element) {
        CompositeElement composite = (CompositeElement) node;
        Boolean wrapperSet = composite.getUserData(WRAPPER_SET);
        if (wrapperSet == null || (wrapperSet && element.getClass() != composite.getPsi().getClass())) {
            composite.setPsi(element);
            composite.putUserData(WRAPPER_SET, true);
        }
        return element;
    }
}