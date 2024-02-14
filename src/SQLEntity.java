public class SQLEntity {
	protected String name;
	protected String comment;
	public final String PARAM_TEMPLATE = "| %-20.20s | %-15.15s | %-50.50s |%n";
	public final String FIELD_TEMPLATE = "| %-20.20s | %-15.15s | %-6.6s | %-4.4s | %-20.20s | %-20.20s | %-50.50s |%n";
	public final String INDEX_TEMPLATE = "| %-20.20s | %-6.6s | %-30.30s | %-50.50s |%n";
	public final String FOREIGN_TEMPLATE = "| %-20.20s | %-25.25s | %-9.9s | %-9.9s | %-50.50s |%n";
	
	public SQLEntity() {
		this("", "");
	}
	
	public SQLEntity(String newName, String newComment) {
		name = newName;
		if (newComment != null)
			comment = newComment.replaceAll("\\*\\s*", "").replaceAll("(?:\s+)?\n(?:\s+)(?!@)", " ").replaceAll("\s+@", "@").trim();
		else
			comment = "";
	}
	
	protected String tableLine(int width) {
		return String.format("%-" + width + "s%n", "").replaceAll("\s", "-");
	}
	
	protected String mdTableRow(String ...cols) {
	   return "| " + String.join(" | ", cols) + " |\n";
	}
	
	protected String mdTableHeader(String ...cols) {
	   StringBuilder sb = new StringBuilder(mdTableRow(cols));
	   
	   for (int i = 0; i < cols.length; ++i) {
	      sb.append("|---");
	   }
	   
	   sb.append("|\n");
	   
	   return sb.toString();
	}
	
	public static String mdTableHeaderS(String ...cols) {
	   return new SQLEntity().mdTableHeader(cols);
	}
	
	public static String mdTableHRowS(String ...cols) {
	   return new SQLEntity().mdTableRow(cols);
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
}
