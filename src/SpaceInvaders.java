import java.awt.EventQueue;
import java.io.IOException;

import javax.swing.JFrame;


public class SpaceInvaders extends JFrame{
	
	private final int WIDTH = 500;
	private final int HEIGTH = 800;
	
	public SpaceInvaders() throws IOException {
        initUI();
    }

    private void initUI() throws IOException {

        add(new Board());

        setSize(WIDTH,HEIGTH);
        setResizable(false);
        
        setTitle("Space Invaders v1.0 ALPHA");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }    
    
    public static void main(String[] args) throws IOException {
        
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                SpaceInvaders space;
				try {
					space = new SpaceInvaders();
					space.setVisible(true);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                
            }
        });
    }

}
