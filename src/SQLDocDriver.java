import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SQLDocDriver {
    private static StringBuilder cmd = new StringBuilder();
    private static char selectedOutput;
    private static String fileName;
    private static String title;

    public static void main(String[] args) {
        try {
            // Initialize settings and get values
            SQLDocSettings.init();
            selectedOutput = SQLDocSettings.getSettingAsChar("output");
            fileName = SQLDocSettings.getSetting("filename");
            title = SQLDocSettings.getSetting("title");

            // Re-build command to make flag detection easier
            for (String s : args)
                cmd.append(String.format(!cmd.isEmpty() ? " %s" : "%s", s));

            // Print help
            if (args.length > 0 && (args[0].equals("-help") || args[0].equals("-h"))) {
                printHelp();
                return;
            } else if (cmd.indexOf("settings") > -1) { // List settings
                SQLDocSettings.printSettings();
                return;
            } else if (cmd.indexOf("reset") > -1) { // Reset settings
                SQLDocSettings.resetToDefault();
                return;
            } else if (cmd.indexOf("set ") > -1) { // Update settings
                SQLDocSettings.updateSettings(cmd.toString());
                return;
            }

            // Check for specified output type
            if (checkForFlag("md")) {
                selectedOutput = 'm';
            } else if (checkForFlag("c")) {
                selectedOutput = 'c';
            }

            if (selectedOutput != 'm' && selectedOutput != 'c') {
                printError("Invalid output type. Valid types are md (markdown) and c (console)");
                return;
            }

            // Default file location is where the program is run from
            File location = new File(System.getProperty("user.dir"));

            // Will be initialized later
            String outputDirectory;

            // Set the title of the document that will be generated (if any)
            // If none set, default to SQLDocDriver.title (from .settings)
            title = getFlag("title", true, getFlag("t", true, title));

            // Check if first argument is a valid file, and update
            // the destination if so
            if (args.length > 0) {
                File tmp = new File(args[0]);
                if (tmp.exists())
                    location = tmp;
            }

            // If the -o flag is set, use that as the destination. Otherwise, create
            // sqldoc.md in the current directory
            outputDirectory = location.isDirectory() ? location.getAbsolutePath()
                    : location.getParentFile().getAbsolutePath();
            fileName = getFlag("o", false, getFlag("out", false, String.format("%s\\%s", outputDirectory, fileName)));

            SQLParser p = new SQLParser(location, title);

            // If the -quiet flag is set, do not output anything to console
            p.parse(checkForFlag("q") || checkForFlag("quiet"));

            if (selectedOutput == 'm') {
                p.printMD(fileName);
            } else if (selectedOutput == 'c') {
                p.print();
            } else {
                printError(
                        "SQLDoc could not find an output type specified. Use \033[1msqldoc set default_output=<value>\033[0;91m to set the default");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            printError("Malformed command or invalid file reference.");
        }
    }

    private static String getFlag(String flagName, boolean allowSpaces, String defaultValue) {
        final Pattern flagPattern = Pattern.compile(
                allowSpaces ? String.format("-%s\\s([^-]+)", flagName) : String.format("-%s\\s(\\S+)", flagName),
                Pattern.CASE_INSENSITIVE);

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

    public static void printError(String msg, boolean showHelp) {
        System.out.printf("%n\u001B[91m%s\u001B[0m%n%n", msg);
        if (showHelp)
            printHelp();
    }

    public static void printError(String msg) {
        printError(msg, true);
    }

    private static void printHelp() {
        System.out.println("\n\033[1;93mSQLDoc Help Menu\033[0m");
        System.out.println("\nUsage:");
        helpLine();
        printCmd("sqldoc",
                "Run the program in the current directory. Will output to \"sqldoc.md\" in the current directory.");
        printCmd("sqldoc [path]",
                "Run the program in the specified directory or on the specified file. Will output to \"sqldoc.md\" in that directory.");
        printCmd("sqldoc settings", "Print a list of all settings and their current value.");
        printCmd("sqldoc set [setting]=[value]",
                "Change the value of a setting. Replace spaces with \"+\" in the setting value");
        printCmd("sqldoc reset", "Reset all settings.", "\033[1;91m");
        System.out.println();
        System.out.println("\nFlags:\n");
        helpLine();
        printCmd("-md", "Specifies output to a markdown file. This is the default, so flag is redundant");
        printCmd("-q(uiet)", "Runs the program without any logging in the console.");
        printCmd("-o(ut) [path]", "Sends the output to the specified file or directory.");
        printCmd("-t(itle) [title]", "Specifies a document title (for Markdown and HTML outputs)");
        printCmd("-h(elp)", "Print this menu");
    }

    private static void printCmd(String cmd, String desc, String colorCode) {
        if (desc.length() > 50) {
            int lastPrintInd = desc.substring(0, 50).lastIndexOf(' ');
            System.out.printf(" %s%-35.35s\033[0m %-50.50s %n", colorCode, cmd, desc.substring(0, lastPrintInd));
            printCmd("", desc.substring(lastPrintInd + 1));
        } else {
            System.out.printf(" %s%-35.35s\033[0m %-50.50s %n", colorCode, cmd, desc);
            helpLine();
        }
    }

    private static void printCmd(String cmd, String desc) {
        printCmd(cmd, desc, "\033[0;93m");
    }

    private static void helpLine() {
        System.out.println(String.format(" %35s %50s ", "", "").replaceAll("\\s", "-"));
    }
}
