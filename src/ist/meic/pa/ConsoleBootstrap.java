package ist.meic.pa;

import ist.meic.pa.commands.CallMethodCommand;
import ist.meic.pa.commands.HelpCommand;
import ist.meic.pa.commands.InspectFieldCommand;
import ist.meic.pa.commands.InspectSavedObjectCommand;
import ist.meic.pa.commands.ListFieldsCommand;
import ist.meic.pa.commands.ListMethodsCommand;
import ist.meic.pa.commands.ModifyFieldCommand;
import ist.meic.pa.commands.NextCommand;
import ist.meic.pa.commands.PrevCommand;
import ist.meic.pa.commands.QuitCommand;
import ist.meic.pa.commands.SaveCommand;
import ist.meic.pa.parsers.BooleanParser;
import ist.meic.pa.parsers.ByteParser;
import ist.meic.pa.parsers.CharParser;
import ist.meic.pa.parsers.DoubleParser;
import ist.meic.pa.parsers.FloatParser;
import ist.meic.pa.parsers.IntegerParser;
import ist.meic.pa.parsers.LongParser;
import ist.meic.pa.parsers.ShortParser;
import ist.meic.pa.parsers.StringParser;

/**
 * The Class ConsoleBootstrap.
 */
public class ConsoleBootstrap {

	public static void bootstrap(Console console) {
		initCommands(console);
		initParameterParsers(console);
		console.readEvalPrint();
	}

	/**
	 * Inits the commands.
	 *
	 * @param console the console
	 */
	private static void initCommands(Console console) {
		Inspector inspector = console.getInspector();

		console.addCommand("q", new QuitCommand(inspector, console));
		console.addCommand("i", new InspectFieldCommand(inspector, console));
		console.addCommand("m", new ModifyFieldCommand(inspector, console));
		console.addCommand("c", new CallMethodCommand(inspector, console));
		console.addCommand("help", new HelpCommand(inspector, console));
		console.addCommand("methods", new ListMethodsCommand(inspector, console));
		console.addCommand("fields", new ListFieldsCommand(inspector, console));
		console.addCommand("inspect", new InspectSavedObjectCommand(inspector, console));
		console.addCommand("next", new NextCommand(inspector, console));
		console.addCommand("prev", new PrevCommand(inspector, console));
		console.addCommand("save", new SaveCommand(inspector, console));
	}
	
	/**
	 * Inits the parameter parsers.
	 *
	 * @param console the console
	 */
	private static void initParameterParsers(Console console) {
		console.addParameterParser("b", new ByteParser());
		console.addParameterParser("s", new ShortParser());
		console.addParameterParser("i", new IntegerParser());
		console.addParameterParser("L", new LongParser());
		console.addParameterParser("f", new FloatParser());
		console.addParameterParser("d", new DoubleParser());
		console.addParameterParser("B", new BooleanParser());
		console.addParameterParser("\'", new CharParser());
		console.addParameterParser("\"", new StringParser());
		console.addParameterParser("true", new BooleanParser());
		console.addParameterParser("false", new BooleanParser());
	}
}
