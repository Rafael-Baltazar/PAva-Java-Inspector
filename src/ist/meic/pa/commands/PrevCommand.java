package ist.meic.pa.commands;

import ist.meic.pa.Console;
import ist.meic.pa.Inspector;

public class PrevCommand extends Command {

	public PrevCommand(Inspector inspector, Console console) {
		super(inspector, console);
	}

	@Override
	public void execute(String[] commandLineInput) {
		prev(getInspector());
	}
	
	/**
	 * Prev.Go to the previous object in the graph of inspected objects.
	 * 
	 * @param inspector
	 *            the inspector
	 */
	private void prev(Inspector inspector) {
		if (inspector.goToPreviousObject()) {
			inspector.printInspection();
		} else {
			System.err.println("You are already inspecting the first object");
		}
	}

}
