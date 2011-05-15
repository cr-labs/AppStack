package com.challengeandresponse.appstack;

/**
 * Test cases for AppStack
 * @author jim
 */
public class Test extends AppStack {

	public String testExceptionThrow(AppStackPathI aspi, Object args)
	throws AppStackException {
		throw new AppStackException("This is the message for the AppStackException thrown from testExceptionThrow() method");
	}
	
	/**
	 * Test with an Object parameter
	 * @param args
	 */
	public String testObject(AppStackPathI aspi, Object args) {
		System.out.println("in testObjectArray()");
		String stringVersion = "";
		if (args instanceof Integer) {
			System.out.println("received object is an integer");
			stringVersion = ((Integer) args).toString();
		}
		else 
			System.out.println("received object is NOT an integer");
			stringVersion = args.toString();
		return "aspi:"+aspi.toString()+" args:"+ stringVersion;
	}

	
	/**
	 * Test with a conventional AppStackPathI argument
	 * @param aspi
	 * @return the next item in provided AppStackPathI (it calls aspi.popNext() and returns that)
	 */
	public String testAppStackPathI(AppStackPathI aspi,Object o) {
		return ("aspi.popNext():"+aspi.popNext());
	}
	
	
	public static void main(String[] args) {
		Test t = new Test();

		try {
			t.addMethod("nonexistent","nonexistent");
			System.out.println("FAIL: allowed adding nonexistent method");
		}
		catch (Exception e) {
			System.out.println("OK: addMethod threw exception for nonexistent method");
		}
		
		try {
			t.addMethod("testObject","testObject");
			t.addMethod("test2","testAppStackPathI");
			t.addMethod("testExceptionThrow","testExceptionThrow");

			String sTest = "test1argument";
			Integer iTest = new Integer(12);
			
			try {
				t.get(new AppStackDelimitedPath("nonexistent/nice"),sTest);
				System.out.println("FAIL: allowed calling nonexistend method");
			}
			catch (Exception e) {
				System.out.println("OK: get threw exception for non-registered label");
			}
			
			System.out.println("Calling testObject -> testObject() with modifier 'nice' and String object 'test1argument'");
			System.out.println(t.get(new AppStackDelimitedPath("testObject/nice"), sTest));
			
			System.out.println("Calling testObject -> testObject() with modifier 'nice' and Integer object 12");
			System.out.println(t.get(new AppStackDelimitedPath("testObject/nice"), iTest));
			
			System.out.println("Calling test2 with modifier 'nice' and null object");
			System.out.println(t.get(new AppStackDelimitedPath("test2/nice"),(Object) null));

			System.out.println("Calling textExceptionThrow()");
			t.get(new AppStackDelimitedPath("testExceptionThrow"),sTest);
		}
		catch (Exception e) {
			System.out.println("Exception: "+e.getMessage());
		}
	}

	
}
