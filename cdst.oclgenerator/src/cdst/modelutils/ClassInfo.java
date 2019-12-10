/**
 * 
 */
package cdst.modelutils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Hassan Sartaj
 * @version 1.0
 */
public class ClassInfo {
	private String className;
	private HashMap<String, String> attrDtMap;
	private HashMap<String, ArrayList<String>> enumLiteralMap;
	private ArrayList<String> states;
	
	public ClassInfo() {
		this.attrDtMap = new HashMap<>();
		this.states = new ArrayList<>();
		this.enumLiteralMap = new HashMap<>();
	}
	
	public ClassInfo(String className, HashMap<String, String> attrDtMap, ArrayList<String> states) {
		super();
		this.className = className;
		this.attrDtMap = attrDtMap;
		this.states = states;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public HashMap<String, String> getAttrDtMap() {
		return attrDtMap;
	}
	public void setAttrDtMap(HashMap<String, String> attrDtMap) {
		this.attrDtMap = attrDtMap;
	}
	public ArrayList<String> getStates() {
		return states;
	}
	public void setStates(ArrayList<String> states) {
		this.states = states;
	}
	public void addState(String state) {
		if(this.states != null)
			if(!this.states.contains(state))
				this.states.add(state);
	}
	
	public void addAttributeDT(String attr, String dt) {
		if(this.attrDtMap != null)
			this.attrDtMap.put(attr, dt);
	}
	
	public void addEnumLiterals(String enm, ArrayList<String> literals) {
		if(this.enumLiteralMap != null)
			this.enumLiteralMap.put(enm, literals);
	}
	public HashMap<String, ArrayList<String>> getEnumLiteralsMap() {
		return enumLiteralMap;
	}
}
