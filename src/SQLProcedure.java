import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SQLProcedure extends SQLEntity {
    private final ArrayList<SQLParam> params;

    public SQLProcedure(String name, String comment, String args) {
        super(name, comment);
        params = new ArrayList<>();

        if (args != null) {
            Pattern argPattern = Pattern.compile("(?:\\s+)?(?<name>\\S+)\\s+(?<type>[^-,]+),?(?:\\s+)?(?<comment>--.+)?");
            Matcher argMatcher = argPattern.matcher(args);

            while (argMatcher.find()) {
                params.add(new SQLParam(argMatcher.group("name"), argMatcher.group("comment"), argMatcher.group("type")));
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(String.format("PROCEDURE %s%n", getName()));
        sb.append("- ").append(getPlainComment()).append("\n");

        if (params.isEmpty()) {
            sb.append("No parameters\n");
        } else {
            int TABLE_WIDTH = 95;
            sb.append(tableLine(TABLE_WIDTH));

            sb.append(String.format(PARAM_TEMPLATE, "Name", "Type", "Comment"));

            sb.append(tableLine(TABLE_WIDTH));

            for (SQLParam par : params) {
                sb.append(par.toString());
            }

            sb.append(tableLine(TABLE_WIDTH));
        }

        return sb.toString();
    }

    public String toMD() {
        StringBuilder sb = new StringBuilder(String.format("## `%s()`%n", getName()));
        sb.append("> ").append(getCommentMD()).append("\n\n").append(backToTop());

        if (params.isEmpty()) {
            sb.append("No parameters");
        } else {
            sb.append("### Parameters\n");

            sb.append(mdTableHeader("Name", "Type", "Comment"));

            for (SQLParam p : params) {
                sb.append(p.toMD());
            }
        }

        return sb.toString();
    }

    @Override
    public String getMDLink() {
        return "[" + getName() + "()](" + getLinkHref() + ")";
    }
}
