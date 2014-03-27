package ist.meic.pa.parsers;

import ist.meic.pa.Parameter;

public class StringParser extends ParameterParser {

	public StringParser() {
	}

	@Override
	public Parameter parse(String valueString) {
		Object value = valueString.substring(1);
		return new Parameter(String.class, value);
	}

}
