package ist.meic.pa;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * The Class Console.
 */
public class Console {

	/**
	 * Read eval print.
	 * 
	 * @param inspector
	 *            the inspector
	 */
	public static void readEvalPrint(Inspector inspector) {
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
			if (cmd[0].equals("q")) {
				return;
			} else if (cmd[0].equals("i")) {
				if (cmd.length == 2) {
					inspectField(inspector, cmd);
				} else {
					System.err.println("Correct use of i: i <name>");
				}
			} else if (cmd[0].equals("m")) {
				if (cmd.length == 3) {
					modifyField(inspector, cmd);
					inspector.printInspection();
				} else {
					System.err.println("Correct use of m: m <name> <value>");
				}
			} else if (cmd[0].equals("c")) {
				if (cmd.length > 1) {
					callMethod(inspector, cmd);
				} else {
					System.err.println("Correct use of c: c <name> "
							+ "[<value 0> ... <value n>]");
				}
			} else if (cmd[0].equals("help")) {
				consoleHelp();
			} else if (cmd[0].equals("methods")) {
				inspector.printMethods();
			} else if (cmd[0].equals("fields")) {
				inspector.printFields();
			} else if (cmd[0].equals("objects")) {
				inspector.printSavedObjects();
			} else if (cmd[0].equals("inspect")) {
				if(cmd.length == 2) {
					inspectSavedObject(inspector, cmd);
				}
				else {
					System.err.println("Correct use of inspect: inspect <object-name>");
				}
			} else if (cmd[0].equals("next")) {
				next(inspector);
			} else if (cmd[0].equals("prev")) {
				prev(inspector);
			} else if (cmd[0].equals("save")) {
				if (cmd.length == 2) {
					saveCurrentInspectedObject(inspector, cmd[1]);
				} else {
					System.err.println("Correct use of save: save <name>");
				}
			} else {
				System.err.println("Command not recognized.");
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

	private static boolean isWhiteSpace(char c) {
		return Character.isWhitespace(c);
	}

	/**
	 * Check if the given type is primitive or not.
	 * 
	 * @param type
	 *            the field to be checked
	 * @return true, if is primitive
	 */
	private static boolean isPrimitive(Class<?> type) {
		return (type == int.class || type == float.class
				|| type == boolean.class || type == short.class
				|| type == long.class || type == byte.class
				|| type == char.class || type == double.class || type == void.class);
	}

	/**
	 * Inspects the field of the object and makes it the current inspected
	 * object as long as its type is not primitive.
	 * 
	 * @param inspector
	 *            inspector the inspector that contains the object to be
	 *            modified
	 * @param cmd
	 *            the array of options/values
	 */
	private static void inspectField(Inspector inspector, String[] cmd) {
		String fieldName = cmd[1];
		try {
			Field field = inspector.getFieldByName(fieldName);
			if (field == null) {
				throw new NoSuchFieldException();
			}

			if (isPrimitive(field.getType())) {
				System.err.println(field.get(inspector.getObject()));
			} else {
				Object object = field.get(inspector.getObject());
				inspector.addAndSetCurrentInspectedObject(object);
				inspector.printInspection();
			}
		} catch (NoSuchFieldException e) {
			System.err.println("No such field " + fieldName);
		} catch (SecurityException e) {
			System.err.println("Error: Security exception while accessing "
					+ fieldName);
		} catch (IllegalArgumentException e) {
			System.err.println("Error: Illegal argument while accessing "
					+ fieldName);
		} catch (IllegalAccessException e) {
			System.err.println("Error: Illegal access while accessing "
					+ fieldName);
		}
	}

	/**
	 * Calls method c from the current inspected object and inspects the
	 * returned value. Supported argument types: byte, short, int, long, float,
	 * double, boolean, char and String.
	 * 
	 * @param inspector
	 *            the inspector that contains the object to be modified
	 * @param cmd
	 *            the array of options/values
	 */
	private static void callMethod(Inspector inspector, String[] cmd) {
		String methodName = cmd[1];
		List<Object> args = new ArrayList<Object>();
		List<Class<?>> parameterTypes = new ArrayList<Class<?>>();

		for (int i = 2; i < cmd.length; i++) {
			String str = cmd[i];
			Object value = null;
			int typeIndex = str.length() - 1;
			try {
				if (str.charAt(typeIndex) == 'b') {
					value = Byte.parseByte(str.substring(0, typeIndex));
					parameterTypes.add(byte.class);
				} else if (str.charAt(typeIndex) == 's') {
					value = Short.parseShort(str.substring(0, typeIndex));
					parameterTypes.add(short.class);
				} else if (str.charAt(typeIndex) == 'i') {
					value = Integer.parseInt(str.substring(0, typeIndex));
					parameterTypes.add(int.class);
				} else if (str.charAt(typeIndex) == 'L') {
					value = Long.parseLong(str.substring(0, typeIndex));
					parameterTypes.add(long.class);
				} else if (str.charAt(typeIndex) == 'f') {
					value = Float.parseFloat(str.substring(0, typeIndex));
					parameterTypes.add(float.class);
				} else if (str.charAt(typeIndex) == 'd') {
					value = Double.parseDouble(str.substring(0, typeIndex));
					parameterTypes.add(double.class);
				} else if (str.charAt(typeIndex) == 'B') {
					value = Boolean.parseBoolean(str.substring(0, typeIndex));
					parameterTypes.add(boolean.class);
				} else if (str.charAt(typeIndex) == '\'') {
					value = str.charAt(1);
					parameterTypes.add(char.class);
				} else if (str.charAt(typeIndex) == '"') {
					value = str.substring(1, str.length() - 1);
					parameterTypes.add(String.class);
				} else if (str.equals("true") || str.equals("false")) {
					value = Boolean.parseBoolean(str);
					parameterTypes.add(boolean.class);
				} else {
					value = Integer.parseInt(str);
					parameterTypes.add(int.class);
				}
			} catch (NumberFormatException e) {
				// Try to use a saved object
				value = inspector.getSavedObject(str);

				if (value == null) {
					System.err.println("Error: Could not parse the parameter "
							+ str.substring(0, typeIndex));
					return;
				} else {
					parameterTypes.add(value.getClass());
				}

			}
			args.add(value);
		}
		/* Get Best method for the given methodName and parameterTypes */
		Method m = inspector.getBestMethod(inspector.getObject(), methodName,
				parameterTypes.toArray(new Class<?>[0]));
		if (m == null) {
			String parStr = "";
			for (Class<?> c : parameterTypes) {
				parStr += c.getName() + " ";
			}
			System.err.println("Error: No such method with name " + methodName
					+ (parStr != "" ? " and parameters " + parStr : ""));
		} else {
			try {
				/* Invoke m */
				m.setAccessible(true);
				Object result = m.invoke(inspector.getObject(), args.toArray());
				if (result != null) {
					/* Print result */
					if (isPrimitive(m.getReturnType())) {
						System.err.println(result);
					} else {
						System.err.println(result);
						inspector.addAndSetCurrentInspectedObject(result);
						inspector.printInspection();
					}
				}
			} catch (IllegalAccessException e) {
				System.err
						.println("Error: Illegal access when invoking method "
								+ m.getName());
			} catch (IllegalArgumentException e) {
				System.err
						.println("Error: Illegal argument when invoking method "
								+ m.getName());
			} catch (InvocationTargetException e) {
				System.err
						.println("Error: Invocation target when invoking method "
								+ m.getName());
			}
		}
	}

	/**
	 * Modifies the value of a field of an instance of some class that the
	 * inspector is inspecting.
	 * 
	 * @param inspector
	 *            the inspector that contains the object to be modified
	 * 
	 * @param cmd
	 *            the array of options/values
	 */
	private static void modifyField(Inspector inspector, String[] cmd) {
		String fieldName = cmd[1];
		String newValue = cmd[2];

		try {
			Field field = inspector.getFieldByName(fieldName);
			if (field == null)
				throw new NoSuchFieldException();
			else
				field.setAccessible(true);
			/* Check which type to parse the value to */
			if (field.getType() == boolean.class) {
				field.set(inspector.getObject(), Boolean.parseBoolean(newValue));
			} else if (field.getType() == int.class) {
				field.set(inspector.getObject(), Integer.parseInt(newValue));
			} else if (field.getType() == short.class) {
				field.set(inspector.getObject(), Short.parseShort(newValue));
			} else if (field.getType() == long.class) {
				field.set(inspector.getObject(), Long.parseLong(newValue));
			} else if (field.getType() == byte.class) {
				field.set(inspector.getObject(), Byte.parseByte(newValue));
			} else {
				field.set(inspector.getObject(), newValue);
			}
		} catch (NoSuchFieldException e) {
			System.err.println("Error: No such field with name " + fieldName);
		} catch (SecurityException e) {
			System.err.println("Error: Security exception while accessing "
					+ fieldName);
		} catch (IllegalArgumentException e) {
			System.err.println("Error: Illegal argument while accessing "
					+ fieldName);
		} catch (IllegalAccessException e) {
			System.err.println("Error: Illegal access while accessing "
					+ fieldName);
		}
	}

	/**
	 * Next. Go to the next object in the graph of inspected objects.
	 * 
	 * @param inspector
	 *            the inspector
	 */
	private static void next(Inspector inspector) {
		if (inspector.goToNextObject()) {
			inspector.printInspection();
		} else {
			System.err.println("You are already inspecting the last object");
		}
	}

	/**
	 * Prev.Go to the previous object in the graph of inspected objects.
	 * 
	 * @param inspector
	 *            the inspector
	 */
	private static void prev(Inspector inspector) {
		if (inspector.goToPreviousObject()) {
			inspector.printInspection();
		} else {
			System.err.println("You are already inspecting the first object");
		}
	}

	/**
	 * Save current inspected object.
	 * 
	 * @param inspector
	 *            the inspector
	 * @param name
	 *            the name
	 */
	private static void saveCurrentInspectedObject(Inspector inspector,
			String name) {
		inspector.saveCurrentInspectedObject(name);
	}
	
	private static void inspectSavedObject(Inspector inspector, String cmd[]) {
		String objectName = cmd[1];
		Object object = inspector.getSavedObject(objectName);
		if(object == null) {
			System.err.println("Error: There is no saved object with name " + objectName);
		}
		else {
			inspector.setCurrentInspectedObject(object);
			inspector.printInspection();
		}
	}

	/**
	 * Prints the help for the console.
	 */
	private static void consoleHelp() {
		System.err.println("------------------");
		System.err.println("| Java Inspector |");
		System.err.println("------------------");
		System.err.println("Available commands and usage:");
		System.err.println("i <field-name>               - inspects the field with the given name of the current inspected object");
		System.err.println("m <field-name> <new-value>   - modifies the value of a given field of the current inspected object");
		System.err.println("c <method-name> [parameter1] ... [parametern] - calls the method with the given name of the current inspected object using any parameters given");
		System.err.println("\t [parameteri] : <value>[<type>]");
		System.err.println("\t\t If you omit the <type> the <value> can be:");
		System.err.println("\t\t\t [0-9]+, \"text in quotes\", true, false, <name of a previous saved object>");
		System.err.println("\t\t If you want to explicitly give the parameter's type the <value> can be anything but you can't omit the <type>");
		System.err.println("\t\t The <type> can be one of the following");
		System.err.println("\t\t\t b, s, i, l, f, d, B ");
		System.err.println("\t\t\t\t b - byte");
		System.err.println("\t\t\t\t s - short");
		System.err.println("\t\t\t\t i - int");
		System.err.println("\t\t\t\t L - long");
		System.err.println("\t\t\t\t f - float");
		System.err.println("\t\t\t\t d - double");
		System.err.println("\t\t\t\t B - boolean");
		System.err.println("\t\t The <value> and <type> combination needs to make sense");
		System.err.println("\t\t So, for instance, a float with value 3.14 is just 3.14f");
		System.err.println("\t\t But, if you try 3.14l it will not make sense because you cannot assign 3.14 to a long");
		System.err.println("save <object-name>           - saves the current inspected object with the given name");
		System.err.println("inspect <object-name>        - inspects the saved object with the given name");
		System.err.println("objects                      - shows all the previously saved objects");
		System.err.println("methods                      - shows all the available methods of the current inspected object");
		System.err.println("fields                       - shows all the available fields of the current inspected object");
		System.err.println("next                         - jumps to the next inspected object in the graph of objects, if it exists");
		System.err.println("prev                         - jumps to the previous inspected object in the graph of objects, if it exists");
		System.err.println("help                         - brings up the help menu");
		System.err.println("q                            - terminates the application");
	}
}
