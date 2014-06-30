import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import ucar.nc2.NetcdfFile;

public class ssusiUtils
{
	/**
	 * Displays a dialogue to save an image, then saves the image (or doesn't
	 * based on user input).
	 *
	 * @param map
	 *            an image to be saved
	 */
	public static void saveImage(BufferedImage map)
	{
		JFileChooser            chSaver = new JFileChooser();
		FileNameExtensionFilter png     = new FileNameExtensionFilter("GIF Image File", "gif");
		chSaver.setApproveButtonText("Save");
		chSaver.setFileFilter(png);
		
		int returns = chSaver.showOpenDialog(null);

		if (returns == JFileChooser.APPROVE_OPTION)
		{
			String filename = chSaver.getSelectedFile().toString();

			if (!filename.contains(".png"))
				filename = filename + ".png";
			try
			{
				File file = new File(filename);
				ImageIO.write(map, "png", file);
			}
			catch (IOException e)
			{
				JOptionPane.showMessageDialog(new JFrame(),"There was an error saving the picture"
						+" to the specified location. Ensure that the location has write permissions open.",
						"IO Exception", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	/**
	 * Converts a time in seconds to a time in standard hours/minutes/seconds
	 * format.
	 *
	 * @param seconds
	 *            The number of seconds since 00:00 local time
	 * @return Formatted string in the form "XX Hours, XX Minutes, XX Seconds"
	 */
	public static String secondsToTime(double seconds)
	{
		double  hours   = seconds / 3600;
		double  minutes = 60 * (hours % 1);
		seconds         = 60 * (minutes % 1);

		return String.format("%2.0f Hours, %2.0f Minutes, %2.0f Seconds", hours, minutes, seconds);
	}

	/**
	 * Creates two ArrayLists to be passed to the sort method. Then returns the
	 * sorted list of files.
	 *
	 * @param directory
	 *            The initial array of all files in the directory
	 * @return A sorted array of files. Sorted from youngest to oldest.
	 */
	public static ArrayList<File> sortTime(ArrayList<File> directory)
	{
		int 				a		= 0;
		NetcdfFile 			file 	= null;
		ArrayList<Integer> 	nums 	= new ArrayList<Integer>();
		for (a = 0; a < directory.size() - 1; a++);
		{
			// sanitize directory of unreadable files
			try
			{
				file = NetcdfFile.open(directory.get(a).getPath());
			}
			catch (IOException e)
			{
				directory.remove(a);
			}
		}

		for (a = 0; a < directory.size(); a++)
		{
			try
			{
				file 		= NetcdfFile.open(directory.get(a).getPath());
				int dayInt  = file.findVariable("DOY").read().getInt(0);
				int timeInt = (int) (file.findVariable("TIME").read().getDouble(0) + dayInt * 86400);
				nums.add(a, timeInt);
			}
			catch (IOException e)
			{
				directory.remove(a);
				nums.remove(a);
			}
		}
		return sort(directory, nums);
	}

	/**
	 * An intermediate sort for sortTime. CombSort.
	 *
	 * @param files
	 *            The files to be sorted
	 * @param nums
	 *            The time in seconds for each file
	 * @return The sorted list of files
	 */
	private static ArrayList<File> sort(ArrayList<File> files, ArrayList<Integer> nums)
	{
		//TODO This is slow, needs to be much faster
		//need to find a new sorting algorithm
		boolean unsorted = true;
		File    tempFile = null;
		int     tempInt  = -1;
		int     gap      = nums.size() - 1;

		//comb sort
		while (unsorted)
		{
			boolean didSort = false;
			for (int a = 0; a + gap < nums.size(); a++)
			{
				if (nums.get(a) > nums.get(a + gap))
				{
					tempFile = files.get(a);
					tempInt  = nums.get(a);

					files.set(a, files.get(a + gap));
					nums .set(a, nums .get(a + gap));
					files.set(a + gap, tempFile);
					nums .set(a + gap, tempInt);

					didSort  = true;
				}
			}
			if (gap == 1 && !didSort)
				unsorted = false;

			gap = (int) (gap / 1.3);

			if (gap < 1)
				gap = 1;
		}
		return files;
	}

	/**
	 * Generates the color bar.
	 *
	 * @param height
	 *            the maximum allowed height for the color bar
	 * @param width
	 *            the width for the color bar
	 * @return A color bar in BufferedImage format
	 */
	public static BufferedImage generateColorBar(int height, int width)
	{
		int           bucket = (int) Math.ceil(1789.0 / height);
		int           layer  = 1789 / bucket;
		BufferedImage image  = new BufferedImage(width, 1789 / bucket + 1, BufferedImage.TYPE_INT_RGB);

		// paints each line
		for (int f = 0; f < 1789; f += bucket)
		{
			int colores = getColorFromValue(f, 1);

			for (int i = 0; i < image.getWidth(); i++)
				image.setRGB(i, layer, colores);

			layer--;
		}
		return image;
	}

	/**
	 * Gets the integer color value of a numerical value.
	 *
	 * @param value
	 *            the value to be color-ified
	 * @param bucket
	 *            the number of values represented by each integer color value
	 * @return the integer color value
	 */
	public static int getColorFromValue(int value, int bucket)
	{
		//TODO Change this to be a loop
		if(value <= 0)
			return 0;
		int a = 0;
		int b = 0;
		int c = 0;
		
		b       = value / bucket;
		value  -= 255 * bucket;

		if (value > 0)
		{
			b       = 255;
			a       = value / bucket;
			value  -= 255 * bucket;

			if (value > 0)
			{
				a       = 255;
				b      -= value / bucket;
				value  -= 255 * bucket;

				if (value > 0)
				{
					b       = 0;
					c       = value / bucket;
					value  -= 255 * bucket;

					if (value > 0)
					{
						c       = 255;
						a      -= value / bucket;
						value  -= 255 * bucket;

						if (value > 0)
						{
							a       = 0;
							b       = value / bucket;
							value  -= 255 * bucket;

							if (value > 0)
							{
								b = 255;
								a = value / bucket;

								if (a > 256)
									a = 255;
							}
						}
					}
				}
			}
		}
		return 65536 * a + 256 * b + c;
	}
}