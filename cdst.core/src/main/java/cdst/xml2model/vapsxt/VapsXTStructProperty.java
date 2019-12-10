/**
 * 
 */
package cdst.xml2model.vapsxt;

import java.util.ArrayList;

/**
 * A class that represents the structural (struct) property of the model in VapsXT XML
 * 
 * @author Hassan Sartaj
 * @version 1.0
 */
public class VapsXTStructProperty extends VapsXTProperty{
	private ArrayList<Field> fields;
	
	public VapsXTStructProperty() {
		this.fields = new ArrayList<>(1);
	}
	
	public void addField(String name, String value) {
		Field field = new Field(name, value);
		fields.add(field);
	}

	/**
	 * @return the fields
	 */
	public ArrayList<Field> getFields() {
		return fields;
	}

	/**
	 * A class that represents the field of structural property 
	 * 
	 * @author Hassan Sartaj
	 * @version 1.0
	 */
	class Field {
		private String name, value;
		/**
		 * @param name
		 * @param value
		 */
		public Field(String name, String value) {
			super();
			this.name = name;
			this.value = value;
		}
		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}
		/**
		 * @param name the name to set
		 */
		public void setName(String name) {
			this.name = name;
		}
		/**
		 * @return the value
		 */
		public String getValue() {
			return value;
		}
		/**
		 * @param value the value to set
		 */
		public void setValue(String value) {
			this.value = value;
		}
	}
}
