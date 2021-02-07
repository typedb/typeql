package ai.graknlabs.graql.highlighter;

import ai.graknlabs.graql.GraqlLanguage;
import ai.graknlabs.graql.GraqlLexer;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.HighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import org.antlr.intellij.adaptor.lexer.ANTLRLexerAdaptor;
import org.antlr.intellij.adaptor.lexer.PSIElementTypeFactory;
import org.antlr.intellij.adaptor.lexer.TokenIElementType;
import org.jetbrains.annotations.NotNull;

import static com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey;

/**
 * @author <a href="mailto:bfergerson@apache.org">Brandon Fergerson</a>
 */
public class GraqlSyntaxHighlighter extends SyntaxHighlighterBase {

    private static final TextAttributesKey[] EMPTY_KEYS = new TextAttributesKey[0];
    protected static final TextAttributesKey LINE_COMMENT =
            createTextAttributesKey("LINE_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT);
    protected static final TextAttributesKey KEYWORD =
            createTextAttributesKey("GQL_KEYWORD", DefaultLanguageHighlighterColors.KEYWORD);
    protected static final TextAttributesKey STRING =
            createTextAttributesKey("GQL_STRING", DefaultLanguageHighlighterColors.STRING);
    protected static final TextAttributesKey NUMBER =
            createTextAttributesKey("GQL_NUMBER", DefaultLanguageHighlighterColors.NUMBER);
    protected static final TextAttributesKey ID =
            createTextAttributesKey("GQL_IDENTIFIER", DefaultLanguageHighlighterColors.CONSTANT);
    protected static final TextAttributesKey THING =
            createTextAttributesKey("GQL_THING", DefaultLanguageHighlighterColors.IDENTIFIER);
    protected static final TextAttributesKey BAD_CHARACTER =
            createTextAttributesKey("SIMPLE_BAD_CHARACTER", HighlighterColors.BAD_CHARACTER);

    static {
        PSIElementTypeFactory.defineLanguageIElementTypes(
                GraqlLanguage.INSTANCE, GraqlLexer.tokenNames, GraqlLexer.ruleNames);
    }

    @NotNull
    @Override
    public Lexer getHighlightingLexer() {
        GraqlLexer lexer = new GraqlLexer(null);
        return new ANTLRLexerAdaptor(GraqlLanguage.INSTANCE, lexer);
    }

    @NotNull
    @Override
    public TextAttributesKey[] getTokenHighlights(IElementType tokenType) {
        if (!(tokenType instanceof TokenIElementType)) return EMPTY_KEYS;
        if (tokenType.equals(TokenType.BAD_CHARACTER)) return new TextAttributesKey[]{BAD_CHARACTER};
        TokenIElementType myType = (TokenIElementType) tokenType;
        int type = myType.getANTLRTokenType();
        TextAttributesKey attrKey;
        switch (type) {
            case GraqlLexer.IID_:
            case GraqlLexer.VAR_:
            case GraqlLexer.VAR_NAMED_:
            case GraqlLexer.LABEL_:
            case GraqlLexer.LABEL_SCOPED_:
                attrKey = ID;
                break;
            case GraqlLexer.THING:
            case GraqlLexer.ENTITY:
            case GraqlLexer.ATTRIBUTE:
            case GraqlLexer.RELATION:
            case GraqlLexer.ROLE:
            case GraqlLexer.RULE:
                attrKey = THING;
                break;
            case GraqlLexer.DEFINE:
            case GraqlLexer.UNDEFINE:
            case GraqlLexer.MATCH:
            case GraqlLexer.GET:
            case GraqlLexer.INSERT:
            case GraqlLexer.DELETE:
            case GraqlLexer.COMPUTE:
            case GraqlLexer.OFFSET:
            case GraqlLexer.LIMIT:
            case GraqlLexer.GROUP:
            case GraqlLexer.SORT:
            case GraqlLexer.ASC:
            case GraqlLexer.DESC:
            case GraqlLexer.CENTRALITY:
            case GraqlLexer.USING:
            case GraqlLexer.ABSTRACT:
            case GraqlLexer.AS:
            case GraqlLexer.TYPE:
            case GraqlLexer.ISA:
            case GraqlLexer.ISAX:
            case GraqlLexer.ISA_:
            case GraqlLexer.SUB:
            case GraqlLexer.SUBX:
            case GraqlLexer.SUB_:
            case GraqlLexer.OWNS:
            case GraqlLexer.PLAYS:
            case GraqlLexer.RELATES:
            case GraqlLexer.VALUE:
            case GraqlLexer.REGEX:
            case GraqlLexer.WHEN:
            case GraqlLexer.THEN:
            case GraqlLexer.LONG:
            case GraqlLexer.DOUBLE:
            case GraqlLexer.STRING:
            case GraqlLexer.BOOLEAN:
            case GraqlLexer.DATETIME:
                attrKey = KEYWORD;
                break;
            case GraqlLexer.COMMENT:
                attrKey = LINE_COMMENT;
                break;
            case GraqlLexer.LONG_:
            case GraqlLexer.DOUBLE_:
            case GraqlLexer.BOOLEAN_:
            case GraqlLexer.DATE_:
            case GraqlLexer.DATETIME_:
                attrKey = NUMBER;
                break;
            case GraqlLexer.STRING_:
                attrKey = STRING;
                break;
            default:
                return EMPTY_KEYS;
        }
        return new TextAttributesKey[]{attrKey};
    }
}