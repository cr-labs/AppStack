package com.challengeandresponse.appstack;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * This class may be instantiated as an object in another class, or extended, creating an object that is
 * innately an AppStack.
 * 
 * Methods that can be called via the AppStack have this signature:<br />
 * public (returnType) methodName(AppStackPathI, Object)<br />
 * 
 * @author jim
 */

/*
 * REVISION HISTORY
 * <p>20070329 added ability to call down an appstack with a distinct OBJECT sent to the method
 * at the end of the path (if the search terminates in a method)... so that appstack methods
 * can operate on other than strings
 * <p>20070330 The Object methods are now method(AppStackPathI, Object) so that ALL
 * methods called by Appstack have the ability to continue using the rest of the
 * AppStack path when processing the passed-in object. For example:
 * AppStackMethod("path/to/command/METHOD/modifier",Object)
 * where the METHOD is called as soon as it's encountered in the path. The method is then
 * handed the partial AppStack "/modifier", and the object to operate on.
 * <p>20070405 Now there is ONLY support for method(AppStackPathI,Object) methods...
 * the single-arg method is not supported at all
 * <p>20091111 - Now this can be either extended by a child class (the methods called
 * are all in 'this' - the child class - or it can be explicitly instantiated and
 * handled as a freestanding object within some other class, with a target object
 * specified to operate on, in the constructor. Cool and flexible.
 * <p>20100722 Code review. Cleanups for compatibility with the new AppStackDelimitedPath
 * 
 * 
 */

public class AppStack {

	private Object target;
	
	// the mapping of names to stacked objects
	private Hashtable <String, Object> CATALOG = new Hashtable <String, Object> ();

	private static String GETALL_SYMBOL = "*";
	private static String GETPARAMS_SYMBOL = "?";
	
	public static transient boolean DEBUG = false; 


	/**
	 * A constant that's used in the addMethod() method below to reference a method that's called with one argument
	 */
	private static final Class <?> [] ARGS_OBJECT_ARRAY = new Class[2];
	static {
		ARGS_OBJECT_ARRAY[0] = AppStackPathI.class;
		ARGS_OBJECT_ARRAY[1] = Object.class;
	}


	/**
	 * Default empty constructor, works great when a class extends AppStack - method calls are on 'this'
	 */
	public AppStack() {
		this(null);
	}
	
	/**
	 * This constructor sets an explicit object to operate on - when class does not
	 * extend AppStack but creates an AppStack object - this tells the new AppStack object what other
	 * object to operate on 
	 * 
	 * @param target
	 */
	public AppStack(Object target) {
		if (target == null)
			this.target = this;
		else
			this.target = target;
	}

	/**
	 * Override the symbol that will invoke the "getAll()" method. If not changed by a call to this method, the default is "*"
	 * @param symbol the symbol that indicates "getAll()" 
	 */
	public static void setGetAllSymbol(String symbol) {
		GETALL_SYMBOL = symbol;
	}

	/**
	 * Override the symbol that will invoke the "getParams()" method. If not changed by a call to this method, the default is "?"
	 * @param symbol the symbol that indicates "getParams()" 
	 */
	public static void setGetParamsSymbol(String symbol) {
		GETPARAMS_SYMBOL = symbol;
	}

	/**
	 * Override the symbol that will invoke the "getAll()" method. If not changed by a call to this method, the default is "*"
	 */
	public static String getGetAllSymbol() {
		return GETALL_SYMBOL;
	}

	/**
	 * Override the symbol that will invoke the "getParams()" method. If not changed by a call to this method, the default is "?"
	 */
	public static String getGetParamsSymbol() {
		return GETPARAMS_SYMBOL;
	}

	private void internalAdd(String label, Object o)
	throws AppStackException {
		if ( (GETALL_SYMBOL.equals(label)) || (GETPARAMS_SYMBOL.equals(label)) )
			throw new AppStackException("Cannot add label. Label '"+label+"' cannot be the same as GetAllSymbol ("+GETALL_SYMBOL+") or GetParamsSymbol ("+GETPARAMS_SYMBOL+")");
		if (! hasLabel(label))
			CATALOG.put(label,o);
		else
			throw new AppStackException("Cannot add label. Label "+label+" is already assigned");
	}

	/**
	 * Add a non-method object to this AppStack's catalog, e.g. a String that is returned
	 * @param label the name under which this gettable is accessed
	 * @param o the object to store, an AppStack or a String.
	 */
	public final void add(String label,Object o)
	throws AppStackException {
		if (o instanceof Method)
			throw new AppStackException("To add a method, use addMethod() with the method's name, not add()");
		internalAdd(label,o);
	}

	/**
	 * Add a method to this AppStack's catalog. The method will service calls to "label"
	 * Methods that care MUST read the path to check it. Also methods may be INVOKED when the AppStackPathI
	 * is recursing to running getAll(), depending on the design of the child class.... so make very sure 
	 * that methods that should not be invoked unintentionally, always require an argument and always
	 * check for it in the passed-in AppStackPathI. For example, if you have a method called "shutdown"
	 * make the method check for a call like "shutdown/now" rather than ignoring the contents
	 * of the AppStackPathI. Otherwise, a getAll() on this would shut down the system.
	 * 
	 * The LABEL is appended here with the parameters for the method, so method signature can be divined
	 * 
	 * @param label the name under which this is accessed
	 * @param methodName the name of the method to add. This will be searched for, and an exception thrown if it can't be found
	 * @throws AppStackException
	 */
	public final void addMethod(String label, String methodName)
	throws AppStackException {
		Vector <Method> methods = findMethods(methodName);
		if (methods.size() > 0) {
			Iterator <Method> it = methods.iterator();
			while (it.hasNext()) 
				internalAdd(label, it.next());
		}
		else
			throw new AppStackException("Method not found:"+methodName+"(AppStackPathI,Object). Registered methods have that signature.");
	}

		
		/**
		 * Search for a method with the given name, having the arguments we support here.
		 * All methods must have the signature methodname(AppStackPathI,Object)
		 * @param methodName the string name of the method to find
		 * @return a Vector of Methods that has the name and required signature, or an empty Vector if no method was found
		 */
		private Vector <Method> findMethods(String methodName) {
			Vector <Method> methods = new Vector <Method> ();
			// look for a method named 'methodName' that takes (aspi,Object) arguments
			try { 
				Method mm = target.getClass().getMethod(methodName, ARGS_OBJECT_ARRAY);
				methods.add(mm);
			}
			catch (NoSuchMethodException nsme) {
			}
	
			return methods;
		}




	/**
	 * Remove any registered Object from this Controllable's catalog.
	 * @param label
	 */
	public final void remove(String label) {
		CATALOG.remove(label);
	}



	/**
	 * 
	 * @param label the label to check for
	 * @return true if the label is in the catalog
	 */
	public final boolean hasLabel(String label) {
		return CATALOG.containsKey(label);
	}

	/**
	 * @return a Vector of String labels of all things registered with this Appstack
	 */
	public final Vector <String> getLabels()
	throws AppStackException {
		return getLabelsForClass(null);
	}


	/**
	 * Return only the labels for objects of the given class
	 * @param c the class to tease out of the CATALOG, or null to fetch all classes
	 * @return a vector of String labels of the CATALOG objects whose Class is 'c'
	 */
	public final Vector <String> getLabelsForClass(Class <?> c)
	throws AppStackException {
		try {
			Vector <String> v = new Vector <String> ();
			Enumeration <String> en = CATALOG.keys();
			while (en.hasMoreElements()) {
				String key = en.nextElement();
				Object value = CATALOG.get(key);
				if ((c == null) || (Class.forName (c.getName()).isInstance(value)))
					v.add(key);
			}
			return v;
		} 
		catch (Exception e) {
			throw new AppStackException("Exception in getLabelsForClass():"+e.getMessage());
		}
	}


	/**
	 * getAll() is called when the end of the path is GETALL_SYMBOL indicating "get all"
	 */
	public Object getAll(AppStackPathI asp)
	throws AppStackException {
		Hashtable <String, Object> h = new Hashtable <String,Object> ();
		Iterator <String> i = getLabels().iterator();
		while (i.hasNext()) {
			String s = i.next();
			if (s.equals(GETALL_SYMBOL)) // dont recurse forever!
				continue;
			Object o = null;
			try {
				o = get(new AppStackDelimitedPath(s));
			}
			catch (Exception e) {
				continue;
			}
			// omit any call that doesn't return a value -- e.g. registered set-only methods
			if (o == null)
				continue;
			h.put(s,o);
		}
		return h;
	}


	/**
	 * getParams() is called when the last item in the path is GETPARAMS_SYMBOL indicating "get parameters"
	 * By default, getParams() returns all labels that have been added to the class, simply
	 * calling and returning the results of method getLabels(), which is provided in AppStack. 
	 * getParams() may be overridden for more customized results, but getLabels() may be
	 * sufficient much of the time (as long as an unsorted, unfiltered return is acceptable)
	 */
	public Object getParams(AppStackPathI asp) 
	throws AppStackException {
		return getLabels();
	}



	/*
	 * Recursive getter for ALL child classes, values and params too with objects-as-arguments support
	 * 
	 * Unlike the single-argument version of get(), if this version is 
	 * walking the appstack an encounters a method, it does not pass the
	 * remainder of the AppStack to that method, but instead passes it
	 * the ob[] array - containing arguments for the target method, presumably.
	 * 
	 * <p>It is entirely up to the caller at the top of the recursion stack to
	 * deal with whatever comes back... in other words, make sure all methods
	 * that extend AppStack are returning expected kinds of values, and that 
	 * the thing at the top knows what to do with whatever comes back...</p>
	 * 
	 * @param path an AppStackPathI
	 * @param ob an array of objects to be passed to the method, if the appstack ends in a method
	 * @return the result of the method call if there was one, or a param list, or "all"
	 */
	public final Object get(AppStackPathI asp, Object ob)
	throws AppStackException {
		String item;

		if (! asp.hasNext()) {
			throw new AppStackException("Path ran out before a terminal action was found");
		}
		else {
			item = asp.popNext();
		}
		// handle special cases. GETALL_SYMBOL and GETPARAMS_SYMBOL are special symbols mapped to required methods
		if  (item.equals(GETALL_SYMBOL))
			return getAll(new AppStackDelimitedPath(GETALL_SYMBOL));
		else if (item.equals(GETPARAMS_SYMBOL))
			return getParams(new AppStackDelimitedPath(GETPARAMS_SYMBOL));

		Object o = CATALOG.get(item);
		if (o == null)
			throw new AppStackException("Not found:"+item);
		else if (o instanceof AppStack) {
			return ((AppStack) o).get(asp);
		}
		else if (o instanceof Method) {
			Method m = (Method) o;
			try {
				Object[] oba = new Object[2];
				oba[0] = asp;
				oba[1] = ob;
				if (DEBUG) {
					System.out.println("oba: "+oba);
					System.out.println("[0,1]: ["+oba[0]+"],["+oba[1]+"]");
				}
				return m.invoke(target, oba);
			}
			catch (IllegalAccessException iae) {
				throw new AppStackException("Illegal Access Exception. Item:"+item+"; Retrieve method name:"+m.getName()+" Message:"+iae.getLocalizedMessage());
			}
			catch (InvocationTargetException ite) {
				if (DEBUG)
					ite.printStackTrace();
				if (ite.getCause() instanceof AppStackException) {
					throw (AppStackException) ite.getCause();
				}
				else {
					throw new AppStackException(ite.getMessage());
				}
			}
		}
		// otherwise just return the object
		else
			return o;
	}



	/*
	 * Recursive getter for ALL child classes, values and params too
	 * 
	 * It is entirely up to the caller at the top of the recursion stack to
	 * deal with whatever comes back... in other words, make sure all methods
	 * that extend AppStack are returning expected kinds of values, and that 
	 * the thing at the top knows what to do with whatever comes back...
	 * 
	 * If a method is found in the appstack, the rest of the stack is passed to it
	 * as an argument... and it's up to the method to figure out what to do from there.
	 * 
	 * @param path an AppStackPathI
	 * @return the result of the method call if there was one, or a param list, or "all"
	 */
	public final Object get(AppStackPathI asp)
	throws AppStackException {
		return get(asp,null);
	}
	
	

}
