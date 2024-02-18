public class SQLTrigger extends SQLEntity {
    private final String action;
    private final String table;

    /**
     * Description
     * @param name
     * @param comment
     * @param newAction
     * @param newTable
     */
    public SQLTrigger(String name, String comment, String newAction, String newTable) {
        super(name, comment);
        action = newAction;
        table = newTable;
    }

    @Override
    public String toString() {
        return String.format(TRIGGER_TEMPLATE, name, action, table, comment);
    }

    public String toMD() {
        return String.format("## `%s()`%n", name) + "> " + getCommentMD() + "\n\n" + backToTop() +
                "### On table: " + getTableLinkMD();
    }

    private String getTableLinkMD() {
        return String.format("[%s](#%s)", table, table);
    }
}
