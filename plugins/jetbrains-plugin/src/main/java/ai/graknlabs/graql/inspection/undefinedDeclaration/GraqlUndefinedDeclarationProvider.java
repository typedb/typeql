package ai.graknlabs.graql.inspection.undefinedDeclaration;

import com.intellij.codeInspection.InspectionToolProvider;

/**
 * @author <a href="mailto:bfergerson@apache.org">Brandon Fergerson</a>
 */
public class GraqlUndefinedDeclarationProvider implements InspectionToolProvider {

    public Class[] getInspectionClasses() {
        return new Class[]{GraqlUndefinedDeclarationInspection.class};
    }
}
