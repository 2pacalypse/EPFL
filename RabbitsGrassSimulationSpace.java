import uchicago.src.sim.space.Object2DTorus;

/**
 * Class that implements the simulation space of the rabbits grass simulation.
 * 
 * @author
 */

public class RabbitsGrassSimulationSpace {
	private Object2DTorus grassSpace;
	private Object2DTorus agentSpace;

	private int grassCount = 0;

	public RabbitsGrassSimulationSpace(int xSize, int ySize) {
		grassSpace = new Object2DTorus(xSize, ySize);
		agentSpace = new Object2DTorus(xSize, ySize);

		for (int i = 0; i < xSize; i++) {
			for (int j = 0; j < ySize; j++) {
				grassSpace.putObjectAt(i, j, Integer.valueOf(0));
			}
		}

	}

	public void growGrass(int limit) {
		for (int i = 0; i < limit; i++) {
			int x = (int) (Math.random() * grassSpace.getSizeX());
			int y = (int) (Math.random() * grassSpace.getSizeY());
			if (getGrassAt(x, y) == 0) {
				grassSpace.putObjectAt(x, y, Integer.valueOf(1));
				grassCount++;
			}

		}
	}

	private int getGrassAt(int x, int y) {
		int ret = 0;
		if (grassSpace.getObjectAt(x, y) != null) {
			ret = ((Integer) grassSpace.getObjectAt(x, y)).intValue();
		}
		return ret;
	}

	public boolean isCellOccupied(int x, int y) {
		boolean retVal = false;
		if (agentSpace.getObjectAt(x, y) != null) {
			retVal = true;
		}
		return retVal;
	}

	public boolean addAgent(RabbitsGrassSimulationAgent agent) {
		boolean retVal = false;
		int count = 0;
		int countLimit = 10 * agentSpace.getSizeX() * agentSpace.getSizeY();

		while (retVal == false && count < countLimit) {
			int x = (int) (Math.random() * agentSpace.getSizeX());
			int y = (int) (Math.random() * agentSpace.getSizeY());
			if (isCellOccupied(x, y) == false) {
				agentSpace.putObjectAt(x, y, agent);
				agent.setXY(x, y);
				agent.setRgsSpace(this);
				retVal = true;
			}
			count++;
		}
		return retVal;
	}

	public Object2DTorus getCurrentAgentSpace() {
		return agentSpace;
	}

	public Object2DTorus getCurrentGrassSpace() {
		return grassSpace;
	}

	public int getGrassCount() {
		return grassCount;
	}

	public void setGrassCount(int grassCount) {
		this.grassCount = grassCount;
	}

}
