import java.awt.image.BufferedImage;
import java.util.Random;

public class Scaler {
	
	private final static int minImportantX=450;
	private final static int maxImportantX=1180;//
	private final static int minImportantY=330;
	private final static int maxImportantY=550;//950
	private final static int gradientThreshold=90;//140
	
	public Scaler(){
	}
	
	public static int getScale(BufferedImage img){//returns number of pixels in one centimeter
		img=ImageUtilities.markEdges(img,gradientThreshold,minImportantX,maxImportantX,minImportantY,maxImportantY);
		
		Random rnd = new Random();
		
		boolean scaleFound = false;
		int pixelsPerCentimeter = -1;
		
		while(!scaleFound){
			int x = rnd.nextInt(img.getWidth());
			int y = rnd.nextInt(img.getHeight());
			
			int dist = getDistanceBetweenPylons(img, x, y);
			if(dist>0){
				scaleFound = true;
				pixelsPerCentimeter = dist;
			}
		}
		return pixelsPerCentimeter;
	}
	
	private static int getDistanceBetweenPylons(BufferedImage img, int x, int y){
		int lastLeftPixel = -1;
		int lastRightPixel = -1;
		
		//find lastLeftPixel
		for(int startX=x;startX>0;startX--){
			if(ImageUtilities.getRedValue(img, startX, y)==255)
				lastLeftPixel = startX;
		}
		
		//find lastRightPixel
		for(int startX=x;startX<img.getWidth()-1;startX++){
			if(ImageUtilities.getRedValue(img, startX, y)==255)
				lastRightPixel = startX;
		}
		
		return lastRightPixel - lastLeftPixel;
	}
}
