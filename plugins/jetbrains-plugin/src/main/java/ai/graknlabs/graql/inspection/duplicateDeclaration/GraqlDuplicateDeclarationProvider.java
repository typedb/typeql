package ai.graknlabs.graql.inspection.duplicateDeclaration;

import com.intellij.codeInspection.InspectionToolProvider;

/**
 * @author <a href="mailto:brandon@srcpl.us">Brandon Fergerson</a>
 */
public class GraqlDuplicateDeclarationProvider implements InspectionToolProvider {

    public Class[] getInspectionClasses() {
        return new Class[]{GraqlDuplicateDeclarationInspection.class};
    }
}
