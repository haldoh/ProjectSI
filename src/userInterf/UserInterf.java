package userInterf;

import gridworld.GridAction;
import gridworld.GridAgent;
import gridworld.GridEnvironment;
import gridworld.GridPolicyDet;
import gridworld.GridPolicyEGreedy;
import gridworld.GridState;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Set;

import algorithms.Algorithm;
import algorithms.PolicyIteration;
import algorithms.QLearn;
import algorithms.QLearnL;
import algorithms.Sarsa;
import algorithms.SarsaL;
import algorithms.ValueIteration;
import concepts.Action;
import concepts.Policy;

public class UserInterf {

	public static void main(String[] args){
		//Environment
		GridEnvironment gridEnv = null;
		//Agent
		GridAgent agent = null;
		//Policy
		Policy pol = null;
		//Algorithm
		Algorithm alg = null;
		//Reading buffer
		BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
		/*
		 * Step 1: select algorithm
		 */
		int selAlg = -1;
		//Print options menu
		System.out.println();
		System.out.println("Select algorithm:");
		System.out.println("1- Policy Iteration");
		System.out.println("2- Value Iteration");
		System.out.println("3- Sarsa");
		System.out.println("4- Q-learning");
		System.out.println("5- Sarsa-lambda");
		System.out.println("6- Q-learning-lambda");
		System.out.println();
		System.out.print("Enter selection:");
		//Read input
		try {selAlg = Integer.parseInt(inFromUser.readLine());}
		catch (IOException e) {e.printStackTrace();}
		/*
		 * Step 2: load data file
		 */
		String inFileName = "input.txt";
		String tmpFile = "";
		System.out.println();
		System.out.println("Input file name [" + inFileName + "]: ");
		//Read input
		try {tmpFile = inFromUser.readLine();}
		catch (IOException e) {e.printStackTrace();}
		inFileName = (!tmpFile.equals("")) ? tmpFile : inFileName;
		/*
		 * Step 3: prepare for execution
		 */
		System.out.println("Opening file...");
		Parser parser = new Parser(inFileName);
		/*
		 * First line of file contains parameters for the algorithm;
		 * The first number is the discount rate, then there is learning
		 * rate alpha or threshold theta (depending on algorithm).
		 * then there are trace-decay parameter lambda and epsilon
		 * value for e-greedy policies (where needed, use zero for algorithms not using these)
		 */
		System.out.println("Reading data...");
		String[] params = parser.readLine();
		double dRate = Double.parseDouble(params[0]);
		double alphaTetha = Double.parseDouble(params[1]);
		double lambda = Double.parseDouble(params[2]);
		double epsilon = Double.parseDouble(params[3]);
		//Second line of file contains grid dimension
		String[] dims = parser.readLine();
		int x = Integer.parseInt(dims[0]);
		int y = Integer.parseInt(dims[1]);
		//Third line enables or disables king's moves
		String[] moves = parser.readLine();
		boolean kingMoves = false;
		if(moves[0].trim().equals("1"))
			kingMoves = true;
		//System.out.println(kingMoves);
		System.out.println("Creating grid...");
		gridEnv = new GridEnvironment(x, y, kingMoves);
		//Fourth line contains starting point
		String[] start = parser.readLine();
		int stX = Integer.parseInt(start[0]);
		int stY = Integer.parseInt(start[1]);
		gridEnv.setInitState(gridEnv.getState(stX, stY));
		//Fifth line contains ending points
		String[] end = parser.readLine();
		int endNum = end.length/2;
		for(int i = 0; i < endNum; i ++){
			int endX = Integer.parseInt(end[2*i]);
			int endY = Integer.parseInt(end[2*i+1]);
			gridEnv.addTermState(gridEnv.getState(endX, endY));
		}
		//From the sixth line on, coordinates of walls
		String[] line = parser.readLine();
		System.out.println("Adding walls...");
		while(line != null){
			//The first two numbers are the coordinates of the first cell
			int x1 = Integer.parseInt(line[0]);
			int y1 = Integer.parseInt(line[1]);
			//The third element of the line is the position of the wall
			String pos = line[2];
			//Put a wall in the gridworld
			gridEnv.setWall(x1, y1, pos);
			//Read another line of the file
			line = parser.readLine();
		}
		//Input reading complete, close parser and release input file
		parser.close();
		/*System.out.println(dRate);
		System.out.println(alphaTetha);
		System.out.println(lambda);
		System.out.println(epsilon);
		printAll(gridEnv);*/
		//Instantiate policy, agent and algorithm
		System.out.println("Instantiating objects...");
		String outName = "";
		switch(selAlg){
		case 1:
			//Policy Iteration
			pol = new GridPolicyDet();
			agent = new GridAgent(gridEnv, pol);
			alg = new PolicyIteration(agent, dRate, alphaTetha);
			outName = "PI";
			break;
		case 2:
			//Value Iteration
			pol = new GridPolicyDet();
			agent = new GridAgent(gridEnv, pol);
			alg = new ValueIteration(agent, dRate, alphaTetha);
			outName = "VI";
			break;
		case 3:
			//Sarsa
			pol = new GridPolicyEGreedy(epsilon);
			agent = new GridAgent(gridEnv, pol);
			alg = new Sarsa(agent, alphaTetha, dRate);
			outName = "Sarsa";
			break;
		case 4:
			//Q-learning
			pol = new GridPolicyEGreedy(epsilon);
			agent = new GridAgent(gridEnv, pol);
			alg = new QLearn(agent, alphaTetha, dRate);
			outName = "QLearn";
			break;
		case 5:
			//Sarsa-lambda
			pol = new GridPolicyEGreedy(epsilon);
			agent = new GridAgent(gridEnv, pol);
			alg = new SarsaL(agent, alphaTetha, dRate, lambda);
			outName = "SarsaL";
			break;
		case 6:
			//Q-learning-lambda
			pol = new GridPolicyEGreedy(epsilon);
			agent = new GridAgent(gridEnv, pol);
			alg = new QLearnL(agent, alphaTetha, dRate, lambda);
			outName = "QLearnL";
			break;
		default:
			//Something's wrong...
			System.out.println("Input error.");
			break;
		}
		System.out.println();
		System.out.println("Environment creation completed.");
		/*
		 * Step 4: option based on algorithm selected
		 */
		//If algorithm is not PI or VI, ask user how many iteration
		int numIt = 1000;
		if(selAlg > 2){
			System.out.println();
			System.out.println("Number of iterations[" + numIt + "]: ");
			String tmpIt = "";
			try {tmpIt = inFromUser.readLine();}
			catch (IOException e) {e.printStackTrace();}
			numIt = (!tmpIt.equals("")) ? Integer.parseInt(tmpIt) : numIt;
		}
		/*
		 * Step 5: Run algorithm and save results
		 */
		System.out.println();
		System.out.println("Running algorithm...");
		//Prepare output
		java.util.Date date= new java.util.Date();
		long timestamp = date.getTime()/1000; 
		//If iteration algorithm, just run
		if(selAlg <=2){
			alg.execAlg();
		}
		//If TD algorithm, run for the selected number of iterations
		else{
			for(int i = 0; i < numIt; i++){
				System.out.println("Iteration: " + i);
				alg.execAlg();
			}
		}
		System.out.println("Execution completed.");
		//Read input
		/*
		 * Step 5: run tests, or exit
		 */
		int selOpt = -1;
		while(selOpt != 0){
			System.out.println();
			System.out.println("Select an option:");
			System.out.println("1- Print policy and walls to file");
			System.out.println("2- Print value-function values to file");
			System.out.println("3- Run a test");
			System.out.println("0- Exit");
			//Read input
			String tmpSel = "";
			try {tmpSel = inFromUser.readLine();}
			catch (IOException e) {e.printStackTrace();}
			selOpt = (!tmpSel.equals("")) ? Integer.parseInt(tmpSel) : -1;
			switch(selOpt){
				case 1:
					//Print policy and walls on file
					Writer writerPol = new Writer(timestamp + "-" + outName + "-policy-out.txt", true);
					writerPol.write("Policy computed from input file " + inFileName + ":\n");
					//Remove randomness
					if(selAlg >=3)
						((GridPolicyEGreedy)(agent.getPol())).setEpsilon(0);
					writerPol.write("Policy:\n");
					//Print policy
					for(int i = gridEnv.getDimY()-1; i >= 0; i--){
						for(int j = 0; j < gridEnv.getDimX(); j++){
							GridState s = gridEnv.getState(j, i);
							String tmp = ((GridAction)(pol.nextAction(s))).getName();
							//tmp = tmp.length() == 1 ? "¦ " + tmp + "¦": "¦" + tmp + "¦";
							writerPol.write(tmp+";");
						}
						writerPol.write("\n");
					}
					writerPol.write("\n");
					writerPol.write("Walls:\n");
					//Print walls
					for(int i = gridEnv.getDimY()-1; i >= 0; i--){
						for(int j = 0; j < gridEnv.getDimX(); j++){
							GridState s = gridEnv.getState(j, i);
							String tmp = "x";
							tmp = (s.getX() == s.getNeighState("N").getX() && s.getY() == s.getNeighState("N").getY()) ? tmp+"N" : tmp+"";
							tmp = (s.getX() == s.getNeighState("S").getX() && s.getY() == s.getNeighState("S").getY()) ? tmp+"S" : tmp+"";
							tmp = (s.getX() == s.getNeighState("E").getX() && s.getY() == s.getNeighState("E").getY()) ? tmp+"E" : tmp+"";
							tmp = (s.getX() == s.getNeighState("W").getX() && s.getY() == s.getNeighState("W").getY()) ? tmp+"W" : tmp+"";
							writerPol.write(tmp+";");
						}
						writerPol.write("\n");
					}
					writerPol.close();
					//Restore randomness
					if(selAlg >=3)
						((GridPolicyEGreedy)(agent.getPol())).setEpsilon(epsilon);
					break;
				case 2:
					//Print value function on file
					Writer writerVal = new Writer(timestamp + "-" + outName + "-value-funct-out.txt", true);
					writerVal.write("Value function computed from input file " + inFileName + ":\n");
					if(selAlg <= 2){
						//Print to file state-value function obtained
						int dimX = gridEnv.getDimX();
						int dimY = gridEnv.getDimY();
						for(int i = 0; i < dimX; i++){
							for(int j = 0; j < dimY; j++){
								GridState s1 = gridEnv.getState(i, j);
								writerVal.write("State " + s1.getX() + "," + s1.getY());
								if(gridEnv.termState(s1))
									writerVal.write(" (term): ");
								else
									writerVal.write(" (non-term): ");
								writerVal.write(s1.getvFunct() + "\n");
							}
						}
					} else {
						//Print to file action-value function obtained
						int dimX = gridEnv.getDimX();
						int dimY = gridEnv.getDimY();
						for(int i = 0; i < dimX; i++){
							for(int j = 0; j < dimY; j++){
								GridState s1 = gridEnv.getState(i, j);
								writerVal.write("State " + s1.getX() + "," + s1.getY());
								if(gridEnv.termState(s1))
									writerVal.write(" (term):\n");
								else
									writerVal.write(" (non-term):\n");
								Set<? extends Action> actions = s1.getActions();
								for(Action a : actions){
									GridAction a1 = (GridAction)a;
									writerVal.write("	Action-value function for action " + a1.getName() + ": " + s1.getqFunct(a1) + "\n");
								}
							}
						}	
					}
					writerVal.close();
					break;
				case 3:
					//Run test
					int numTests = 10;
					System.out.println();
					System.out.println("Number of tests [" + numTests + "]: ");
					//Read input
					String tmpNumTests = "";
					try {tmpNumTests = inFromUser.readLine();}
					catch (IOException e) {e.printStackTrace();}
					numTests = (!tmpNumTests.equals("")) ? Integer.parseInt(tmpNumTests) : numTests;
					//Prepare output
					Writer writerTest = new Writer(timestamp + "-" + outName + "-test-out.txt", true);
					writerTest.write("Test of policy computed from input file " + inFileName + " using the " + outName + " algorithm:\n");
					//Remove randomness
					//((GridPolicyEGreedy)(agent.getPol())).setEpsilon(0);
					double avg = 0;
					//Run the tests
					for(int i = 0; i < numTests; i++){
						writerTest.write("Test " + i + ": ");
						//Reset environment
						gridEnv.setState(gridEnv.getInitState());
						//writerTest.write("	State: (" + ((GridState) gridEnv.getState()).getX() + "," + ((GridState) gridEnv.getState()).getY() + ")\n");
						//Search for a terminal state
						int cnt = 0;
						while(!gridEnv.termState(gridEnv.getState())){
							agent.nextStep();
							//writerTest.write("	State: (" + ((GridState) gridEnv.getState()).getX() + "," + ((GridState) gridEnv.getState()).getY() + ")\n");
							cnt++;
						}
						writerTest.write("Solution obtained in " + cnt + " steps.\n\n");
						avg += cnt;
					}
					avg = avg / numTests;
					writerTest.write("After " + numTests + " runs, solution obtained averagely in " + avg + " steps.\n\n");
					writerTest.close();
					//Restore randomness
					//((GridPolicyEGreedy)(agent.getPol())).setEpsilon(epsilon);
					break;
				case 0:
					//Exit
					System.out.println("Closing...");
					break;
				default:
					System.out.println("Invalid option.");
					break;
			}
		}
	}
	public static void printAll(GridEnvironment gridEnv){
		for(int i = 0; i < gridEnv.getDimX(); i++){
			for(int j = 0; j < gridEnv.getDimY(); j++){
				GridState s1 = gridEnv.getState(i, j);
				System.out.println("State: " + s1.getX() + "," + s1.getY());
				System.out.println("	Neigh. N: " + s1.getNeighState("N").getX() + "," + s1.getNeighState("N").getY());
				System.out.println("	Neigh. E: " + s1.getNeighState("E").getX() + "," + s1.getNeighState("E").getY());
				System.out.println("	Neigh. W: " + s1.getNeighState("W").getX() + "," + s1.getNeighState("W").getY());
				System.out.println("	Neigh. S: " + s1.getNeighState("S").getX() + "," + s1.getNeighState("S").getY());
				if(gridEnv.useKingMoves()){
					System.out.println("	Neigh. NW: " + s1.getNeighState("NW").getX() + "," + s1.getNeighState("NW").getY());
					System.out.println("	Neigh. NE: " + s1.getNeighState("NE").getX() + "," + s1.getNeighState("NE").getY());
					System.out.println("	Neigh. SW: " + s1.getNeighState("SW").getX() + "," + s1.getNeighState("SW").getY());
					System.out.println("	Neigh. SE: " + s1.getNeighState("SE").getX() + "," + s1.getNeighState("SE").getY());
				}
			}
		}
	}
}
