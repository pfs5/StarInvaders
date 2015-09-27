import java.util.Comparator;

public abstract class CustomComparator implements Comparator<Highscore> {
    @Override
    public int compare(Highscore o1, Highscore o2) {
    	if (o1.getScore()>o2.getScore())
    		return -1;
    	else if (o1.getScore()<o2.getScore())
    		return 1;
    	return 0;
    }
}