import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;


/*
 * static class that returns a copy of the input image in greyscale with the edges (gradient cutoff specified in the markEdges call) marked in red.
 * use the class by calling SobelOperator.markEdges(inputImage,gradientCutoff).  Return type: BufferedImage.
 */

public final class ImageUtilities {

	//static.
	private ImageUtilities(){
		
	}
	
	/*
	 * returns the brightness of a pixel.  brightness is determined by our eyes (green is very intense to us while blue is not so much.)
	 */
	private static double getBrightness(BufferedImage img, int x, int y) {
		return (int)(0.2126*getRedValue(img,x,y) + 0.7152*getGreenValue(img,x,y) + 0.0722*getBlueValue(img,x,y));//correlates to how intense r, g, and b wavelengths are to human eyes
	}
	
	/*
	 * uses the sobel operator to return a pixel's gradient.
	 */
	private static double getGradient(BufferedImage img, int x, int y){

		
		double Gx = -1*getBrightness(img,x-1,y-1)-2*getBrightness(img,x-1,y)-getBrightness(img,x-1,y+1)//compares left
					  +getBrightness(img,x+1,y-1)+2*getBrightness(img,x+1,y)+getBrightness(img,x+1,y+1);//to right side of pixel
		
		double Gy = -1*getBrightness(img,x-1,y-1)-2*getBrightness(img,x,y-1)-getBrightness(img,x+1,y-1)//compares top
					+1*getBrightness(img,x-1,y+1)+2*getBrightness(img,x,y+1)+getBrightness(img,x+1,y+1);//to bottom side of pixel
		
	//	System.out.println(Gx + " "+ Gy);
		
		return Math.sqrt(Math.pow(Gx,2)+Math.pow(Gy,2));//gets resultant vector using Pythagorean theorem.
	}
	
	public static int getRedValue(BufferedImage img,int x , int y){
		Color c = new Color(img.getRGB(x ,y));//get all colors in the pixel
		int red = c.getRed();//isolate desired color value
		return red;//return color value		
	}

	public static int getBlueValue(BufferedImage img, int x, int y){
			Color c = new Color(img.getRGB(x, y));
			int blue = c.getBlue();
			return blue;
		}
		
	public static int getGreenValue(BufferedImage img, int x, int y){
			Color c = new Color(img.getRGB(x, y));
			int green = c.getGreen();
			return green;
		}
	
	/*
	 * the driver of this static class.  calls grayScale and marks the edges.
	 */
	public static BufferedImage markEdges(BufferedImage img, int gradCutoff){
		System.out.println("A");
		Color myRed = new Color(255, 0, 0);
		int rgb=myRed.getRGB();
		
		img=toGrayScale(img);
		System.out.println("GRAYSCALE");
		
		BufferedImage bi = copy(img);//make a separate copy of the input image.
		
	
		for(int x=1;x<img.getWidth()-1;x++)//raster over  //520,1750
			for(int y=1;y<img.getHeight()-1;y++){//the important image pixels  //755,950
				System.out.println("FOR LOOP");
				if((int)getGradient(bi,x,y)>gradCutoff)//checks on the clean copy of the image (bi) so edge markings don't influence the gradient.
				{
					System.out.println("RED");
					img.setRGB( x, y, rgb );//marks the 'dirty copy'
				}
				System.out.println((int)getGradient(bi,x,y)+" > "+gradCutoff);
			}
		return img;	
	}
	
	/*
	 * the driver of this static class. calls grayScale and marks the edges, 
	 */
	public static BufferedImage markEdges(BufferedImage img, int gradCutoff,int minImportantX, int maxImportantX, int minImportantY, int maxImportantY){
		Color myRed = new Color(255, 0, 0);
		int rgb=myRed.getRGB();
		
		
//		img=toGrayScale(img);
//		System.out.println("grayscale");
//		img=gaussianFilter(img,minImportantX,maxImportantX,minImportantY,maxImportantY);
		
		BufferedImage bi = copy(img);//make a separate copy of the input image.
		
	
	//	int maxGrad=0;
		for(int x=minImportantX;x<maxImportantX;x++)//raster over
			for(int y=minImportantY;y<maxImportantY;y++){//the important image pixels
				if((int)getGradient(bi,x,y)>gradCutoff)//checks on the clean copy of the image (bi) so edge markings don't influence the gradient.
				{
				//	System.out.println("ReD");
					img.setRGB( x, y, rgb );//marks the 'dirty copy'
				}
		//		if((int)getGradient(bi,x,y)>maxGrad)
		//			maxGrad = (int)getGradient(bi,x,y);
			}
	//	System.out.println(maxGrad);
		return img;	
	}
	
	public static boolean isRed(BufferedImage img, int x, int y){
		if(ImageUtilities.getBlueValue(img, x, y)==0 && ImageUtilities.getGreenValue(img,x,y)==0 && ImageUtilities.getRedValue(img, x, y)==255)
			return true;
		else
			return false;
	}
	
	
	public static boolean isBlue(BufferedImage img, int x, int y){
		if(ImageUtilities.getBlueValue(img, x, y)==255 && ImageUtilities.getGreenValue(img,x,y)==0 && ImageUtilities.getRedValue(img, x, y)==0)
			return true;
		else
			return false;
	}
	
	public static BufferedImage supressFeatures(BufferedImage img, int gradCutoff, int minImportantX, int maxImportantX, int minImportantY, int maxImportantY){
		img = toGrayScale(img);
		int white_rgb = new Color(255,255,255).getRGB();
		int black_rgb = new Color(0,0,0).getRGB();
		long totalBrightness = 0;
		int totalPixels = img.getWidth()*img.getHeight();
		
		for(int x=0;x<img.getWidth();x++)//raster over
			for(int y=0;y<img.getHeight();y++)//the important image pixels
				totalBrightness+=getBrightness(img,x,y);
		
		int brightness_cutoff = (int) (totalBrightness/totalPixels);
//		System.out.println(brightness_cutoff);
		
		for(int x=0;x<img.getWidth();x++)//raster over
			for(int y=0;y<img.getHeight();y++)//the important image pixels
				if(getBrightness(img,x,y)>brightness_cutoff-13)//-36 for 1063
					img.setRGB(x, y, white_rgb);
				else
					img.setRGB(x, y, black_rgb);
		
		return img;
	}
	
		
	public static BufferedImage copy(BufferedImage bi){
		BufferedImage b = new BufferedImage(bi.getWidth(), bi.getHeight(), bi.getType());
	    Graphics g = b.createGraphics();//makes it to where you can draw on the image (b)
	    g.drawImage(bi, 0, 0, null);//copy the image over from bi to b (draws as much of bi on to b starting from 0,0 as it can)
	    g.dispose();
	    return b;
	}
		/*
		 * changes the image space to TYPE_BYTE_GRAY, resulting in a loss of the color information.
		 * then changes the color space back to TYPE_INT_RGB, allowing markings on the image to show up in color.
		 */
	private static BufferedImage toGrayScale(BufferedImage bi){
		BufferedImage image = new BufferedImage(bi.getWidth(), bi.getHeight(),  BufferedImage.TYPE_BYTE_GRAY);  
		Graphics g = image.getGraphics();  
		g.drawImage(bi, 0, 0, null);  
		g.dispose();
		BufferedImage image2= new BufferedImage(bi.getWidth(),bi.getHeight(),BufferedImage.TYPE_INT_RGB);
		g = image2.getGraphics();
		g.drawImage(image, 0, 0, null);
		return image2;
	}
	
	/*
	 * applies gaussian filter to each pixel.
	 */
	private static BufferedImage gaussianFilter(BufferedImage image, int minImportantX, int maxImportantX, int minImportantY, int maxImportantY){
		BufferedImage bi = copy(image);//make a separate copy of the input image.
		for(int x=minImportantX-4;x<maxImportantX+4;x++){
			for(int y=minImportantY-4;y<maxImportantY+4;y++){
				double gaussian = (2*getIntensity(image,x,y)+4*getIntensity(image,x-1,y-2)+5*getIntensity(image,x,y-2)+4*getIntensity(image,x+1,y-2)+2*getIntensity(image,x+2,y-2)
    					+ 4*getIntensity(image,x-2,y-1)+9*getIntensity(image,x-1,y-1)+12*getIntensity(image,x,y-1)+9*getIntensity(image,x+1,y-1)+4*getIntensity(image,x+2,y-1)
    					+ 5*getIntensity(image,x-2,y)+12*getIntensity(image,x-1,y)+15*getIntensity(image,x,y)+12*getIntensity(image,x+1,y)+5*getIntensity(image,x+2,y)
    					+ 4*getIntensity(image,x-2,y+1)+9*getIntensity(image,x-1,y+1)+12*getIntensity(image,x,y+1)+9*getIntensity(image,x+1,y+1)+4*getIntensity(image,x+2,y+1)
    					+ 2*getIntensity(image,x-2,y+2)+4*getIntensity(image,x-1,y+2)+5*getIntensity(image,x,y+2)+4*getIntensity(image,x+1,y+2)+2*getIntensity(image,x+2,y+2))/159;
				Color blurred = new Color((int)Math.round(gaussian),(int)Math.round(gaussian),(int)Math.round(gaussian));
				bi.setRGB(x, y, blurred.getRGB());
			}
		}

    	return bi;
    }
	
	/*
	 * return the intensity of a pixel (used for gaussianFilter)
	 */
	private static int getIntensity(BufferedImage image,int x,int y){
		return (int)((getRedValue(image,x,y)+getBlueValue(image,x,y)+getGreenValue(image,x,y))/3);
	}
	
	public static double getAvgIntensity(BufferedImage image, int x1, int y1, int x2, int y2){
		double totalIntensity = 0;
		
		for(int x=Math.min(x1, x2);x<Math.max(x1, x2);x++)
			for(int y=Math.min(y1, y2);y<Math.max(y1, y2);y++)
				totalIntensity+=(double)getIntensity(image,x,y);
		
		
			
		
		
		return totalIntensity/((Math.max(x1, x2)-Math.min(x1, x2))*(Math.max(y1, y2)-Math.min(y1, y2)));
	}
}

