import java.awt.Color;
import java.util.ArrayList;

import uchicago.src.sim.engine.BasicAction;
import uchicago.src.sim.engine.Schedule;
import uchicago.src.sim.engine.SimInit;
import uchicago.src.sim.engine.SimModelImpl;
import uchicago.src.sim.gui.ColorMap;
import uchicago.src.sim.gui.DisplaySurface;
import uchicago.src.sim.gui.Displayable;
import uchicago.src.sim.gui.Object2DDisplay;
import uchicago.src.sim.gui.SimGraphics;
import uchicago.src.sim.gui.Value2DDisplay;
import uchicago.src.sim.space.Discrete2DSpace;

/**
 * Class that implements the simulation model for the rabbits grass
 * simulation.  This is the first class which needs to be setup in
 * order to run Repast simulation. It manages the entire RePast
 * environment and the simulation.
 *
 * @author 
 */


public class RabbitsGrassSimulationModel extends SimModelImpl {	
	
		/*
		 * Initial values.
		 */
		private static final int GRIDX = 20;
		private static final int GRIDY = 20;
		private static final int NUMRABBITS = 20;
		private static final int BIRTHTHRESHOLD = 20;
		private static final int GRASSGROWTHRATE = 20;
		private static final int RABBITENERGY = 20;
		private static final int GRASSENERGY = 20;
		private static final int NUMGRASS = 20;
		
		
		private int gridX = GRIDX;
		private int gridY = GRIDY;
		private int numRabbits = NUMRABBITS;
		private int numGrass = NUMGRASS;
		private int birthThreshold = BIRTHTHRESHOLD;
		private int grassGrowthRate = GRASSGROWTHRATE;
		private int rabbitEnergy = RABBITENERGY;
		private int grassEnergy = GRASSENERGY;
		
		private Schedule schedule;
		
		private RabbitsGrassSimulationSpace rgsSpace;
		
		private ArrayList<RabbitsGrassSimulationAgent> agentList; 
		
		private DisplaySurface displaySurface;
		
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
			
			if(displaySurface != null) {
				displaySurface.dispose();
			}
			displaySurface = new DisplaySurface(this, "Model Window");
			registerDisplaySurface("Model Window", displaySurface);	
		}

		public void begin() {	
			buildModel();
		 	buildSchedule();
		    buildDisplay();
		    
		    displaySurface.display();
		}

		private void buildModel() {
			System.out.println("Running BuildModel");
			
			rgsSpace = new RabbitsGrassSimulationSpace(gridX, gridY);
			
			rgsSpace.growGrass(numGrass);
			
			for(int i = 0; i < numRabbits;i++) {
				addNewAgent();
			}
		}
		
		private void addNewAgent() {
			// TODO Auto-generated method stub
			RabbitsGrassSimulationAgent a = new RabbitsGrassSimulationAgent(rabbitEnergy);
			agentList.add(a);
			rgsSpace.addAgent(a);
		}

		private void buildSchedule() {
			System.out.println("Running BuildSchedule");
			
			
			
			class RabbitsGrassSimulationStep extends BasicAction{

				@Override
				public void execute() {
					
					rgsSpace.growGrass(grassGrowthRate);
					ArrayList<RabbitsGrassSimulationAgent> toBeDeleted = new ArrayList<RabbitsGrassSimulationAgent>();
					ArrayList<RabbitsGrassSimulationAgent> toBeReproduced = new ArrayList<RabbitsGrassSimulationAgent>();
						
					for(int i = 0; i < agentList.size(); i++) {
						RabbitsGrassSimulationAgent rgsAgent = agentList.get(i);
						rgsAgent.step(grassEnergy);
						
						if(rgsAgent.getEnergy() <= 0) {
							
							toBeDeleted.add(rgsAgent);
						}
						

					}
					
					for(RabbitsGrassSimulationAgent a : toBeDeleted) {
						agentList.remove(a);
						rgsSpace.getCurrentAgentSpace().putObjectAt(a.getX(), a.getY(), null);
					}
					
					
					
					
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
		}




		public String[] getInitParam() {
			return new String[] {"GridX", "GridY", "NumRabbits", "NumGrass", "BirthThreshold", "GrassGrowthRate", "RabbitEnergy", "GrassEnergy"};
		}



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

		public int getNumGrass() {
			return numGrass;
		}

		public void setNumGrass(int numGrass) {
			this.numGrass = numGrass;
		}
		
		public int getRabbitEnergy() {
			return rabbitEnergy;
		}

		public void setRabbitEnergy(int rabbitEnergy) {
			this.rabbitEnergy = rabbitEnergy;
		}
		
		


		

}
