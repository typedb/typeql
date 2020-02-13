package ai.graknlabs.graql;

import com.intellij.testFramework.ParsingTestCase;

public class GraqlParsingTest extends ParsingTestCase {

    public GraqlParsingTest() {
        super("", "gql", new GraqlParserDefinition());
    }

    public void testParsingTestData() {
        doTest(true);
    }

    @Override
    protected String getTestDataPath() {
        return "src/test/resources";
    }

    @Override
    protected boolean skipSpaces() {
        return false;
    }

    @Override
    protected boolean includeRanges() {
        return true;
    }
}