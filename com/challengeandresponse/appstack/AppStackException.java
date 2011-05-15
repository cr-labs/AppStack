package com.challengeandresponse.appstack;

/**
 * This form adds a 'condition' field to the exception...
 * it will be used in Agent communications to send an exception with error status code
 * back from a called method to a ProcessIQ method (in the RocketSync2 project for example)
 * 
 * @author jim
 *
 */
public class AppStackException extends Exception {
	private static final long serialVersionUID = 1L;
	
	private String condition;

	public AppStackException() {
		super();
		condition="";
	}

	public AppStackException(String arg0) {
		super(arg0);
		condition="";
	}
	
	public AppStackException(String arg0, String condition) {
		super(arg0);
		this.condition = condition;
	}

	public String getCondition() {
		return this.condition;
	}
}
