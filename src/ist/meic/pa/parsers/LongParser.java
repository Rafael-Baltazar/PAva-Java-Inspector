package ist.meic.pa.parsers;

import ist.meic.pa.Parameter;

public class LongParser extends ParameterParser {

	public LongParser() {
	}

	@Override
	public Parameter parse(String valueString) {
		Object value = Long.parseLong(valueString);
		return new Parameter(long.class, value);
	}

}
