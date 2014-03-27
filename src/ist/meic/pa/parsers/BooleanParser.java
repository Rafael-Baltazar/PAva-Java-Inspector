package ist.meic.pa.parsers;

import ist.meic.pa.commands.Parameter;

public class BooleanParser extends ParameterParser {

	public BooleanParser() {
	}

	@Override
	public Parameter parse(String valueString) {
		Object value = Boolean.parseBoolean(valueString);
		return new Parameter(boolean.class, value);
	}

}
