package ai.graknlabs.graql.completion;

import org.antlr.intellij.adaptor.parser.SyntaxError;
import org.antlr.intellij.adaptor.parser.SyntaxErrorListener;

import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:bfergerson@apache.org">Brandon Fergerson</a>
 */
public class GraqlCompletionErrorListener extends SyntaxErrorListener {

    private static GraqlCompletionErrorListener INSTANCE;

    public GraqlCompletionErrorListener() {
        INSTANCE = this;
    }

    static Map<Integer, SyntaxError> getTokenToErrorMap() {
        Map<Integer, SyntaxError> tokenToErrorMap = new HashMap<>();
        for (SyntaxError error : INSTANCE.getSyntaxErrors()) {
            tokenToErrorMap.put(error.getOffendingSymbol().getStartIndex(), error);
        }
        return tokenToErrorMap;
    }
}
