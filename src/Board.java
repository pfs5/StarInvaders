import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;

/*
 * 
 * Star Invaders v1.0 BETA
 * 
 * >>>>testing in progress
 * 
 * author:   	Patrik
 * date: 		9/2015.
 * 
 *  
 * 
 * TODO name input
 * 
 * 
 * 
 */



public class Board extends JPanel implements ActionListener {

	//State
	private enum STATE {
		MENU,
		GAME,
		PAUSE,
		INPUT_NAME,
		GAME_OVER
	}

	private STATE state;

	//Instances
	private Ship ship;
	private Timer timer;
	private ArrayList<Laser> lasers;
	private ArrayList<Laser> fighterLasers;
	private ArrayList<Fighter> fighters;
	private Random RANDOM_NUMBER;
	
	private ArrayList<Highscore> highscores;

	//initial positions
	private final int craftInitX = 100;
	private final int craftInitY = 700;

	private final int fighterInitY = 0;

	//Game settings
	private String PLAYER_NAME;
	
	private final int DELAY = 20;
	private int GLOBAL_TIMER = 0;
	private int FIGHTER_FREQUENCY;
	private int POINTS;
	private int SHIP_SPEED;
	private int FIGHTER_SPEED;
	private int LASER_SPEED;
	private int SPEED_INCREASE;
	private int LASER_FREQUENCY;
	private int LASER_COEFFICIENT;

	//Menu settings
	private int BASE = 230;
	private int OFFSET = 80;
	private int LIGHTSABER_POSITION;
	private int LIGHTSABER_INDEX;
	private int LIGHTSABER_COLOR;

	//initialize board
	public Board() throws IOException {

		initBoard();
	}

	private void initBoard() throws IOException {

		//Set state
		state = STATE.MENU;

		//Get highscores
		highscores = getHighscores();

		addKeyListener(new TAdapter());
		setFocusable(true);
		setBackground(Color.BLACK);
		setDoubleBuffered(true);

		//Default settings
		RANDOM_NUMBER = new Random();
		PLAYER_NAME = "";
		POINTS = 0;
		SHIP_SPEED = 5;
		FIGHTER_SPEED = 2;
		LASER_SPEED = 9;
		FIGHTER_FREQUENCY = 4;
		SPEED_INCREASE = 1;
		LASER_COEFFICIENT = 2;
		LASER_FREQUENCY = FIGHTER_FREQUENCY*10*LASER_COEFFICIENT;

		LIGHTSABER_POSITION = BASE;
		LIGHTSABER_INDEX=0;
		LIGHTSABER_COLOR=0;

		ship = new Ship(craftInitX,craftInitY, SHIP_SPEED, LASER_SPEED);
		lasers = new ArrayList<Laser>();
		fighterLasers = new ArrayList<Laser>();
		fighters = new ArrayList<Fighter>();

		timer = new Timer(DELAY, this);
		timer.start();
	}
	
	public ArrayList<Highscore> getHighscores() throws IOException {
		ArrayList<Highscore> highscores = new ArrayList<Highscore>();
		
		try {
			FileReader readFile = new FileReader("scores.dat");
			BufferedReader reader = new BufferedReader(readFile);
			String line = new String();
			while ((line = reader.readLine()) != null) {
				String [] split = line.split(":");
				Highscore highscore = new Highscore(split[0],Integer.parseInt(split[1]));
				highscores.add(highscore);
			}
		}
		catch (Exception e) {
			return highscores;
		}
		
		return highscores;
	}


	//*** Drawing part ***//
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		if (state == STATE.MENU)
			menuDrawing(g);
		if (state == STATE.GAME)
			gameDrawing(g);
		if (state == STATE.PAUSE)
			pauseDrawing(g);
		if (state == STATE.INPUT_NAME)
			inputnameDrawing(g);
		if (state == STATE.GAME_OVER)
			gameoverDrawing(g);

		Toolkit.getDefaultToolkit().sync();
	}

	private void gameDrawing(Graphics g) {

		Graphics2D g2d = (Graphics2D) g;

		//Write time and points
		writeText(g2d);
		
		//Draw ship
		g2d.drawImage(ship.getImage(),ship.getX(),ship.getY(),this);

		//Draw lasers
		lasers = ship.getLasers();

		for (Laser current : lasers) 
			g2d.drawImage(current.getImage(),current.getX(),current.getY(),this);
		
		//Draw fighter lasers
		for (Laser current : fighterLasers)
			g2d.drawImage(current.getImage(),current.getX(),current.getY(),this);

		//Save deleted lasers
		ship.setLasers(lasers);

		//Draw fighters
		for (int i=0 ; i< fighters.size(); i++) {
			Fighter fighter = fighters.get(i);
			if (fighter.isDestroyed() == false) {
				g2d.drawImage(fighter.getImage(),fighter.getX(),fighter.getY(),this);
			}
			else {
				g2d.drawImage(fighter.getImageDestroyed(),fighter.getX(),fighter.getY(),this);
				fighters.remove(i);
			}
		}
	}

	private void menuDrawing(Graphics g) {

		Graphics2D g2d = (Graphics2D) g; 

		//Title image
		ImageIcon ii0 = new ImageIcon (getClass().getResource("/resources/title.png"));
		Image title = ii0.getImage();
		g2d.drawImage(title,50,20,this);
		
		g2d.setColor(Color.white);

		Font font = new Font("Serif", Font.BOLD, 30);
		g2d.setFont(font);

		g2d.drawString("NEW GAME", 150,BASE );

		font = new Font("Arial", Font.PLAIN, 17);
		g2d.setFont(font);

		g2d.drawString("SHIP SPEED: " + SHIP_SPEED, 150, BASE+OFFSET*1);
		g2d.drawString("LASER SPEED: " + LASER_SPEED, 150, BASE+OFFSET*2);
		g2d.drawString("LASER PERIOD: " + LASER_COEFFICIENT, 150, BASE+OFFSET*3);
		g2d.drawString("INVADER SPEED: " + FIGHTER_SPEED, 150, BASE+OFFSET*4);
		g2d.drawString("INVADER PERIOD: " + FIGHTER_FREQUENCY, 150, BASE+OFFSET*5);
		g2d.drawString("SPEED INCR: " + SPEED_INCREASE, 150, BASE+OFFSET*6);
		

		//Draw lightsaber
		String path = new String();
		switch (LIGHTSABER_COLOR) {
			case 0: path = "/resources/lightsaber1.png";
				break;
			case 1: path = "/resources/lightsaber2.png";
				break;
			case 2: path = "/resources/lightsaber3.png";
				break;
		}
		
		ImageIcon ii = new ImageIcon(getClass().getResource(path));
		Image lightsaber = ii.getImage();
		g2d.drawImage(lightsaber,50,LIGHTSABER_POSITION-15,this);

	}

	private void pauseDrawing (Graphics g) {
		
		Graphics2D g2d = (Graphics2D) g;
		
		writeText(g2d);
		
		g2d.setColor(Color.white);
		Font font = new Font("Serif", Font.BOLD, 30);
		g2d.setFont(font);
		g2d.drawString("PAUSE", 180, 400);
	}
	
	private void inputnameDrawing(Graphics g) {
		
		Graphics2D g2d = (Graphics2D) g;
		
		writeText(g2d);
		
		g2d.setColor(Color.white);
		Font font = new Font("Serif", Font.BOLD, 20);
		g2d.setFont(font);
		g2d.drawString("NAME: " + PLAYER_NAME + "_", 50, 400);
		
		
	}
	
	private void gameoverDrawing (Graphics g) {
		
		Graphics2D g2d = (Graphics2D) g;
		
		writeText(g2d);
		
		g2d.setColor(Color.white);
		Font font = new Font("Serif", Font.BOLD, 30);
		g2d.setFont(font);
		g2d.drawString("GAME OVER", 140, 70);
		g2d.drawString("SCORE: "+POINTS,160,120);
		
		//Draw scoreboard
		font = new Font("Serif", Font.BOLD, 20);
		g2d.setFont(font);
		for (int i=0;i<highscores.size()%11;i++) {
			Highscore current = highscores.get(i);
			g2d.drawString (current.getName(),160,200+i*50);
			g2d.drawString (""+current.getScore(),300,200+i*50);
		}
		
	}
	
	public void writeText(Graphics2D g2d) {
		Font font = new Font("Helvetica", Font.PLAIN, 14);

		g2d.setFont(font);
		g2d.setColor(Color.white);
		g2d.drawString("TIME: " + Integer.toString(GLOBAL_TIMER / (1000/DELAY)), 20, 20);
		g2d.drawString("SCORE: " + Integer.toString(POINTS),300,20);
		g2d.drawString("SPEED: " + Integer.toString(FIGHTER_SPEED),400,20);
		
		font = new Font("Helvetica", Font.PLAIN, 8);
		g2d.setFont(font);
		g2d.drawString ("copyright 2015. by: ja",390,793);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
		if (state == STATE.GAME)
			GLOBAL_TIMER++;

		if (state == STATE.GAME)
		{
			//Increase speed
			if (SPEED_INCREASE == 1 && (GLOBAL_TIMER % 1500 == 0)) 
				FIGHTER_SPEED++;
				
			
			//Add fighters
			if (GLOBAL_TIMER % (FIGHTER_FREQUENCY*10) == 0) {
				int fighterRandomX = Math.abs(RANDOM_NUMBER.nextInt()) % (500-60);
				addFighter(fighterRandomX,fighterInitY-40, FIGHTER_SPEED);
			}
			
			//Add fighter lasers
			if (GLOBAL_TIMER % LASER_FREQUENCY == 0) {
				int fighterNumber = fighters.size();
				int fighterIndex = Math.abs(RANDOM_NUMBER.nextInt() % fighterNumber);
				Fighter current = fighters.get(fighterIndex);
				int laserX = current.getX()+30;
				int laserY = current.getY()+60;
				int laserSpeed = FIGHTER_SPEED+2;
				
				Laser newLaser = new Laser(laserX, laserY, laserSpeed, "RED");
				fighterLasers.add(newLaser);

			}

			ship.move();
			moveFighters();
			try {
				updateFighters();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			ship.moveLasers();
			updateLasers();
			
			try {
				updateShip();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}	
			
			moveFighterLasers();
		}
		if (state == STATE.MENU) {
			//TODO MENU 

		}

		repaint();
	}

	public void updateFighters () throws IOException {

		lasers = ship.getLasers();

		for (int i=0 ; i <fighters.size();i++) {

			Fighter fighter = fighters.get(i);
			int fX = fighter.getX();
			int fY = fighter.getY();
			
			if (fY+60 > 800) {
				state = STATE.INPUT_NAME;
				break;
			}

			for (int j=0 ; j<lasers.size(); j++) {

				Laser laser = lasers.get(j);

				int lX = laser.getX();
				int lY = laser.getY();

				if ( (lY < fY+60)&&(lY>fY) && (lX > fX-5+1)&&(lX<fX+60-1) ) {
					//Hit
//					if (fighter.isDestroyed() == false) 
						fighter.setDestroyed();

					lasers.remove(j);
					ship.removeLaser();
					POINTS++;
					break;
				}

			}

		}

		//Update lasers
		ship.setLasers(lasers);
	}

	public void addHighscore() throws IOException {
		Highscore newHighscore = new Highscore(PLAYER_NAME, POINTS);
		highscores.add(newHighscore);
		Collections.sort(highscores,new CustomComparator() {});
		//Write into file
		PrintWriter writer = new PrintWriter("scores.dat", "UTF-8");
		for (Highscore current : highscores)
			writer.println(current.getName()+":"+current.getScore());
		writer.close();
		state = STATE.GAME_OVER;
	}
	
	public void updateShip() throws IOException {
		//Check if ship is hit
		int shipX = ship.getX();
		int shipY = ship.getY();
		
		for (Laser current : fighterLasers) {
			int laserX = current.getX();
			int laserY = current.getY();
			
			if ( (laserX-5>shipX+10)&&(laserX<shipX+60-8) && (laserY+20>shipY+5)&&(laserY<shipY+60-1) ) {
				//hit
				state = STATE.INPUT_NAME;
			}
		}
	}
	
	public void updateLasers() {

		lasers = ship.getLasers();

		for (int i=0; i<lasers.size();i++) {
			Laser current = lasers.get(i);

			if (current.isVisible() == false) {
				lasers.remove(i);
				ship.removeLaser();
			}
		}

		ship.setLasers(lasers);	
	}

	public void moveFighters() {
		for (Fighter fighter : fighters) {
			fighter.move();
		}
	}
	
	public void moveFighterLasers () {
		for (Laser current : fighterLasers) 
			current.move();
	}

	public void addFighter(int x, int y, int SPEED) {
		Fighter fighter = new Fighter(x, y, SPEED);
		fighters.add(fighter);
	}

	private class TAdapter extends KeyAdapter {

		@Override
		public void keyReleased(KeyEvent e) {
			if (state == STATE.GAME)
				ship.keyReleased(e);
			if (state == STATE.MENU)
				menuKeyReleased(e);
		}

		@Override
		public void keyPressed(KeyEvent e) {
			int key = e.getKeyCode();
			if (key == KeyEvent.VK_ESCAPE && state == STATE.GAME)
				state = STATE.PAUSE;
			else if (key == KeyEvent.VK_ESCAPE && state == STATE.PAUSE)
				state = STATE.GAME;
			if (state == STATE.GAME)
				ship.keyPressed(e);
			if (state == STATE.MENU)
				menuKeyPressed(e);
			if (state == STATE.INPUT_NAME)
				try {
					inputnameKeyPressed(e);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			if (state == STATE.GAME_OVER) 
				gameoverKeyPressed(e);
		}

	}

	public void menuKeyReleased(KeyEvent e) {

	}

	public void menuKeyPressed (KeyEvent e) {

		int key = e.getKeyCode();
		switch (key) {
			case KeyEvent.VK_ENTER: if (LIGHTSABER_INDEX==0) initGame();
				break;
			case KeyEvent.VK_C: LIGHTSABER_COLOR = (LIGHTSABER_COLOR+1) % 3;
				break;
			//Arrow keys
			case KeyEvent.VK_DOWN: lightsaberDown();
				break;
			case KeyEvent.VK_UP: lightsaberUp();
				break;
			case KeyEvent.VK_RIGHT: increaseOption();
				break;
			case KeyEvent.VK_LEFT: decreaseOption();
				break;
			//WASD
			case KeyEvent.VK_S: lightsaberDown();
				break;
			case KeyEvent.VK_W: lightsaberUp();
				break;
			case KeyEvent.VK_D: increaseOption();
				break;
			case KeyEvent.VK_A: decreaseOption();
				break;
		}
	}
	
	public void inputnameKeyPressed (KeyEvent e) throws IOException {
		int key = e.getKeyCode();
		switch (key) {
		case KeyEvent.VK_A: PLAYER_NAME = PLAYER_NAME.concat("A");
			break;
		case KeyEvent.VK_B: PLAYER_NAME = PLAYER_NAME.concat("B");
		break;
		case KeyEvent.VK_C: PLAYER_NAME = PLAYER_NAME.concat("C");
		break;
		case KeyEvent.VK_D: PLAYER_NAME = PLAYER_NAME.concat("D");
		break;
		case KeyEvent.VK_E: PLAYER_NAME = PLAYER_NAME.concat("E");
		break;
		case KeyEvent.VK_F: PLAYER_NAME = PLAYER_NAME.concat("F");
		break;
		case KeyEvent.VK_G: PLAYER_NAME = PLAYER_NAME.concat("G");
		break;
		case KeyEvent.VK_H: PLAYER_NAME = PLAYER_NAME.concat("H");
		break;
		case KeyEvent.VK_I: PLAYER_NAME = PLAYER_NAME.concat("I");
		break;
		case KeyEvent.VK_J: PLAYER_NAME = PLAYER_NAME.concat("J");
		break;
		case KeyEvent.VK_K: PLAYER_NAME = PLAYER_NAME.concat("K");
		break;
		case KeyEvent.VK_L: PLAYER_NAME = PLAYER_NAME.concat("L");
		break;
		case KeyEvent.VK_M: PLAYER_NAME = PLAYER_NAME.concat("M");
		break;
		case KeyEvent.VK_N: PLAYER_NAME = PLAYER_NAME.concat("N");
		break;
		case KeyEvent.VK_O: PLAYER_NAME = PLAYER_NAME.concat("O");
		break;
		case KeyEvent.VK_P: PLAYER_NAME = PLAYER_NAME.concat("P");
		break;
		case KeyEvent.VK_Q: PLAYER_NAME = PLAYER_NAME.concat("Q");
		break;
		case KeyEvent.VK_R: PLAYER_NAME = PLAYER_NAME.concat("R");
		break;
		case KeyEvent.VK_S: PLAYER_NAME = PLAYER_NAME.concat("S");
		break;
		case KeyEvent.VK_T: PLAYER_NAME = PLAYER_NAME.concat("T");
		break;
		case KeyEvent.VK_U: PLAYER_NAME = PLAYER_NAME.concat("U");
		break;
		case KeyEvent.VK_V: PLAYER_NAME = PLAYER_NAME.concat("V");
		break;
		case KeyEvent.VK_W: PLAYER_NAME = PLAYER_NAME.concat("W");
		break;
		case KeyEvent.VK_X: PLAYER_NAME = PLAYER_NAME.concat("X");
		break;
		case KeyEvent.VK_Y: PLAYER_NAME = PLAYER_NAME.concat("Y");
		break;
		case KeyEvent.VK_Z: PLAYER_NAME = PLAYER_NAME.concat("Z");
		break;
		case KeyEvent.VK_BACK_SPACE: if (PLAYER_NAME.length()>0)
			PLAYER_NAME = PLAYER_NAME.substring(0,PLAYER_NAME.length()-1);
		break;
		case KeyEvent.VK_ENTER: addHighscore();
		break;
		}
	}
	
	public void gameoverKeyPressed (KeyEvent e) {
		int key = e.getKeyCode();
		if (key == KeyEvent.VK_SPACE) 
			initMenu();
	}
	
	private void initMenu() {

		//Set state
		state = STATE.MENU;

		//Default settings
		PLAYER_NAME = "";
		
		GLOBAL_TIMER = 0;
		RANDOM_NUMBER = new Random();
		POINTS = 0;
		SHIP_SPEED = 5;
		FIGHTER_SPEED = 2;
		LASER_SPEED = 9;
		FIGHTER_FREQUENCY = 4;
		SPEED_INCREASE = 1;
		LASER_COEFFICIENT = 2;
		LASER_FREQUENCY = FIGHTER_FREQUENCY*10*LASER_COEFFICIENT;
		

		LIGHTSABER_POSITION = BASE;
		LIGHTSABER_INDEX=0;
		LIGHTSABER_COLOR=0;

		ship = new Ship(craftInitX,craftInitY, SHIP_SPEED, LASER_SPEED);
		lasers = new ArrayList<Laser>();
		fighterLasers = new ArrayList<Laser>();
		fighters = new ArrayList<Fighter>();

	}
	
	public void initGame () {
		ship = new Ship(craftInitX, craftInitY, SHIP_SPEED, LASER_SPEED);
		LASER_FREQUENCY = FIGHTER_FREQUENCY*10*LASER_COEFFICIENT;
		state = STATE.GAME;
	}
	
	
	public void lightsaberDown() {
		if (LIGHTSABER_POSITION+OFFSET < BASE+OFFSET*6+1) {
		LIGHTSABER_POSITION+=OFFSET;
		LIGHTSABER_INDEX++;
		}
	}
	
	public void lightsaberUp() {
		if (LIGHTSABER_POSITION-OFFSET > BASE-1) {
			LIGHTSABER_POSITION-=OFFSET;
			LIGHTSABER_INDEX--;
		}
	}
	
	public void increaseOption() {
		switch (LIGHTSABER_INDEX) {
			case 1: SHIP_SPEED++;
				break;
			case 2: LASER_SPEED++;
				break;
			case 3: LASER_COEFFICIENT++;
				break;
			case 4: FIGHTER_SPEED++;
				break;
			case 5: FIGHTER_FREQUENCY++;
				break;
			case 6 : SPEED_INCREASE= (SPEED_INCREASE+1)%2;
				break;
				
		}
	}
	
	public void decreaseOption() {
		switch (LIGHTSABER_INDEX) {
			case 1: SHIP_SPEED--;
				break;
			case 2: LASER_SPEED--;
				break;
			case 3: LASER_COEFFICIENT--;
				break;
			case 4: FIGHTER_SPEED--;
				break;
			case 5: FIGHTER_FREQUENCY--;
				break;
			case 6 : SPEED_INCREASE= (SPEED_INCREASE+1)%2;
				break;
		}
	}


}
