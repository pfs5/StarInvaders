import java.awt.Image;
import java.util.Random;

import javax.swing.ImageIcon;


public class Fighter {
	
	//Coordinates
	private int x;
	private int y;
	
	private boolean visible;
	private boolean destroyed;
	
	private int SPEED = 5;
	private int IMAGE_INDEX;
	
	Image image;
	Image explosion;
	
	public Fighter (int x, int y, int SPEED) {
		this.x = x;
		this.y = y;
		this.SPEED = SPEED;
		
		visible = true;
		destroyed = false;
		Random random = new Random();
		IMAGE_INDEX = Math.abs(random.nextInt())%2;
		
		ImageIcon ii;
		if (IMAGE_INDEX == 0)
			ii = new ImageIcon(getClass().getResource("/resources/TIE_fighter.png"));
		else
			ii = new ImageIcon(getClass().getResource("/resources/star_destroyer.png"));
		image = ii.getImage();

		ImageIcon ii2 = new ImageIcon(getClass().getResource("/resources/boom.png"));
		explosion = ii2.getImage();
	}
	
	public void move () {
		y+=SPEED;
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
	
	public Image getImageDestroyed () {
		return explosion;
	}
	
	
	public boolean isVisible () {
		return visible;
	}
	
	public boolean isDestroyed() {
		return destroyed;
	}
	
	
	public void setVisible (boolean visible) {
		this.visible = visible;
	}
	
	public void setDestroyed () {
		destroyed = true;
	}
	
	

}
