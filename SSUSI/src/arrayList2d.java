import java.util.ArrayList;
import java.util.List;

//A new 2D collection formed as an ArrayList, of ArrayLists, of generic objects "T"
public class arrayList2d<T>
{
	private List<ArrayList<T>> mainList = new ArrayList<ArrayList<T>>();
	
	public arrayList2d()
	{}
	
	public arrayList2d(arrayList2d<T> list)
	{
		for(int a = 0; a < list.getHeight(); a++)
			for(int b = 0; b < list.getWidth(); b++)
				add(list.get(a, b), a);
	}
	
	public int getHeight()
	{
		return mainList.size();
	}
	
	public int getWidth()
	{
		if(!mainList.isEmpty())
			return mainList.get(0).size();
		return -1;
	}
	 
	public void add(T o, int y)
	{
		while(mainList.size()-1 < y)
			mainList.add(new ArrayList<T>());
		mainList.get(y).add(o);
	}
	
	public void remove(int y, int x)
	{
		mainList.get(y).remove(x);
	}
	
	public void removeRow(int y)
	{
		mainList.remove(y);
	}
	
	public void removeColumn(int x)
	{
		for(int a = 0; a < mainList.size(); a++)
			mainList.get(a).remove(x);
	}
	
	public void clear()
	{
		mainList.clear();
	}
	
	public void stackedListTo2dArrayList(ArrayList<ArrayList<T>> arrayList)
	{
		for(int a = 0; a < arrayList.size(); a++)
			for(int b = 0; b < arrayList.get(a).size(); b++)
			{
				this.add(arrayList.get(a).get(b), a);
			}
	}
	
	public T get(int y, int x)
	{
		return(mainList.get(y).get(x));
		
	}
}
