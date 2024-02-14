import java.io.File;

public class SQLParserDriver {
   public static final String RESET = "\033[0m";
   public static final String YELLOW_BRIGHT = "\033[0;93m";
   public static final String RED_BRIGHT = "\033[0;91m";
   public static final String YELLOW_BOLD_BRIGHT = "\033[1;93m";
   public static final int DESC_LENGTH = 50;
   
	public static void main(String[] args) {		
		if (args.length > 0) {
		   if (args[0].equals("-help")) {
		      printHelp();
		   } else if (args.length == 1) {
		      runParser(args[0], "-md", "sqldoc");
		   } else {
		      runParser(args);
		   }
		} else {
		   runParser(System.getProperty("user.dir"), "-md", "sqldoc");
		}
	   
//	   runParser("C:\\programming\\tread", "-md", "C:\\Users\\isaac\\Downloads\\example", "Tread");
//	   runConsoleParser("C:\\programming\\tread");
	}

	private static void runParser(final String ...args) {
	   SQLParser p;
	   
	   if (args.length >= 4) {
	      String title = args[3];
	      
	      for (int i = 4; i < args.length; ++i) {
	         title += " " + args[i];
	      }
	      
	      p = new SQLParser(new File(args[0]), title);
	   } else {
	      p = new SQLParser(new File(args[0]));
	   }
	   
	   p.parse();
	   
	   switch (args[1]) {
	      case "-md":
	         String file = args.length >= 3 ? args[2] : "sqldoc";
	         p.printMD(file);
	         System.out.println("Finished writing to " + file);
	         break;
	      case "-c":
	         runConsoleParser(p);
	         break;
	   }
	}
	
	private static void runConsoleParser(final SQLParser p) {
	   p.parse();
	   p.print();
	}
		
	private static void printHelp() {
	   System.out.println(YELLOW_BOLD_BRIGHT + "SQLDoc help\n" + RESET);
	   System.out.println(" Commands:");
	   helpLine();
	   printCmdColor("sqldoc", "Run the program in the current directory. Output  to a MD file");
	   printCmd("sqldoc [<path>]", "Run the program in the specified directory. Output to a MD file in the current working directory");
	   printCmd("sqldoc [<path>] [-md|-c]", "Run the program in the specified directory. Output to either a MD file or to the console");
	   printCmd("sqldoc [<path>] -md [<output>]", "Run the program in the specified directory. Output to a MD file named by the output argument");
	   printCmd("sqldoc [<path>] -md [<output>] [<title>]", "Run the program in the specified directory. Output to a MD file named by the output argument. The title of the markdown document is specified by the title argument");
	   printCmdColor("sqldoc -help", "Display this menu");
	}
	
	private static void printCmd(final String cmd, final String desc) {
	   if (desc.length() <= DESC_LENGTH) {
	      System.out.printf("   %-45s %-50s%n", cmd, desc.trim());
	      helpLine();
	   } else {
	      System.out.printf("   %-45s %-50s%n", cmd, desc.substring(0, DESC_LENGTH).trim());
	      printCmd("", desc.substring(DESC_LENGTH));
	   }
	}
	
	private static void printCmdColor(final String cmd, final String desc) {
	   if (desc.length() <= DESC_LENGTH) {
	      System.out.printf(YELLOW_BOLD_BRIGHT + "   %-45s " + RESET + "%-50s%n", cmd, desc.trim());
         helpLine();
      } else {
         System.out.printf(YELLOW_BOLD_BRIGHT + "   %-45s " + RESET + "%-50s%n", cmd, desc.substring(0, DESC_LENGTH).trim());
         printCmd("", desc.substring(DESC_LENGTH));
      }
	   
	}
	
	private static void helpLine() {
	   System.out.println("   " + String.format("%102s", "").replace(" ", "-"));
	}
}
