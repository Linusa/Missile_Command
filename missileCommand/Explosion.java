package missileCommand;

public class Explosion {
	
	//the center of the explosion and the amount of frames
	//since it started
	private int centerX, centerY, time;
	
	public Explosion(int x, int y)
	{
		//initializes the center of the explosion with the
		//passed in values, and the time as zero
		centerX = x;
		centerY = y;
		time = 0;
	}
	
	//updates the explosion by incrementing the time it's stood
	public void update()
	{
		time++;
	}
	
	//returns the x position
	public int getX()
	{
		return centerX;
	}
	
	//returns the y position
	public int getY()
	{
		return centerY;
	}
	
	//returns the current radius of the explosion
	public int getRadius()
	{
		return time/3 + 1;
	}

}
