package algorithms;

import java.util.Set;

import concepts.*;

public class QLearnL extends Algorithm{
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
	public QLearnL(Agent a, double al, double dR, double l){
		this.agent = a;
		this.alpha = al;
		this.dRate = dR;
		this.lambda = l;
	}
	//Q-Learning episode
	public int qLearnEp(){
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
		//Do not update action-v funct for terminal states
		if(!agent.getEnv().termState(oldS)){
			//Save data
			Action oldA = agent.getEnv().getLastAction();
			double oldR = agent.getEnv().getLastRew();
			State currS = agent.getEnv().getState();
			//Select next action using policy
			Action returnAct = agent.getPol().nextAction(currS);
			//Select next action with best action-v funct
			Action nextA = this.nextGreedyAction(currS);
			//Check if the two actions selected tie for the max action-v funct value
			boolean tie = false;
			if(currS.getqFunct(returnAct) == currS.getqFunct(nextA)){
				tie = true;
				nextA = returnAct;
			}
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
						if(tie)
							st.seteTrace(upA, (this.dRate * this.lambda * st.geteTrace(upA)));
						else
							st.seteTrace(upA, 0);
					}
				}
			}
			//Return next action
			return returnAct;
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
	public double getLambda(){
		return this.lambda;
	}
	public void setLambda(double l){
		this.lambda = l;
	}
}
