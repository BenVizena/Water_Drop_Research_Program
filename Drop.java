/*
 * this class enables the creation of Drop objects.  A drop object has a width, time, left angel, right angle, etc.  
 * 
 * the objects DO NOT have the BufferedImage of the drop. In stead, they have the filePath to where they can find their respective images.
 * 
 * Most of the todos are to use a best fit line method to find the lines to try to counteract the negative effects of inherently descritized pixels.
 */

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

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
	
	
	/*
	 * these declarations are setting up the "important" zone on the Buffered Image.  The important zone is everywhere the drop might be.
	 */
<<<<<<< HEAD
	/*
	private final static int minImportantX=1;//450
	private final static int maxImportantX=1800;//1180
	private final static int minImportantY=1;//330
	private final static int maxImportantY=1070;//550
	*/
	private final static int gradientThreshold=110;//70
	
	
	private static int minImportantX;
	private static int maxImportantX;
	private static int minImportantY;
	private static int maxImportantY;
=======
	private final static int minImportantX=450;
	private final static int maxImportantX=1180;//
	private final static int minImportantY=330;
	private final static int maxImportantY=550;//950
	private final static int gradientThreshold=115;//140
>>>>>>> f79fd905869ca4f07bdb5e4fb91662d3a9e07bec
	
	/*
	 * drives the drop class.  Accepts the filepath to the drop image, the run info (specified through cmd line arguments), the start time of the run, and the frame number
	 * (used to calculate the time that the frame happened).
	 */
	public Drop(File filePath,String runInfo,double time, double frameNumber, int pixelsPerCentimeter){
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

<<<<<<< HEAD
		minImportantX=250;
		maxImportantX=img.getWidth()-1000;
		minImportantY=250;
		maxImportantY=img.getHeight()-250;
		
		
		
		img=SobelOperator.markEdges(img,gradientThreshold,minImportantX,maxImportantX,minImportantY,maxImportantY);
		System.out.println("MARKED EDGES");
=======

		
		
		img=SobelOperator.markEdges(img,gradientThreshold,minImportantX,maxImportantX,minImportantY,maxImportantY);
>>>>>>> f79fd905869ca4f07bdb5e4fb91662d3a9e07bec
		blueLine = findLine(img);		
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
	
	/*
	 * finds the point where the drop makes it's rightmost contact with the surface.
	 * 
	 * follows the blue line made by imposing blueLine on to the image. (blueLine is the surface that the drop is sitting on.)
	 * once a red pixel is found 1 pixel above the blue line, identify that pixel as the rightmost contact point.
	 * 
	 * This method fails if blueLine does not properly mark the surface that the drop sits on.
	 */
<<<<<<< HEAD
	
	/*
=======
>>>>>>> f79fd905869ca4f07bdb5e4fb91662d3a9e07bec
	private static int[] findRightContactPoint(BufferedImage bi){
		int[] point={-1,-1};
		Graphics2D g = bi.createGraphics();
		g.setColor(new Color(255,165,0));
		for(int x=maxImportantX-10;x>minImportantX+10;x--){//raster over
			for(int y=minImportantY;y<maxImportantY-10;y++){//the image
				if(SobelOperator.getBlueValue(bi, x, y)==255){//to find the blue line
					if(SobelOperator.getRedValue(bi, x, y-1)==255){//then check to see if the pixel below is red    was y+1
						point[0]=x;//if it is
						point[1]=y-1;//you have found the right edge
						x=-1;
						y=maxImportantY;
					}								
				}			
			}
		}
		return point;
	}
<<<<<<< HEAD
	*/
	
	private static int[] findRightContactPoint(BufferedImage bi){
		int[] point={-1,-1};
		Graphics2D g = bi.createGraphics();
		g.setColor(new Color(255,165,0));
		for(int x=maxImportantX-10;x>minImportantX+10;x--){//raster over
			for(int y=minImportantY;y<maxImportantY-10;y++){//the image
				if(SobelOperator.getBlueValue(bi, x, y)==255){//to find the blue line
					if(SobelOperator.getRedValue(bi, x, y-1)==255){//then check to see if the pixel below is red    was y+1
						if(SobelOperator.getRedValue(bi,x,y+1)==255){
							point[0]=x;//if it is
							point[1]=y-1;//you have found the right edge
							x=-1;
							y=maxImportantY;
						}
					}								
				}			
			}
		}
		return point;
	}
=======
>>>>>>> f79fd905869ca4f07bdb5e4fb91662d3a9e07bec
	

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
			for(int y=minImportantY;y<maxImportantY-10;y++){//the image
				if(SobelOperator.getBlueValue(bi, x, y)==255){//to find the blue line
					if(SobelOperator.getRedValue(bi, x, y-1)==255){//then check to see if the pixel below is red    was y+1
						point[0]=x;//if it is
						point[1]=y-1;//you have found the right edge
						x=maxImportantX;
						y=maxImportantY;
					}								
				}			
			}
		}
		return point;
	}

	/*
	 * this method finds blueLine (the line that shows where on the platform the drop is sitting.
	 * it does this by finding the slope of the bottom of the platform (which is not blurry for some reason) and then making a Lines object out of that slope
	 * and the point found by findLeftContactPoint.
	 * 
	 * TODO: make this use multiple point samples from both sides to construct a line of best fit.  (need to update Lines to allow for this)
	 */
	private static Lines findLine(BufferedImage bi){
		Graphics2D g = bi.createGraphics();
		int y1=-1;
		int y2=-1;
		for(int y=minImportantY;y<maxImportantY;y++)
			if(SobelOperator.getRedValue(bi, minImportantX+60, y)==255){//finds a y value on the left side of the platform edge (at a specified x)
				y1=y;
				y+=maxImportantY+50;
			}
		for(int y=minImportantY;y<maxImportantY;y++)
			if(SobelOperator.getRedValue(bi, maxImportantX-20, y)==255){//finds a y value on the right side of the platform edge (at a specified x)
				y2=y;
				y+=maxImportantY+50;
			}
		int p1[]={minImportantX+60,y1};//constructs point on left side.
		int p2[]={maxImportantX-10,y2};//constructs point on right side.
		Lines line = new Lines(p1,p2);// makes a Lines object between the two points.
		g.setColor(Color.BLUE);
		g.drawLine(line.getX1(), line.getY1(), line.getX2(), line.getY2());
		
		return line;//was line2
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
		int y = p1[1]-10;
		int x= scanFromRight(bi,y);//looks for red pixel 10 pixels up from p1.
		int[] p2 = {x,y};//makes point 2
		Lines line = new Lines(p1,p2);
		return line;
	}
	
	/*
	 * scans from right to left at a given y looking for the first red pixel.
	 * 
	 * if it finds a red pixel, we call the data good.
	 * if it does not find a pixel (maybe the drop was too blurry because it was moving) goodData remains false.
	 */
	private static int scanFromRight(BufferedImage bi, int y){
		
		int xPoint=-1;
		Graphics2D g = bi.createGraphics();
		g.setColor(new Color(157,235,233));
		for(int x=maxImportantX-10;x>minImportantX;x--){//rasters from right to left
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
	
	private static int scanFromLeft(BufferedImage bi, int y2, int x1){
		int x2=x1-20;
		for(int x=x2;x<maxImportantX;x++){
			if(SobelOperator.getRedValue(bi, x, y2)==255){
				x2=x;
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
//		System.out.println("["+points[0]+", "+points[1]+"]");
		int y2 = points[1]-5;
		int x2 = scanFromLeft(bi,y2,points[0]);
		int[] p2 = {x2,y2};
		Lines line = new Lines(points,p2);
		
		return line;
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
