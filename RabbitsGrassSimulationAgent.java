import java.awt.Color;

import uchicago.src.sim.gui.Drawable;
import uchicago.src.sim.gui.SimGraphics;

/**
 * Class that implements the simulation agent for the rabbits grass simulation.
 * 
 * @author
 */


public class RabbitsGrassSimulationAgent implements Drawable {

	private int x;
	private int y;
	private int energy;
	private RabbitsGrassSimulationSpace rgsSpace;

	public RabbitsGrassSimulationAgent(int initialEnergy) {
		setXY(-1,-1);
		setEnergy(initialEnergy);
		setRgsSpace(null);
	}


	public void move() {
		energy--;

		int oldX = x;
		int oldY = y;

		double dice = Math.random();
		if (dice < 0.25) {
			x = rgsSpace.getCurrentAgentSpace().xnorm(x + 1);
		} else if (dice < 0.5) {
			x = rgsSpace.getCurrentAgentSpace().xnorm(x - 1);
		} else if (dice < 0.75) {
			y = rgsSpace.getCurrentAgentSpace().ynorm(y + 1);
		} else {
			y = rgsSpace.getCurrentAgentSpace().ynorm(y - 1);
		}

		if (rgsSpace.isCellOccupied(x, y)) {
			x = oldX;
			y = oldY;
		} else {
			rgsSpace.getCurrentAgentSpace().putObjectAt(oldX, oldY, null);
			rgsSpace.getCurrentAgentSpace().putObjectAt(x, y, this);
		}
	}
	
	public void eat(int grassEnergy) {
		
		if (((int) (Integer) rgsSpace.getCurrentGrassSpace().getObjectAt(x, y)) == 1) {
			energy += grassEnergy;
			rgsSpace.getCurrentGrassSpace().putObjectAt(x, y, Integer.valueOf(0));
			rgsSpace.setGrassCount(rgsSpace.getGrassCount() - 1);
		}
		
	}

	public void draw(SimGraphics g) {

		g.drawRect(Color.pink);

	}
	/*
	 * Getters and setters 
	 */
	
	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public void setXY(int newX, int newY) {
		x = newX;
		y = newY;
	}

	public int getEnergy() {
		return energy;
	}

	public void setEnergy(int energy) {
		this.energy = energy;
	}

	public RabbitsGrassSimulationSpace getRgsSpace() {
		return rgsSpace;
	}

	public void setRgsSpace(RabbitsGrassSimulationSpace rgsSpace) {
		this.rgsSpace = rgsSpace;
	}




}
