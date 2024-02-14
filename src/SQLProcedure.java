import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SQLProcedure extends SQLEntity {
	private ArrayList<SQLParam> params;
	final private Pattern argPattern = Pattern.compile("(?:\\s+)?(?<name>\\S+)(?:\\s+)(?<type>(?:[^-,]+))(?:,)?(?:\\s+)?(?<comment>--(?:.+))?");
	final private int TABLE_WIDTH = 95;
	
	public SQLProcedure(String name, String comment) { 
	  super(name, comment);
	  params = new ArrayList<SQLParam>();
	}
	
	public SQLProcedure(String name, String comment, String args) {
	   super(name, comment);
	   params = new ArrayList<SQLParam>();
	   
	   if (args != null) {
   		Matcher argMatcher = argPattern.matcher(args);
   		
   		while (argMatcher.find()) {
   			params.add(new SQLParam(argMatcher.group("name"), argMatcher.group("comment"), argMatcher.group("type")));
   		}
	   }
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(String.format("PROCEDURE %s%n", name));
		sb.append("- " + comment.replaceAll("\n", "\n  ") + "\n");
		
		if (params.size() == 0) {
		   sb.append("No parameters\n");
		} else {
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
	   StringBuilder sb = new StringBuilder(String.format("## `%s()`%n", name));
	   sb.append("> " + getCommentMD() + "\n\n" + backToTop());
	   
	   if (params.size() == 0) {
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
	   return "[" + name + "()](" + getLinkHref() + ")";
	}
}
