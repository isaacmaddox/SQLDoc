import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SQLTable extends SQLEntity {
   private ArrayList<SQLField> fields;
   private ArrayList<SQLIndex> indexes;
   private ArrayList<SQLForeignKey> foreignKeys;
   final private String removePattern = "(?i)(UNIQUE INDEX.+|FOREIGN[^,]+,|INDEX.+)";
   final private Pattern fieldPattern = Pattern.compile("(?<name>\\S+)(?:\\s)(?<type>(?:\\sUNSIGNED|\\sPRECISION|\\S)+)\\s(?<modifiers>(?:[^,\\-\\n]+))?(?:,)?(?:\\s+)?(?:--\\s+(?<comment>.+))?");
   final private Pattern indexPattern = Pattern.compile("(?<unique>UNIQUE(?:\\s+)(?:\\n)?)?INDEX\\s+(?:\\n)?(?<name>\\S+)\\s+\\((?<columns>[^)]+|\\n+)\\)(?:,)?(?:\\s+--\\s+(?<comment>.+))?");
   final private Pattern foreignPattern = Pattern.compile("FOREIGN KEY\\s+\\((?<name>\\S+)\\)(?:\\s+--\\s+(?<comment>.+))?(?:\\s+|\\n)REFERENCES\\s+(?<table>[^(]+)\\((?<column>[^)]+)\\)(?:\\s|\\n)(?<ons>[^,]+)", Pattern.CASE_INSENSITIVE);
   final private int FIELD_TABLE_WIDTH = 157;
   final private int INDEX_TABLE_WIDTH = 119;
   final private int KEY_TABLE_WIDTH = 129;
   
   public SQLTable(String name, String comment, String fieldString) {
      super(name, comment);
      Matcher fm = fieldPattern.matcher(fieldString.replaceAll(removePattern, ""));
      Matcher im = indexPattern.matcher(fieldString);
      Matcher km = foreignPattern.matcher(fieldString);
      fields = new ArrayList<SQLField>();
      indexes = new ArrayList<SQLIndex>();
      foreignKeys = new ArrayList<SQLForeignKey>();
      
      while (fm.find()) {
         String fieldName = fm.group("name");
         if (!fieldName.toUpperCase().equals("INDEX") && !fieldName.toUpperCase().equals("UNIQUE") && !fieldName.toUpperCase().equals("FOREIGN")) {
            fields.add(new SQLField(fm.group("name"), fm.group("type"), fm.group("comment"), fm.group("modifiers")));
         }
      }
      
      while (im.find()) {
         indexes.add(new SQLIndex(im.group("name"), im.group("comment"), im.group("unique") != null, im.group("columns")));
      }
      
      while (km.find()) {
         foreignKeys.add(new SQLForeignKey(km.group("name"), km.group("comment"), km.group("table"), km.group("column"), km.group("ons")));
      }
   }
   
   @Override
   public String toString() {
      String r = "TABLE " + name + "\n";
      r += "- " + comment.replaceAll("\n", "\n  ") + "\n";
      
      if (fields.size() == 0) {
         r += "No fields\n";
      } else {
         r += tableLine(FIELD_TABLE_WIDTH);
         
         r += String.format("| \033[3m%-" + (FIELD_TABLE_WIDTH - 4) + "s\033[0m |%n", "COLUMNS");
         
         r += tableLine(FIELD_TABLE_WIDTH);
         
         r += String.format(FIELD_TEMPLATE, "Name", "Type", "Unique", "NULL", "Default", "On Update", "Comment");
         
         r += tableLine(FIELD_TABLE_WIDTH);
         
         for (SQLField field : fields) {
            r += field.toString();
         }
         
         r += tableLine(FIELD_TABLE_WIDTH);
      }
      
      r += "\n";
      
      if (indexes.size() == 0) {
         r += "No indexes\n";
      } else {
         r += tableLine(INDEX_TABLE_WIDTH);
         
         r += String.format("| \033[3m%-" + (INDEX_TABLE_WIDTH - 4) + "s\033[0m |%n", "INDEXES");
         
         r += tableLine(INDEX_TABLE_WIDTH);
         
         r += String.format(INDEX_TEMPLATE, "Name", "Unique", "Columns", "Comment");
         
         r += tableLine(INDEX_TABLE_WIDTH);
         
         for (SQLIndex i : indexes) {
            r += i.toString();
         }
         
         r += tableLine(INDEX_TABLE_WIDTH);
      }
      
      r += "\n";
      
      if (foreignKeys.size() == 0) {
         r += "No foreign keys\n";
      } else {
         r += tableLine(KEY_TABLE_WIDTH);
         
         r += String.format("| \033[3m%-" + (KEY_TABLE_WIDTH - 4) + "s\033[0m |%n", "FOREIGN KEYS");
         
         r += tableLine(KEY_TABLE_WIDTH);
         
         r += String.format(FOREIGN_TEMPLATE, "Name", "References", "On Delete", "On Update", "Comment");
         
         r += tableLine(KEY_TABLE_WIDTH);
         
         for (SQLForeignKey k : foreignKeys) {
            r += k.toString();
         }
         
         r += tableLine(KEY_TABLE_WIDTH);
      }
      
      return r;
   }
   
   public String toMD() {
      String r = "## `" + name + "`\n";
      r += "> " + getCommentMD() + "\n\n" + backToTop();
      
      r += "### Fields\n";
      
      if (fields.size() == 0) {
         r += "This table has no fields.\n";
      } else {
         r += mdTableHeader("Name", "Type", "Unique", "NULL", "Default", "On Update", "Comment");
         
         for (SQLField f : fields) {
            r += f.toMD();
         }
      }
      
      r += "\n";
      
      r += "### Indexes\n";
      
      if (indexes.size() == 0) {
         r += "This table has no explicit indexes.\n";
      } else {
         r += mdTableHeader("Name", "Unique", "Columns", "Comment");
         
         for (SQLIndex i : indexes) {
            r += i.toMD();
         }
      }
      
      r += "\n";
      
      r += "### Relationships\n";
      
      if (foreignKeys.size() == 0) {
         r += "This table has no relationships";
      } else {
         r += mdTableHeader("Name", "References", "On Delete", "On Update", "Comment");
         
         for (SQLForeignKey k : foreignKeys) {
            r += k.toMD();
         }
      }
      
      return r;
   }
}
