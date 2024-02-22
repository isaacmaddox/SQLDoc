public class SQLEntity implements Comparable<SQLEntity> {
    public final String PARAM_TEMPLATE = "| %-20.20s | %-15.15s | %-50.50s |%n";
    public final String FIELD_TEMPLATE = "| %-20.20s | %-15.15s | %-6.6s | %-4.4s | %-18.18s | %-18.18s | %-50.50s |%n";
    public final String INDEX_TEMPLATE = "| %-20.20s | %-6.6s | %-30.30s | %-50.50s |%n";
    public final String FOREIGN_TEMPLATE = "| %-20.20s | %-25.25s | %-9.9s | %-9.9s | %-50.50s |%n";
    public final String TRIGGER_TEMPLATE = "| %-20.20s | %-25.25s | %-50.50s |%n";
    private String name;
    private final String comment;

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

    protected String tableLine(int width) {
        return String.format("%-" + width + "s%n", "").replaceAll(" ", "-");
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
        return comment.replaceAll("@see\\s+([^@]+)", "\n\n[See `$1`](#$1)\n");
    }

    public String getPlainComment() {
        return comment.replaceAll("@see\\s+\\S+", "");
    }

    public String getName() {
        return name;
    }

    public int compareTo(SQLEntity e) {
        return getName().compareTo(e.getName());
    }
}
