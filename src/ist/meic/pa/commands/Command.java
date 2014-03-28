/**
 * 
 */
package ist.meic.pa.commands;

import ist.meic.pa.Console;
import ist.meic.pa.Inspector;

/**
 * The Class Command.
 */
public abstract class Command {
	
	/** The inspector. */
	private Inspector inspector;
	
	/** The console. */
	private Console console;
	
	/**
	 * Instantiates a new command.
	 *
	 * @param inspector the inspector
	 * @param console the console
	 */
	public Command(Inspector inspector, Console console) {
		super();
		this.inspector = inspector;
		this.console = console;
	}
	
	/**
	 * Gets the inspector.
	 *
	 * @return the inspector
	 */
	public Inspector getInspector() {
		return inspector;
	}

	/**
	 * Gets the console.
	 *
	 * @return the console
	 */
	public Console getConsole() {
		return console;
	}

	/**
	 * Execute.
	 *
	 * @param commandLineInput the command line input
	 */
	public abstract void execute(String[] commandLineInput);
}
