/*
 * 
 */
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import ucar.ma2.Array;
import ucar.ma2.ArrayFloat;
import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;

/**
 * The Class ssusiRender.
 */
public class ssusiRender

{

	/** The frame. */
	private JFrame            frame      = new JFrame();

	/** The current file in the directory. */
	private File              current    = null;

	/** The index of the file in the array of files. */
	private int               index;

	/** File chooser, set for directory choose mode. */
	private JFileChooser      chFiler;

	/** Button for execution of file choosing functions. */
	private JButton           btChooseFile;

	/** Button for execution of map rendering. */
	private JButton           btExecute;

	/** A combo box with layer numbers 0-4. */
	private JComboBox<String> cbLayer;

	/** A combo box with the south and north pole data names. */
	private JComboBox<String> cbVariable;

	/** The map "frame" */
	private JLabel            mapView;

	/** A label for the layer box. */
	private JLabel            lbBox;

	/** The color bar. */
	private JLabel            cBar;

	/** A label to show the current file in a directory. */
	private JLabel            numOfNum;

	/** The upper label for the color bar. */
	private JLabel            cBarHigh;

	/** The bucket size. */
	private int               bucketSize = 5;

	/** The directory name in a text field. */
	private TextField         fileName;

	/** List of files in the directory*/
	private ArrayList<File>   directory;

	/** menu item to set the window as resizable*/
	private JCheckBoxMenuItem resizable;

	/** The map image itself */
	private BufferedImage     map;
	

	/**
	 * The main method.
	 *
	 * @param args
	 *            the arguments
	 */
	public static void main(String[] args)
	{
		new ssusiRender();
	}

	/**
	 * Instantiates the GUI.
	 */
	private ssusiRender()
	{
		directory              = new ArrayList<File>();
		map                    = new BufferedImage(363, 363, BufferedImage.TYPE_INT_RGB);
		BufferedImage colorBar = generateColorBar(363, 25);

		// //////////////////////////////////////////////////////
		//
		// GUI Code
		//
		// //////////////////////////////////////////////////////

		frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("SSUSI Map Data Rendering");
		frame.setSize(674, 520);
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setLayout(new BorderLayout());

		// GUI elements
		// assistance vars
		String[] numbers   = new String[] { "0", "1", "2", "3", "4" };
		String[] variables = new String[] { "DISK_RADIANCEDATA_INTENSITY_SOUTH",
		"DISK_RADIANCEDATA_INTENSITY_NORTH" };

		// file chooser
		chFiler = new JFileChooser();
		chFiler.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		// buttons
		btExecute                 = new JButton("Generate Maps");
		btChooseFile              = new JButton("Choose Directory");
		JButton      btNext       = new JButton("Next");
		JButton      btBack       = new JButton("Back");
		JButton      btSave       = new JButton("Save Image");
		JButton      btMltExecute = new JButton("View Magnetic Map");

		// lists, boxes, and text
		numOfNum         = new JLabel("<html> <br> <br> <br>");
		lbBox            = new JLabel("<html>3 and 4 are likely<br>LBHL and LBHS, repectively</html>");
		mapView          = new JLabel(new ImageIcon(map));
		cBarHigh         = new JLabel((bucketSize * 1789) + " R");
		cBar             = new JLabel(new ImageIcon(colorBar));
		JLabel   cBarLow = new JLabel("0 R");

		cbLayer    = new JComboBox<String>(numbers);
		cbVariable = new JComboBox<String>(variables);
		cbLayer.setSelectedIndex(3);
		cbLayer.setMaximumSize(new Dimension(50, 50));

		fileName = new TextField(60);

		// the upper menu bar
		JMenuBar  menuBar   = new JMenuBar();
		JMenu     about     = new JMenu("About");
		JMenu     settings  = new JMenu("Settings");
		JMenuItem legal     = new JMenuItem("Legal Info");
		JMenuItem data      = new JMenuItem("SSUSI Data Download");
		JMenuItem bucketSet = new JMenuItem("Change Bucket Size");
		resizable           = new JCheckBoxMenuItem("Resizable Window");

		frame.setJMenuBar(menuBar);
		menuBar.add(about);
		menuBar.add(settings);
		about.add(legal);
		about.add(data);
		settings.add(resizable);
		settings.add(bucketSet);

		// Upper portion GUI elements
		JPanel upTop = new JPanel();
		JPanel top   = new JPanel();
		upTop.setLayout(new BoxLayout(upTop, BoxLayout.PAGE_AXIS));
		upTop.add(top);
		upTop.add(cbVariable);
		top.setLayout(new FlowLayout());
		top.add(btChooseFile);
		top.add(fileName);
		frame.add(upTop, BorderLayout.NORTH);

		// right side GUI elements
		JPanel right = new JPanel();
		btSave.setPreferredSize(new Dimension(120, 50));
		btNext.setPreferredSize(new Dimension(120, 50));
		right.setLayout(new BoxLayout(right, BoxLayout.PAGE_AXIS));
		right.add(btExecute);
		right.add(btBack);
		right.add(btNext);
		right.add(btSave);
		right.add(btMltExecute);
		right.add(lbBox);
		right.add(cbLayer);
		frame.add(right, BorderLayout.EAST);

		// center GUI elements
		JPanel center    = new JPanel();
		JPanel cBarPanel = new JPanel();
		center.setLayout(new BorderLayout());
		center.add(mapView, BorderLayout.CENTER);
		center.add(cBarPanel, BorderLayout.EAST);
		cBarPanel.setLayout(new BoxLayout(cBarPanel, BoxLayout.PAGE_AXIS));
		cBarPanel.add(cBarHigh);
		cBarPanel.add(cBar);
		cBarPanel.add(cBarLow);
		frame.add(center, BorderLayout.WEST);

		// bottom GUI elements
		frame.add(numOfNum, BorderLayout.SOUTH);

		// //////////////////////////////////////////////////////
		//
		// Action Listeners
		//
		// //////////////////////////////////////////////////////

		// choose folder button action listener
		btChooseFile.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent chooseFile)
			{
				if (chooseFile.getSource() == btChooseFile)
				{
					if (chFiler.showOpenDialog(fileName) == JFileChooser.APPROVE_OPTION)
					{
						fileName.setText(chFiler.getSelectedFile().toString());
						File[] temp = new File(chFiler.getSelectedFile().toString()).listFiles();

						directory.clear();
						for (File i : temp)
							directory.add(i);
						try
						{
							sortTime(directory);
							current = directory.get(0);
							index   = 0;
						}
						catch (IndexOutOfBoundsException iox)
						{
							JOptionPane.showMessageDialog(new JFrame(),
									"The specified directory is either empty, "
											+ "or contains no netCDF files", "Directory Empty",
											JOptionPane.ERROR_MESSAGE);
							btChooseFile.doClick();
						}
						// tell us where we are
						numOfNum.setText(index + " of " + (directory.size() - 1));
						btExecute.doClick();
					}
				}
			}
		});

		// Next button action listener
		btNext.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent next)
			{
				if (directory.size() > ++index)
					current = directory.get(index);
				else
				{
					current = directory.get(0);
					index   = 0;
				}
				// refresh map
				btExecute.doClick();
			}
		});

		// Back button action listener
		btBack.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent back)
			{
				if (--index < 0)
					current = directory.get(index);
				else
				{
					index   = directory.size() - 1;
					current = directory.get(index);
				}
				btExecute.doClick();
			}
		});

		// Execute button action listener
		btExecute.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent execute)
			{
				
				try
				{
					if (current != null)
					{
						// clears the map
						Graphics2D rectangle = map.createGraphics();
						rectangle.setPaint(new Color(0, 0, 0));
						rectangle.fillRect(0, 0, map.getWidth(), map.getHeight());
						rectangle.dispose();

						// grab the file
						NetcdfFile file       = NetcdfFile.open(current.getPath());

						// Variables from information in the CDF file
						Variable   year       = file.findVariable("YEAR");
						Variable   day        = file.findVariable("DOY");
						Variable   time       = file.findVariable("TIME");
						int        yearInt    = year.read().getInt(0);
						int        dayInt     = day.read().getInt(0);
						double     timeDouble = time.read().getDouble(0);

						// file information output
						numOfNum.setText("<html>" + index + " of " + (directory.size() - 1) + "<br>Year: "
								+ yearInt + " Day: " + dayInt + "<br>" + secondsToTime(timeDouble)
								+ "</html>");

						// this changes depending on what the user has selected
						// as the target hemisphere
						Variable      var    = file.findVariable((String) cbVariable.getSelectedItem());
						Array         data3d = var.read();
						ArrayFloat.D3 data   = (ArrayFloat.D3) data3d;

						// i, j, and k variables are used as physics vectors
						// to describe the data location inside the 3D array
						int i = cbLayer.getSelectedIndex();
						if (i < 0)
							i = 0;
						for (int j = 0; j < data.getShape()[1]; j++)
							for (int k = 0; k < data.getShape()[2]; k++)
							{
								// make the map pixel by pixel
								if (data.get(i, j, k) == 0)
									map.setRGB(k, j, 3289650);
								else
									map.setRGB(k, j, getColorFromValue((int) data.get(i, j, k), bucketSize));
							}
					}
					Graphics2D outlines = map.createGraphics();
					outlines.setColor(new Color(150, 150, 150));
					outlines.draw(new Ellipse2D.Double(-1, -1, map.getWidth() + 2, map.getHeight() + 2));
					outlines.drawLine(map.getWidth() / 2 + 1, 0, map.getWidth() / 2 + 1, map.getHeight() / 2 + 1);
					outlines.dispose();

					mapView.repaint();
				}
				catch (IOException iox)
				{
					JOptionPane.showMessageDialog(new JFrame(),
							"There was an error reading the specified directory"
							+ " or the files within it. Ensure that the files are netCDF format,"
							+ " and that the directory is not empty!", "IO Exception",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		// refresh map on layer change
		cbLayer.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent layerChage)
			{
				btExecute.doClick();
			}
		});

		// save button functionality
		btSave.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent saveImage)
			{
				saveImage(map);
			}
		});

		// refresh the map on hemisphere change
		cbVariable.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent poleChange)
			{
				btExecute.doClick();
			}
		});

		// the legal notice uses Hyperlinks in a jEditorPanel to provide
		// direct links to Internet resources through the user's browser
		legal.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent legalInfo)
			{
				JEditorPane aboutPane = new JEditorPane();
				aboutPane.setEditable(false);
				aboutPane.setEditorKit(JEditorPane.createEditorKitForContentType("text/html"));
				aboutPane.setText("<html><div align=center><p>This program utilizes libraries from the UNIDATA NetCDF libraries."
						+ " Those libraries are released under a <a href=http://www.unidata.ucar.edu/software/netcdf/copyright.html>"
						+ " modified MIT-Style License</a>.<br>This program is released under the "
						+ "<a href = http://creativecommons.org/licenses/by-sa/4.0/>Creative Commons Share-Alike Attribution License.</a>"
						+ " You are free to share and edit this program as you wish.<br><br>If this program did not come with the base code,"
						+ " you may request it by contacting <a href = mailto:andreancowley@gmail.com>andreancowley@gmail.</a></p></html>");

				aboutPane.addHyperlinkListener(new HyperlinkListener()
				{
					public void hyperlinkUpdate(HyperlinkEvent hyper)
					{
						if (hyper.getEventType() == HyperlinkEvent.EventType.ACTIVATED)
						{
							if (Desktop.isDesktopSupported())
							{
								try
								{
									Desktop.getDesktop().browse(hyper.getURL().toURI());
								}
								catch (IOException ioX)
								{
									JOptionPane.showMessageDialog(new JFrame(),
											"There was an IOException while processing your request.",
											"IOException", JOptionPane.ERROR_MESSAGE);
								}
								catch (URISyntaxException synX)
								{
									JOptionPane.showMessageDialog(new JFrame(),
											"There was a syntax error while processing your request.",
											"Syntax Error", JOptionPane.ERROR_MESSAGE);
								}
							}
							else
							{
								JOptionPane.showMessageDialog(new JFrame(),
										"Unsupported system. Visit link directly at:" + hyper.getURL(),
										"Unsupported System", JOptionPane.INFORMATION_MESSAGE);
							}
						}
					}
				});
				JOptionPane.showMessageDialog(new JFrame(), aboutPane);
			}
		});

		// this uses the same methods as the legal info
		// to provide links to the SSUSI data downloads
		data.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent dataInfo)
			{
				JEditorPane aboutPane = new JEditorPane();
				aboutPane.setEditable(false);
				aboutPane.setEditorKit(JEditorPane.createEditorKitForContentType("text/html"));
				aboutPane.setText("<html><div align=center><p>Data from the SSUSI instruments on DMSP satellites f16 - "
						+ "f18 is available from the <a href=http://ssusi.jhuapl.edu/data_retriver>SSUSI project website</a>."
						+ "<br>For use with this program, please select the data type \"EDR-AUR\" for download.</p></html>");

				aboutPane.addHyperlinkListener(new HyperlinkListener()
				{
					public void hyperlinkUpdate(HyperlinkEvent hyper)
					{
						if (hyper.getEventType() == HyperlinkEvent.EventType.ACTIVATED)
						{
							if (Desktop.isDesktopSupported())
							{
								try
								{
									Desktop.getDesktop().browse(hyper.getURL().toURI());
								}
								catch (IOException ioX)
								{
									JOptionPane.showMessageDialog(new JFrame(),
											"There was an IOException while processing your request.",
											"IOException", JOptionPane.ERROR_MESSAGE);
								}
								catch (URISyntaxException synX)
								{
									JOptionPane.showMessageDialog(new JFrame(),
											"There was a syntax error while processing your request.",
											"Syntax Error", JOptionPane.ERROR_MESSAGE);
								}
							}
							else
							{
								JOptionPane.showMessageDialog(new JFrame(),
										"Unsupported system. Visit link directly at:" + hyper.getURL(),
										"Unsupported System", JOptionPane.INFORMATION_MESSAGE);
							}
						}
					}
				});
				JOptionPane.showMessageDialog(new JFrame(), aboutPane);
			}
		});

		resizable.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent canResize)
			{
				frame.setResizable(resizable.getState());
			}
		});

		bucketSet.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent setBucket)
			{

				String bucketString =(String) JOptionPane.showInputDialog(frame,
								"<html><div align = center><p>"
								+ "Enter a new value for the bucket size.<br><hr>"
								+ "The bucket size determines how many values are represented by a single color value,<br>"
								+ "the default is 5, which represents 5 values over 1789 distinct colors<br>"
								+ "for a maximum discernable value of 8945 values.",
								"Bucket Size", JOptionPane.PLAIN_MESSAGE, null, null, bucketSize);

				int bucketInt = bucketSize;
				if (bucketString != null)
					try
				{
						bucketInt = Integer.parseInt(bucketString);
						if (bucketInt <= 0)
							throw new NumberFormatException();
				}
				catch (NumberFormatException nfx)
				{
					JOptionPane.showMessageDialog(frame,
							"<html><p><div align = center>Invalid entry, please provide an integer greater than 0!"
									+"<br>The bucket size remains unchanged at " + bucketSize +".", "Error",
									JOptionPane.WARNING_MESSAGE);
				}

				bucketSize = Math.abs(bucketInt);
				cBarHigh.setText((bucketSize * 1789) + " R");
			}
		});

		btMltExecute.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent magMap)
			{
				try
				{
					if (current != null)
					{
						NetcdfFile    file   = NetcdfFile.open(current.getPath());
						Variable      var    = file.findVariable("MLT_GRID_MAP");
						Array         data2d = var.read();
						ArrayFloat.D2 data   = (ArrayFloat.D2) data2d;
						
						for (int j = 0; j < data.getShape()[0]; j++)
							for (int k = 0; k < data.getShape()[1]; k++)
							{
								int colorOut = 65280; // green

								if (data.get(j, k) >= 3)
								{
									colorOut = 16776960; // yellow
									if (data.get(j, k) >= 6)
									{
										colorOut = 16711680; // red

										if (data.get(j, k) >= 9)
										{
											colorOut = 16711935; // magenta

											if (data.get(j, k) >= 12)
											{
												colorOut = 255; // blue

												if (data.get(j, k) >= 15)
												{
													colorOut = 65536; // aqua

													if (data.get(j, k) >= 18)
													{
														colorOut = 16777215; // white

														if (data.get(j, k) >= 21)
															colorOut = 6579300; // grey
													}
												}
											}
										}
									}
								}
								map.setRGB(k, j, colorOut);
							}
						mapView.repaint();
					}
				}
				catch (IOException a)
				{
					JOptionPane.showMessageDialog(new JFrame(),"There was an error reading the specified directory "
					+"or the files within it. Ensure that the files are netCDF format, and that the directory is not empty!",
					"IO Exception", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		frame.setVisible(true);
	}

	// //////////////////////////////////////////////////////
	//
	// Methods
	//
	// //////////////////////////////////////////////////////

	/**
	 * Displays a dialogue to save an image, then saves the image (or doesn't
	 * based on user input).
	 *
	 * @param map
	 *            an image to be saved
	 */
	private void saveImage(BufferedImage map)
	{
		JFileChooser            chSaver = new JFileChooser();
		FileNameExtensionFilter png     = new FileNameExtensionFilter("GIF Image File", "gif");
		chSaver.setApproveButtonText("Save");
		chSaver.setFileFilter(png);
		int                     returns = chSaver.showOpenDialog(null);

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
	private String secondsToTime(double seconds)
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
	private ArrayList<File> sortTime(ArrayList<File> directory)
	{
		int        a    = 0;
		NetcdfFile file = null;
		ArrayList<Integer> nums = new ArrayList<Integer>();
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
				file             = NetcdfFile.open(directory.get(a).getPath());

				Variable day     = file.findVariable("DOY");
				int      dayInt  = day.read().getInt(0);
				Variable time    = file.findVariable("TIME");

				// time from year start in seconds, integer for precision
				int      timeInt = (int) (time.read().getDouble(0) + dayInt * 86400);

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
	private ArrayList<File> sort(ArrayList<File> files, ArrayList<Integer> nums)
	{
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
					nums.set(a, nums.get(a + gap));
					files.set(a + gap, tempFile);
					nums.set(a + gap, tempInt);

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
	private BufferedImage generateColorBar(int height, int width)
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
	private int getColorFromValue(int value, int bucket)
	{
		int a = 0;
		int b = 0;
		int c = 0;
		if (value > 0)
		{
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
		}
		return 65536 * a + 256 * b + c;
	}

}