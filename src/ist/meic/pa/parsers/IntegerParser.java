package ist.meic.pa.parsers;

import ist.meic.pa.commands.Parameter;

public class IntegerParser extends ParameterParser {

	public IntegerParser() {
	}

	@Override
	public Parameter parse(String valueString) {
		Object value = Integer.parseInt(valueString);
		return new Parameter(int.class, value);
	}

}
