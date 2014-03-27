package ist.meic.pa.parsers;

import ist.meic.pa.Parameter;

public class DoubleParser extends ParameterParser {

	public DoubleParser() {
	}

	@Override
	public Parameter parse(String valueString) {
		Object value = Double.parseDouble(valueString);
		return new Parameter(double.class, value);
	}

}
