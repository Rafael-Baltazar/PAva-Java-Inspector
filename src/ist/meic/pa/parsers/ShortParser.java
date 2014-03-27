package ist.meic.pa.parsers;

import ist.meic.pa.Parameter;

public class ShortParser extends ParameterParser {

	public ShortParser() {
	}

	@Override
	public Parameter parse(String valueString) {
		Object value = Short.parseShort(valueString);
		return new Parameter(short.class, value);
	}

}
