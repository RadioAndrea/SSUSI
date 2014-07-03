package ssusiDataRendering;

import java.util.ArrayList;
import java.util.List;

//A new 2D collection formed as an ArrayList, of ArrayLists, of generic objects "T"
/**
 * The Class arrayList2d.
 *
 * @param <T> the generic type
 */
public class arrayList2d<T>
{
	
	/** The main list. */
	private List<ArrayList<T>> mainList = new ArrayList<ArrayList<T>>();
	
	/**
	 * Instantiates a new arraylist2d.
	 */
	public arrayList2d()
	{}
	
	/**
	 * Instantiates a new arraylist2d.
	 *
	 * @param list another arraylist2d instance to deep copy into the new arraylist2d instance
	 */
	public arrayList2d(arrayList2d<T> list)
	{
		for(int a = 0; a < list.getHeight(); a++)
			for(int b = 0; b < list.getWidth(); b++)
				add(list.get(a, b), a);
	}
	
	/**
	 * Gets the size of the ArrayList of ArrayLists
	 *
	 * @return the height
	 */
	public int getHeight()
	{
		return mainList.size();
	}
	
	/**
	 * Gets the size of the first ArrayList in the ArrayList of ArrayLists. If that array doesn't exist, returns -1.
	 *
	 * @return the width
	 */
	public int getWidth()
	{
		if(!mainList.isEmpty())
			return mainList.get(0).size();
		return -1;
	}
	 
	/**
	 * Adds the specified object to the end of the list at location y on the arraylist of arraylists
	 *
	 * @param o the object
	 * @param y the location of the target list on the list of lists
	 */
	public void add(T o, int y)
	{
		while(mainList.size()-1 < y)
			mainList.add(new ArrayList<T>());
		mainList.get(y).add(o);
	}
	
	/**
	 * Removes the object at the specified y and x coordinate
	 *
	 * @param y the location of the target list on the list of lists
	 * @param x the location of the object within the targeted list
	 */
	public void remove(int y, int x)
	{
		mainList.get(y).remove(x);
	}
	
	/**
	 * Removes the specified array from the list of lists
	 *
	 * @param y the location of the array within the list of lists
	 */
	public void removeRow(int y)
	{
		mainList.remove(y);
	}
	
	/**
	 * Removes the specified index from all lists within the list of lists
	 *
	 * @param x the index of the location to be removed from ALL LISTS within those lists
	 */
	public void removeColumn(int x)
	{
		for(int a = 0; a < mainList.size(); a++)
			mainList.get(a).remove(x);
	}
	
	/**
	 * Clears the list.
	 */
	public void clear()
	{
		mainList.clear();
	}
	
	/**
	 * Converts a manually created stacked array list into an arrayList2d object
	 *
	 * @param arrayList the manually created array list.
	 */
	public void stackedListTo2dArrayList(ArrayList<ArrayList<T>> arrayList)
	{
		for(int a = 0; a < arrayList.size(); a++)
			for(int b = 0; b < arrayList.get(a).size(); b++)
			{
				this.add(arrayList.get(a).get(b), a);
			}
	}
	
	/**
	 * Gets the object at the specified y and x location
	 *
	 * @param y the location of the object's list in the list of lists
	 * @param x the object's location in it's list
	 * @return the object at location y,x
	 */
	public T get(int y, int x)
	{
		return(mainList.get(y).get(x));
		
	}
}
