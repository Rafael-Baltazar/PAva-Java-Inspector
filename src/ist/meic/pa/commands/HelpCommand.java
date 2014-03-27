package ist.meic.pa.commands;

import ist.meic.pa.Console;
import ist.meic.pa.Inspector;

public class HelpCommand extends Command {

	public HelpCommand(Inspector inspector, Console console) {
		super(inspector, console);
	}

	@Override
	public void execute(String[] commandLineInput) {
		consoleHelp();
	}
	
	/**
	 * Prints the help for the console.
	 */
	private void consoleHelp() {
		System.err.println("------------------");
		System.err.println("| Java Inspector |");
		System.err.println("------------------");
		System.err.println("Available commands and usage:");
		System.err.println("i <field-name>               - inspects the field with the given name of the current inspected object");
		System.err.println("m <field-name> <new-value>   - modifies the value of a given field of the current inspected object");
		System.err.println("c <method-name> [parameter1] ... [parametern] - calls the method with the given name of the current inspected object using any parameters given");
		System.err.println("\t [parameteri] : <value>[<type>]");
		System.err.println("\t\t If you omit the <type> the <value> can be:");
		System.err.println("\t\t\t [0-9]+, \"text in quotes\", true, false, <name of a previous saved object>");
		System.err.println("\t\t If you want to explicitly give the parameter's type the <value> can be anything but you can't omit the <type>");
		System.err.println("\t\t The <type> can be one of the following");
		System.err.println("\t\t\t b, s, i, l, f, d, B ");
		System.err.println("\t\t\t\t b - byte");
		System.err.println("\t\t\t\t s - short");
		System.err.println("\t\t\t\t i - int");
		System.err.println("\t\t\t\t L - long");
		System.err.println("\t\t\t\t f - float");
		System.err.println("\t\t\t\t d - double");
		System.err.println("\t\t\t\t B - boolean");
		System.err.println("\t\t The <value> and <type> combination needs to make sense");
		System.err.println("\t\t So, for instance, a float with value 3.14 is just 3.14f");
		System.err.println("\t\t But, if you try 3.14l it will not make sense because you cannot assign 3.14 to a long");
		System.err.println("save <object-name>           - saves the current inspected object with the given name");
		System.err.println("inspect <object-name>        - inspects the saved object with the given name");
		System.err.println("objects                      - shows all the previously saved objects");
		System.err.println("methods                      - shows all the available methods of the current inspected object");
		System.err.println("fields                       - shows all the available fields of the current inspected object");
		System.err.println("next                         - jumps to the next inspected object in the graph of objects, if it exists");
		System.err.println("prev                         - jumps to the previous inspected object in the graph of objects, if it exists");
		System.err.println("help                         - brings up the help menu");
		System.err.println("q                            - terminates the application");
	}

}
