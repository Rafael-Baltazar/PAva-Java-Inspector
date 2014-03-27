package ist.meic.pa.commands;

import ist.meic.pa.Console;
import ist.meic.pa.Inspector;

public class SaveCommand extends Command {

	public SaveCommand(Inspector inspector, Console console) {
		super(inspector, console);
	}

	@Override
	public void execute(String[] commandLineInput) {
		if (commandLineInput.length == 2) {
			saveCurrentInspectedObject(getInspector(), commandLineInput[1]);
		} else {
			System.err.println("Correct use of save: save <name>");
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
	private void saveCurrentInspectedObject(Inspector inspector,
			String name) {
		inspector.saveCurrentInspectedObject(name);
	}

}
