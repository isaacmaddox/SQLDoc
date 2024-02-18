public class SQLEntity implements Comparable<SQLEntity> {
    public final String PARAM_TEMPLATE = "| %-20.20s | %-15.15s | %-50.50s |%n";
    public final String FIELD_TEMPLATE = "| %-20.20s | %-15.15s | %-6.6s | %-4.4s | %-20.20s | %-20.20s | %-50.50s |%n";
    public final String INDEX_TEMPLATE = "| %-20.20s | %-6.6s | %-30.30s | %-50.50s |%n";
    public final String FOREIGN_TEMPLATE = "| %-20.20s | %-25.25s | %-9.9s | %-9.9s | %-50.50s |%n";
    public final String TRIGGER_TEMPLATE = "| %-20.20s | %-25.25s | %-15.15s | %-50.50s |";
    protected String name;
    protected String comment;

    public SQLEntity() {
        this("", "");
    }

    public SQLEntity(String newName, String newComment) {
        name = newName;
        if (newComment != null)
            comment = newComment.replaceAll("\\*\\s*", "").replaceAll("\\s*\n\\s+(?!@)", " ")
                    .replaceAll("\\s+@", "@").trim();
        else
            comment = "";
    }

    public static String mdTableHeaderS(String... cols) {
        return new SQLEntity().mdTableHeader(cols);
    }

    public static String mdTableHRowS(String... cols) {
        return new SQLEntity().mdTableRow(cols);
    }

    protected String tableLine(int width) {
        return String.format("%-" + width + "s%n", "").replaceAll("\\s", "-");
    }

    protected String mdTableRow(String... cols) {
        return "| " + String.join(" | ", cols) + " |\n";
    }

    protected String mdTableHeader(String... cols) {
        return mdTableRow(cols) + "|---".repeat(cols.length) + "|\n";
    }

    public String getMDLink() {
        return "[" + name + "](" + getLinkHref() + ")";
    }

    public String getLinkHref() {
        return "#" + name;
    }

    public String backToTop() {
        return "[Back to top](#table-of-contents)\n\n";
    }

    public String getCommentMD() {
        return comment.replaceAll("@see\\s+(.+)", "\n[See `$1`](#$1)\n");
    }

    public String getName() {
        return name;
    }

    public int compareTo(SQLEntity e) {
        return getName().compareTo(e.getName());
    }
}
