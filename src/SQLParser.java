import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;

public class SQLParser {
	private File path;
	private ArrayList<SQLProcedure> procedures;
	private ArrayList<SQLTable> tables;
	private int fileCount;
	private int procCount;
	private int tableCount;
	private String title;
	final private Pattern docPattern = Pattern.compile("\\/\\*\\*(?<comment>(?:\\n.+){1,})\\*\\/(?:\\n)?CREATE(?:\\s+)(?<type>\\S+)(?:\\s+)(?<name>[^\\s()]+)(?:\\s+)?\\((?:\\n)?(?<args>(?:[^)].+\\n+){1,})?\\)", Pattern.CASE_INSENSITIVE);
	final private Pattern titlePattern = Pattern.compile("(?:\\\\|\\/)(?<title>[^\\\\\\/.]+)(?:\\.\\S+)?$");
	
	public SQLParser(final File file) {
		path = file;
		procedures = new ArrayList<SQLProcedure>();
		tables = new ArrayList<SQLTable>();
		fileCount = 0;
		procCount = 0;
		tableCount = 0;
		Matcher tm = titlePattern.matcher(file.getAbsolutePath().replaceAll("\\\\\\.", ""));
		
		title = tm.find() ? tm.group("title") : "SQLDoc Generated Documentation";
		
		title = Character.toUpperCase(title.charAt(0)) + title.substring(1);
	}
	
	public SQLParser(final File file, String newTitle) {
	   this(file);
	   title = newTitle;
	}
	
	public void parse() {
		parse(path);
		System.out.println("Finished reading " + fileCount + " file" + (fileCount > 1 ? 's' : "") + '.');
	}
	
	public void parse(final File file) {
		if (file.isDirectory()) {
			for (File f : file.listFiles()) {
				parse(f);
			}
		} else {
			++fileCount;
			procCount = 0;
			tableCount = 0;
			processFile(file);
		}
	}
	
	public void print() {
		System.out.println();
		
		for (SQLProcedure p : procedures) {
			System.out.println(p.toString());
		}
		
		for (SQLTable t : tables) {
		   System.out.println(t.toString());
		}
	}
	
	public void printMD(String location) {
	   try {
	      if (!location.endsWith(".md")) location += ".md";
	      FileWriter fw = new FileWriter(location);
	      PrintWriter output = new PrintWriter(fw);
   	   System.out.println();
   	   
   	   output.println("# " + title);
   	   
   	   // Write Table of Contents
   	   
   	   output.println("# Table of Contents");
   	   output.println("[Tables](#tables)");
   	   
   	   for (SQLTable t : tables) {
   	      output.println("- " + t.getMDLink());
   	   }
   	   
   	   output.println();
   	   
   	   output.println("[Procedures](#procedures)");
   	   
   	   for (SQLProcedure p : procedures) {
   	      output.println("- " + p.getMDLink());
   	   }
   	   
   	   output.println();
   	   
   	   output.println("# Tables");
   	   
   	   for (SQLTable t : tables) {
   	      output.println(t.toMD());
   	   }
   	   
   	   output.println("# Procedures");
   	      	      	   
   	   for (SQLProcedure p : procedures) {
   	      output.println(p.toMD());
   	   }
   	   
   	   output.close();
	   } catch (Exception e) {
	      System.out.println("SQLDoc ran into an error: ");
	      System.out.println(e.getMessage());
	   }
	}
	
	private void processFile(final File file) {
		try {
			if (file.isDirectory()) {
				parse(file);
			} else {
			   String path = file.getAbsolutePath();
			   String ext = path.substring(path.lastIndexOf('.') + 1);
			   
			   if (!ext.equals("sql")) return;
			   
				Scanner inputFR = new Scanner(file);
				String line = "";
				String buffer = "";
				
				while (inputFR.hasNextLine()) {
					line = inputFR.nextLine();
					
					if (!line.trim().startsWith("/**") && buffer.isEmpty()) {
						continue;
					}
										
					buffer += (!buffer.isEmpty() ? "\n" : "") + line;
					
					
					Matcher m = docPattern.matcher(buffer);
					boolean matches = m.find();
					
					if (!matches) {
						continue;
					} else {
						processDoc(m.group("name"), m.group("type"), m.group("comment"), m.group("args"));
						buffer = "";
					}
				}
				
				inputFR.close();
				if (procCount > 0)
				   System.out.println("[" + file.getName() + "] Found " + procCount + " procedures");
				if (tableCount > 0) {
				   System.out.println("[" + file.getName() + "] Found " + tableCount + " tables");
				}
			}
		} catch (FileNotFoundException e) {
			System.out.println("Couldn't find file");
		}
	}
	
	private void processDoc(final String name, final String type, final String comment, final String args) {		
		switch (type.toUpperCase()) {
		case "PROCEDURE":
			++procCount;
			procedures.add(new SQLProcedure(name, comment, args));
			break;
		case "TABLE":
		   ++tableCount;
		   tables.add(new SQLTable(name, comment, args));
		   break;
		}
	}
}
