import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
//import java.util.Random;

import javax.imageio.ImageIO;

public class TopDrop {
	private double startTime;
	private double time;
	private String runName;
//	private File filePath;
	private double area;
	private BufferedImage img;
	private double height;
	private double width;
	private int unscaledWidth = -1;
	private int pixelsPerCentimeter;
	
	/*
	 * these declarations are setting up the "important" zone on the Buffered Image.  The important zone is everywhere the drop might be.
	 */
	private final static int minImportantX=644;//800
	private final static int maxImportantX=944;//1200
	private final static int minImportantY=600;//550
	private final static int maxImportantY=850;//950
//	private final static int gradientThreshold=180;//180
	private int gradientThreshold;
	
	@Override
	public String toString(){
		return ""+runName+","+height+","+width+","+area+","+startTime+","+time;
	}
	
	
	public TopDrop(File filePath,String runName,double time, double frameNumber, int pixelsPerCentimeter, int gradThresh){
		gradientThreshold = gradThresh;
		startTime = time;
		this.time=time+(double)(frameNumber/60);
		this.runName=runName;//update with angles and width.  
		this.pixelsPerCentimeter=pixelsPerCentimeter;
//		this.filePath=filePath;

		//makes a Buffered Image from the file at the filePath.
		//BufferedImage img=null;
		img=null;
		try {
			img = ImageIO.read(filePath);
		} catch (IOException e1) {
			e1.printStackTrace();
		}


		
		img=ImageUtilities.supressFeatures(img,gradientThreshold,minImportantX,maxImportantX,minImportantY,maxImportantY);
		img=ImageUtilities.markEdges(img,gradientThreshold,minImportantX,maxImportantX,minImportantY,maxImportantY);
		/*
		 * method to fill in the circle...
		 * 
		 * int[] pointInDrop = findPointInDrop();//looks at random points in the important range until it finds a drop where there is red on both sides of it.
		 */
		
		
//		int[] pointInDrop = findPointInDrop();
//		fillInDrop(pointInDrop);
		fillInDrop();
		area = findAreaRaster();
//		  area = findAreaMCsim();
		
		width = findWidth();
		height = findHeight();
//		height = 55;
		
		try {
			ImageIO.write(img, "png", filePath);
			img.flush();
		} catch (IOException e) {
			System.out.println("DIDN'T ACTUALLY WRITE NEW IMAGE");
			e.printStackTrace();
		}
	}
	
	private double findAreaRaster(){
		double hits = 0;
		double totalPixels = (maxImportantX - minImportantX)*(maxImportantY - minImportantY);
		
		for(int x=minImportantX;x<=maxImportantX;x++)
			for(int y=minImportantY;y<=maxImportantY;y++)
				if(ImageUtilities.isRed(img, x, y))
					hits+=1;
		
		//TODO contemplate the meaning of the area of a single pixel...
		System.out.println("Percentage of important area: "+(double)(hits/totalPixels*100)+"%");
		
		double scaledTotalArea = (double)totalPixels/(double)pixelsPerCentimeter/(double)pixelsPerCentimeter;//(number of pixels in X * number of pixels in Y / scale for X / scale for Y)
		
		return scaledTotalArea * hits/totalPixels;//the total area of the important region (in cm^2) * the percentage of pixels that are a part of the drop.
	}

/*	
	private int[] findPointInDrop(){
		int[] point = {0,0};
		
		Random rnd = new Random();
		
		boolean found = false;
		while(!found){
			point[0]=rnd.nextInt(maxImportantX-minImportantX)+minImportantX;
			point[1]=rnd.nextInt(maxImportantY-minImportantY)+minImportantY;
			if(pointIsInDrop(point))
				found=true;
		}
		return point;
	}
	
	private boolean pointIsInDrop(int[] point){
		boolean edgeToLeft=false;
		boolean edgeToRight=false;
		
		for(int x=point[0];x>minImportantX;x--)
			if(ImageUtilities.getRedValue(img, x, point[1])==255){
				edgeToLeft = true;
				break;
			}
		
		for(int x=point[0];x<maxImportantX;x++)
			if(ImageUtilities.getRedValue(img, x, point[1])==255){
				edgeToRight=true;
				break;
			}
		
		if(edgeToLeft && edgeToRight)
			return true;
		else
			return false;
	}
	*/
	private void fillInDrop(){
		//raster over every point.  if it is not red, if it is between two red points, draw a line between those two points.
		Graphics2D g = img.createGraphics();
		g.setColor(Color.RED);
		
		for(int x=minImportantX;x<maxImportantX;x++){
			for(int y=minImportantY;y<maxImportantY;y++){
				if(!ImageUtilities.isRed(img, x, y)){
					int a = hasRedPointAbove(x,y);
					int b = hasRedPointBelow(x,y);
					if(a!=0 && b!=0)
						g.drawLine(x, a, x, b);
					
					int l = hasRedPointLeft(x,y);
					int r = hasRedPointRight(x,y);
					if(l!=0 && r!=0)
						g.drawLine(l, y, r, y);
				}
			}
		}
	}
/*	
	private void fillInDrop(int[] pointInDrop){
		int[] blackDetection = fillInLineOfDrop(pointInDrop);//0 if no black. 1 if black above. -1 if black below.
		
		
		int[] minMax = findMaxValsInDropForY(pointInDrop);
		int middleX = (minMax[0]+minMax[1])/2;
			
		boolean didStuff=false;
			
			
		if((hasRedPointAbove(middleX, pointInDrop[1])!=0) && pointInDrop[1]-1>=minImportantY && middleX > minImportantX && middleX<maxImportantX){
			fillInDrop(new int[]{middleX,pointInDrop[1]-1});
			didStuff = true;
		}
		if((hasRedPointBelow(middleX, pointInDrop[1])!=0) && pointInDrop[1]+1<=maxImportantY && middleX > minImportantX && middleX<maxImportantX ){
			fillInDrop(new int[]{middleX, pointInDrop[1]+1});		
			didStuff = true;
		}
		
		if(blackDetection[0] == -1 && didStuff ==false)
			fillInDrop(new int[]{blackDetection[1], pointInDrop[1]+1});
		
		if(blackDetection[0] == 1 && didStuff == false)
			fillInDrop(new int[]{blackDetection[1], pointInDrop[1]-1});
		
		
	}
	*/
	private double findWidth(){
		return (double)unscaledWidth/(double)pixelsPerCentimeter;
	}
	
	private int hasRedPointAbove(int x, int y){
		
		int yVal=0;
		
		for(int i = y;i>minImportantY;i--){
			if(ImageUtilities.isRed(img, x, i))
				yVal=i;
		}
		return yVal;
	}
	
	private int hasRedPointBelow(int x, int y){
		
		int yVal = 0;
		
		for(int i=y;i<maxImportantY;i++){
			if(ImageUtilities.isRed(img, x, i))
				yVal = i;
		}
		return yVal;
	}
	
private int hasRedPointLeft(int x, int y){
		
		int xVal=0;
		
		for(int i = x;i>minImportantX;i--){
			if(ImageUtilities.isRed(img, i, y))
				xVal=i;
		}
		return xVal;
	}
	
	private int hasRedPointRight(int x, int y){
		
		int xVal = 0;
		
		for(int i=x;i<maxImportantX;i++){
			if(ImageUtilities.isRed(img, i, y))
				xVal = i;
		}
		return xVal;
	}
/*	
	private int[] fillInLineOfDrop(int[] point){
		Graphics2D g = img.createGraphics();
		g.setColor(Color.RED);
		int[] minMax = findMaxValsInDropForY(point);
		int minX = minMax[0];
		int maxX = minMax[1];
		
		int result[] = {0,0};
		for(int x=minMax[0];x<minMax[1];x++){
			if(ImageUtilities.getRedValue(img,x,point[1]+1) < 80){
				result[0] = -1;
				result[1] = x;
			}
			if(ImageUtilities.getRedValue(img, x, point[1]-1) < 80){
				result[0] = 1;
				result[1] = x;
			}
		}
		
/////////////// this is to find the max width. since this method goes over every piece of the drop and looks at its width, we might as well find the largest width.//////
		g.drawLine(minX, point[1], maxX, point[1]);	
		if(unscaledWidth<maxX-minX)
			unscaledWidth = maxX-minX;
		
		return result;
	}
	*/
	private double findHeight(){
		int minY=-1;
		int maxY=-1;
		for(int y=minImportantY;y<maxImportantY;y++)//raster from top down to find highest red pixel.
			for(int x=minImportantX;x<maxImportantX;x++)
				if(ImageUtilities.isRed(img, x, y)){
					minY = y;
					break;
				}	
		for(int y=maxImportantY;y>minImportantY;y--)//raster from bottom up to find the lowest red pixel.
			for(int x=minImportantX;x<maxImportantX;x++)
				if(ImageUtilities.isRed(img, x, y)){
					maxY=y;
					break;
				}
		
		int unscaledHeight = maxY-minY;
		
		//TODO 
		//return unscaledHeight * scalingFactor;
		return (double)unscaledHeight/(double)pixelsPerCentimeter;	
	}
/*	
	private int[] findMaxValsInDropForY(int[] point){
		int minX=1;
		int maxX=1;
		
		
		for(int x= point[0];x>minImportantX;x--)
			if(ImageUtilities.getRedValue(img, x, point[1])==255){
				minX = x;
			}
		
		for(int x=point[0];x<maxImportantX;x++)
			if(ImageUtilities.getRedValue(img, x, point[1])==255){
				maxX=x;
			}
		
		return new int[]{minX,maxX};
	}
	*/
}









