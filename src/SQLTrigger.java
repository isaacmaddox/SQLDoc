import java.util.ArrayList;

public class SQLTrigger extends SQLEntity {
    private final String action;
    private final String table;
    private final int TABLE_WIDTH = 123;

    public SQLTrigger(String name, String comment, String newAction, String newTable) {
        super(name, comment);
        action = newAction;
        table = newTable;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();

        s.append(String.format("TRIGGER %s%n", name));
        s.append(String.format("- %s%n", getPlainComment()));
        s.append(tableLine(TABLE_WIDTH));
        s.append(String.format(TRIGGER_TEMPLATE, "Name", "Run", "On Table", "Comment"));
        s.append(tableLine(TABLE_WIDTH));
        s.append(String.format(TRIGGER_TEMPLATE, name, action, table, getPlainComment()));
        s.append(tableLine(TABLE_WIDTH));

        return s.toString();
    }

    public String toMD() {
        return mdTableRow(name, action, getCommentMD());
    }

    private String getTableLinkMD() {
        return String.format("[%s](#%s)", table, table);
    }
}
