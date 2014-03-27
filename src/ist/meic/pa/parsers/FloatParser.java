package ist.meic.pa.parsers;

import ist.meic.pa.Parameter;

public class FloatParser extends ParameterParser {

	public FloatParser() {
	}

	@Override
	public Parameter parse(String valueString) {
		Object value = Float.parseFloat(valueString);
		return new Parameter(float.class, value);
	}

}
