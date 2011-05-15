package com.challengeandresponse.appstack;

import java.util.Iterator;

/**
 * An AppStackPath is a String path through the AppStack...
 * for example, /ports/COM1/bitrate/*  to return all "bitrate" values for /ports/COM1
 * by running the method mapped to ports/COM1/bitrate
 * 
 * @author jim
 */

public interface AppStackPathI {

	/**
	 * @return true if this AppStackPath has another item, false if not
	 */
	public boolean hasNext();

	/**
	 * Pop the next item off the head of this AppStackPath and return it, if there is one
	 * @return null if the path is empty, or the first item in the path if not (and also remove that item from the path)
	 */
	public String popNext();

	/**
	 * Appends 's' as a new node at the end of this AppStackPath
	 * @param s
	 */
	public void append(String s);
	
	/**
	 * Append another AppStackPath the end of this AppStackPath.
	 * Implementations should accept any AppStackPathI implementing class...
	 * <p>Recommendation: use instanceof to select between different import methods.
	 * Stepping through 'asp' using its Iterator (from getIterator()) is guaranteed
	 * to work. Caution, don't call popNext() when appending -- this method should have no
	 * side effects, and should not destroy the appended path!</p>
	 * @param asp
	 */
	public void append(AppStackPathI asp);

	/**
	 * @return this AppStackPath as a string in a form as close to its native form if possible
	 */
	public String toString();

	/**
	 * Get an Iterator over this AppStackPathI so the path can be nondestructively browsed or imported
	 * @return an iterator over the items in this AppStackPath
	 */
	public Iterator <AppStackPathI> getIterator();
	
}
