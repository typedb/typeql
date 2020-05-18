package ai.graknlabs.graql.highlighter;

import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.options.colors.AttributesDescriptor;
import com.intellij.openapi.options.colors.ColorDescriptor;
import com.intellij.openapi.options.colors.ColorSettingsPage;
import com.intellij.openapi.util.IconLoader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Map;

/**
 * @author <a href="mailto:bfergerson@apache.org">Brandon Fergerson</a>
 */
public class GraqlColorSettingsPage implements ColorSettingsPage {

    private static final AttributesDescriptor[] DESCRIPTORS = new AttributesDescriptor[]{
            new AttributesDescriptor("Line Comment", GraqlSyntaxHighlighter.LINE_COMMENT),
            new AttributesDescriptor("Keyword", GraqlSyntaxHighlighter.KEYWORD),
            new AttributesDescriptor("String", GraqlSyntaxHighlighter.STRING),
            new AttributesDescriptor("Number", GraqlSyntaxHighlighter.NUMBER),
            new AttributesDescriptor("Id", GraqlSyntaxHighlighter.ID),
            new AttributesDescriptor("Thing", GraqlSyntaxHighlighter.THING),
            new AttributesDescriptor("Bad Value", GraqlSyntaxHighlighter.BAD_CHARACTER)
    };

    @Nullable
    @Override
    public Icon getIcon() {
        return IconLoader.getIcon("/icons/grakn.png");
    }

    @NotNull
    @Override
    public SyntaxHighlighter getHighlighter() {
        return new GraqlSyntaxHighlighter();
    }

    @NotNull
    @Override
    public String getDemoText() {
        //todo: better demo text; doesn't show numbers/text/bad chars/etc
        return "#Example comment\n" +
                "define\n" +
                "\n" +
                "school-mutuality sub relation,\n" +
                "  relates schoolmate,\n" +
                "  relates mutual-school;\n" +
                "\n" +
                "people-gone-to-the-same-school sub rule,\n" +
                "  when {\n" +
                "    (student: $p1, enrolled-course: $c1) isa school-course-enrollment;\n" +
                "    (student: $p2, enrolled-course: $c2) isa school-course-enrollment;\n" +
                "    (offered-course: $c1, offering-school: $s) isa school-course-offering;\n" +
                "    (offered-course: $c2, offering-school: $s) isa school-course-offering;\n" +
                "    $p1 != $p2;\n" +
                "  }, then {\n" +
                "    (schoolmate: $p1, schoolmate: $p2, mutual-school: $s) isa school-mutuality;\n" +
                "  };";
    }

    @Nullable
    @Override
    public Map<String, TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap() {
        return null;
    }

    @NotNull
    @Override
    public AttributesDescriptor[] getAttributeDescriptors() {
        return DESCRIPTORS;
    }

    @NotNull
    @Override
    public ColorDescriptor[] getColorDescriptors() {
        return ColorDescriptor.EMPTY_ARRAY;
    }

    @NotNull
    @Override
    public String getDisplayName() {
        return "Graql";
    }
}