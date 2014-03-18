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
				e.printStackTrace();
				System.err.println("Error reading from input.");
				continue;
			}
			String[] cmd = line.split("\\s+");
			switch (cmd[0]) {
			case "q":
				return;
			case "i":
				if (cmd.length == 2) {
					/*
					 * Inspects the field of the object and makes it the current
					 * inspected object
					 */
					String fieldName = cmd[1];
					try {
						Field field = inspector.getFieldByName(fieldName);
						if (field == null)
							throw new NoSuchFieldException();
						Object object = field.get(inspector.getObject());
						System.err.println(inspector.inspectField(field));
						inspector.setCurrentInspectedObject(object);
					} catch (NoSuchFieldException e) {
						System.err.println("No such field " + fieldName);
					} catch (SecurityException e) {
						System.err
								.println("Security exception while accessing "
										+ fieldName);
					} catch (IllegalArgumentException e) {
						System.err.println("Illegal argument while accessing "
								+ fieldName);
					} catch (IllegalAccessException e) {
						System.err.println("Illegal access while accessing "
								+ fieldName);
					}
				} else {
					System.err.println("Correct use of i: i <name>");
				}
				break;
			case "m":
				if (cmd.length == 3) {
					/* Modifies the value of the field */
					String fieldName = cmd[1];
					String newValue = cmd[2];

					try {
						Field field = inspector.getFieldByName(fieldName);
						if (field == null)
							throw new NoSuchFieldException();
						/* Check which type to parse the value to */
						if (field.getType() == boolean.class) {
							field.set(inspector.getObject(),
									Boolean.parseBoolean(newValue));
						} else if (field.getType() == int.class
								|| field.getType() == short.class
								|| field.getType() == long.class) {
							field.set(inspector.getObject(),
									Integer.parseInt(newValue));
						} else {
							field.set(inspector.getObject(), newValue);
						}
					} catch (NoSuchFieldException e) {
						System.err.println("No such field " + fieldName);
					} catch (SecurityException e) {
						System.err
								.println("Security exception while accessing "
										+ fieldName);
					} catch (IllegalArgumentException e) {
						System.err.println("Illegal argument while accessing "
								+ fieldName);
					} catch (IllegalAccessException e) {
						System.err.println("Illegal access while accessing "
								+ fieldName);
					}
				} else {
					System.err.println("Correct use of m: m <name> <value>");
				}
				break;
			case "c":
				if (cmd.length > 1) {
					/*
					 * Calls method c from the current inspected object and
					 * inspects the returned value
					 */
					String methodName = cmd[1];
					List<Object> args = new ArrayList<Object>();
					List<Class<?>> parameterTypes = new ArrayList<Class<?>>();

					for (int i = 2; i < cmd.length; i++) {
						Integer value = Integer.parseInt(cmd[i]);
						args.add(value);
						parameterTypes.add(int.class);
					}
					Method m = inspector
							.getBestMethod(inspector.getObject(), methodName,
									parameterTypes.toArray(new Class<?>[0]));
					if (m == null) {
						System.err.println("No such method " + methodName);
					} else {
						try {
							Object result = m.invoke(inspector.getObject(),
									args.toArray());
							if (result != null) {
//								Object currentObject = inspector.getObject();
//								inspector.setCurrentInspectedObject(result);
//								inspector.printInspection();
//								inspector
//										.setCurrentInspectedObject(currentObject);
								System.err.println(result);
							}
						} catch (IllegalAccessException e) {
							System.err
									.println("Illegal access when invoking method "
											+ m.getName());
						} catch (IllegalArgumentException e) {
							System.err
									.println("Illegal argument when invoking method "
											+ m.getName());
						} catch (InvocationTargetException e) {
							System.err
									.println("Invocation target when invoking method "
											+ m.getName());
						}
					}
				} else {
					System.err
							.println("Correct use of c: c <name> [<value 0> ... <value n>]");
				}
				break;
			default:
				System.err.println("Command not recognized.");
			}
			inspector.printInspection();
		}
	}
}
