package ai.graknlabs.graql.refactor;

import ai.graknlabs.graql.GraqlParser;
import com.intellij.lang.refactoring.NamesValidator;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href="mailto:bfergerson@apache.org">Brandon Fergerson</a>
 */
public class GraqlNamesValidator implements NamesValidator {

    //todo: filter for real keywords ("VAR_" isn't keyword)
    public static final Set<String> GRAQL_KEYWORDS = new HashSet<>(Arrays.asList(GraqlParser.tokenNames));

    @Override
    public boolean isKeyword(@NotNull final String name, final Project project) {
        return GRAQL_KEYWORDS.contains("'" + name + "'");
    }

    @Override
    public boolean isIdentifier(@NotNull final String name, final Project project) {
        return !isKeyword(name, project);
    }
}
