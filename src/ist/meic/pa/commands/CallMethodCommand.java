package ist.meic.pa.commands;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import ist.meic.pa.Console;
import ist.meic.pa.Inspector;
import ist.meic.pa.Parameter;
import ist.meic.pa.parsers.IntegerParser;

public class CallMethodCommand extends Command {

	public CallMethodCommand(Inspector inspector, Console console) {
		super(inspector, console);
	}

	@Override
	public void execute(String[] commandLineInput) {
		if (commandLineInput.length > 1) {
			callMethod(getInspector(), commandLineInput);
		} else {
			System.err.println("Correct use of c: c <name> "
					+ "[<value 0> ... <value n>]");
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
	private void callMethod(Inspector inspector, String[] cmd) {
		String methodName = cmd[1];
		List<Object> args = new ArrayList<Object>();
		List<Class<?>> parameterTypes = new ArrayList<Class<?>>();

		for (int i = 2; i < cmd.length; i++) {
			try {
				Parameter parameter = getConsole().parseParameter(cmd[i],
						new IntegerParser());

				args.add(parameter.getValue());
				parameterTypes.add(parameter.getType());
			}
			catch(Exception e) {
				System.err.println(cmd[i] + " is not a valid parameter");
				return;
			}
			
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
