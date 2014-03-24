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

// TODO: Auto-generated Javadoc
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
			switch (cmd[0]) {
			case "q":
				return;
			case "i":
				if (cmd.length == 2) {
					inspectField(inspector, cmd);
				} else {
					System.err.println("Correct use of i: i <name>");
				}
				break;
			case "m":
				if (cmd.length == 3) {
					modifyField(inspector, cmd);
					inspector.printInspection();
				} else {
					System.err.println("Correct use of m: m <name> <value>");
				}
				break;
			case "c":
				if (cmd.length > 1) {
					callMethod(inspector, cmd);
				} else {
					System.err.println("Correct use of c: c <name> "
							+ "[<value 0> ... <value n>]");
				}
				break;
			case "methods": 
				inspector.printMethods();
				break;
			case "next":
				next(inspector);
				break;
			case "prev":
				prev(inspector);
				break;
			case "save":
				if (cmd.length == 2) {
					saveCurrentInspectedObject(inspector, cmd[1]);
				} else {
					System.err.println("Correct use of save: save <name>");
				}
				break;
			default:
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
	 *             the parse exception
	 */
	private static String[] parseInputLine(String line) throws ParseException {
		int length = line.length();
		boolean inString = false;
		String s = "";
		List<String> cmd = new ArrayList<String>();

		for (int i = 0; i < length; i++) {
			char c = line.charAt(i);
			if (c == '"') {
				if (!inString && s == "") {
					/* Start a new string */
					inString = true;
				} else if (inString
						&& (i + 1 == length || line.charAt(i + 1) == ' ')) {
					/* Close the string */
					inString = false;
				} else {
					/* Not a string */
					throw new ParseException(
							"Error: Can't have characters before opening or after closing a string",
							i);
				}
			} else if (c == '\\' && inString && i + 1 != length
					&& line.charAt(i + 1) == '"') {
				/* Add '"' to the string */
				s += "\"";
			} else if (inString && i + 1 == length) {
				throw new ParseException("Error: Missed \" to close string", i);
			} else if (c != ' ' || inString) {
				s += c;
			} else if (s != "") {
				/* Add value */
				cmd.add(s);
				s = "";
			}
		}
		if (s != "") {
			cmd.add(s);
		}
		return cmd.toArray(new String[0]);
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
			if (str.charAt(0) == 'b') {
				value = Byte.parseByte(str.substring(1));
				parameterTypes.add(byte.class);
			} else if (str.charAt(0) == 's') {
				value = Short.parseShort(str.substring(1));
				parameterTypes.add(short.class);
			} else if (str.charAt(0) == 'i') {
				value = Integer.parseInt(str.substring(1));
				parameterTypes.add(int.class);
			} else if (str.charAt(0) == 'l') {
				value = Long.parseLong(str.substring(1));
				parameterTypes.add(long.class);
			} else if (str.charAt(0) == 'f') {
				value = Float.parseFloat(str.substring(1));
				parameterTypes.add(float.class);
			} else if (str.charAt(0) == 'd') {
				value = Double.parseDouble(str.substring(1));
				parameterTypes.add(double.class);
			} else if (str.charAt(0) == 'f') {
				value = Boolean.parseBoolean(str.substring(1));
				parameterTypes.add(boolean.class);
			} else if (str.charAt(0) == '\'') {
				value = str.charAt(1);
				parameterTypes.add(char.class);
			} else {
				value = str;
				parameterTypes.add(String.class);
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
				Object result = m.invoke(inspector.getObject(), args.toArray());
				if (result != null) {
					/* Print result */
					if (isPrimitive(m.getReturnType())) {
						System.err.println(result);
					} else {
						Object currentObject = inspector.getObject();
						inspector.addAndSetCurrentInspectedObject(result);
						inspector.printInspection();
						inspector
								.addAndSetCurrentInspectedObject(currentObject);
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
}
