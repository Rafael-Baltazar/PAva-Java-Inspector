package ist.meic.pa.commands;

import ist.meic.pa.Console;
import ist.meic.pa.Inspector;

public class NextCommand extends Command {

	public NextCommand(Inspector inspector, Console console) {
		super(inspector, console);
	}

	@Override
	public void execute(String[] commandLineInput) {
		next(getInspector());
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

}
