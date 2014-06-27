import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class zoomView
{
	JFrame frame;
	
	JPanel panel;
	
	JLabel mapView;
	
	JButton btReturn;
	
	BufferedImage originalMap = new BufferedImage(363, 363, BufferedImage.TYPE_INT_RGB);
	
	BufferedImage liveMap = new BufferedImage(363, 363, BufferedImage.TYPE_INT_RGB);
	
	public zoomView(BufferedImage map)
	{
		Graphics2D gfxOriginal = originalMap.createGraphics();
		gfxOriginal.drawImage(map, 0, 0, null);
		gfxOriginal.dispose();
		
		Graphics2D gfxLiveMap = liveMap.createGraphics();
		gfxLiveMap.drawImage(map, 0, 0, null);
		gfxLiveMap.dispose();
		
		frame       = new JFrame();
		panel       = new JPanel();
		
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
				Graphics2D gfxLive = liveMap.createGraphics();
				gfxLive.drawImage(originalMap, 0, 0, originalMap.getWidth(), originalMap.getHeight(), null);
				gfxLive.dispose();
				mapView.setIcon(new ImageIcon(liveMap));
			}
		});
		
		mapView.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				BufferedImage tempMap = new BufferedImage((int)(liveMap.getWidth()*1.1), (int)(liveMap.getHeight()*1.1), BufferedImage.TYPE_INT_RGB);
				Graphics2D gfxTempImage = tempMap.createGraphics();
				gfxTempImage.drawImage(liveMap, 0, 0, (int)(liveMap.getWidth()*1.1), (int)(liveMap.getHeight()*1.1), null);
				gfxTempImage.dispose();
				int xValueTL = (int) (mapView.getMousePosition().getX()-(originalMap.getWidth()/2+1));
				int yValueTL = (int) (mapView.getMousePosition().getY()-(originalMap.getHeight()/2+1));
				
				
				//fix the top left corner
				if(xValueTL < 0)
					xValueTL = 0;
				if(yValueTL < 0)
					yValueTL = 0;
				//if the top left corner is inside the image
				if(xValueTL >= 0 && yValueTL >= 0)
				{
					//and if the bottom right corner is outside the image
					if(xValueTL + originalMap.getWidth() > tempMap.getWidth())
						xValueTL = tempMap.getWidth() - originalMap.getWidth();
					if(yValueTL + originalMap.getHeight() > tempMap.getHeight())
						yValueTL = tempMap.getHeight() - originalMap.getHeight();
				}
				Graphics2D gfxLive = liveMap.createGraphics();
				gfxLive.drawImage(tempMap.getSubimage(xValueTL, yValueTL, liveMap.getWidth(), liveMap.getHeight()), 0, 0, liveMap.getWidth(), liveMap.getHeight(), null);
				gfxLive.dispose();
				//liveMap = tempMap.getSubimage(xValueTL, yValueTL, liveMap.getWidth(), liveMap.getHeight());
				mapView.setIcon(new ImageIcon(liveMap));
			}
		});
	}
}
