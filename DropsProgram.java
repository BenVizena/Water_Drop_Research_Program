/*
 * Git set up complete
 */

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
/*
 * DropsProgram reads in drop images from an mp4 file and then sends the individual images to become drop objects.  then prints out the info for the newly created drop objects.
 * 
 * TODO: set up the scaling stuff.
 */
public class DropsProgram{
	
	public static void runProgram(String runName, String vidPath, String outPath, String scalePathInput, String startTimeText, String endTimeText, boolean sideViewTop,
			boolean sideViewBot, String rightPlatformY, String leftPlatformY, String intensityCutoff, String rightPlatformX, String leftPlatformX) throws Exception, IOException{
		double startTime = Double.parseDouble(startTimeText);
		double endTime = Double.parseDouble(endTimeText);
		
		int numFrames = getNumFrames(startTime,endTime);
		
    	Path mp4Path = Paths.get(vidPath).toAbsolutePath();		//the file path of the mp4	
    	Path destinationPath=Paths.get(outPath).toAbsolutePath();   //the file path of the destination
		
		Java2DFrameConverter fc = new Java2DFrameConverter();
    	@SuppressWarnings("resource")
		FFmpegFrameGrabber frameGrabber = new FFmpegFrameGrabber(mp4Path+"");//this takes several seconds for some reason.
 //   	FFmpegFrameGrabber startTimeGrabber = new FFmpegFrameGrabber(mp4Path+"");
 //   	startTimeGrabber.start();
 //   	startTimeGrabber.setFrameNumber(0);
 //   	Frame startFrame;
    	
    	frameGrabber.start();
    	frameGrabber.setFrameNumber((int)Math.floor(startTime*60));//sets the images to start being pulled at the specified start time.
    	Frame image;
    	
    	
    	
    	int intensityCutoffInt=Integer.parseInt(intensityCutoff);
    	
    	int rightPlatformYInt = Integer.parseInt(rightPlatformY);
    	int leftPlatformYInt = Integer.parseInt(leftPlatformY);
    	int rightPlatformXInt = Integer.parseInt(rightPlatformX);
    	int leftPlatformXInt = Integer.parseInt(leftPlatformX);
    	
    	double actualStartTime=DropsProgram.getStartingTime(mp4Path, leftPlatformXInt, leftPlatformYInt, rightPlatformXInt, rightPlatformYInt);
    	System.out.println(actualStartTime);
//    	double actualStartTime =StartingTimeGetter.getStartTime(filePath);
    	
    	if(sideViewTop||sideViewBot){
    		Drop[] drops = new Drop[numFrames];
    		
    //    	int intensityCutoffInt=Integer.parseInt(intensityCutoff);
        	
    		
    		for (int i = 0; i < numFrames; i++) {
            	try{
            		image=frameGrabber.grabImage();
            		BufferedImage bi = fc.getBufferedImage(image);
            		int nameNumber = i+1;//so drop names start at d1 instead of d0
            		File filePath = new File(destinationPath+"\\d"+nameNumber+".png");
            		ImageIO.write(bi, "png", filePath);//writes the image to the file that drop will use.
            		Double currentTime = startTime+(double)i/60;
         //   		System.out.println(currentTime+" "+startTime+" "+i);
            		drops[i]=new Drop(filePath,runName,(startTime+i/60),i,leftPlatformYInt, rightPlatformYInt, intensityCutoffInt, leftPlatformXInt, rightPlatformXInt,
            				sideViewTop,currentTime,actualStartTime);//creates drop.
            		bi.flush();
            	}
            	catch(Exception e){
            		System.out.println("failed while creating drops");
            		e.printStackTrace();
            	}          
            }
    		
            File results = new File(destinationPath+"\\results.txt");//creates a new text file for the results
            BufferedWriter writer = null;//makes the new bufferedwriter
            try {
                writer = new BufferedWriter(new FileWriter(results));//instantiates the new bufferedwriter
                writer.write("run_name,width,left_angle,right_angle,start_time,current_time,distance_to_left_edge,distance_to_right_edge,radial_distance,drop_number,actualStartTime");//creates header
                writer.newLine();
                for(int i=0;i<numFrames;i++){
                	writer.write(drops[i].toString()+","+(int)(i+1)+","+actualStartTime);//puts into from the drops below that header
                	writer.newLine();
                }
            } finally {
                writer.close();
                System.out.println("DONE");
            }
    	}
    	else{//if not sideViewTop or sideViewBot, then it is topView.
    		TopDrop[] drops = new TopDrop[numFrames];
    		
    		for (int i = 0; i < numFrames; i++) {
            	try{
            		image=frameGrabber.grabImage();
            		BufferedImage bi = fc.getBufferedImage(image);
            		int nameNumber = i+1;//so drop names start at d1 instead of d0
            		File filePath = new File(destinationPath+"\\d"+nameNumber+".png");
            		ImageIO.write(bi, "png", filePath);//writes the image to the file that drop will use.

            		Double currentTime = startTime+(double)i/60;
            		drops[i]=new TopDrop(filePath,runName,(startTime+i/60),i, currentTime, leftPlatformXInt, leftPlatformYInt, rightPlatformXInt, rightPlatformYInt, intensityCutoffInt,actualStartTime);
            		bi.flush();
            	}
            	catch(Exception e){
            		System.out.println("failed while creating drops");
            		e.printStackTrace();
            	}          
            }
    		
    		File results = new File(destinationPath+"\\results.txt");//creates a new text file for the results
            BufferedWriter writer = null;//makes the new bufferedwriter
            try {
                writer = new BufferedWriter(new FileWriter(results));//instantiates the new bufferedwriter
                writer.write("run_name,height,width,area,start_time,current_time,drop_number,actual_start_time");//creates header
                writer.newLine();
                for(int i=0;i<numFrames;i++){
                	writer.write(drops[i].toString()+","+(int)(i+1)+","+actualStartTime);//puts into from the drops below that header
                	writer.newLine();
                }
            } finally {
                writer.close();
                System.out.println("DONE");
            }
    		
    	}
	}

    /*
     * returns the number of frames in the time interval.
     */
    private static int getNumFrames(double start, double finish){
		return (int)Math.ceil((finish-start)*60);//camera operates at 60fps
    }  
    
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
   	   	
   	   	double loops=60;
   	   	
   	   	while(lightSeen==false){
   	   		loops++;
   	   		image=startTimeGrabber.grabImage();
   	   		BufferedImage bi = fc.getBufferedImage(image);
   	   		double thisAvgIntensity=ImageUtilities.getAvgIntensity(bi, x1, y1, x2, y2);
   	   		if((thisAvgIntensity-averageIntensity)/averageIntensity >.2){
   	   			lightSeen=true;
   	   		}
   	   		else{
   	   			System.out.println("Frame number: "+ (loops/60)+" "+(thisAvgIntensity/averageIntensity-1));
   	   		}
   	   	}
   	   	System.out.println(loops/60);
   	   	return loops/60;
   	}
}
