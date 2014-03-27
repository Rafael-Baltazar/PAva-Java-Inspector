package ist.meic.pa.commands;

public class Parameter {
	
	private Class<?> type;
	private Object value;
	
	public Parameter(Class<?> type, Object value) {
		super();
		this.type = type;
		this.value = value;
	}

	public Class<?> getType() {
		return type;
	}

	public Object getValue() {
		return value;
	}
}
