public class SQLTrigger extends SQLEntity {
    private final String action;
    private final String table;

    public SQLTrigger(String name, String comment, String newAction, String newTable) {
        super(name, comment);
        action = newAction;
        table = newTable;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();

        s.append(String.format("TRIGGER %s%n", getName()));
        s.append(String.format("- %s%n", getPlainComment()));
        int TABLE_WIDTH = 123;
        s.append(tableLine(TABLE_WIDTH));
        s.append(String.format(TRIGGER_TEMPLATE, "Name", "Run", "On Table", "Comment"));
        s.append(tableLine(TABLE_WIDTH));
        s.append(String.format(TRIGGER_TEMPLATE, getName(), action, table, getPlainComment()));
        s.append(tableLine(TABLE_WIDTH));

        return s.toString();
    }

    public String toMD() {
        return mdTableRow(getName(), action, getCommentMD());
    }
}
