public class SQLParam extends SQLEntity {
    private String type;

    public SQLParam(String newName, String newComment, String newType) {
        super(newName, (newComment == null ? "" : newComment.replaceAll("(?: +)?--(?: )+", "").replaceAll("\n", " ")));
        type = newType.replaceAll("( +)?\n( +)?", " ");
    }

    @Override
    public String toString() {
        return String.format(PARAM_TEMPLATE, name, type, getPlainComment());
    }

    public String toMD() {
        return mdTableRow(name, type, getCommentMD());
    }
}
