import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.imageio.ImageIO;


import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber.Exception;
import org.bytedeco.javacv.Java2DFrameConverter;

public class StartingTimeGetter {
	
   	
   	public static double getStartingTime(Path mp4Path, int x1, int y1, int x2, int y2) throws Exception{
   		Java2DFrameConverter fc = new Java2DFrameConverter();
    	@SuppressWarnings("resource")
   		FFmpegFrameGrabber startTimeGrabber = new FFmpegFrameGrabber(mp4Path+"");
   	   	startTimeGrabber.start();
   	   	startTimeGrabber.setFrameNumber(0);
   	   	Frame image;
   	   	
   	   	if(Math.abs(y2-y1)<50)
   	   		y1=y1+100;
   	   	
   	   	double averageIntensity=0;
   	   	
   	   	boolean lightSeen = false;
   	   	
   	   	//find my average to compare to.
   	   	for(int x=0;x<60;x++){
   	   		image=startTimeGrabber.grabImage();
   	   		BufferedImage bi = fc.getBufferedImage(image);
   	   		averageIntensity+=ImageUtilities.getAvgIntensity(bi, x1, y1, x2, y2);
   	   	}
   	   	
   	   	averageIntensity=averageIntensity/60;
   	   	
   	   	int loops=60;
   	   	
   	   	while(lightSeen==false){
   	   		loops++;
   	   		image=startTimeGrabber.grabImage();
   	   		BufferedImage bi = fc.getBufferedImage(image);
   	   		double thisAvgIntensity=ImageUtilities.getAvgIntensity(bi, x1, y1, x2, y2);
   	   		if(thisAvgIntensity/averageIntensity-1 > .1){
   	   			lightSeen=true;
   	   		}
   	   	}
   	   	
   	   	return loops/60;
   	}
}
