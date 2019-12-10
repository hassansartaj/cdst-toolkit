package cdst.sm;

public class SMTransition {

	private String funcName;
	private String sourceState, targetState;
	private String parentStateName;

	public SMTransition(String funcName, String sourceState,
			String targetState, String parentStateName) {
		this.funcName = funcName;
		this.sourceState = sourceState;
		this.targetState = targetState;
		this.parentStateName=parentStateName;
	}
	
	public String getParentStateName()
	{
		return this.parentStateName;
	}
	
	public String getTransitionName()
	{
		return this.funcName;
	}
	
	public String getSourceState() {
		return this.sourceState;
	}
	
	public String getTargetState() {
		return this.targetState;
	}
}
