package graql.lang;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

// TODO: think of a better class name
public class TestDeploymentTest {
    // TODO: more tests
    @Test
    public void queryBuilder_shouldConvertFromAndToStringCorrectly() {
        final String query = "match $t sub thing; get;";
        assertEquals(query, Graql.parse(query).toString());
    }
}
