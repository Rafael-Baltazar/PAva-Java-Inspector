package ist.meic.pa.commands;

import java.lang.reflect.Field;

import ist.meic.pa.Console;
import ist.meic.pa.Inspector;

public class InspectFieldCommand extends Command {

	public InspectFieldCommand(Inspector inspector, Console console) {
		super(inspector, console);
	}

	@Override
	public void execute(String[] commandLineInput) {
		if (commandLineInput.length == 2) {
			inspectField(getInspector(), commandLineInput);
		} else {
			System.err.println("Correct use of i: i <name>");
		}
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

}
