/**
 * 
 */
package ist.meic.pa.commands;

import ist.meic.pa.Console;
import ist.meic.pa.Inspector;

public abstract class Command {
	private Inspector inspector;
	private Console console;
	
	public Command(Inspector inspector, Console console) {
		super();
		this.inspector = inspector;
		this.console = console;
	}
	
	public Inspector getInspector() {
		return inspector;
	}

	public Console getConsole() {
		return console;
	}

	public abstract void execute(String[] commandLineInput);
}
