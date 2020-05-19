package ai.graknlabs.graql.psi.statement;

import ai.graknlabs.graql.GraqlParserDefinition;
import ai.graknlabs.graql.psi.PsiGraqlElement;
import ai.graknlabs.graql.psi.property.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:bfergerson@apache.org">Brandon Fergerson</a>
 */
public class PsiStatementType extends PsiGraqlElement {

    public PsiStatementType(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public String getName() {
        return getFirstChild().getText();
    }

    public String getSubType() {
        //todo: seems wrong
        if (getNode().getFirstChildNode() == null || getNode().getFirstChildNode().getTreeNext() == null
                || getNode().getFirstChildNode().getTreeNext().getTreeNext() == null
                || getNode().getFirstChildNode().getTreeNext().getTreeNext().getLastChildNode() == null) {
            return null;
        }
        return getNode().getFirstChildNode().getTreeNext().getTreeNext().getLastChildNode().getText();
    }

    public List<PsiRelatesTypeProperty> findRelatesTypeProperties() {
        List<PsiRelatesTypeProperty> relatesTypes = new ArrayList<>();
        for (PsiElement child : getChildren()) {
            PsiElement element = GraqlParserDefinition.getRuleTypePropertyElement(child.getNode());
            if (element instanceof PsiRelatesTypeProperty) {
                relatesTypes.add((PsiRelatesTypeProperty) element);
            }
        }
        return relatesTypes;
    }

    public List<PsiPlaysTypeProperty> findPlaysTypeProperties() {
        List<PsiPlaysTypeProperty> playsTypes = new ArrayList<>();
        for (PsiElement child : getChildren()) {
            PsiElement element = GraqlParserDefinition.getRuleTypePropertyElement(child.getNode());
            if (element instanceof PsiPlaysTypeProperty) {
                playsTypes.add((PsiPlaysTypeProperty) element);
            }
        }
        return playsTypes;
    }

    public List<PsiHasTypeProperty> findHasTypeProperties() {
        List<PsiHasTypeProperty> hasTypes = new ArrayList<>();
        for (PsiElement child : getChildren()) {
            PsiElement element = GraqlParserDefinition.getRuleTypePropertyElement(child.getNode());
            if (element instanceof PsiHasTypeProperty) {
                hasTypes.add((PsiHasTypeProperty) element);
            }
        }
        return hasTypes;
    }

    public List<PsiSubTypeProperty> findSubTypeProperties() {
        List<PsiSubTypeProperty> subTypes = new ArrayList<>();
        for (PsiElement child : getChildren()) {
            PsiElement element = GraqlParserDefinition.getRuleTypePropertyElement(child.getNode());
            if (element instanceof PsiSubTypeProperty) {
                subTypes.add((PsiSubTypeProperty) element);
            }
        }
        return subTypes;
    }

    public List<PsiTypeProperty> findTypeProperties() {
        List<PsiTypeProperty> relatesTypes = new ArrayList<>();
        for (PsiElement child : getChildren()) {
            PsiElement element = GraqlParserDefinition.getRuleTypeElement(child.getNode());
            if (element instanceof PsiTypeProperty) {
                relatesTypes.add((PsiTypeProperty) element);
            }
        }
        return relatesTypes;
    }
}