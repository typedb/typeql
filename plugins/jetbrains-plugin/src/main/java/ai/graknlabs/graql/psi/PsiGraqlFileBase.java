package ai.graknlabs.graql.psi;

import ai.graknlabs.graql.GraqlFileType;
import ai.graknlabs.graql.GraqlLanguage;
import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.util.IconLoader;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import org.antlr.intellij.adaptor.psi.ScopeNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * @author <a href="mailto:bfergerson@apache.org">Brandon Fergerson</a>
 */
public class PsiGraqlFileBase extends PsiFileBase implements ScopeNode {

    public PsiGraqlFileBase(@NotNull FileViewProvider viewProvider) {
        super(viewProvider, GraqlLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public FileType getFileType() {
        return GraqlFileType.INSTANCE;
    }

    @Override
    public String toString() {
        return "Graql Language file";
    }

    @Override
    public Icon getIcon(int flags) {
        return IconLoader.getIcon("/icons/grakn.png");
    }

    /**
     * Return null since a file scope has no enclosing scope. It is
     * not itself in a scope.
     * <p>
     * todo: a file could be in the scope of all the other files in the project
     */
    @Override
    public ScopeNode getContext() {
        return null;
    }

    @Nullable
    @Override
    public PsiElement resolve(PsiNamedElement element) {
        return null;
    }
}
