/*
 * this class enables the creation of Drop objects.  A drop object has a width, time, left angel, right angle, etc.  
 * 
 * the objects DO NOT have the BufferedImage of the drop. In stead, they have the filePath to where they can find their respective images.
 * 
 * Most of the todos are to use a best fit line method to find the lines to try to counteract the negative effects of inherently descritized pixels.
 */

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;


public class Drop{
	private double width;
	private double time;
	private double angleLeft;
	private double angleRight;
	private String runInfo;
	private File filePath;
	private Lines blueLine;
	private Lines leftDropLine;
	private Lines rightDropLine;
	private double startTime;
	private static boolean goodData=false;
	private double xL;
	private double xR;
	private int pixelsPerCentimeter;
	
	

	
	
	private static int minImportantX;
	private static int maxImportantX;
	private static int minImportantY;
	private static int maxImportantY;
	
	/*
	 * drives the drop class.  Accepts the filepath to the drop image, the run info (specified through cmd line arguments), the start time of the run, and the frame number
	 * (used to calculate the time that the frame happened).
	 */
	public Drop(File filePath,String runInfo,double time, double frameNumber, int pixelsPerCentimeter, int leftPlatformY, int rightPlatformY, int gradientThreshold,
			int leftPlatformX, int rightPlatformX, boolean topSide){
		startTime = time;
		this.time=time+(double)(frameNumber/60);
		this.runInfo=runInfo;//update with angles and width.  
		this.filePath=filePath;
		this.pixelsPerCentimeter=pixelsPerCentimeter;


		//makes a Buffered Image from the file at the filePath.
		BufferedImage img=null;
		try {
			img = ImageIO.read(filePath);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		minImportantX=leftPlatformX-1;//600
		maxImportantX=rightPlatformX+1;//img.getWidth()-800
		minImportantY=leftPlatformY-60;//250
		maxImportantY=rightPlatformY+30;//img.getHeight()-250
		
		if(!topSide){//if not a drop on the top, then flip the image and adjust the important area to account for the flip.
			AffineTransform at = AffineTransform.getScaleInstance(1, -1);
			at.translate(0, -img.getHeight(null));
			
			AffineTransformOp op = new AffineTransformOp(at, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
			img = op.filter(img, null);
			
			leftPlatformY = img.getHeight()-leftPlatformY;
			rightPlatformY = img.getHeight()-rightPlatformY;
			
			minImportantY = leftPlatformY-30;//correcting the important area
			maxImportantY = rightPlatformY+60;//because we just flipped the image across the horizontal center of the image.
		}

		
		
		
		
		img=SobelOperator.markEdges(img,gradientThreshold,minImportantX,maxImportantX,minImportantY,maxImportantY);

		
		blueLine = findLine(img,leftPlatformX, leftPlatformY, rightPlatformX, rightPlatformY);
		leftDropLine=findLeftLine(img);
		rightDropLine=findRightLine(img);
		xL=leftDropLine.getX1()/pixelsPerCentimeter;
		xR=rightDropLine.getX1()/pixelsPerCentimeter;
		angleLeft=Math.abs(Lines.getAngleDegrees(blueLine,leftDropLine));
		angleRight=Math.abs(Lines.getAngleDegrees(blueLine, rightDropLine));
		width=(xR-xL)/pixelsPerCentimeter;

		
		
		img=imposeRightLine(img,rightDropLine,Color.GREEN);//(draw right line on drop)
		img=imposeLeftLine(img,leftDropLine,Color.GREEN);//(draw left line on drop)
		
		//write now edited image of drop to the designated filePath.
		try {
			ImageIO.write(img, "png", filePath);
			img.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}

		
	}
	
	/*
	 * returns the filePath of the image
	 */
	public File getImageFP(){
		return filePath;
	}
	
	public static Lines findLine(BufferedImage bi, int leftPlatformX, int leftPlatformY, int rightPlatformX, int rightPlatformY){
		Graphics2D g = bi.createGraphics();
		g.setColor(Color.BLUE);
		g.drawLine(leftPlatformX, leftPlatformY, rightPlatformX, rightPlatformY);
		
		int[] p1 = {leftPlatformX, leftPlatformY};
		int[] p2 = {rightPlatformX, rightPlatformY};
		
		return new Lines(p1,p2);
	}
	

	
	private static int[] findRightContactPoint(BufferedImage bi){
		int[] point={-1,-1};
		Graphics2D g = bi.createGraphics();
		g.setColor(new Color(255,165,0));
		for(int x=maxImportantX-10;x>minImportantX+10;x--){//raster over
			for(int y=minImportantY;y<maxImportantY-10;y++){//the image
				if(SobelOperator.getBlueValue(bi, x, y)==255){//to find the blue line
					if(SobelOperator.getRedValue(bi, x, y-2)==255){//then check to see if the pixel below is red    was y+1
						if(SobelOperator.getRedValue(bi,x,y+1)==255){
							boolean isOnDrop = false;
							for(int rasterX = x; rasterX > x-5;rasterX--)
								if(SobelOperator.getRedValue(bi, rasterX, y-5)==255)
									isOnDrop = true;
							if(isOnDrop){
								point[0]=x;//if it is
								point[1]=y-1;//you have found the right edge
								x=-1;
								y=maxImportantY;
							}
						}
					}								
				}			
			}
		}
		return point;
	}

	/*
	 * finds the point where the drop makes it's leftmost contact with the surface.
	 * 
	 * rasters over the image until it finds the leftmost red mark. That is the leftmost contact point.
	 * 
	 * known bug: sometimes the reflection of the drop is farther left than the actual drop.  This results in a bad angle.
	 * possible solutions: 1: don't take the very first left pixel. (probably not a great solution)  (the commented out lines implement this solution)
	 * 					   2: check to make sure that the adjacent red pixels to upwards (-y direction) in stead of downwards.
	 */
	private static int[] findLeftContactPoint(BufferedImage bi){
		int[] point={100,100};//-1,-1
		Graphics2D g = bi.createGraphics();
		g.setColor(new Color(255,165,0));
		for(int x=minImportantX+10;x<maxImportantX-10;x++){//raster over
			for(int y=minImportantY+16;y<maxImportantY-16;y++){//the image
				if(SobelOperator.getBlueValue(bi, x, y)==255){//to find the blue line
					if(SobelOperator.getRedValue(bi, x, y+1)==255){//then check to see if the pixel below is red    was y+1
						if(SobelOperator.getRedValue(bi, x, y-2)==255){
							//if the point 15 pixels higher (y-15) is part of the drop (within a certain x range away), take this point.
							boolean isOnDrop = false;
							for(int rasterX = x; rasterX < x+5; rasterX++)
								if(SobelOperator.getRedValue(bi, rasterX, y-5)==255)
									isOnDrop = true;
							if(isOnDrop){
								point[0]=x;//if it is
								point[1]=y-1;//you have found the right edge
								x=maxImportantX;
								y=maxImportantY;
							}
						}
					}								
				}			
			}
		}
		return point;
	}



	/*
	 * makes the line that follows the edge of the right side of the drop.
	 * 
	 * does this by finding the right contact point and then finding the point on the drop 10 pixels up and making a Lines object with those two points.
	 * 
	 * TODO: make this use multiple points to construct a line of best fit. (need to update Lines to allow this)
	 */
	

	private Lines findRightLine(BufferedImage bi){
		int[] p1 = findRightContactPoint(bi);//finds point 1
		
//		Lines lineGroup[] = new Lines[10];
		ArrayList<Lines> lineGroup = new ArrayList<>();
		
		
		for(int i=5;i<=14;i++){
			int thisPoint[] = {scanFromRight(bi,p1[1]-i,p1[0]),p1[1]-i};
			lineGroup.add(new Lines(p1,thisPoint));
		}
		
		double avgM = Lines.getAverageSlope(lineGroup);
		
		
		return new Lines(p1,avgM);
	}
	
	/*
	 * scans from right to left at a given y looking for the first red pixel.
	 * 
	 * if it finds a red pixel, we call the data good.
	 * if it does not find a pixel (maybe the drop was too blurry because it was moving) goodData remains false.
	 */
	private static int scanFromRight(BufferedImage bi, int y, int startX){
		
		int xPoint=-1;
		Graphics2D g = bi.createGraphics();
		g.setColor(new Color(157,235,233));
		for(int x=startX;x>minImportantX;x--){//rasters from right to left
			try{
				if(SobelOperator.getRedValue(bi, x, y)==255){//looking for a red pixel
				xPoint=x;
				g.drawLine(x, y, x, y);
				x-=maxImportantX;
				goodData=true;
				break;
				}
			}catch(Exception e){
				
			}	
		}	
		return xPoint;
	}
	
	private static int scanFromLeft(BufferedImage bi, int y2, int x1){//y2 is the height that you will scan in at.  x1 is a term that speeds things up.  you basically set the x point at which you start scanning
		int x2=x1-20;
		for(int x=x2;x<maxImportantX;x++){
			if(SobelOperator.getRedValue(bi, x, y2)==255){
				x2=x;
				x+=maxImportantX;
				break;
			}
		}
		return x2;
	}
	
	
	/*
	 * constructs the line following the left edge of the drop by finding the left contact point and the point 10 pixels above that to construct a Lines object.
	 * 
	 * TODO: make this use multiple points to construct a line of best fit. (need to update Lines to allow this)
	 */
	private Lines findLeftLine(BufferedImage bi){
		int points[]=findLeftContactPoint(bi);
		
		ArrayList<Lines> lineGroup = new ArrayList<>();
		
		for(int i=5;i<=14;i++){
			int thisPoint[] = {scanFromLeft(bi,points[1]-i,points[0]),points[1]-i};
			lineGroup.add(new Lines(points,thisPoint));
		//	System.out.println(lineGroup[i-5].getM());
		}
			
		double avgM = Lines.getAverageSlope(lineGroup);
		
		return new Lines(points,avgM);
	}
	
	/*
	 * draws both left and right drop lines
	 * 
	 * TODO: change the name to imposeDropLine or something.
	 */
	private BufferedImage imposeLeftLine(BufferedImage bi, Lines line, Color c){
		Graphics2D g = bi.createGraphics();
		int[] point = findLeftContactPoint(bi);
			
		int x= point[0]+100;
			
		int y=(int)Math.round(line.getM()*(x-line.getX1())+line.getY1());//x-line.getX1()
		g.setColor(c);
		g.drawLine(line.getX1(),line.getY1(),x,y);
		g.dispose();
					
		return bi;
	}
	
	private BufferedImage imposeRightLine(BufferedImage bi, Lines line, Color c){
		Graphics2D g = bi.createGraphics();
		int[] point = findRightContactPoint(bi);
		
		int x = point[0]-100;
		
		
		int y=(int)Math.round(line.getM()*(x-line.getX1())+line.getY1());//+line.getY1());        both twos were ones
		g.setColor(c);
		g.drawLine(x,y,line.getX1(),line.getY1());//1
		g.dispose();

				
		return bi;
}
	
	/*
	 * overrides toString.
	 */
	@Override
	public String toString(){
		if(goodData==true)//TODO I'm not too sure that the radial distance (the last term on the next line) has any relevance anymore. 
			return runInfo+","+width+","+angleLeft+","+angleRight+","+startTime+","+time+","+xL+","+xR+","+(double)((xL+.5*width)/pixelsPerCentimeter);//add radial_distance (distance to left side + .5* width)
		else
			return "";
		
	}
}
