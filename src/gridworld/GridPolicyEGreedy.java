package gridworld;

import java.util.*;

import concepts.*;

public class GridPolicyEGreedy extends Policy{
	private double epsilon;
	/*
	 * Constructor
	 */
	public GridPolicyEGreedy(double e){
		this.reset();
		this.setEpsilon(e);
	}
	
	
	@Override
	public Action nextAction(State s) {
		//Random number to decide if using random action
		double rand = (new Random()).nextDouble();
		Set<Action> actions = s.getActions();
		if(rand < this.epsilon){
			//Random action
			Object[] actArr = actions.toArray();
			int actSize = actions.size();
			int rand2 = (int) Math.floor(((new Random()).nextDouble()) * actSize);
			return (Action)actArr[rand2];
		} else {
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
	}

	@Override
	public void changePolicy(State s, Action a) {
	}

	@Override
	public void reset() {
	}

	//Getter and setter methods
	public double getEpsilon() {
		return epsilon;
	}
	public void setEpsilon(double epsilon) {
		this.epsilon = epsilon;
	}
}
