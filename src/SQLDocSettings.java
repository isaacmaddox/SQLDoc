import java.io.*;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SQLDocSettings {
    private final static Map<String, String> settings = new LinkedHashMap<String, String>();
    private final static Pattern settingPattern = Pattern.compile("(?<key>\\S+)=(?<value>\\S+)");
    private final static Pattern settingFilePattern = Pattern.compile("([^=]+)=(.+)");
    private final static String settingsTable = "| %-25.25s | %-40.40s |%n";
    private final static Map<String, String> validValues = new HashMap<>();
    private static File settings_file;

    public static void init() throws IOException {
        if (System.getenv("SQLDOC_HOME") != null) {
            settings_file = new File(String.format("%s\\.settings", System.getenv("SQLDOC_HOME")));
        }

        setDefaultSettings(false);

        // Settings file exists, so populate the settings Map
        if (settings_file != null && settings_file.exists()) {
            Scanner settingsInput = new Scanner(settings_file);
            String line;
            Matcher settingMatcher;

            while (settingsInput.hasNextLine()) {
                line = settingsInput.nextLine();
                settingMatcher = settingFilePattern.matcher(line);

                if (settingMatcher.find()) {
                    settings.replace(settingMatcher.group(1), settingMatcher.group(2));
                }
            }

            settingsInput.close();
        }

        validValues.put("output", "c md");
        validValues.put("hide-credits", "true false");
        validValues.put("hide-tables", "true false");
        validValues.put("hide-procedures", "true false");
    }

    public static void updateSettings(String cmd) throws IOException {
        Matcher settingMatcher = settingPattern.matcher(cmd);

        while (settingMatcher.find()) {
            String key = settingMatcher.group("key");
            String value = settingMatcher.group("value").replaceAll("(?<!\\\\)\\+", " ").replaceAll("\\\\\\+", "+");
            StringBuilder out = new StringBuilder();

            if (!settings.containsKey(key)) {
                out.append(String.format("\033[91m%s is not a valid setting\033[0m%n", key));
            } else if (validValues.containsKey(key) && !validValues.get(key).contains(value)) {
                SQLDocDriver.printError(String.format("%s is not a valid value for %s", value, key), false);
            } else {
                settings.put(key, value);
                out.append(String.format("Set \033[3;93m%s\033[0m to %s%n", key, value));
            }

            if (writeSettings()) {
                System.out.print(out);
            } else {
                SQLDocDriver.printError("An error occurred while updating the settings.", true);
            }
        }
    }

    public static String getSetting(String key) {
        return settings.get(key);
    }

    public static char getSettingAsChar(String key) {
        if (settings.containsKey(key)) return settings.get(key).charAt(0);
        else return '-';
    }

    public static void printSettings() {
        System.out.println();
        System.out.printf(settingsTable, "Setting", "Value");
        System.out.print(String.format(settingsTable, "", "").replaceAll(" ", "-"));

        for (Map.Entry<String, String> e : settings.entrySet()) {
            System.out.printf(settingsTable, e.getKey(), e.getValue());
        }

        System.out.println();
    }

    private static void setDefaultSettings(boolean overwrite) throws IOException {
        if (overwrite) settings.clear();

        settings.put("output", "md");
        settings.put("filename", "sqldoc");
        settings.put("title", "SQLDoc Generated Documentation");
        settings.put("hide-credits", "false");
        settings.put("hide-tables", "false");
        settings.put("hide-procedures", "false");

        if (overwrite) writeSettings();
    }

    public static void resetToDefault() throws IOException {
        Scanner in = new Scanner(System.in);
        System.out.print("\033[0;91mAre you sure you want to reset all settings? (y/n):\033[0m ");

        char choice = Character.toLowerCase(in.next().charAt(0));

        while (choice != 'n' && choice != 'y') {
            System.out.print("\033[0;91mInvalid choice. Enter y or n:\033[0m ");
            choice = Character.toLowerCase(in.next().charAt(0));
        }

        if (choice == 'y') {
            setDefaultSettings(true);
            System.out.println("\n\033[1;91mSettings have been reset\033[0m\n");
        }

        in.close();
    }

    private static boolean writeSettings() throws IOException {
        FileWriter fw = new FileWriter(settings_file);
        BufferedWriter bw = new BufferedWriter(fw);
        PrintWriter output = new PrintWriter(bw);

        for (Map.Entry<String, String> e : settings.entrySet()) {
            output.printf("%s=%s%n", e.getKey(), e.getValue());
        }

        output.close();

        return true;
    }
}
