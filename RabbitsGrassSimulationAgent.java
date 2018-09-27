import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.sun.javafx.tk.Toolkit;

import uchicago.src.sim.gui.Drawable;
import uchicago.src.sim.gui.SimGraphics;


/**
 * Class that implements the simulation agent for the rabbits grass simulation.

 * @author
 */

public class RabbitsGrassSimulationAgent implements Drawable {
	
	private int x;
	private int y;
	private int energy;
	
	
	
	private RabbitsGrassSimulationSpace rgsSpace;
	
	public void setRabbitsGrassSimulationSpace(RabbitsGrassSimulationSpace rgsSpace) {
		this.rgsSpace = rgsSpace;
	}
	
	public RabbitsGrassSimulationAgent(int initialEnergy) {
		x = -1;
		y = -1;
		setEnergy(initialEnergy);
	}
	
	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
	
	 public void setXY(int newX, int newY){
	    x = newX;
	    y = newY;
	}
	 
	public int getEnergy() {
			return energy;
		}

	public void setEnergy(int energy) {
			this.energy = energy;
		}	

	

	public void draw(SimGraphics g) {

		g.drawRect	(Color.pink);
		
		
	}



	public void step(int givenEnergy) {
		
		int oldX = x;
		int oldY = y;
		
		double dice = Math.random();
		if(dice < 0.25) {
			x = rgsSpace.getCurrentAgentSpace().xnorm(x + 1);
		}
		else if(dice < 0.5) {
			x = rgsSpace.getCurrentAgentSpace().xnorm(x - 1);
		}
		else if(dice < 0.75) {
			y = rgsSpace.getCurrentAgentSpace().ynorm(y + 1);
		}
		else {
			y = rgsSpace.getCurrentAgentSpace().ynorm(y - 1);
		}
		
		
		energy--;
		if(rgsSpace.isCellOccupied(x, y)) {
			x = oldX;
			y = oldY;
		}else {
			
			rgsSpace.getCurrentAgentSpace().putObjectAt(oldX, oldY, null);
			rgsSpace.getCurrentAgentSpace().putObjectAt(x, y, this);
			
			if(  ((int) (Integer) rgsSpace.getCurrentGrassSpace().getObjectAt(x, y))  == 1)  {
				energy += givenEnergy;
				rgsSpace.getCurrentGrassSpace().putObjectAt(x, y, Integer.valueOf(0));
			}
			

			
		}
		
		
	}


}
