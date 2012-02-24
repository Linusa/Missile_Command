package missileCommand;

import java.awt.geom.*;

public class Missiles {
	//the terminal is the ending location of a missile and the curPnt
	//is where it is currently, and speed is the x and y speed, and the
	//start is where it started
	public Point2D.Double terminal, curPnt, start;
	private boolean player;
	private double angle;
	private int speed;
	
	
	public Missiles(int termX, int termY, int startX, int startY, boolean player)
	{
		
		terminal = new Point2D.Double(termX, termY);
		curPnt = new Point2D.Double(startX, startY);
		start = new Point2D.Double(startX, startY);
		//creates the terminal and curPnt with passed in values
		
		
		angle = Math.atan(
				(terminal.getX() - curPnt.getX()) /
				(terminal.getY() - curPnt.getY()));
		if(player)
		{
			angle = Math.atan(
					(terminal.getY() - curPnt.getY()) /
					(terminal.getX() - curPnt.getX()));
			if(angle > 0)
				angle += Math.PI;
		}
		
		
		if(player)
			speed = 4;
		else
			speed = (int)(Math.random() * 2) + 1;
		this.player = player;
			
	}
	
	public void update()
	{
		
		//increases the location based on the speed
		if(!player)
			curPnt.setLocation(curPnt.getX() + speed * Math.sin(angle), 
				curPnt.getY() + speed * Math.cos(angle));
		else
			curPnt.setLocation(curPnt.getX() + speed * Math.cos(angle), 
				curPnt.getY() + speed * Math.sin(angle));
		
		//if the missile would overshoot, leaves it at the terminal
		if((player && curPnt.getY() < terminal.getY()) ||
				(!player && curPnt.getY() > terminal.getY()))
			curPnt.setLocation(terminal);
		
			
		
	}
	
	//returns current x position
	public Point2D.Double getCurPnt()
	{
		return curPnt;
	}
	
	//returns current y position
	public Point2D.Double getStartPnt()
	{
		return start;
	}
	
	//creates and returns an explosion where the missile blew up
	public Explosion destroyed()
	{
		return new Explosion((int)curPnt.getX(), (int)curPnt.getY());
	}
	
	//returns whether or not the missile reached its target
	public boolean hit()
	{
		if(curPnt.equals(terminal))
			return true;
		
		return false;
	}
	

}
