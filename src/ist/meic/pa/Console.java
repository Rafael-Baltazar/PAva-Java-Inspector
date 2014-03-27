package ist.meic.pa;

import ist.meic.pa.commands.Command;
import ist.meic.pa.parsers.InputLineParser;
import ist.meic.pa.parsers.ParameterParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

/**
 * The Class Console.
 */
public class Console {

	/** The commands. */
	private Map<String, Command> commands;

	/** The parameter parsers. */
	private Map<String, ParameterParser> parameterParsers;

	/** The inspector. */
	private Inspector inspector;

	/**
	 * Instantiates a new console.
	 * 
	 * @param inspector
	 *            the inspector
	 */
	public Console(Inspector inspector) {
		this.commands = new HashMap<String, Command>();
		this.parameterParsers = new HashMap<String, ParameterParser>();
		this.inspector = inspector;
	}

	/**
	 * Adds the command.
	 * 
	 * @param commandString
	 *            the command string
	 * @param command
	 *            the command
	 */
	public void addCommand(String commandString, Command command) {
		this.commands.put(commandString, command);
	}

	/**
	 * Adds the parameter parser.
	 * 
	 * @param typeString
	 *            the type string
	 * @param parser
	 *            the parser
	 */
	public void addParameterParser(String typeString, ParameterParser parser) {
		this.parameterParsers.put(typeString, parser);
	}

	/**
	 * Gets the inspector.
	 * 
	 * @return the inspector
	 */
	public Inspector getInspector() {
		return inspector;
	}

	/**
	 * Read eval print.
	 */
	public void readEvalPrint() {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		while (true) {
			String line = "";
			try {
				System.err.print(" >: ");
				line = br.readLine();
			} catch (IOException e) {
				System.err.println("Error reading from input.");
				continue;
			}
			String[] cmd = null;
			try {
				cmd = InputLineParser.parseInputLine(line);
			} catch (ParseException e) {
				System.err.println(e.getMessage());
				continue;
			}
			if (cmd.length == 0) {
				// The user just pressed enter
				continue;
			}

			Command command = this.commands.get(cmd[0]);

			if (command == null) {
				System.err.println("Command not recognized.");
			} else {
				command.execute(cmd);
			}

		}
	}

	/**
	 * Parses the parameter in the string.
	 * 
	 * @param parameterString
	 *            the parameter string. Last character in this string can
	 *            represent the type. If there's no last character representing
	 *            the type the string can be a name of a previous saved object.
	 *            If it is, use it as a parameter. Otherwise, the the default
	 *            parser will be used
	 * @return the parameter
	 */
	public Parameter parseParameter(String parameterString,
			ParameterParser defaultParser) {
		
		// Let's check if it is the name of a previous saved object
		Object object = this.inspector.getSavedObject(parameterString);
		
		if(object == null) {
			// There's no such object
			// Let's see if the last character represents the type
			int typeIndex = parameterString.length() - 1;
			String typeString = parameterString.charAt(typeIndex) + "";
			ParameterParser parser = this.parameterParsers.get(typeString);
			
			if(parser == null) {
				// Last character doesn't represent the type
				// Maybe the string is the parameter itself
				parser = this.parameterParsers.get(parameterString);
				
				if(parser == null) {
					// The string is not the parameter itself
					// Let's just use the default parser
					return defaultParser.parse(parameterString);
				}
				
				else {
					return parser.parse(parameterString);
				}
			}
			else {
				return parser.parse(parameterString.substring(0, typeIndex));
			}
		}
		else {
			return new Parameter(object.getClass(), object);
		}

	}

}
