package userInterf;

import java.util.Set;

import gridworld.GridAgent;
import gridworld.GridEnvironment;
import gridworld.GridPolicyDet;
import gridworld.GridPolicyEGreedy;
import algorithms.Algorithm;
import algorithms.PolicyIteration;
import algorithms.QLearn;
import algorithms.QLearnL;
import algorithms.Sarsa;
import algorithms.SarsaL;
import algorithms.ValueIteration;
import concepts.Policy;
import concepts.State;

public class RunTest {
	
	public static void main(String[] args) {
		String inFileName = "input.txt";
		int numIt = 2000;
		double dRate = 0.9;
		double salpha = 0.2;
		double qalpha = 0.8;
		double tetha = 0.01;
		double lambda = 0.8;
		double epsilon = 0.1;
		boolean decrFlag = false;
		double epsilonEnd = 0.01;
		double decrement = (epsilon - epsilonEnd) / numIt;
		//Environment
		GridEnvironment gridEnv = null;
		//Agent
		GridAgent agent = null;
		//Policy
		Policy pol = null;
		//Algorithm
		Algorithm alg = null;
		Parser parser = new Parser(inFileName);
		/*
		 * First line of file contains parameters for the algorithm;
		 * The first number is the discount rate, then there is learning
		 * rate alpha or threshold theta (depending on algorithm).
		 * then there are trace-decay parameter lambda and epsilon
		 * value for e-greedy policies (where needed, use zero for algorithms not using these)
		 */
		System.out.println((new java.util.Date().toString()) + " - Reading data...");
		/*String[] params = */parser.readLine();
		//double dRate = Double.parseDouble(params[0]);
		//double alphaTetha = Double.parseDouble(params[1]);
		//double lambda = Double.parseDouble(params[2]);
		//double epsilon = Double.parseDouble(params[3]);
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
		System.out.println((new java.util.Date().toString()) + " - Creating grid...");
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
		System.out.println((new java.util.Date().toString()) + " - Adding walls...");
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
		//Prepare output
		String outName = "";
		java.util.Date date= new java.util.Date();
		long timestamp = date.getTime()/1000;
		Writer writerTest = new Writer(timestamp + "-" + "total-test-out.txt", true);
		writerTest.write("Test of algorithms on input file " + inFileName + ".\n");
		writerTest.write("Number of iterations for TD algorithms: " + numIt + "\n");
		writerTest.write("Discount rate: " + dRate + "\n");
		writerTest.write("Threshold for iteration algorithms: " + tetha + "\n");
		writerTest.write("Learning rate for sarsa: " + salpha + "\n");
		writerTest.write("Learning rate for q-learning: " + qalpha + "\n");
		writerTest.write("Trace decay:" + lambda + "\n");
		writerTest.write("Epsilon value for e-greedy policy: " + epsilon + "\n");
		for(int selAlg = 6; selAlg <= 6; selAlg++){
			//Reset states
			Set<State> states = gridEnv.getStates();
			for(State s : states){
				s.reseteTraces();
				s.resetqFunct();
				s.resetvFunct();
			}
			//Reset environment to initial state
			gridEnv.setState(gridEnv.getInitState());
			switch(selAlg){
			case 1:
				//Policy Iteration
				pol = new GridPolicyDet();
				agent = new GridAgent(gridEnv, pol);
				alg = new PolicyIteration(agent, dRate, tetha);
				outName = "PI";
				break;
			case 2:
				//Value Iteration
				pol = new GridPolicyDet();
				agent = new GridAgent(gridEnv, pol);
				alg = new ValueIteration(agent, dRate, tetha);
				outName = "VI";
				break;
			case 3:
				//Sarsa
				pol = new GridPolicyEGreedy(epsilon);
				agent = new GridAgent(gridEnv, pol);
				alg = new Sarsa(agent, salpha, dRate);
				outName = "Sarsa";
				break;
			case 4:
				//Q-learning
				pol = new GridPolicyEGreedy(epsilon);
				agent = new GridAgent(gridEnv, pol);
				alg = new QLearn(agent, qalpha, dRate);
				outName = "QLearn";
				break;
			case 5:
				//Sarsa-lambda
				pol = new GridPolicyEGreedy(epsilon);
				agent = new GridAgent(gridEnv, pol);
				alg = new SarsaL(agent, salpha, dRate, lambda);
				outName = "SarsaL";
				break;
			case 6:
				//Q-learning-lambda
				pol = new GridPolicyEGreedy(epsilon);
				agent = new GridAgent(gridEnv, pol);
				alg = new QLearnL(agent, qalpha, dRate, lambda);
				outName = "QLearnL";
				break;
			default:
				//Something's wrong...
				System.out.println((new java.util.Date().toString()) + " - Input error.");
				break;
			}
			System.out.println((new java.util.Date().toString()) + " - Running " + outName + "...");
			//If iteration algorithm, just run
			if(selAlg <=2){
				writerTest.write("Execution with algorithm: " + outName + ".\n");
				int tmp = alg.execAlg();
				writerTest.write("	Convergence reached after " + tmp + " iterations of the algorithm.\n");
			}
			//If TD algorithm, run for the selected number of iterations
			else{
				writerTest.write("Execution with algorithm: " + outName + ".\n");
				for(int i = 0; i < numIt; i++){
					int tmp = alg.execAlg();
					writerTest.write("	" + i + "	" + tmp + "\n");
					if(decrFlag){
						((GridPolicyEGreedy) agent.getPol()).setEpsilon(((GridPolicyEGreedy) agent.getPol()).getEpsilon() - decrement);
					}
				}
			}
		}
		System.out.println((new java.util.Date().toString()) + " - Test completed.");
		writerTest.close();
	}
}
