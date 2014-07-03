package ssusiDataRendering;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * The Class zoomView.
 */
public class zoomView
{
	
	/** The main frame. */
	JFrame frame;

	/** The main panel for the frame. */
	JPanel panel;

	/** Label that contains the map. */
	JLabel mapView;

	/** Button that lets user return image to default state. */
	JButton btReturn;

	/** The map itself. */
	BufferedImage liveMap;

	/** The live data list. */
	arrayList2d<Float> data;

	/** The original data, to restore the live data list. */
	arrayList2d<Float>  originalData;

	/** The bucket size. */
	int bucketSize;

	/**
	 * Instantiates a new zoom GUI.
	 *
	 * @param dataIn A 2d array list of values
	 */
	public zoomView(arrayList2d<Float> dataIn)
	{
		if(dataIn == null)
		{
			dataIn = new arrayList2d<Float>();
			dataIn.add((float) 0, 0);
		}
		data = new arrayList2d<Float>(dataIn);
		originalData = new arrayList2d<Float>(dataIn);
		
		liveMap = arrayToImage(data);
			
		frame   = new JFrame();
		panel   = new JPanel();

		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setTitle("Zoom View");
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setSize(363, 415);
		frame.add(panel);

		panel.setLayout(new BorderLayout());
		mapView  = new JLabel(new ImageIcon(liveMap));
		mapView.setMaximumSize(new Dimension(363, 363));
		btReturn = new JButton("Return to Full Size");

		panel.add(mapView, BorderLayout.CENTER);
		panel.add(btReturn, BorderLayout.SOUTH);

		frame.setVisible(true);

		btReturn.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				data = new arrayList2d<Float>(originalData);
				Graphics2D gfx = liveMap.createGraphics();
				gfx.drawImage(arrayToImage(data), 0, 0, null);
				gfx.dispose();
				mapView.repaint();
			}
		});

		mapView.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				if(e.getModifiers() == InputEvent.BUTTON1_MASK)
				{
					Graphics2D gfx = liveMap.createGraphics();
					gfx.drawImage(arrayToImage(cropArray(data, 0.40, mapView.getMousePosition().getX(), mapView.getMousePosition().getY())), 0, 0, null);
					gfx.dispose();
					mapView.repaint();
				}
				else
				{
					frame.setTitle(getValueAtPoint(mapView.getMousePosition(), data) + " Rayleighs at point " 
							+ mapView.getMousePosition().getX() + "," + mapView.getMousePosition().getY());
				}
			}
		});
	}

	/**
	 * Converts a 2d array list of values into a colored buffered image
	 *
	 * @param array2d Data to be processed
	 * @return BufferedImage representation of array2d
	 */
	private BufferedImage arrayToImage(arrayList2d<Float> array2d)
	{
		BufferedImage image = new BufferedImage(363, 363, BufferedImage.TYPE_INT_RGB);
		
		int y = array2d.getHeight();
		
		while(image.getHeight()%y!=0)
			y--;
		
		int bucket = image.getHeight()/y;
		for(int a = 0; a < y; a++)
			for(int b = 0; b < y; b++)
				for(int c = 1; c <= bucket; c++)
					for(int d = 1; d <= bucket; d++)
					{
						if (array2d.get(a, b) == 0)
							image.setRGB((b*bucket)+(c-1), (a*bucket)+(d-1), 3289650);
						else
						image.setRGB((b*bucket)+(c-1), (a*bucket)+(d-1), ssusiUtils.getColorFromValue((int)((float) array2d.get(a, b)), 5));
					}

		return image;
	}
	
	/**
	 * Crops an array.
	 *
	 * @param array2d 2d array list to be cropped
	 * @param factor a decimal factor to crop by (<1)
	 * @param mousex the mouse x location
	 * @param mousey the mouse y location
	 * @return a cropped version of array2d
	 */
	private arrayList2d<Float> cropArray(arrayList2d<Float> array2d, double factor, double mousex, double mousey)
	{
		
		while(factor>1)
			factor /= 10;
		int linesToRemove = (int) (array2d.getHeight()*factor);
		while(363%(array2d.getHeight()-linesToRemove)!=0)
			linesToRemove++;
		
		int    side     = array2d.getHeight()-linesToRemove;
		int    halfSide = side/2;
	
		double scale    = 363/array2d.getHeight();
		
		//upperlefty, upperleftx, bottomrighty, bottomrightx
		//respectively
		int uly = (int) (mousey/scale - halfSide);
		int ulx = (int) (mousex/scale - halfSide);
		int bry = uly + side;
		int brx = ulx + side;
		
		
		if(uly < 0)
		{
			uly = 0;
			bry = side;
		}
		if(ulx < 0)
		{
			ulx = 0;
			brx = side;
		}
		if(bry >= array2d.getHeight())
		{
			bry = array2d.getHeight()-1;
			uly = bry-side;
		}
		if(brx >= array2d.getWidth())
		{
			brx = array2d.getWidth()-1;
			ulx = brx - side;
		}
		
		while(uly-- > 0)
		{
			array2d.removeRow(0);
		}
		while(side < array2d.getHeight())
		{
			array2d.removeRow(array2d.getHeight()-1);
		}
		while(ulx-- > 0)
		{
			array2d.removeColumn(0);
		}
		while(side < array2d.getWidth())
		{
			array2d.removeColumn(array2d.getWidth()-1);
		}

		return array2d;
	}
	
	/**
	 * Gets the value at a point.
	 *
	 * @param point Point to examine
	 * @param array2d 2d array list to examine
	 * @return the value at point
	 */
	private float getValueAtPoint(Point point, arrayList2d<Float> array2d)
	{
		int ratio = 363/array2d.getHeight();
		int x = (int) point.getX()/ratio;
		int y = (int) point.getY()/ratio;
		
		return (float) array2d.get(y, x);
	}
}