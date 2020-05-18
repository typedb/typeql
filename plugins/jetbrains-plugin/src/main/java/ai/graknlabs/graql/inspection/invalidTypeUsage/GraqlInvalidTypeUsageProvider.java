package ai.graknlabs.graql.inspection.invalidTypeUsage;

import com.intellij.codeInspection.InspectionToolProvider;

/**
 * @author <a href="mailto:bfergerson@apache.org">Brandon Fergerson</a>
 */
public class GraqlInvalidTypeUsageProvider implements InspectionToolProvider {

    public Class[] getInspectionClasses() {
        return new Class[]{GraqlInvalidTypeUsageInspection.class};
    }
}
