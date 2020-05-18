package ai.graknlabs.graql.inspection.unplayedRole;

import com.intellij.codeInspection.InspectionToolProvider;

/**
 * @author <a href="mailto:bfergerson@apache.org">Brandon Fergerson</a>
 */
public class GraqlUnplayedRoleProvider implements InspectionToolProvider {

    public Class[] getInspectionClasses() {
        return new Class[]{GraqlUnplayedRoleInspection.class};
    }
}
