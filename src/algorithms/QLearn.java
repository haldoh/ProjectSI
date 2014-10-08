package algorithms;

import java.util.Set;

import concepts.*;

public class QLearn extends Algorithm{
	//Learning rate alpha
	private double alpha;
	//Discount rate
	private double dRate;
	//Agent
	private Agent agent;
	
	/*
	 * Constructor
	 */
	public QLearn(Agent a, double alpha, double dRate){
		this.agent = a;
		this.alpha = alpha;
		this.dRate = dRate;
	}
	//Q-Learning episode
	public int qLearnEp(){
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
			a = qLearnStep(a);
			//Retrieve new state to check if it's terminal
			currState = agent.getEnv().getState();
			cnt++;
		}
		return cnt;
	}
	//Q-Learning step
	public Action qLearnStep(Action a){
		//System.out.println("qLearn step.");
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
			//Select next action with best action-v funct
			Action nextA = this.nextGreedyAction(currS);
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
	private Action nextGreedyAction(State s){
		//Greedy action
		Set<Action> acts = s.getActions();
		Action maxAct = null;
		double maxqFunct = Double.NEGATIVE_INFINITY;
		//Search for the best action-v function
		for(Action a : acts){
			if(s.getqFunct(a) > maxqFunct){
				maxAct = a;
				maxqFunct = s.getqFunct(a);
			}
		}
		return maxAct;
	}
	@Override
	public int execAlg() {
		return this.qLearnEp();
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
