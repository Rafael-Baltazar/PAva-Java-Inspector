package ist.meic.pa.parsers;

import ist.meic.pa.Parameter;

public class CharParser extends ParameterParser {

	public CharParser() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public Parameter parse(String valueString) {
		Object value = valueString.charAt(1);
		return new Parameter(char.class, value);
	}

}
