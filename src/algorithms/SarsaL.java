package algorithms;

import concepts.*;
import java.util.*;

public class SarsaL extends Algorithm{
	//Learning rate alpha
	private double alpha;
	//Discount rate
	private double dRate;
	//trace-decay parameter
	private double lambda;
	//Agent
	private Agent agent;
	//Parameter to avoid excessive computation
	private double threshold = 0.0001;
	
	/*
	 * Constructor
	 */
	public SarsaL(Agent a, double al, double dR, double l){
		this.agent = a;
		this.alpha = al;
		this.dRate = dR;
		this.lambda = l;
	}
	//Sarsa episode
	public int sarsaEp(){
		//Start from initial state
		agent.getEnv().setState(agent.getEnv().getInitState());
		//Reset elegibility traces
		agent.getEnv().resetETraces();
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
		//Do not update action-v funct for terminal states
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
			double delta = (oldR + (this.dRate * nextqFunct) - oldqFunct);
			//Update elegibility trace
			oldS.seteTrace(oldA, oldS.geteTrace(oldA)+1);
			//Update all action-v functions
			Set<State> allStates = agent.getEnv().getStates();
			for(State st : allStates){
				Set<Action> allActions = st.getActions();
				for(Action upA : allActions){
					if(st.geteTrace(upA) >= this.threshold){
						//Update value
						st.setqFunct(upA, (st.getqFunct(upA) + (this.alpha * delta * st.geteTrace(upA))));
						//Update elegibility trace
						st.seteTrace(upA, (this.dRate * this.lambda * st.geteTrace(upA)));
					}
				}
			}
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
	public double getLambda(){
		return this.lambda;
	}
	public void setLambda(double l){
		this.lambda = l;
	}
}
