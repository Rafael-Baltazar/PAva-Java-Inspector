package ist.meic.pa;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class Console {
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
			String[] cmd = line.split("\\s+");
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
					System.err
							.println("Correct use of c: c <name> [<value 0> ... <value n>]");
				}
				break;
			default:
				System.err.println("Command not recognized.");
			}

		}
	}

	/**
	 * Check if the given type is primitive or not
	 * 
	 * @param type
	 *            the field to be checked
	 */
	private static boolean isPrimitive(Class<?> type) {
		return (type == int.class || type == float.class
				|| type == boolean.class
				|| type == short.class
				|| type == long.class
				|| type == byte.class
				|| type == char.class
				|| type == double.class || type == void.class);
	}

	/**
	 * Inspects the field of the object and makes it the current inspected
	 * object as long as its type is not primitive
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
				inspector.setCurrentInspectedObject(object);
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
	 * returned value
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
			Integer value = Integer.parseInt(cmd[i]);
			args.add(value);
			parameterTypes.add(int.class);
		}
		Method m = inspector.getBestMethod(inspector.getObject(), methodName,
				parameterTypes.toArray(new Class<?>[0]));
		if (m == null) {
			System.err.println("Error: No such method with name " + methodName);
		} else {
			try {
				Object result = m.invoke(inspector.getObject(), args.toArray());
				if (result != null) {
					if (isPrimitive(m.getReturnType())) {
						System.err.println(result);
					} else {
						Object currentObject = inspector.getObject();
						inspector.setCurrentInspectedObject(result);
						inspector.printInspection();
						inspector.setCurrentInspectedObject(currentObject);
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
}
