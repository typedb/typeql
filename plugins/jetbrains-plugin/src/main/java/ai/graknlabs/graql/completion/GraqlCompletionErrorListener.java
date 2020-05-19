package ai.graknlabs.graql.completion;

import org.antlr.intellij.adaptor.parser.SyntaxError;
import org.antlr.intellij.adaptor.parser.SyntaxErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:bfergerson@apache.org">Brandon Fergerson</a>
 */
public class GraqlCompletionErrorListener extends SyntaxErrorListener {

    //todo: evicting cache
    static Map<Integer, SyntaxError> tokenToErrorMap = new HashMap<>();

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer,
                            Object offendingSymbol,
                            int line, int charPositionInLine,
                            String msg, RecognitionException e) {
        super.syntaxError(recognizer, offendingSymbol, line, charPositionInLine, msg, e);

        for (SyntaxError error : getSyntaxErrors()) {
            tokenToErrorMap.put(error.getOffendingSymbol().getStartIndex(), error);
        }
    }
}
