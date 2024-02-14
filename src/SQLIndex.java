public class SQLIndex extends SQLEntity {
   private boolean unique;
   private String columns;
   
   public SQLIndex(String name, String comment, boolean uni, String cols) {
      super(name, comment);
      unique = uni;
      columns = cols.replaceAll("([^,\s]+)", "`$1`");
   }
   
   @Override
   public String toString() {
      return String.format(INDEX_TEMPLATE, name, getUnique(), columns, comment);
   }
   
   public String toMD() {
      return mdTableRow(name, getUnique(), columns, comment);
   }
   
   public String getUnique() {
      return unique ? "YES" : "NO";
   }
}
