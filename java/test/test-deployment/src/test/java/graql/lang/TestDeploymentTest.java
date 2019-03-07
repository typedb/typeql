package graql.lang;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Unit test for simple App.
 */
public class TestDeploymentTest {
    @Test
    public void test() {
        final String query = "match $t sub thing; get;";
        assertEquals(query, Graql.parse(query).toString());
    }
}
