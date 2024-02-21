import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SQLField extends SQLEntity {
    private final boolean key;
    private final boolean unique;
    private final boolean nullable;
    private final String type;
    private final String defaultValue;
    private final String onUpdate;

    public SQLField(String name, String newType, String newComment, String modifiers) {
        super(name, (newComment == null ? "" : newComment.replaceAll("(?: +)?-- +", "").replaceAll("\n", " ")));
        type = newType;
        Pattern defaultValuePattern = Pattern
                .compile("DEFAULT (?<default>\".+\"|[^ ,]+),?\\n?", Pattern.CASE_INSENSITIVE);
        Matcher dm = defaultValuePattern.matcher(modifiers);
        Pattern updateValuePattern = Pattern.compile("ON UPDATE(?: |\\n)+(?<action>\"[^\"]+\"| +)",
                Pattern.CASE_INSENSITIVE);
        Matcher up = updateValuePattern.matcher(modifiers);

        if (dm.find()) {
            defaultValue = dm.group("default");
        } else {
            defaultValue = null;
        }

        if (up.find()) {
            onUpdate = up.group("action");
        } else {
            onUpdate = null;
        }

        modifiers = modifiers.toUpperCase();

        nullable = !modifiers.contains("NOT NULL");
        unique = modifiers.contains("UNIQUE");
        key = modifiers.contains("KEY");
    }

    @Override
    public String toString() {
        return String.format(FIELD_TEMPLATE, getName(), type, getKey(), getNullable(), getDefault(), getOnUpdate(), getPlainComment());
    }

    public String toMD() {
        return mdTableRow(getName(), type, getKey(), getNullable(), getDefault(), getOnUpdate(), getCommentMD());
    }

    private String getKey() {
        return key ? "PRI" : (unique ? "YES" : "");
    }

    private String getNullable() {
        return nullable ? "YES" : "NO";
    }

    private String getDefault() {
        return defaultValue == null ? "NULL" : defaultValue.replaceAll("['\"]", "");
    }

    private String getOnUpdate() {
        return onUpdate == null ? "" : onUpdate;
    }
}
