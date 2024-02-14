import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SQLField extends SQLEntity {
   private boolean key;
   private boolean unique;
   private boolean nullable;
   private String type;
   private String defaultValue;
   private String onUpdate;
   final private Pattern defaultValuePattern = Pattern.compile("DEFAULT\\s(?<default>(?:\".+\")|(?:[^\\s,]+))(?:,)?(?:\\n)?", Pattern.CASE_INSENSITIVE);
   final private Pattern updateValuePattern = Pattern.compile("ON UPDATE(?:\\s|\\n)+(?<action>\\\"(?:[^\"]+)\\\"|\\S+)", Pattern.CASE_INSENSITIVE);
   
   public SQLField(String name, String newType, String newComment, String modifiers) {
         super(name, (newComment == null ? "" : newComment.replaceAll("(?:\s+)?--(?:\s)+", "").replaceAll("\n", " ")));
         type = newType;
         Matcher dm = defaultValuePattern.matcher(modifiers);
         Matcher up = updateValuePattern.matcher(modifiers);
                  
         if (dm.find()) {
            defaultValue = dm.group("default");
         } else {
            defaultValue = null;
         }
         
         if (up.find()) {
            onUpdate = up.group("action");
         } else {
            onUpdate = null;
         }
         
         modifiers = modifiers.toUpperCase();
         
         nullable = !modifiers.contains("NOT NULL");
         unique = modifiers.contains("UNIQUE");
         key = modifiers.contains("KEY");
   }
   
   @Override
   public String toString() {
      return String.format(FIELD_TEMPLATE, name, type, getKey(), getNullable(), getDefault(), getOnUpdate(), comment);
   }
   
   public String toMD() {
      return mdTableRow(name, type, getKey(), getNullable(), getDefault(), getOnUpdate(), comment);
   }
   
   private String getKey() {
      return key ? "PRI" : (unique ? "YES" : "");
   }
   
   private String getNullable() {
      return nullable ? "YES" : "NO";
   }
   
   private String getDefault() {
      return defaultValue == null ? "NULL" : defaultValue;
   }
      
   private String getOnUpdate() {
      return onUpdate == null ? "" : onUpdate;
   }
}
