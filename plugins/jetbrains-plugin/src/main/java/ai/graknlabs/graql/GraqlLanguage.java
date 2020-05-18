package ai.graknlabs.graql;

import com.google.common.collect.ImmutableSet;
import com.intellij.lang.Language;

import java.util.Set;

/**
 * @author <a href="mailto:bfergerson@apache.org">Brandon Fergerson</a>
 */
public class GraqlLanguage extends Language {

    public static final GraqlLanguage INSTANCE = new GraqlLanguage();
    public static final Set<String> GRAQL_TYPES = ImmutableSet.of("attribute", "entity", "relation", "rule");

    private GraqlLanguage() {
        super("Graql");
    }
}
