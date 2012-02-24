package missileCommand;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

import javax.imageio.ImageIO;
import javax.swing.*;

public class MissileCommand implements MouseListener{
	
	private JFrame frame;
	private Canvas playField;
	private final int frameWidth = 750, frameHeight = 500, amtBases = 6,
		reloadTime = 5, baseHealth = 3, baseWidth = 20, baseHeight = 20,
		explosionRadius = 30;
	private Vector<Missiles> enemyMissiles, playerMissiles;
	private Vector<Explosion> expOnScrn;
	private Base[] playerBases;
	private int sinceFired, level, score;
	private Boolean gameOver, clicked;
	Point2D.Double target;
	BufferedImage turret;
	String turretFile = "turret.jpg";
	
	public static void main(String args[])
	{
		new MissileCommand();
	}
	
	private MissileCommand()
	{
		//sets the basics of the game
		frame = new JFrame("Missile Command");
		frame.setSize(frameWidth, frameHeight);
		
		playField = new Canvas();
		playField.setSize(frameWidth, frameHeight);
		playField.setBackground(Color.BLACK);
		playField.addMouseListener(this);
		
		//creates a crosshair cursor
		playField.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(playField);
		frame.setResizable(false);
		frame.setVisible(true);
		File file = new File(turretFile);
		try
		{
			turret = ImageIO.read(file);
		}
		catch(IOException e)
		{
			System.out.println("Could not find all files.");
		}
		//begins the game
		playGame();
	}
	
	private void playGame()
	{
		//initializers for game basics
		level = 1;
		target = new Point2D.Double();
		gameOver = false;
		int amtBasesDestroyed = 0, amtMissiles = 10, amtMissilesDestroyed = 0,
			missilesSpawned = 0;
		sinceFired = reloadTime;
		Graphics g = playField.getGraphics();
		score = 0;
		long time;
		playerBases = new Base[amtBases];
		expOnScrn = new Vector<Explosion>();
		playerMissiles = new Vector<Missiles>();
		enemyMissiles = new Vector<Missiles>();
		
		//main game loop
		while(amtBasesDestroyed != amtBases)
		{
			//variables that are re-initialized each level
			missilesSpawned = 0;
			clicked = false;
			amtMissilesDestroyed = 0;
			amtBasesDestroyed = 0;
			int[] spawnTimes = new int[amtMissiles];
			int amtFrames;
			//creates random spawn times for each missile
			for(int i = 0; i < amtMissiles; i++)
			{
				amtFrames = (int)(Math.random() * 100);
				if(i != 0)
					spawnTimes[i] = amtFrames + spawnTimes[i-1];
				else
					spawnTimes[i] = amtFrames;
				
				
			}
			//sets up the locations of the Bases
			for(int i = 0; i < amtBases/2; i++)
				playerBases[i] = 
					new Base(((4 * frame.getWidth() / 10) / (amtBases / 2 + 1)) * (i + 1));
			for(int i = amtBases/2; i < amtBases; i++)
				playerBases[i] = 
					new Base(frame.getWidth() - ((4 * frame.getWidth() / 10) / (amtBases / 2 + 1)) * (i + 1 - amtBases/2));
			
			
			amtFrames = 0;
			
			//loops for each level
			while(amtMissilesDestroyed != amtMissiles && amtBasesDestroyed != amtBases)
			{
				//redraws all on screen objects each screen
				time = System.currentTimeMillis();
				sinceFired++;
				g.setColor(Color.WHITE);
				g.drawString("Level: " + level, playField.getWidth()/2, 10);
				g.drawString("Score: " + score, 30, 10);
				g.drawImage(turret,
						playField.getWidth()/2-20, playField.getHeight()-20, 
						playField.getWidth()/2 + 20, playField.getHeight(),
						0, 0, 
						turret.getWidth(), turret.getHeight(),
						null);
				
				//creates a new missile if that missile's time has come
				while(missilesSpawned < amtMissiles && spawnTimes[missilesSpawned] == amtFrames)
				{
					int targetBase;
					do
					{
						targetBase = (int)(amtBases * Math.random());
					}while(playerBases[targetBase].getDamage() == baseHealth);
					enemyMissiles.add(new Missiles(playerBases[targetBase].getX(), 
							playField.getHeight() - baseHeight/2, 
							(int)(Math.random() * frame.getWidth()), 0, false));
					missilesSpawned++;
				}
				
				fire();
				
				//draws all remaining Bases
				for(int i = 0; i < amtBases; i++)
				{
					if(playerBases[i].getDamage() < baseHealth)
					{
						g.setColor(Color.BLUE);
						
						g.fillRect(playerBases[i].getX()-baseWidth, 
								playField.getHeight() - baseHeight, 
								baseWidth, baseHeight);
					}
				}
				
				//draws all the explosions
				for(int i = 0; i < expOnScrn.size(); i++)
				{
					g.setColor(Color.BLACK);
					g.fillOval(expOnScrn.elementAt(i).getX(), expOnScrn.elementAt(i).getY(), 
							expOnScrn.elementAt(i).getRadius(), 
							expOnScrn.elementAt(i).getRadius());
					expOnScrn.elementAt(i).update();
					g.setColor(Color.RED);
					//removes an explosion once it's reached the maximum size
					if(expOnScrn.elementAt(i).getRadius() > explosionRadius)
					{
						expOnScrn.remove(i);
						i--;
					}
					else
						g.fillOval(expOnScrn.elementAt(i).getX(), expOnScrn.elementAt(i).getY(), 
								expOnScrn.elementAt(i).getRadius(), 
								expOnScrn.elementAt(i).getRadius());
				}
				
				//updates all the enemy missiles
				for(int i = 0; i < enemyMissiles.size(); i++)
				{
					
					boolean destroyed = false;
					Point2D.Double start = new Point2D.Double(), end = new Point2D.Double();
					
					//Erases the previous missile trail
					start = enemyMissiles.elementAt(i).getStartPnt();
					end = enemyMissiles.elementAt(i).getCurPnt();
					
					g.setColor(Color.BLACK);
					
					g.drawLine((int)start.getX(), (int)start.getY(), 
							(int)end.getX(), (int)end.getY());
					enemyMissiles.elementAt(i).update();
					
					//checks to see if the missile has been hit by an explosion
					//and removes it if it has, and updates the score
					for(Explosion explosion : expOnScrn)
					{
						if(Math.sqrt(Math.pow(end.getX() - explosion.getX(), 2) +
								Math.pow(end.getY() - explosion.getY(), 2))
								< explosion.getRadius())
						{
							g.setColor(Color.BLACK);
							
							
							g.drawLine((int)start.getX(), (int)start.getY(), 
									(int)end.getX(), (int)end.getY());
							amtMissilesDestroyed++;
							destroyed = true;
							enemyMissiles.remove(i);
							i--;
							g.setColor(Color.BLACK);
							g.drawString("Score: " + score, 30, 10);
							score += 100;
							g.setColor(Color.WHITE);
							g.drawString("Score: " + score, 30, 10);
							break;
							
						}
					}
					//if the missile wasn't destroyed, update or see if it
					//hit a Base
					if(!destroyed)
					{
						
						if(enemyMissiles.elementAt(i).hit())
						{
							
							amtMissilesDestroyed++;
							g.setColor(Color.BLACK);
							
							
							g.drawLine((int)start.getX(), (int)start.getY(), 
									(int)end.getX(), (int)end.getY());
							
							for(int x = 0; x < amtBases; x++)
							{
								if(playerBases[x].getX() == end.getX())
								{
									
									playerBases[x].hit();
									enemyMissiles.remove(i);
									i--;
									break;
								}
							}
							
						}
						else
						{
							
							g.setColor(Color.RED);
							g.drawLine((int)start.getX(), (int)start.getY(), 
										(int)end.getX(), (int)end.getY());
							
						}
					}
				}
				
				//updates player missiles
				for(int i = 0; i < playerMissiles.size(); i++)
				{
					//erases previous missile trails
					Point2D.Double start = playerMissiles.elementAt(i).getStartPnt(), 
						end = playerMissiles.elementAt(i).getCurPnt();
					g = playField.getGraphics();
					g.setColor(Color.BLACK);
					g.drawLine((int)start.getX(), (int)start.getY(), 
							(int)end.getX(), (int)end.getY());
					
					playerMissiles.elementAt(i).update();
					
					//if the missile hit, replace it with an explosion
					if(playerMissiles.elementAt(i).hit())
					{
						
						Point2D.Double center = playerMissiles.elementAt(i).getCurPnt();
						expOnScrn.add(new Explosion((int)center.getX(), (int)center.getY()));
						playerMissiles.remove(i);
						i--;
					}
					//otherwise, draw the player missile trail
					else
					{
						
						g.setColor(Color.BLUE);
						g.drawLine((int)start.getX(), (int)start.getY(), 
								(int)end.getX(), (int)end.getY());
					}
				}
				//erases destroyed Bases
				amtBasesDestroyed = 0;
				for(int i = 0; i < amtBases; i++)
				{
					if(playerBases[i].getDamage() == baseHealth)
					{
						g.setColor(Color.BLACK);
						
						g.fillRect(playerBases[i].getX()-baseWidth, 
								playField.getHeight() - baseHeight, 
								baseWidth, baseHeight);
					}
				}
				//the game's over if all the Bases are destroyed
				if(amtBasesDestroyed == amtBases)
				{
					gameOver = true;
					break;
				}
				try
				{
					long delay = Math.max(0, 32-(System.currentTimeMillis()-time));
					
					Thread.sleep(delay);
				}
				catch(InterruptedException e)
				{
				}
				//updates the amount of frames for missile spawning purposes
				amtFrames++;
				
			}
			
			//erases and updates all the information for the next level
			amtMissiles += 2;
			if(gameOver)
				break;
			g.setColor(Color.BLACK);
			g.drawString("Level: " + level, playField.getWidth()/2, 10);
			for(int i = 0; i < playerMissiles.size(); i++)
			{
				Point2D.Double start = playerMissiles.elementAt(i).getStartPnt(),
					end = playerMissiles.elementAt(i).getCurPnt();
				
				g.setColor(Color.BLACK);
				
				g.drawLine((int)start.getX(), (int)start.getY(), 
						(int)end.getX(), (int)end.getY());
			}
			for(int i = 0; i < enemyMissiles.size(); i++)
			{
				Point2D.Double start = enemyMissiles.elementAt(i).getStartPnt(),
					end = enemyMissiles.elementAt(i).getCurPnt();
				
				g.setColor(Color.BLACK);
				
				g.drawLine((int)start.getX(), (int)start.getY(), 
						(int)end.getX(), (int)end.getY());
			}
			playerMissiles.removeAllElements();
			enemyMissiles.removeAllElements();
			level++;
			g.setColor(Color.WHITE);
			g.drawString("Level: " + level, playField.getWidth()/2, playField.getHeight()/2);
			try
			{
				Thread.sleep(3000);
			}
			catch(InterruptedException e)
			{
			}
			g.setColor(Color.BLACK);
			g.drawString("Level: " + level, playField.getWidth()/2, playField.getHeight()/2);
			g.setColor(Color.BLACK);
			g.drawString("Score: " + score, 30, 10);
			
			//Bonus score for surviving Bases
			score += 500 * (amtBases - amtBasesDestroyed);
			g.setColor(Color.WHITE);
			g.drawString("Score: " + score, 30, 10);
			
				
		}
		
	}

	private void fire()
	{
		//creates a new missile when and where the player clicks
		if(clicked)
		{
			
			playerMissiles.add(new Missiles((int)target.getX(), (int)target.getY(), 
					frame.getWidth()/2, frame.getHeight() - 20, true));
			sinceFired = 0;
			clicked = false;
		}
	}
	@Override
	public void mouseClicked(MouseEvent arg0) {
		
		//only fires if enough time has passed since the last shot
		if(sinceFired > reloadTime)
		{
			clicked = true;
			target.setLocation(arg0.getX(), arg0.getY());
			
		}
			
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
		
}
