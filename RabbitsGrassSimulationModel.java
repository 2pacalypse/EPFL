


import java.awt.Color;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import uchicago.src.reflector.RangePropertyDescriptor;
import uchicago.src.sim.analysis.DataSource;
import uchicago.src.sim.analysis.OpenSequenceGraph;
import uchicago.src.sim.analysis.Sequence;
import uchicago.src.sim.engine.BasicAction;
import uchicago.src.sim.engine.Schedule;
import uchicago.src.sim.engine.SimInit;
import uchicago.src.sim.engine.SimModelImpl;
import uchicago.src.sim.gui.ColorMap;
import uchicago.src.sim.gui.DisplaySurface;
import uchicago.src.sim.gui.Object2DDisplay;
import uchicago.src.sim.gui.Value2DDisplay;


/**
 * Class that implements the simulation model for the rabbits grass
 * simulation.  This is the first class which needs to be setup in
 * order to run Repast simulation. It manages the entire RePast
 * environment and the simulation.
 *
 * @author 
 */


public class RabbitsGrassSimulationModel extends SimModelImpl {

	private RabbitsGrassSimulationSpace rgsSpace;
	private ArrayList<RabbitsGrassSimulationAgent> agentList;
	private Schedule schedule;
	private DisplaySurface displaySurface;
	private OpenSequenceGraph graph;


	/*
	 * Initial values
	 */

	private static final int GRIDX = 20;
	private static final int GRIDY = 20;
	private static final int NUMRABBITS = 20;
	private static final int BIRTHTHRESHOLD = 20;
	private static final int GRASSGROWTHRATE = 20;
	private static final int RABBITENERGY = 20;
	private static final int GRASSENERGY = 20;


	/*
	 * Parameter variables
	 */

	private int gridX = GRIDX;
	private int gridY = GRIDY;
	private int numRabbits = NUMRABBITS;

	private int birthThreshold = BIRTHTHRESHOLD;
	private int grassGrowthRate = GRASSGROWTHRATE;
	private int rabbitEnergy = RABBITENERGY;
	private int grassEnergy = GRASSENERGY;


	public String[] getInitParam() {
		return new String[] { "GridX", "GridY", "NumRabbits", "BirthThreshold", "GrassGrowthRate", "RabbitEnergy",
		"GrassEnergy" };
	}



	/*
	 * Inner classes to draw graph.
	 */

	class RabbitPopulation implements DataSource, Sequence {

		@Override
		public double getSValue() {
			//System.out.println(agentList.size());
			return agentList.size();
		}

		@Override
		public Object execute() {
			return Double.valueOf(getSValue());
		}

	}

	class GrassPopulation implements DataSource, Sequence {

		@Override
		public double getSValue() {
			return rgsSpace.getGrassCount();
		}

		@Override
		public Object execute() {
			return Double.valueOf(getSValue());
		}

	}

	public static void main(String[] args) {
		System.out.println("Rabbit skeleton");
		SimInit init = new SimInit();
		RabbitsGrassSimulationModel model = new RabbitsGrassSimulationModel();
		init.loadModel(model, "", false);
	}

	public String getName() {
		return "EPFL CS430 HW1";
	}

	public void setup() {
		System.out.println("Running setup");
		rgsSpace = null;
		agentList = new ArrayList<RabbitsGrassSimulationAgent>();
		schedule = new Schedule(1);

		if (displaySurface != null) {
			displaySurface.dispose();
		}
		displaySurface = null;
		displaySurface = new DisplaySurface(this, "Model Window");
		registerDisplaySurface("Model Window", displaySurface);

		if (graph != null) {
			graph.dispose();
		}
		graph = null;
		graph = new OpenSequenceGraph("Population Evolution", this);
		graph.setSize(250, 250);
		graph.setAxisTitles("Time", "Population");
		graph.setXRange(0, 30);
		this.registerMediaProducer("Plot", graph);
		

		String[] parameters = getInitParam();

		for(String p: parameters) {
			RangePropertyDescriptor d = new RangePropertyDescriptor(p, 0, 500, 100);
			descriptors.put(p, d);
		}

	}

	public void begin() {
		if(gridX == 0 || gridY == 0) {
			JOptionPane.showMessageDialog(null, "Grid dimensions must be positive. Terminating the program.");
			System.exit(0);
		}
		buildModel();
		buildSchedule();
		buildDisplay();

		displaySurface.display();
		graph.display();
	}

	private void buildModel() {
		System.out.println("Running BuildModel");

		rgsSpace = new RabbitsGrassSimulationSpace(gridX, gridY);

		for (int i = 0; i < numRabbits; i++) {
			addNewAgent();
		}
	}


	private void buildSchedule() {
		System.out.println("Running BuildSchedule");
		
		

		class RabbitsGrassSimulationStep extends BasicAction {

			@Override
			public void execute() {
				graph.step();
				rgsSpace.growGrass(grassGrowthRate);

				for (RabbitsGrassSimulationAgent agent : agentList) {
					agent.move();
					agent.eat(grassEnergy);

				}

				reapDeadAgents();
				reproduceAgents();

				displaySurface.updateDisplay();
			}

		}
		schedule.scheduleActionBeginning(0, new RabbitsGrassSimulationStep());

	}

	private void buildDisplay() {
		System.out.println("Running BuildDisplay");

		ColorMap map = new ColorMap();
		map.mapColor(0, Color.black);
		map.mapColor(1, Color.green);

		Value2DDisplay displayGrass = new Value2DDisplay(rgsSpace.getCurrentGrassSpace(), map);

		Object2DDisplay displayAgents = new Object2DDisplay(rgsSpace.getCurrentAgentSpace());
		displayAgents.setObjectList(agentList);
		
		displaySurface.addDisplayableProbeable(displayGrass, "Grass");
		displaySurface.addDisplayableProbeable(displayAgents, "Agents");

		graph.addSequence("Rabbit Population", new RabbitPopulation(), OpenSequenceGraph.FILLED_CIRCLE);
		graph.addSequence("Grass Population", new GrassPopulation(), OpenSequenceGraph.FILLED_CIRCLE);
	}
	
	
	
	private void addNewAgent() {
		RabbitsGrassSimulationAgent a = new RabbitsGrassSimulationAgent(rabbitEnergy);
		if(rgsSpace.addAgent(a)) {
			agentList.add(a);	
		}
	}
	
	private void removeAgent(RabbitsGrassSimulationAgent a) {
		agentList.remove(a);
		rgsSpace.getCurrentAgentSpace().putObjectAt(a.getX(), a.getY(), null);
	}


	
	private void reproduceAgents() {
		ArrayList<RabbitsGrassSimulationAgent> toBeReproduced = new ArrayList<RabbitsGrassSimulationAgent>();
		for(RabbitsGrassSimulationAgent agent : agentList) {
			if (agent.getEnergy() >= birthThreshold) {
				toBeReproduced.add(agent);
			}
		}

		for (RabbitsGrassSimulationAgent a : toBeReproduced) {
			RabbitsGrassSimulationAgent child = new RabbitsGrassSimulationAgent(rabbitEnergy);
			if(rgsSpace.addAgent(child)) {
				agentList.add(child);	
				a.setEnergy(a.getEnergy() / 2);
			}
			

		}

	}

	private void reapDeadAgents() {
		ArrayList<RabbitsGrassSimulationAgent> toBeDeleted = new ArrayList<RabbitsGrassSimulationAgent>();
		for(RabbitsGrassSimulationAgent agent : agentList) {
			if (agent.getEnergy() <= 0) {
				toBeDeleted.add(agent);
			}
		}

		for (RabbitsGrassSimulationAgent a : toBeDeleted) {
			removeAgent(a);
		}

	}
	


	/*******************************************************************************
	 *
	 * 	Getters and Setters
	 *
	 ******************************************************************************/


	public Schedule getSchedule() {
		return schedule;
	}

	public int getGridX() {
		return gridX;
	}

	public void setGridX(int gridX) {
		this.gridX = gridX;
	}

	public int getGridY() {
		return gridY;
	}

	public void setGridY(int gridY) {
		this.gridY = gridY;
	}

	public int getNumRabbits() {
		return numRabbits;
	}

	public void setNumRabbits(int numRabbits) {
		this.numRabbits = numRabbits;
	}

	public int getBirthThreshold() {
		return birthThreshold;
	}

	public void setBirthThreshold(int birthThreshold) {
		this.birthThreshold = birthThreshold;
	}

	public int getGrassGrowthRate() {
		return grassGrowthRate;
	}

	public void setGrassGrowthRate(int grassGrowthRate) {
		this.grassGrowthRate = grassGrowthRate;
	}

	public int getGrassEnergy() {
		return grassEnergy;
	}

	public void setGrassEnergy(int grassEnergy) {
		this.grassEnergy = grassEnergy;
	}

	public int getRabbitEnergy() {
		return rabbitEnergy;
	}

	public void setRabbitEnergy(int rabbitEnergy) {
		this.rabbitEnergy = rabbitEnergy;
	}

}
