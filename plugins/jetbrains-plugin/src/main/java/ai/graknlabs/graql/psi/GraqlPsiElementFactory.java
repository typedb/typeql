package ai.graknlabs.graql.psi;

import ai.graknlabs.graql.GraqlFileType;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFileFactory;

/**
 * @author <a href="mailto:bfergerson@apache.org">Brandon Fergerson</a>
 */
public class GraqlPsiElementFactory {

    public static PsiGraqlNamedElement createTypeProperty(Project project, String name) {
        String dummyText = String.format("define\n%s sub entity;", name);
        final PsiGraqlFileBase file = createFile(project, dummyText);
        return (PsiGraqlNamedElement) file.getFirstChild().getFirstChild().getFirstChild().getLastChild().getFirstChild();
    }

    public static PsiGraqlElement createSubTypeProperty(Project project, String name) {
        String dummyText = String.format("define\ndummy sub %s;", name);
        final PsiGraqlFileBase file = createFile(project, dummyText);
        return (PsiGraqlElement) file.getFirstChild().getFirstChild().getFirstChild().getLastChild().getFirstChild()
                .getNextSibling().getNextSibling();
    }

    public static PsiGraqlNamedElement createRelatesTypeProperty(Project project, String name) {
        String dummyText = String.format("define\ndummy sub entity, relates %s;", name);
        final PsiGraqlFileBase file = createFile(project, dummyText);
        return (PsiGraqlNamedElement) file.getFirstChild().getFirstChild().getFirstChild().getLastChild().getFirstChild()
                .getNextSibling().getNextSibling().getNextSibling().getNextSibling().getNextSibling();
    }

    public static PsiGraqlElement createPlaysTypeProperty(Project project, String name) {
        String dummyText = String.format("define\ndummy sub entity, plays %s;", name);
        final PsiGraqlFileBase file = createFile(project, dummyText);
        return (PsiGraqlElement) file.getFirstChild().getFirstChild().getFirstChild().getLastChild().getFirstChild()
                .getNextSibling().getNextSibling().getNextSibling().getNextSibling().getNextSibling();
    }

    public static PsiGraqlFileBase createFile(Project project, String text) {
        return (PsiGraqlFileBase) PsiFileFactory.getInstance(project).createFileFromText(
                "dummy.gql", GraqlFileType.INSTANCE, text);
    }
}
