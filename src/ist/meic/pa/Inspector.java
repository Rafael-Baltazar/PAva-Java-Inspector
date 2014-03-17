package ist.meic.pa;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

/**
 * The Class Inspector. Can be started from any point of a Java program,
 * accepting as argument an object that should be inspected. The inspector must
 * be started using the following form: new ist.meic.pa.Inspector().
 * inspect(object)
 */
public class Inspector {

	private Object object;

	private Map<String, Field> objectFields;

	/**
	 * Instantiates a new inspector.
	 */
	public Inspector() {
	}

	public Object getObject() {
		return object;
	}

	public void setCurrentInspectedObject(Object object) {
		this.object = object;
		this.objectFields = getAllInstanceFields(object);
	}

	/**
	 * Presents all the relevant features of the object, namely: The class of
	 * the object; The name, type and current value of each of the object's
	 * fields; Plus other features we deemed important. Then provides a simple
	 * read-eval-print interface for further inspection.
	 */
	public void inspect(Object object) {
		setCurrentInspectedObject(object);

		printInspection();

		Console.readEvalPrint(this);
	}
	
	public void printInspection() {
		System.err.println(object + " is an instance of " + object.getClass().getName());
		System.err.println("----------");

		for (Field f : objectFields.values()) {
			try {
				System.err.println(inspectField(f));
			} catch (IllegalArgumentException e) {
				System.err.println("Illegal argument at inspecting field "
						+ f.getName());
			} catch (IllegalAccessException e) {
				System.err.println("Illegal access at inspecting field "
						+ f.getName());
			}
		}
	}

	public Field getFieldByName(String fieldName) throws NoSuchFieldException,
			SecurityException, IllegalArgumentException, IllegalAccessException {
		return this.objectFields.get(fieldName);
	}

	/*
	 * Similar to a Modifier toString. Returns null, if no modifier is found.
	 */
	private String getModifierString(int modifiers) {
		if (Modifier.isPrivate(modifiers)) {
			return "private";
		} else if (Modifier.isProtected(modifiers)) {
			return "protected";
		} else if (Modifier.isPublic(modifiers)) {
			return "public";
		} else
			return null;
	}

	public String inspectField(Field field) throws IllegalArgumentException,
			IllegalAccessException {
		field.setAccessible(true);

		String modifier = getModifierString(field.getModifiers());
		String fieldType = field.getType().getName();
		String fieldName = field.getName();
		String fieldValue = field.get(this.object).toString();

		return (modifier == null ? "" : (modifier + " ")) + fieldType + " "
				+ fieldName + " = " + fieldValue;
	}

	private Map<String, Field> getAllInstanceFields(Object object) {
		Map<String, Field> fields = new HashMap<String, Field>();

		Class<? extends Object> c = object.getClass();
		while (c != null) {
			for (Field f : c.getDeclaredFields()) {
				try {
					if (!Modifier.isStatic(f.getModifiers())) {
						fields.put(f.getName(), f);
					}
				} catch (IllegalArgumentException e) {
				}
			}
			c = c.getSuperclass();
		}

		return fields;
	}
}
