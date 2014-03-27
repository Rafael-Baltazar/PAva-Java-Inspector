package ist.meic.pa;

import ist.meic.pa.commands.Command;
import ist.meic.pa.commands.Parameter;
import ist.meic.pa.parsers.ParameterParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
				cmd = parseInputLine(line);
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
	 * Parses the input into a String[].
	 * 
	 * @param line
	 *            the input line
	 * @return the array with the parsed input
	 * @throws ParseException
	 *             the parse exception
	 */
	private static String[] parseInputLine(String line) throws ParseException {
		int length = line.length();
		boolean inString = false;
		String s = "";
		List<String> cmd = new ArrayList<String>();

		for (int i = 0; i < length; i++) {
			char c = line.charAt(i);
			if (inString) {
				if (c == '"') {
					/* show error or close string */
					if ((i != length - 1) && !isWhiteSpace(line.charAt(i + 1))) {
						throw new ParseException(
								"Cannot have characters after closing a string.",
								i);
					} else {
						s += c;
						inString = false;
					}
				} else if (i == length - 1) {
					throw new ParseException("Didn't close string", i);
				} else if ((c == '\\') && (line.charAt(i + 1) == '"')) {
					/* add '"' to string */
					s += '"';
					i++;
				} else {
					s += c;
				}
			} /* not in String */
			else {
				if (c == '"') {
					/* show error or open string */
					if ((i != 0) && !isWhiteSpace(line.charAt(i - 1))) {
						throw new ParseException(
								"Cannot open string, if the value already has characters.",
								i);
					} else {
						s += c;
						inString = true;
					}
				} else if (isWhiteSpace(c) && (!s.equals(""))) {
					/* add s, if it isn't the empty string */
					cmd.add(s);
					s = "";
				} else if (!isWhiteSpace(c)) {
					s += c;
				}
			}
		}
		if (!s.equals("")) {
			/* Add the final value */
			cmd.add(s);
			s = "";
		}
		return cmd.toArray(new String[0]);
	}

	/**
	 * Checks if is white space.
	 * 
	 * @param c
	 *            the c
	 * @return true, if is white space
	 */
	private static boolean isWhiteSpace(char c) {
		return Character.isWhitespace(c);
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
