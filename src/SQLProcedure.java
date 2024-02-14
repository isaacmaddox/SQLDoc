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
		String r = "PROCEDURE " + name + "\n";
		r += "- " + comment.replaceAll("\n", "\n  ") + "\n";
		
		if (params.size() == 0) {
		   r += "No parameters\n";
		} else {
   		r += tableLine(TABLE_WIDTH);
   		
   		r += String.format(PARAM_TEMPLATE, "Name", "Type", "Comment");
   		
   		r += tableLine(TABLE_WIDTH);
   		
   		for (SQLParam par : params) {
   			r += par.toString();
   		}
   		
   		r += tableLine(TABLE_WIDTH);
		}
		
		return r;
	}
	
	public String toMD() {
	   String r = "## `" + name + "()`\n";
	   r += "> " + getCommentMD() + "\n\n" + backToTop();
	   
	   if (params.size() == 0) {
	      r += "No parameters";
	   } else {
   	   r += "### Parameters\n";
   	   
   	   r += mdTableHeader("Name", "Type", "Comment");
   	   
   	   for (SQLParam p : params) {
   	      r += p.toMD();
   	   }
	   }
	   
	   return r;
	}
	
	@Override
	public String getMDLink() {
	   return "[" + name + "()](" + getLinkHref() + ")";
	}
}
