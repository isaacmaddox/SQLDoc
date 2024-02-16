import java.util.regex.Pattern;
import java.io.File;
import java.util.regex.Matcher;

public class SQLDocDriver {
   private static StringBuilder cmd = new StringBuilder("");
   
   public static void main(String[] args) {     
      try {
         // Re-build command to make flag detection easier
         for (String s : args) cmd.append(String.format(!cmd.isEmpty() ? " %s" : "%s", s));
         
         // Print help
         if (args.length > 0 && (args[0].equals("-help") || args[0].equals("-h"))) {
            printHelp();
            return;
         }
         
         // Default file location is where the program is run from
         File location = new File(System.getProperty("user.dir"));
         
         // Will be initialized later
         String outputFile = "";
         String outputDirectory = "";
         
         // Set the title of the document that will be generated (if any)
         String outputTitle = getFlag("title", true, null);
         
         if (outputTitle == null)
            outputTitle = getFlag("t", true, "SQLDoc Generated Documentation");
         
         // Check if first argument is a valid file, and update
         // the destination if so
         if (args.length > 0) {
            File tmp = new File(args[0]);
            if (tmp.exists())
               location = tmp;
         }
         
         // If the -o flag is set, use that as the destination. Otherwise, create
         // sqldoc.md in the current directory
         outputDirectory = location.isDirectory() ? location.getAbsolutePath() : location.getParentFile().getAbsolutePath();
         outputFile = getFlag("o", false, String.format("%s\\sqldoc.md", outputDirectory));
         
         SQLParser p = new SQLParser(location, outputTitle);
         
         // If the -quiet flag is set, do not output anything to console
         p.parse((checkForFlag("quiet") || checkForFlag("q")));
         
         if (checkForFlag("md")) {
            p.printMD(outputFile);
         } else if (checkForFlag("c")) {
            p.print();
         } else {
            p.printMD(outputFile);
         }
         
      } catch (Exception e) {
         printError("Malformed command or invalid file reference.");
      }
   }
   
   private static String getFlag(String flagName, boolean allowSpaces, String defaultValue) {
      final Pattern flagPattern = Pattern.compile(
            allowSpaces ?
                  String.format("-%s\\s([^-]+)", flagName) :
                  String.format("-%s\\s(\\S+)", flagName),
            Pattern.CASE_INSENSITIVE
      );
      
      Matcher flagMatcher = flagPattern.matcher(cmd);
      
      if (flagMatcher.find()) {
         String match = flagMatcher.group(1);
         return match.isBlank() ? defaultValue : match;
      }
      
      return defaultValue;
   }
   
   private static boolean checkForFlag(String flag) {
      return cmd.indexOf("-" + flag) > -1;
   }
   
   public static void printError(String msg) {
      System.out.println(String.format("\n\033[91m%s\033[0m\n", msg));
      printHelp();
   }
   
   private static void printHelp() {
      System.out.println("\033[1;93mSQLDoc help\033[0m");
      System.out.println("\nUsage:");
      helpLine();
      printCmd("sqldoc", "Run the program in the current directory. Will output to \"sqldoc.md\" in the current directory.");
      printCmd("sqldoc [path]", "Run the program in the specified directory or on the specified file. Will output to \"sqldoc.md\" in that directory.");
      System.out.println();
      System.out.println("\nFlags:\n");
      helpLine();
      printCmd("-md", "Specifies output to a markdown file");
      printCmd("-q(uiet)", "Runs the program without any logging in the console.");
      printCmd("-o [path]", "Sends the output to the specified file or directory.");
      printCmd("-t(itle) [title]", "Specifies a document title (for Markdown and HTML outputs)");
      printCmd("-h(elp)", "Specifies a document title (for Markdown and HTML outputs)");
   }
   
   private static void printCmd(String cmd, String desc) {
      if (desc.length() > 50) {
         int lastPrintInd = desc.substring(0, 50).lastIndexOf(' ');
         System.out.printf(" \033[0;93m%-35.35s\033[0m %-50.50s %n", cmd, desc.substring(0, lastPrintInd));
         printCmd("", desc.substring(lastPrintInd + 1));
      } else {
         System.out.printf(" \033[0;93m%-35.35s\033[0m %-50.50s %n", cmd, desc);
         helpLine();
      }
   }
   
   private static void helpLine() {
      System.out.println(String.format(" %35s %50s ", "", "").replaceAll("\s", "-"));
   }
}
