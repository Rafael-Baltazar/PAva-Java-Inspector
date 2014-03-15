package ist.meic.pa;

import java.lang.reflect.Field;

/**
 * The Class Inspector. Can be started from any point of a Java program, 
 * accepting as argument an object that should be inspected. The inspector 
 * must be started using the following form: new ist.meic.pa.Inspector().
 * inspect(object)
 */
public class Inspector {
	
	/**
	 * Instantiates a new inspector.
	 */
	public Inspector() { }
	
	/**
	 * Presents all the relevant features of the object, namely: The class of 
	 * the object; The name, type and current value of each of the object's 
	 * fields; Plus other features we deemed important.
	 * Then provides a simple read-eval-print interface for further inspection.
	 */
	public void inspect(Object object) {
		Class<? extends Object> c = object.getClass();
		System.err.println(object + " is an instance of " + c.getName());
		System.err.println("----------");
		for(Field f : c.getFields()) {
			//TODO: print public, private, protected
			//TODO: print field name
			//TODO: print =
			//TODO: print print value
		}
		Console.readEvalPrint();
	}
}
