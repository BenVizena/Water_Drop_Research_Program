import java.util.ArrayList;

/*
 * Class allows for the creation of lines.  Lines can then be compared to find the angle between them with getAngleDegrees(Lines, Lines).
 * 
 * TODO: Need to add constructor that accepts a two-d array of points and creates a Lines object from the best fit line.
 * TODO: Need to use wildcard generics (i.e. ? extends Number) and then test it.
 */


public class Lines {
	private int x1;
	private int y1;
	private int x2;
	private int y2;
	private double m;

	/*
	 * Make line from two points.
	 */
	public Lines(int[] p1, int[]p2){
		this.x1=p1[0];
		this.y1=p1[1];
		this.x2=p2[0];
		this.y2=p2[1];
		
		double temp1=y2-y1;
		double temp2=x2-x1;
		
		if(temp2 == 0)
			temp2=.1;
		this.m=temp1/temp2;
		
	}
	
	/*
	 * make line from a point and a slope.
	 */
	public Lines(int[] p1, double m){
		this.x1=p1[0];
		this.y1=p1[1];
		this.x2=p1[0];
		this.y2=p1[1];
		
		this.m=m;
	}
	
	/*
	 * get the angle (in degrees) between two lines.
	 */
	public static double getAngleDegrees(Lines l1, Lines l2){
		double theta1 = Math.atan(l1.getM());
		double theta2 = Math.atan(l2.getM());
		double angle = theta1-theta2;
		angle=360*angle/2/Math.PI;
		return angle;
	}
	
	public static double getAverageSlope(ArrayList<Lines> lines){
		double sumSlope = 0;
		
		for(int x=0;x<lines.size();x++){
				sumSlope += lines.get(x).getM();
				System.out.println(lines.get(x).getX1()+" "+lines.get(x).getY1()+" "+lines.get(x).getX2()+" "+lines.get(x).getY2()+" "+lines.get(x).getM());
			
		}
		//System.out.println("lines size: "+lines.size());
		return sumSlope/(lines.size());
	}
	

	
	
	public int getX1(){
		return x1;
	}
	
	public int getY1(){
		return y1;
	}
	
	public int getX2(){
		return x2;
	}
	
	public int getY2(){
		return y2;
	}
	
	public double getM(){
		return m;
	}
}

