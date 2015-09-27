import java.awt.Image;
import java.awt.event.KeyEvent;
import java.lang.reflect.Array;
import java.util.ArrayList;

import javax.swing.ImageIcon;


public class Ship {

	//Edges
	private int maxX = 500;
	private int maxY = 800;
	
	
	//Location
	private int x;
	private int y;

	//Moving
	private int dL, dR;
	private int dU, dD;
	
	private int SPEED;
	private int LASER_SPEED;
	
	private int MAX_LASERS = 10;
	private int CURRENT_LASERS;
	
	
	private Image image;
	private ArrayList <Laser> lasers; 
	
	
	public Ship (int x, int y, int SPEED, int LASER_SPEED) {
		this.x = x;
		this.y = y;
		this.SPEED = SPEED;
		this.LASER_SPEED = LASER_SPEED;
		
		ImageIcon ii = new ImageIcon(getClass().getResource("/resources/millenium_falcon.png"));
		image = ii.getImage();
		
		lasers = new ArrayList <Laser>();
		CURRENT_LASERS = 0;
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
	
	public ArrayList<Laser> getLasers () {
		return lasers;
	}
	
	public void setLasers (ArrayList<Laser> lasers) {
		this.lasers = lasers;
	}
	
	
	public void move () {
		if (x+dR<480)
			x+=dR;
		if (x-dL>-30)
			x+=dL;
		
		y+=dU;
		y+=dD;
		
	}
	
	public void moveLasers () {
		for (Laser current : lasers) 
			current.move();
	}
	
	public void removeLaser () {
		CURRENT_LASERS--;
	}
	
	public void fire() {
		if (CURRENT_LASERS<MAX_LASERS) {
		Laser newLaser = new Laser(x+image.getWidth(null)/2-5,y-1, LASER_SPEED, "GREEN");
		lasers.add(newLaser);
		
		CURRENT_LASERS++;
		}
	}
	
	public void keyPressed (KeyEvent e) {
		
		int key = e.getKeyCode();
		switch (key) {
			//Arrow keys
			case KeyEvent.VK_LEFT : dL = -1*SPEED;
				break;
			case KeyEvent.VK_RIGHT : dR = 1*SPEED;
				break;
			case KeyEvent.VK_UP: dU = -1*SPEED;
				break;
			case KeyEvent.VK_DOWN: dD = 1*SPEED;
				break;
			//WASD
			case KeyEvent.VK_A : dL = -1*SPEED;
				break;
			case KeyEvent.VK_D : dR = 1*SPEED;
				break;
			case KeyEvent.VK_W: dU = -1*SPEED;
				break;
			case KeyEvent.VK_S: dD = 1*SPEED;
				break;
			case KeyEvent.VK_SPACE: fire ();
				break;
		}
		
	}
	
	public void keyReleased (KeyEvent e) {
		
		int key = e.getKeyCode();
		switch (key) {
			//Arrow keys
			case KeyEvent.VK_LEFT : dL = 0;
				break;
			case KeyEvent.VK_RIGHT : dR = 0;
				break;
			case KeyEvent.VK_UP: dU = 0;
				break;
			case KeyEvent.VK_DOWN: dD = 0;
				break;
			//WASD
			case KeyEvent.VK_A: dL = 0;
				break;
			case KeyEvent.VK_D: dR = 0;
				break;
			case KeyEvent.VK_W: dU = 0;
				break;
			case KeyEvent.VK_S: dD = 0;
				break;
		}
		
	}
}
