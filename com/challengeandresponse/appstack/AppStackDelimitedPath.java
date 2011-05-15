package com.challengeandresponse.appstack;

import java.util.*;

/**
 * A concrete implementation of AppStackPathI in which the path is a string, and elements are separated by a delimiter.<br />
 * The default delimiter is '/', so a call to the method 'ports' with three arguments might look like this:<br />
 * ports/COM1/bitrate/8<br />
 * some other examples:<br />
 * Zammer/time/*<br />
 * Zammer/all<br />
 *  
 * <p>A path ELEMENT is all the text from one delimiter to the next, but not including the slash. The other delimiters
 * are start-of-text and end-of-text.</p>
 * <p>The delimiter cannot occur in an element as there is no escape character (yet).</p>
 * 
 * @author jim
 */
/*
 * REVISION HISTORY
 * 2010-07-22	Improved from AppStackSlashPath to AppStackDelimitedPath with a default delimiter '/'. Still no escape character, however.
 */


public class AppStackDelimitedPath implements AppStackPathI {
	private ArrayList <Object> aPath;
	private static final char DEFAULT_DELIMITER = '/';
	private String delimiter;
	
	public AppStackDelimitedPath() {
		this (null);
	}
	
	public AppStackDelimitedPath(String path) {
		this(path,DEFAULT_DELIMITER);
	}
	
	public AppStackDelimitedPath(String path, char delimitChar) {
		this.delimiter = delimitChar+"";
		aPath = new ArrayList <Object> ();
		append(path);
	}
	
	
	public void append(String path) {
		if (path != null)
			aPath.addAll(Arrays.asList(path.split("\\"+this.delimiter)));
	}
	
	public void append(AppStackPathI aspi) {
		if (aspi != null) {
			Iterator <?> it = aspi.getIterator();
			while (it.hasNext())
				aPath.add(it.next());
		}
	}

	public boolean hasNext() {
		return (aPath.size() > 0);
	}
	
	/**
	 * @return null if the path is empty, or the first item in the path if not, and removes the head element from the path
	 */
	public String popNext() {
		if (! hasNext())
			return null;
		String s = (String) aPath.get(0);
		aPath.remove(0);
		return s;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		Iterator <?> i = aPath.iterator();
		while (i.hasNext())
			sb.append(this.delimiter).append(i.next());
		return sb.toString();
	}
	
	/**
	 * @return an Iterator over the path, so that it can be explored nondestructively
	 */
	@SuppressWarnings("unchecked")
	public Iterator getIterator() {
		return aPath.iterator();
	}
	

	// for testing
	public static void main(String[] args) {
		System.out.println("TEST 1 default case, '/' delimiter BEGIN");
		AppStackDelimitedPath ap = new AppStackDelimitedPath("111/2222/33");
		if (! "111".equals(ap.popNext()))
			System.out.println("Error1 - 111");
		if (! "2222".equals(ap.popNext()))
			System.out.println("Error1 - 2222");
		if (! "33".equals(ap.popNext()))
			System.out.println("Error1 - 33");
		System.out.println("TEST 1 default case, '/' delimiter END");
		
		System.out.println("TEST 2 custom delimiter, '*' delimiter BEGIN");
		AppStackDelimitedPath ap2 = new AppStackDelimitedPath("111*2222*33",'*');
		if (! "111".equals(ap2.popNext()))
			System.out.println("Error2 - 111");
		if (! "2222".equals(ap2.popNext()))
			System.out.println("Error2 - 2222");
		if (! "33".equals(ap2.popNext()))
			System.out.println("Error2 - 33");
		System.out.println("TEST 2 custom delimiter, '*' delimiter END");
		
		System.out.println("TEST 3 default delimiter, but no delimited content BEGIN");
		AppStackDelimitedPath ap3 = new AppStackDelimitedPath("111*2222*33");
		if (! ap3.hasNext())
			System.out.println("Error3 - first call to hasNext() returned false, expected true");
		else
			ap3.popNext();
		if (ap3.hasNext())
			System.out.println("Error3 - hasNext returned true, expected false");
		if (ap3.popNext() != null)
			System.out.println("Error3 - popNext returned non-null object, expected null");
		System.out.println("TEST 3 default delimiter, but no delimited content END");
		
	}
		
	
	
	
}