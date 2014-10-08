package algorithms;

import concepts.*;

public class Sarsa extends Algorithm{
	//Learning rate alpha
	private double alpha;
	//Discount rate
	private double dRate;
	//Agent
	private Agent agent;
	
	/*
	 * Constructor
	 */
	public Sarsa(Agent a, double alpha, double dRate){
		this.agent = a;
		this.alpha = alpha;
		this.dRate = dRate;
	}
	//Sarsa episode
	public int sarsaEp(){
		//Start from initial state
		agent.getEnv().setState(agent.getEnv().getInitState());
		//Get the initial state
		State currState = agent.getEnv().getState();
		//Select next action
		Action a = agent.getPol().nextAction(currState);
		if(a == null)
			System.out.println("a is null");
		int cnt = 0;
		//Keep going until state is terminal
		while(!agent.getEnv().termState(currState)){
			//Do a step
			a = sarsaStep(a);
			//Retrieve new state to check if it's terminal
			currState = agent.getEnv().getState();
			cnt++;
		}
		return cnt;
	}
	//Sarsa step
	public Action sarsaStep(Action a){
		//System.out.println("Sarsa step.");
		//Take a step following action a
		agent.nextStep(a);
		State oldS = agent.getEnv().getLastState();
		//System.out.println("Old state: " + ((GridState) oldS).getX() + "," + ((GridState) oldS).getY());
		//Do not update actio-v funct for terminal states
		if(!agent.getEnv().termState(oldS)){
			//Save data
			Action oldA = agent.getEnv().getLastAction();
			double oldR = agent.getEnv().getLastRew();
			State currS = agent.getEnv().getState();
			//Ask policy for next action from current state
			Action nextA = agent.getPol().nextAction(currS);
			//Current action-v function for the state before the step and action taken
			double oldqFunct = oldS.getqFunct(oldA);
			//action-v function for the current state and the next action
			double nextqFunct = 0;
			if(!agent.getEnv().termState(currS))
				nextqFunct = currS.getqFunct(nextA);
			//Update action-v funct for state oldS, action oldA
			double newValue = oldqFunct + (this.alpha * (oldR + (this.dRate * nextqFunct) - oldqFunct));
			oldS.setqFunct(oldA, newValue);
			//Return next action
			return nextA;
		} else {
			return null;
		}
	}
	@Override
	public int execAlg() {
		return this.sarsaEp();
	}
	/*
	 * Getter and setter methods
	 */
	public double getAlpha() {
		return alpha;
	}
	public void setAlpha(double alpha) {
		this.alpha = alpha;
	}
	public double getdRate() {
		return dRate;
	}
	public void setdRate(double dRate) {
		this.dRate = dRate;
	}
	public Agent getAgent() {
		return agent;
	}
	public void setAgent(Agent agent) {
		this.agent = agent;
	}
}
