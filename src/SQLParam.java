
public class SQLParam extends SQLEntity {
	private String type;
	
	public SQLParam(String newName, String newComment, String newType) {
		super(newName, (newComment == null ? "" : newComment.replaceAll("(?:\s+)?--(?:\s)+", "").replaceAll("\n", " ")));
		type = newType.replaceAll("(\s+)?\n(\s+)?", " ");
	}
	
	@Override
	public String toString() {
		return String.format(PARAM_TEMPLATE, name, type, comment);
	}
	
	public String toMD() {
	   return mdTableRow(name, type, comment);
	}
}
