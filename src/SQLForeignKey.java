import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SQLForeignKey extends SQLEntity {
    private final String table;
    private final String column;
    private String onDelete;
    private String onUpdate;

    public SQLForeignKey(String name, String comment, String newTable, String newColumn, String ons) {
        super(name, comment);
        table = newTable;
        column = newColumn;
        Pattern deletePattern = Pattern.compile("DELETE\\s+(?<action>[^,\\n]+)", Pattern.CASE_INSENSITIVE);
        Matcher delMatcher = deletePattern.matcher(ons);
        Pattern updatePattern = Pattern.compile("UPDATE\\s+(?<action>[^,\\n]+)", Pattern.CASE_INSENSITIVE);
        Matcher updMatcher = updatePattern.matcher(ons);

        if (delMatcher.find()) {
            onDelete = delMatcher.group("action");
        }

        if (updMatcher.find()) {
            onUpdate = updMatcher.group("action");
        }
    }

    @Override
    public String toString() {
        return String.format(FOREIGN_TEMPLATE, name, getRef(), getOnDelete(), getOnUpdate(), comment);
    }

    public String toMD() {
        return mdTableRow(name, getRefMD(), getOnDelete(), getOnUpdate(), comment);
    }

    public String getOnDelete() {
        return onDelete != null ? onDelete : "";
    }

    public String getOnUpdate() {
        return onUpdate != null ? onUpdate : "";
    }

    public String getRef() {
        return table.trim() + "." + column.trim();
    }

    public String getRefMD() {
        return '`' + getRef() + '`';
    }
}
