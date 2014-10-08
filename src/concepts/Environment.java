package concepts;

import java.util.Map;
import java.util.Random;
import java.util.Set;

/*
 * Class defining aspect and behaviour of Environments
 */
public abstract class Environment {
	/*
	 * Abstract methods - to be implemented by subclasses
	 */
	//Returns a map containing all possible next states and probabilities given action act
	public abstract Map<State, Double> allNextStates(Action act);
	//Give reward for going from actual state to state s1 after action act
	public abstract double getReward(Action act, State s1);
	//True if s is a terminal state
	public abstract boolean termState(State s);
	//Add a terminal state
	public abstract void addTermState(State s);
	//Remove a terminal state
	public abstract void removeTermState(State s);
	//Set the initial state
	public abstract void setInitState(State s);
	//Get the initial state
	public abstract State getInitState();
	//Get current state
	public abstract State getState();
	//Set current state
	public abstract void setState(State s);
	//Retrieve a Set of all possible states
	public abstract Set<State> getStates();
	//Retrieve a set of all possible actions
	public abstract Set<Action> getActions();
	//Retrieve last state
	public abstract State getLastState();
	//Save last state
	protected abstract void setLastState(State s);
	//Retrieve last action
	public abstract Action getLastAction();
	//Save last action
	protected abstract void setLastAction(Action a);
	//Retrieve last reward
	public abstract double getLastRew();
	//Save last reward
	protected abstract void setLastReward(double rew);
	//Reset elegibility traces
	public abstract void resetETraces();
	/*
	 * Update environment with next state and return a reward
	 */
	//For consistency, this should use the methods allNextActs and getReward
	public double nextState(Action a){
		//Get all possible next States
		Map<State, Double> nextSs = this.allNextStates(a);
		if(nextSs == null){
			//If result is null, the env is in a terminal state
			return this.getReward(a, null);
		} else if(nextSs.size() == 1) {
			//If size is 1, there is only one next state
			Set<State> sKeys = nextSs.keySet();
			for(State s : sKeys){
				//Get reward before changing state
				double rew = this.getReward(a, s);
				//Save last state, action, reward
				this.setLastState(this.getState());
				this.setLastAction(a);
				this.setLastReward(rew);
				//Set new state
				this.setState(s);
				//And return reward
				return rew;
			}
		} else{
			//There is more than one possible next state - randomly select one based on probabilities
			double rand = (new Random()).nextDouble();
			double cumulatDist = 0;
			Set<State> sKeys = nextSs.keySet();
			for(State s : sKeys){
				//Cumulative distribution for state s
				cumulatDist += nextSs.get(s);
				//If cumulative distribution is greater than the random number, this is the next state
				if(rand <= cumulatDist){
					//Get reward before changing state
					double rew = this.getReward(a, s);
					//Save last state, action, reward
					this.setLastState(s);
					this.setLastAction(a);
					this.setLastReward(rew);
					//Set new state
					this.setState(s);
					//And return reward
					return rew;
				}
			}
		}
		return this.getReward(a, null);
	}
}
