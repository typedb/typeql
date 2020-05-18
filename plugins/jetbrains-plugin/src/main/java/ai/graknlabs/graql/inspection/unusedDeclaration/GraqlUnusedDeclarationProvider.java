package ai.graknlabs.graql.inspection.unusedDeclaration;

import com.intellij.codeInspection.InspectionToolProvider;

/**
 * @author <a href="mailto:bfergerson@apache.org">Brandon Fergerson</a>
 */
public class GraqlUnusedDeclarationProvider implements InspectionToolProvider {

    public Class[] getInspectionClasses() {
        return new Class[]{
                GraqlUnusedAttributeDeclarationInspection.class,
                GraqlUnusedRoleDeclarationInspection.class
        };
    }
}
