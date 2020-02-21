package ai.graknlabs.graql;

import ai.graknlabs.graql.psi.PsiGraqlFileBase;
import ai.graknlabs.graql.psi.property.*;
import ai.graknlabs.graql.psi.statement.PsiStatementType;
import com.intellij.lang.ASTNode;
import com.intellij.lang.ParserDefinition;
import com.intellij.lang.PsiParser;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.TokenSet;
import org.antlr.intellij.adaptor.lexer.ANTLRLexerAdaptor;
import org.antlr.intellij.adaptor.lexer.PSIElementTypeFactory;
import org.antlr.intellij.adaptor.lexer.RuleIElementType;
import org.antlr.intellij.adaptor.lexer.TokenIElementType;
import org.antlr.intellij.adaptor.parser.ANTLRParserAdaptor;
import org.antlr.intellij.adaptor.psi.ANTLRPsiNode;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.tree.ParseTree;
import org.jetbrains.annotations.NotNull;

import static ai.graknlabs.graql.GraqlLanguage.GRAQL_TYPES;

/**
 * @author <a href="mailto:brandon@srcpl.us">Brandon Fergerson</a>
 */
public class GraqlParserDefinition implements ParserDefinition {

    public static final IFileElementType FILE =
            new IFileElementType(GraqlLanguage.INSTANCE);

    static {
        PSIElementTypeFactory.defineLanguageIElementTypes(GraqlLanguage.INSTANCE,
                GraqlParser.tokenNames,
                GraqlParser.ruleNames);
    }

    public static final TokenSet IDS =
            PSIElementTypeFactory.createTokenSet(
                    GraqlLanguage.INSTANCE, GraqlParser.TYPE_NAME_);

    public static final TokenSet COMMENTS =
            PSIElementTypeFactory.createTokenSet(
                    GraqlLanguage.INSTANCE,
                    GraqlLexer.COMMENT);

    public static final TokenSet WHITESPACE =
            PSIElementTypeFactory.createTokenSet(
                    GraqlLanguage.INSTANCE,
                    GraqlLexer.WS);

    public static final TokenSet STRING =
            PSIElementTypeFactory.createTokenSet(
                    GraqlLanguage.INSTANCE,
                    GraqlLexer.STRING_);

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
        return new ANTLRParserAdaptor(GraqlLanguage.INSTANCE, new GraqlParser(null)) {
            @Override
            protected ParseTree parse(Parser parser, IElementType root) {
                if (root instanceof IFileElementType) {
                    return ((GraqlParser) parser).eof_query_list();
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
        IElementType elType = node.getElementType();
        if (elType instanceof TokenIElementType) {
            return new ANTLRPsiNode(node);
        }
        if (!(elType instanceof RuleIElementType)) {
            return new ANTLRPsiNode(node);
        }

        RuleIElementType ruleElType = (RuleIElementType) elType;
        switch (ruleElType.getRuleIndex()) {
            case GraqlParser.RULE_statement_type:
                return new PsiStatementType(node);
            case GraqlParser.RULE_type_property:
                if (node.getFirstChildNode() != null && node.getFirstChildNode().getText().equals("has")) {
                    return new PsiHasTypeProperty(node);
                } else if (node.getFirstChildNode() != null && node.getFirstChildNode().getText().equals("plays")) {
                    return new PsiPlaysTypeProperty(node);
                } else if (node.getFirstChildNode() != null && node.getFirstChildNode().getText().equals("relates")) {
                    return new PsiRelatesTypeProperty(node);
                } else if (node.getFirstChildNode() != null && node.getFirstChildNode().getText().equals("sub") &&
                        !GRAQL_TYPES.contains(node.getLastChildNode().getText())) {
                    return new PsiSubTypeProperty(node);
                }
            case GraqlParser.RULE_type:
                if (node.getTreePrev() != null && node.getTreePrev().getTreePrev() != null
                        && node.getTreePrev().getTreePrev().getText().equals("as")) {
                    return new PsiRelatesSuperRoleTypeProperty(node);
                } else if (node.getTreeNext() != null && node.getTreeNext().getTreeNext() != null
                        && node.getTreeNext().getTreeNext().getFirstChildNode() != null
                        && node.getTreeNext().getTreeNext().getFirstChildNode().getText().equals("sub")) {
                    return new PsiTypeProperty(node);
                }
            default:
                return new ANTLRPsiNode(node);
        }
    }
}