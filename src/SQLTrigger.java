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

        s.append(String.format(TRIGGER_TEMPLATE, getName(), action, getPlainComment()));

        return s.toString();
    }

    public String toMD() {
        return mdTableRow(getName(), action, getCommentMD());
    }
}
