package algorithms;

import java.util.*;

import concepts.*;

public class ValueIteration extends Algorithm{
	//Agent
	private Agent agent;
	//Discount rate
	private double dRate;
	//Threshold for evaluation step
	private double theta;
	
	/*
	 * Constructor
	 */
	public ValueIteration(Agent a, double dr, double t){
		this.setAgent(a);
		this.setdRate(dr);
		this.setTheta(t);
	}
	//Value iteration
	private int vFunctImpr(){
		int numIter = 0;
		//Init delta
		double delta = this.theta + 1;
		//Repeat until delta is lesser then theta
		while(delta >= this.theta){
			numIter++;
			//Reset delta
			delta = 0;
			Set<State> allStates = this.agent.getEnv().getStates();
			//Go through all the states
			for(State s : allStates){
				//Set state in environment
				agent.getEnv().setState(s);
				double oldV = s.getvFunct();
				double newV = Double.NEGATIVE_INFINITY;
				Set<Action> allActions = s.getActions();
				//Go through the possible actions for the state
				for(Action a : allActions){
					double tempV = 0;
					//All possible next states given action act and their probabilities
					Map<? extends State, Double> allNextStates = agent.getEnv().allNextStates(a);
					//If result is null, s is a terminal state
					if(allNextStates != null){
						//Key for iteration on map
						Set<? extends State> allNSKeys = allNextStates.keySet();
						for(State s1: allNSKeys){
							//Probability of this state following action act
							double probS1 = allNextStates.get(s1);
							//Reward for going in this state
							double rewS1 = this.agent.getEnv().getReward(a, s1);
							//State-v funct of state s1
							double vFunctS1 = s1.getvFunct();
							//Add value to the new state-v funct
							tempV += probS1 * (rewS1 + (dRate * vFunctS1));
						}
					}
					//Save the maximum new state-value function
					if(tempV > newV)
						newV = tempV;
				}
				//Save new value
				s.setvFunct(newV);
				//Compute difference between old and new value
				double dV = Math.abs((oldV - newV));
				//Update delta
				if(dV >= delta)
					delta = dV;
			}
		}
		return numIter;
	}
	//policiy definition
	private void defPolicy(){
		Set<State> allStates = this.agent.getEnv().getStates();
		//Go through all states
		for(State s : allStates){
			//Set state in environment
			agent.getEnv().setState(s);
			Action tempAct = null;
			double newV = Double.NEGATIVE_INFINITY;
			Set<Action> allActions = s.getActions();
			//Go through the possible actions for the state
			for(Action a : allActions){
				double tempV = 0;
				//All possible next states given action act and their probabilities
				Map<? extends State, Double> allNextStates = agent.getEnv().allNextStates(a);
				//If result is null, s is a terminal state
				if(allNextStates != null){
					//Key for iteration on map
					Set<? extends State> allNSKeys = allNextStates.keySet();
					for(State s1: allNSKeys){
						//Probability of this state following action act
						double probS1 = allNextStates.get(s1);
						//Reward for going in this state
						double rewS1 = this.agent.getEnv().getReward(a, s1);
						//State-v funct of state s1
						double vFunctS1 = s1.getvFunct();
						//Add value to the new state-v funct
						tempV += probS1 * (rewS1 + (dRate * vFunctS1));
					}
				}
				//Save the maximum new state-value function
				if(tempV > newV){
					newV = tempV;
					tempAct = a;
				}
			}
			this.agent.getPol().changePolicy(s, tempAct);
		}
	}
	public int runVI(){
		//compute state-value function for all possible states
		int numIter = this.vFunctImpr();
		//define policy using the computed state-value function
		this.defPolicy();
		return numIter;
	}
	@Override
	public int execAlg() {
		return this.runVI();
	}
	/*
	 * Getter and setter methods
	 */
	public Agent getAgent() {
		return this.agent;
	}
	public void setAgent(Agent agent) {
		this.agent = agent;
	}
	public double getdRate() {
		return this.dRate;
	}
	public void setdRate(double dRate) {
		this.dRate = dRate;
	}
	public double getTheta() {
		return this.theta;
	}
	public void setTheta(double theta) {
		this.theta = theta;
	}

}
