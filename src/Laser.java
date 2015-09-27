import java.awt.Image;

import javax.swing.ImageIcon;




public class Laser {

	//Coordinates
	private int x;
	private int y;
	
	//Visibility
	private boolean visible;
	
	//Movement speed
	private int SPEED;
	private String COLOUR;
	
	Image image;
	
	public Laser (int x, int y, int SPEED, String COLOUR) {
		this.x = x;
		this.y = y;
		this.SPEED = SPEED;
		this.COLOUR = COLOUR;
		
		String path = new String ();
		path = "/resources/laser.png";
		if (COLOUR.equals("GREEN"))
			path = "/resources/laser.png";
		else if (COLOUR.equals("RED"))
			path = "/resources/laser2.png";
		
		ImageIcon ii = new ImageIcon(getClass().getResource(path));
		image = ii.getImage();
		
		visible = true;
		
	}
	
	public int getX () {
		return x;
	}
	
	public int getY () {
		return y;
	}
	
	public Image getImage () {
		return image;
	}
	
	public boolean isVisible () {
		return visible;
	}
	
	public void move () {
		if (COLOUR.equals("GREEN")) {
		y-= SPEED;
		if (y<10)
			visible = false;
		}
		else if (COLOUR.equals("RED")) {
			y+=SPEED;
			if (y>800)
				visible = false;
		}
	}
}
