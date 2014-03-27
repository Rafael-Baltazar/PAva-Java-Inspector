package ist.meic.pa.commands;

import ist.meic.pa.Console;
import ist.meic.pa.Inspector;

public class InspectSavedObjectCommand extends Command {

	public InspectSavedObjectCommand(Inspector inspector, Console console) {
		super(inspector, console);
	}

	@Override
	public void execute(String[] commandLineInput) {
		if(commandLineInput.length == 2) {
			inspectSavedObject(getInspector(), commandLineInput);
		}
		else {
			System.err.println("Correct use of inspect: inspect <object-name>");
		}
	}
	
	private void inspectSavedObject(Inspector inspector, String cmd[]) {
		String objectName = cmd[1];
		Object object = inspector.getSavedObject(objectName);
		if(object == null) {
			System.err.println("Error: There is no saved object with name " + objectName);
		}
		else {
			inspector.setCurrentInspectedObject(object);
			inspector.printInspection();
		}
	}

}
