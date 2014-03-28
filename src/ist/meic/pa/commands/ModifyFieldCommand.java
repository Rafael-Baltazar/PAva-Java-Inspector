package ist.meic.pa.commands;

import java.lang.reflect.Field;

import ist.meic.pa.Console;
import ist.meic.pa.Inspector;
import ist.meic.pa.Parameter;
import ist.meic.pa.parsers.IntegerParser;

public class ModifyFieldCommand extends Command {

	public ModifyFieldCommand(Inspector inspector, Console console) {
		super(inspector, console);
	}

	@Override
	public void execute(String[] commandLineInput) {
		if (commandLineInput.length == 3) {
			modifyField(getInspector(), commandLineInput);
			getInspector().printInspection();
		} else {
			System.err.println("Correct use of m: m <name> <value>");
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
	private void modifyField(Inspector inspector, String[] cmd) {
		String fieldName = cmd[1];
		String newValue = cmd[2];

		try {
			Field field = inspector.getFieldByName(fieldName);
			if (field == null)
				throw new NoSuchFieldException();
			else
				field.setAccessible(true);
			/* Check which type to parse the value to */
			Parameter parameter = getConsole().parseParameter(newValue,
					new IntegerParser());
			field.set(inspector.getObject(), parameter.getValue());
			
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
