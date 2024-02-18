import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SQLTable extends SQLEntity {
    final private String removePattern = "(?i)(UNIQUE INDEX.+|FOREIGN[^,]+,|INDEX.+)";
    final private Pattern fieldPattern = Pattern.compile(
            "(?<name>\\S+)(?:\\s)(?<type>(?:\\sUNSIGNED|\\sPRECISION|\\S)+)\\s(?<modifiers>(?:[^,\\-\\n]+))?(?:,)?(?:\\s+)?(?:--\\s+(?<comment>.+))?");
    final private Pattern indexPattern = Pattern.compile(
            "(?<unique>UNIQUE(?:\\s+)(?:\\n)?)?INDEX\\s+(?:\\n)?(?<name>\\S+)\\s+\\((?<columns>[^)]+|\\n+)\\)(?:,)?(?:\\s+--\\s+(?<comment>.+))?");
    final private Pattern foreignPattern = Pattern.compile(
            "FOREIGN KEY\\s+\\((?<name>\\S+)\\)(?:\\s+--\\s+(?<comment>.+))?(?:\\s+|\\n)REFERENCES\\s+(?<table>[^(]+)\\((?<column>[^)]+)\\)(?:\\s|\\n)(?<ons>[^,]+)",
            Pattern.CASE_INSENSITIVE);
    final private int FIELD_TABLE_WIDTH = 157;
    final private int INDEX_TABLE_WIDTH = 119;
    final private int KEY_TABLE_WIDTH = 129;
    private ArrayList<SQLField> fields;
    private ArrayList<SQLIndex> indexes;
    private ArrayList<SQLForeignKey> foreignKeys;

    public SQLTable(String name, String comment, String fieldString) {
        super(name, comment);
        Matcher fm = fieldPattern.matcher(fieldString.replaceAll(removePattern, ""));
        Matcher im = indexPattern.matcher(fieldString);
        Matcher km = foreignPattern.matcher(fieldString);
        fields = new ArrayList<SQLField>();
        indexes = new ArrayList<SQLIndex>();
        foreignKeys = new ArrayList<SQLForeignKey>();

        while (fm.find()) {
            String fieldName = fm.group("name");
            if (!fieldName.toUpperCase().equals("INDEX") && !fieldName.toUpperCase().equals("UNIQUE")
                    && !fieldName.toUpperCase().equals("FOREIGN")) {
                fields.add(new SQLField(fm.group("name"), fm.group("type"), fm.group("comment"), fm.group("modifiers")));
            }
        }

        while (im.find()) {
            indexes.add(
                    new SQLIndex(im.group("name"), im.group("comment"), im.group("unique") != null, im.group("columns")));
        }

        while (km.find()) {
            foreignKeys.add(new SQLForeignKey(km.group("name"), km.group("comment"), km.group("table"), km.group("column"),
                    km.group("ons")));
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(String.format("TABLE %s%n", name));
        sb.append(String.format("- %s%n", comment.replaceAll("\n", "")));

        if (fields.size() == 0) {
            sb.append("No fields\n");
        } else {
            sb.append(tableLine(FIELD_TABLE_WIDTH));

            sb.append(String.format("| \033[3m%-" + (FIELD_TABLE_WIDTH - 4) + "s\033[0m |%n", "COLUMNS"));

            sb.append(tableLine(FIELD_TABLE_WIDTH));

            sb.append(String.format(FIELD_TEMPLATE, "Name", "Type", "Unique", "NULL", "Default", "On Update", "Comment"));

            sb.append(tableLine(FIELD_TABLE_WIDTH));

            for (SQLField field : fields) {
                sb.append(field.toString());
            }

            sb.append(tableLine(FIELD_TABLE_WIDTH));
        }

        sb.append("\n");

        if (indexes.size() == 0) {
            sb.append("No indexes\n");
        } else {
            sb.append(tableLine(INDEX_TABLE_WIDTH));

            sb.append(String.format("| \033[3m%-" + (INDEX_TABLE_WIDTH - 4) + "s\033[0m |%n", "INDEXES"));

            sb.append(tableLine(INDEX_TABLE_WIDTH));

            sb.append(String.format(INDEX_TEMPLATE, "Name", "Unique", "Columns", "Comment"));

            sb.append(tableLine(INDEX_TABLE_WIDTH));

            for (SQLIndex i : indexes) {
                sb.append(i.toString());
            }

            sb.append(tableLine(INDEX_TABLE_WIDTH));
        }

        sb.append("\n");

        if (foreignKeys.size() == 0) {
            sb.append("No foreign keys\n");
        } else {
            sb.append(tableLine(KEY_TABLE_WIDTH));

            sb.append(String.format("| \033[3m%-" + (KEY_TABLE_WIDTH - 4) + "s\033[0m |%n", "FOREIGN KEYS"));

            sb.append(tableLine(KEY_TABLE_WIDTH));

            sb.append(String.format(FOREIGN_TEMPLATE, "Name", "References", "On Delete", "On Update", "Comment"));

            sb.append(tableLine(KEY_TABLE_WIDTH));

            for (SQLForeignKey k : foreignKeys) {
                sb.append(k.toString());
            }

            sb.append(tableLine(KEY_TABLE_WIDTH));
        }

        return sb.toString();
    }

    public String toMD() {
        StringBuilder sb = new StringBuilder(String.format("## `%s`%n", name));
        sb.append("> " + getCommentMD() + "\n\n" + backToTop());

        sb.append("### Fields\n");

        if (fields.size() == 0) {
            sb.append("This table has no fields.\n");
        } else {
            sb.append(mdTableHeader("Name", "Type", "Unique", "NULL", "Default", "On Update", "Comment"));

            for (SQLField f : fields) {
                sb.append(f.toMD());
            }
        }

        sb.append("\n");

        sb.append("### Indexes\n");

        if (indexes.size() == 0) {
            sb.append("This table has no explicit indexes.\n");
        } else {
            sb.append(mdTableHeader("Name", "Unique", "Columns", "Comment"));

            for (SQLIndex i : indexes) {
                sb.append(i.toMD());
            }
        }

        sb.append("\n");

        sb.append("### Relationships\n");

        if (foreignKeys.size() == 0) {
            sb.append("This table has no relationships");
        } else {
            sb.append(mdTableHeader("Name", "References", "On Delete", "On Update", "Comment"));

            for (SQLForeignKey k : foreignKeys) {
                sb.append(k.toMD());
            }
        }

        return sb.toString();
    }
}
