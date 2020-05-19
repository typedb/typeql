package ai.graknlabs.graql.psi;

import ai.graknlabs.graql.GraqlFileType;
import ai.graknlabs.graql.psi.property.*;
import ai.graknlabs.graql.psi.statement.PsiStatementType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.impl.source.tree.CompositeElement;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import org.antlr.intellij.adaptor.lexer.RuleIElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static ai.graknlabs.graql.GraqlLanguage.GRAQL_TYPES;

/**
 * @author <a href="mailto:bfergerson@apache.org">Brandon Fergerson</a>
 */
public class GraqlPsiUtils {

    public static void ensureGraqlElementsUpToDate(PsiFile file) {
        try {
            PsiTreeUtil.collectElementsOfType(file, PsiGraqlElement.class).forEach(PsiGraqlElement::subtreeChanged);
        } catch (Throwable ignored) {
        }
    }

    public static List<PsiGraqlNamedElement> getDeclarationsByType(Project project, String type) {
        return getAllDeclarations(project).stream().filter(it -> Objects.equals(type, determineDeclarationType(it)))
                .collect(Collectors.toList());
    }

    public static List<PsiGraqlNamedElement> getAllDeclarations(Project project) {
        List<PsiGraqlNamedElement> declarations = new ArrayList<>();
        Collection<VirtualFile> virtualFiles = FileTypeIndex.getFiles(
                GraqlFileType.INSTANCE, GlobalSearchScope.allScope(project));
        for (VirtualFile virtualFile : virtualFiles) {
            PsiGraqlFileBase graqlFile = (PsiGraqlFileBase) PsiManager.getInstance(project).findFile(virtualFile);
            if (graqlFile != null) {
                Collection<PsiGraqlNamedElement> identifiers = PsiTreeUtil.collectElementsOfType(
                        graqlFile, PsiGraqlNamedElement.class);
                for (PsiGraqlNamedElement identifier : identifiers) {
                    declarations.add(identifier);
                }
            }
        }
        return declarations;
    }

    public static List<PsiGraqlElement> findUsages(Project project, PsiGraqlElement element, String name) {
        //todo: review logic in this method
        List<PsiGraqlElement> result = new ArrayList<>();
        Collection<VirtualFile> virtualFiles =
                FileTypeIndex.getFiles(GraqlFileType.INSTANCE, GlobalSearchScope.allScope(project));
        for (VirtualFile virtualFile : virtualFiles) {
            PsiGraqlFileBase graqlFile = (PsiGraqlFileBase) PsiManager.getInstance(project).findFile(virtualFile);
            if (graqlFile != null) {
                if (element instanceof PsiHasTypeProperty) {
                    Collection<PsiHasTypeProperty> hasIdentifiers = PsiTreeUtil.collectElementsOfType(
                            graqlFile, PsiHasTypeProperty.class);
                    for (PsiHasTypeProperty identifier : hasIdentifiers) {
                        if (name.equals(identifier.getHasType())) {
                            result.add(identifier);
                        }
                    }
                } else if (element instanceof PsiSubTypeProperty) {
                    Collection<PsiSubTypeProperty> subTypeIdentifiers = PsiTreeUtil.collectElementsOfType(
                            graqlFile, PsiSubTypeProperty.class);
                    for (PsiSubTypeProperty identifier : subTypeIdentifiers) {
                        if (name.equals(identifier.getSubType())) {
                            result.add(identifier);
                        }
                    }
                } else if (element instanceof PsiPlaysTypeProperty
                        || element instanceof PsiRelatesSuperRoleTypeProperty) {
                    Collection<PsiPlaysTypeProperty> playsIdentifiers = PsiTreeUtil.collectElementsOfType(
                            graqlFile, PsiPlaysTypeProperty.class);
                    for (PsiPlaysTypeProperty identifier : playsIdentifiers) {
                        if (name.equals(identifier.getPlaysType())) {
                            result.add(identifier);
                        }
                    }
                    Collection<PsiRelatesSuperRoleTypeProperty> relatesSuperRoleIdentifiers =
                            PsiTreeUtil.collectElementsOfType(graqlFile, PsiRelatesSuperRoleTypeProperty.class);
                    for (PsiRelatesSuperRoleTypeProperty identifier : relatesSuperRoleIdentifiers) {
                        if (name.equals(identifier.getText())) {
                            result.add(identifier);
                        }
                    }
                } else {
                    Collection<PsiHasTypeProperty> hasIdentifiers = PsiTreeUtil.collectElementsOfType(
                            graqlFile, PsiHasTypeProperty.class);
                    for (PsiHasTypeProperty identifier : hasIdentifiers) {
                        if (name.equals(identifier.getHasType())) {
                            result.add(identifier);
                        }
                    }
                    Collection<PsiSubTypeProperty> subTypeIdentifiers = PsiTreeUtil.collectElementsOfType(
                            graqlFile, PsiSubTypeProperty.class);
                    for (PsiSubTypeProperty identifier : subTypeIdentifiers) {
                        if (name.equals(identifier.getSubType())) {
                            result.add(identifier);
                        }
                    }
                    Collection<PsiPlaysTypeProperty> playsIdentifiers = PsiTreeUtil.collectElementsOfType(
                            graqlFile, PsiPlaysTypeProperty.class);
                    for (PsiPlaysTypeProperty identifier : playsIdentifiers) {
                        if (name.equals(identifier.getPlaysType())) {
                            result.add(identifier);
                        }
                    }
                    Collection<PsiRelatesSuperRoleTypeProperty> relatesSuperRoleIdentifiers =
                            PsiTreeUtil.collectElementsOfType(graqlFile, PsiRelatesSuperRoleTypeProperty.class);
                    for (PsiRelatesSuperRoleTypeProperty identifier : relatesSuperRoleIdentifiers) {
                        if (name.equals(identifier.getText())) {
                            result.add(identifier);
                        }
                    }
                }
            }
        }
        return result;
    }

    @Nullable
    public static PsiGraqlNamedElement findDeclaration(Project project, String name) {
        if (name == null) {
            return null;
        }

        List<PsiGraqlNamedElement> declarations = findDeclarations(project, name);
        if (declarations.isEmpty()) {
            return null;
        } else {
            return declarations.get(0);
        }
    }

    @NotNull
    public static List<PsiGraqlNamedElement> findDeclarations(Project project, String name) {
        List<PsiGraqlNamedElement> declarations = new ArrayList<>();
        Collection<VirtualFile> virtualFiles = FileTypeIndex.getFiles(
                GraqlFileType.INSTANCE, GlobalSearchScope.allScope(project));
        for (VirtualFile virtualFile : virtualFiles) {
            PsiGraqlFileBase graqlFile = (PsiGraqlFileBase) PsiManager.getInstance(project).findFile(virtualFile);
            if (graqlFile != null) {
                Collection<PsiGraqlNamedElement> identifiers = PsiTreeUtil.collectElementsOfType(
                        graqlFile, PsiGraqlNamedElement.class);
                for (PsiGraqlNamedElement identifier : identifiers) {
                    if (name.equals(identifier.getName())) {
                        declarations.add(identifier);
                    }
                }
            }
        }
        return declarations;
    }

    public static String determineDeclarationType(PsiGraqlNamedElement identifier) {
        String subType = ((PsiStatementType) identifier.getParent()).getSubType();
        if (identifier instanceof PsiRelatesTypeProperty) {
            return "role";
        } else if (GRAQL_TYPES.contains(subType)) {
            return subType;
        }
        PsiGraqlNamedElement declaration = findDeclaration(identifier.getProject(), subType);
        if (declaration == null || declaration == identifier) {
            return null;
        } else {
            return determineDeclarationType(declaration);
        }
    }

    public static PsiElement setName(PsiGraqlElement element, String newName) {
        if (element instanceof PsiTypeProperty) {
            PsiGraqlNamedElement typeProperty = GraqlPsiElementFactory.createTypeProperty(element.getProject(), newName);
            element.getParent().getNode().replaceChild(element.getNode(), typeProperty.getNode());
        } else if (element instanceof PsiSubTypeProperty) {
            PsiGraqlElement subTypeProperty = GraqlPsiElementFactory.createSubTypeProperty(element.getProject(), newName);
            element.getParent().getNode().replaceChild(element.getNode(), subTypeProperty.getNode());
        } else if (element instanceof PsiRelatesTypeProperty) {
            PsiGraqlNamedElement typeProperty = GraqlPsiElementFactory.createRelatesTypeProperty(element.getProject(), newName);
            element.getNode().replaceChild(element.getFirstChild().getNextSibling().getNextSibling().getNode(),
                    typeProperty.getFirstChild().getNextSibling().getNextSibling().getNode());
        } else if (element instanceof PsiPlaysTypeProperty) {
            PsiGraqlElement playsTypeProperty = GraqlPsiElementFactory.createPlaysTypeProperty(element.getProject(), newName);
            element.getParent().getNode().replaceChild(element.getNode(), playsTypeProperty.getNode());
        } else {
            throw new UnsupportedOperationException();
        }
        return element;
    }

    public static PsiGraqlElement findParentByType(PsiElement element, RuleIElementType ruleElementType) {
        PsiElement parent = element;
        while ((parent = parent.getParent()) != null) {
            if (parent instanceof PsiGraqlElement) {
                CompositeElement compositeElement = ((PsiGraqlElement) parent).getNode();
                if (compositeElement.getElementType() == ruleElementType) {
                    return (PsiGraqlElement) compositeElement.getPsi();
                }
            }
        }
        return null;
    }
}
