package ist.meic.pa.commands;

import ist.meic.pa.Console;
import ist.meic.pa.Inspector;

public class ListMethodsCommand extends Command {

	public ListMethodsCommand(Inspector inspector, Console console) {
		super(inspector, console);
	}

	@Override
	public void execute(String[] commandLineInput) {
		getInspector().printMethods();
	}

}
