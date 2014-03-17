package ist.meic.pa;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;

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
						Object object = field.get(inspector.getObject());
						System.err.println(inspector.inspectField(field));
						inspector.setCurrentInspectedObject(object);
					} catch (NoSuchFieldException e) {
						System.err.println("No such field " + fieldName);
					} catch (SecurityException e) {
						System.err.println("Security exception while accessing " + fieldName);
					} catch (IllegalArgumentException e) {
						System.err.println("Illegal argument while accessing " + fieldName);
					} catch (IllegalAccessException e) {
						System.err.println("Illegal access while accessing "+ fieldName);
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
				} else {
					System.err.println("Correct use of m: m <name> <value>");
				}
				break;
			case "c":
				if (cmd.length > 1) {
					/* Calls method c from the current inspected object */
					;
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
