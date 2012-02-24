package missileCommand;

public class Base {
	
	//x is the horizontal locations and hits is the amount
	//of times the base has been hit
	private int x, hits;
	
	public Base(int x)
	{
		//initializes x with the passed in value and
		//hits to 0
		this.x = x;
		
		hits = 0;
	}
	
	//increases the amount of times the base has been hit
	public void hit()
	{
		
		hits++;
	}
	
	//returns the amount of times the base has been hit
	public int getDamage()
	{
		return hits;
	}
	
	//returns the x placement
	public int getX()
	{
		return x;
		
	}

}
