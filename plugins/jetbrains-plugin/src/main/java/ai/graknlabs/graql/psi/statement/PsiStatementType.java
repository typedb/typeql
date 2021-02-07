package ai.graknlabs.graql.psi.statement;

import ai.graknlabs.graql.GraqlParserDefinition;
import ai.graknlabs.graql.psi.PsiGraqlElement;
import ai.graknlabs.graql.psi.constraint.*;
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

    public List<PsiRelatesTypeConstraint> findRelatesTypeProperties() {
        List<PsiRelatesTypeConstraint> relatesTypes = new ArrayList<>();
        for (PsiElement child : getChildren()) {
            PsiElement element = GraqlParserDefinition.getRuleTypePropertyElement(child.getNode());
            if (element instanceof PsiRelatesTypeConstraint) {
                relatesTypes.add((PsiRelatesTypeConstraint) element);
            }
        }
        return relatesTypes;
    }

    public List<PsiPlaysTypeConstraint> findPlaysTypeProperties() {
        List<PsiPlaysTypeConstraint> playsTypes = new ArrayList<>();
        for (PsiElement child : getChildren()) {
            PsiElement element = GraqlParserDefinition.getRuleTypePropertyElement(child.getNode());
            if (element instanceof PsiPlaysTypeConstraint) {
                playsTypes.add((PsiPlaysTypeConstraint) element);
            }
        }
        return playsTypes;
    }

    public List<PsiOwnsTypeConstraint> findOwnsTypeProperties() {
        List<PsiOwnsTypeConstraint> ownsTypes = new ArrayList<>();
        for (PsiElement child : getChildren()) {
            PsiElement element = GraqlParserDefinition.getRuleTypePropertyElement(child.getNode());
            if (element instanceof PsiOwnsTypeConstraint) {
                ownsTypes.add((PsiOwnsTypeConstraint) element);
            }
        }
        return ownsTypes;
    }

    public List<PsiSubTypeConstraint> findSubTypeProperties() {
        List<PsiSubTypeConstraint> subTypes = new ArrayList<>();
        for (PsiElement child : getChildren()) {
            PsiElement element = GraqlParserDefinition.getRuleTypePropertyElement(child.getNode());
            if (element instanceof PsiSubTypeConstraint) {
                subTypes.add((PsiSubTypeConstraint) element);
            }
        }
        return subTypes;
    }

    public List<PsiTypeConstraint> findTypeProperties() {
        List<PsiTypeConstraint> relatesTypes = new ArrayList<>();
        for (PsiElement child : getChildren()) {
            PsiElement element = GraqlParserDefinition.getRuleTypeElement(child.getNode());
            if (element instanceof PsiTypeConstraint) {
                relatesTypes.add((PsiTypeConstraint) element);
            }
        }
        return relatesTypes;
    }
}