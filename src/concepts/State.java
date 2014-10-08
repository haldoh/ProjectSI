package concepts;

import java.util.*;

/*
 * Class defining aspect and behaviour of States
 */
public abstract class State{
	
	public State(){
		this.resetActions();
		this.resetqFunct();
		this.resetvFunct();
		this.reseteTraces();
	}
	/*
	 * Abstract methods
	 */
	//Get state-value function value for this state
	public abstract double getvFunct();
	//Set state-value function value for this state
	public abstract void setvFunct(double value);
	//Reset state-value function value for this state
	public abstract void resetvFunct();
	//Get action-value function value for this state and action a
	public abstract double getqFunct(Action a);
	//Set action-value function value for this state and action a
	public abstract void setqFunct(Action a, double value);
	//Reset action-value function value for this state and action a
	public abstract void resetqFunct();
	//Get elegibility trace for this state
	public abstract double geteTrace();
	//Set elegibility trace for this state
	public abstract void seteTrace(double eT);
	//Get elegibility trace for this state - state-action version
	public abstract double geteTrace(Action a);
	//Set elegibility trace for this state - state-action version
	public abstract void seteTrace(Action a, double eT);
	//Reset all elegibility traces
	public abstract void reseteTraces();
	//Get list of possible actions from this state
	public abstract Set<Action> getActions();
	//Set list of possible actions from this state
	public abstract void setActions(Set<Action> actions);
	//Reset list of possible actions from this state
	public abstract void resetActions();
}
