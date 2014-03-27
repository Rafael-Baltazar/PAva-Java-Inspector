package ist.meic.pa.parsers;

import ist.meic.pa.Parameter;

public class ByteParser extends ParameterParser {

	public ByteParser() {
	}

	@Override
	public Parameter parse(String valueString) {
		Object value = Byte.parseByte(valueString);
		return new Parameter(byte.class, value);
	}

}
