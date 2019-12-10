package cdst.sm;

public class SMState {

	private String name;
	private String parentStateName;
	
	public SMState(String name, String parentStateName) {
		this.name = name;
		this.parentStateName=parentStateName;
	}

	public String getStateName() {
		return this.name;
	}
	
	public String getParentStateName() {
		return this.parentStateName;
	}
}