import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SQLTable extends SQLEntity {
    private final ArrayList<SQLField> fields;
    private final ArrayList<SQLIndex> indexes;
    private final ArrayList<SQLForeignKey> foreignKeys;
    private final ArrayList<SQLTrigger> triggers;

    public SQLTable(String name, String comment, String fieldString) {
        super(name, comment);
        String removePattern = "(?i)(UNIQUE INDEX.+|FOREIGN[^,]+,|INDEX.+)";
        Pattern fieldPattern = Pattern.compile("(?<name>\\S+)\\s(?<type>(?:\\sUNSIGNED|\\sPRECISION|\\S)+)\\s(?<modifiers>[^,\\-\\n]+)?,?(?:\\s+)?(?:--\\s+(?<comment>.+))?");
        Matcher fm = fieldPattern.matcher(fieldString.replaceAll(removePattern, ""));
        Pattern indexPattern = Pattern.compile("(?<unique>UNIQUE\\s+\\n?)?INDEX\\s+\\n?(?<name>\\S+)\\s+\\((?<columns>[^)]+|\\n+)\\),?(?:\\s+--\\s+(?<comment>.+))?");
        Matcher im = indexPattern.matcher(fieldString);
        Pattern foreignPattern = Pattern.compile("FOREIGN KEY\\s+\\((?<name>\\S+)\\)(?:\\s+--\\s+(?<comment>.+))?(?:\\s+|\\n)REFERENCES\\s+(?<table>[^(]+)\\((?<column>[^)]+)\\)(?:\\s|\\n)(?<ons>[^,]+)", Pattern.CASE_INSENSITIVE);
        Matcher km = foreignPattern.matcher(fieldString);
        fields = new ArrayList<>();
        indexes = new ArrayList<>();
        foreignKeys = new ArrayList<>();
        triggers = new ArrayList<>();

        while (fm.find()) {
            String fieldName = fm.group("name");
            if (!fieldName.equalsIgnoreCase("INDEX") && !fieldName.equalsIgnoreCase("UNIQUE") && !fieldName.equalsIgnoreCase("FOREIGN")) {
                fields.add(new SQLField(fm.group("name"), fm.group("type"), fm.group("comment"), fm.group("modifiers")));
            }
        }

        while (im.find()) {
            indexes.add(new SQLIndex(im.group("name"), im.group("comment"), im.group("unique") != null, im.group("columns")));
        }

        while (km.find()) {
            foreignKeys.add(new SQLForeignKey(km.group("name"), km.group("comment"), km.group("table"), km.group("column"), km.group("ons")));
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(String.format("TABLE %s%n", getName()));
        sb.append(String.format("- %s%n", getPlainComment()));

        if (fields.isEmpty()) {
            sb.append("No fields\n");
        } else {
            int FIELD_TABLE_WIDTH = 153;
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

        if (indexes.isEmpty()) {
            sb.append("No indexes\n");
        } else {
            int INDEX_TABLE_WIDTH = 119;
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

        if (foreignKeys.isEmpty()) {
            sb.append("No foreign keys\n");
        } else {
            int KEY_TABLE_WIDTH = 129;
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

        sb.append("\n");

        if (triggers.isEmpty()) {
            sb.append("No triggers");
        } else {
            int TRIGGER_TABLE_WIDTH = 105;
            sb.append(tableLine(TRIGGER_TABLE_WIDTH));

            sb.append(String.format("| \033[3m%-" + (TRIGGER_TABLE_WIDTH - 4) + "s\033[0m |%n", "TRIGGERS"));

            sb.append(tableLine(TRIGGER_TABLE_WIDTH));

            sb.append(String.format(TRIGGER_TEMPLATE, "Name", "Run", "Comment"));

            sb.append(tableLine(TRIGGER_TABLE_WIDTH));

            for (SQLTrigger t : triggers) {
                sb.append(t.toString());
            }

            sb.append(tableLine(TRIGGER_TABLE_WIDTH));
        }

        sb.append("\n");

        return sb.toString();
    }

    public String toMD() {
        StringBuilder sb = new StringBuilder(String.format("## `%s`%n", getName()));
        sb.append("> ").append(getCommentMD()).append("\n\n").append(backToTop());

        sb.append("### Fields\n");

        if (fields.isEmpty()) {
            sb.append("This table has no fields.\n");
        } else {
            sb.append(mdTableHeader("Name", "Type", "Unique", "NULL", "Default", "On Update", "Comment"));

            for (SQLField f : fields) {
                sb.append(f.toMD());
            }
        }

        sb.append("\n");

        sb.append("### Indexes\n");

        if (indexes.isEmpty()) {
            sb.append("This table has no explicit indexes.\n");
        } else {
            sb.append(mdTableHeader("Name", "Unique", "Columns", "Comment"));

            for (SQLIndex i : indexes) {
                sb.append(i.toMD());
            }
        }

        sb.append("\n");

        sb.append("### Relationships\n");

        if (foreignKeys.isEmpty()) {
            sb.append("This table has no relationships");
        } else {
            sb.append(mdTableHeader("Name", "References", "On Delete", "On Update", "Comment"));

            for (SQLForeignKey k : foreignKeys) {
                sb.append(k.toMD());
            }
        }

        sb.append("\n");

        sb.append("### Triggers\n");

        if (triggers.isEmpty()) {
            sb.append("This table has no triggers");
        } else {
            sb.append(mdTableHeader("Name", "Run", "Comment"));

            for (SQLTrigger t : triggers) {
                sb.append(t.toMD());
            }
        }

        return sb.toString();
    }

    public void addTrigger(SQLTrigger t) {
        triggers.add(t);
    }
}
