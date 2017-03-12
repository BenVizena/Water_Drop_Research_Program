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
//	private int unscaledWidth = -1;
	private int pixelsPerCentimeter;
	
	/*
	 * these declarations are setting up the "important" zone on the Buffered Image.  The important zone is everywhere the drop might be.
	 */
	private static int minImportantX=644;//800
	private static int maxImportantX=944;//1200
	private static int minImportantY=600;//550
	private static int maxImportantY=850;//950
//	private final static int gradientThreshold=180;//180
	private int gradientThreshold;
	
	@Override
	public String toString(){
		return ""+runName+","+height+","+width+","+area+","+startTime+","+time;
	}
	
	
	public TopDrop(File filePath,String runName,double time, double frameNumber, int pixelsPerCentimeter, int topLeftPlatformX, int topLeftPlatformY, int bottomRightPlatformX,
			int bottomRightPlatformY, int gradThresh){
		gradientThreshold = gradThresh;
		startTime = time;
		this.time=time+(double)(frameNumber/60);
		this.runName=runName;//update with angles and width.  
		this.pixelsPerCentimeter=pixelsPerCentimeter;
		minImportantX=topLeftPlatformX;
		minImportantY=topLeftPlatformY;
		maxImportantX=bottomRightPlatformX;
		maxImportantY=bottomRightPlatformY;
		
//		this.filePath=filePath;

		//makes a Buffered Image from the file at the filePath.
		//BufferedImage img=null;
		img=null;
		try {
			img = ImageIO.read(filePath);
		} catch (IOException e1) {
			e1.printStackTrace();
		}


		
	//	img=ImageUtilities.supressFeatures(img,gradientThreshold,minImportantX,maxImportantX,minImportantY,maxImportantY);
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
		
		img=null;
	}
	
	private double findAreaRaster(){
		double hits = 0;
		double totalPixels = (maxImportantX - minImportantX)*(maxImportantY - minImportantY);
		
		for(int x=minImportantX;x<=maxImportantX;x++)
			for(int y=minImportantY;y<=maxImportantY;y++)
				if(ImageUtilities.isRed(img, x, y))
					hits+=1;
		
		//TODO contemplate the meaning of the area of a single pixel...
//		System.out.println("Percentage of important area: "+(double)(hits/totalPixels*100)+"%");
		
		double scaledTotalArea = (double)totalPixels/(double)pixelsPerCentimeter/(double)pixelsPerCentimeter;//(number of pixels in X * number of pixels in Y / scale for X / scale for Y)
		
		return scaledTotalArea * hits/totalPixels;//the total area of the important region (in cm^2) * the percentage of pixels that are a part of the drop.
	}


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

	private double findWidth(){
		double unscaledWidth = findUnscaledWidth();
		return (double)unscaledWidth/(double)pixelsPerCentimeter;
	}
	
	private int findUnscaledWidth(){
		int unscaledWidth=0;
		for(int y=minImportantY;y<maxImportantY;y++){
			int firstRedPixel=0;
			int lastRedPixel=0;
			boolean foundFirstRedPixel=false;
			for(int x=minImportantX;x<maxImportantX;x++){
				if(foundFirstRedPixel == false && ImageUtilities.getRedValue(img, x, y)==255){
					foundFirstRedPixel=true;
					firstRedPixel=x;
				}else if(ImageUtilities.getRedValue(img,x,y)==255 && foundFirstRedPixel==true)
					lastRedPixel=x;
				
				if(lastRedPixel-firstRedPixel > unscaledWidth)
					unscaledWidth=lastRedPixel-firstRedPixel;
			}
		}
		return unscaledWidth;
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
		int unscaledHeight=0;

		for(int x=minImportantX;x<maxImportantX;x++){//raster from top down to find highest red pixel.
			int firstRedPixel=0;
			int lastRedPixel=0;
			boolean foundFirstRedPixel=false;
			
			for(int y=minImportantY;y<maxImportantY;y++)
				if(!foundFirstRedPixel && ImageUtilities.isRed(img, x, y)){
					foundFirstRedPixel=true;
					firstRedPixel=y;
				}else if(ImageUtilities.isRed(img, x, y))
					lastRedPixel=y;
			
			if(lastRedPixel-firstRedPixel>unscaledHeight)
				unscaledHeight=lastRedPixel-firstRedPixel;
		}
		
		
	//	int unscaledHeight = maxY-minY;
	//	System.out.println(unscaledHeight);
		
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









