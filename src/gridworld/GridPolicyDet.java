package gridworld;

import java.util.*;

import concepts.*;

public class GridPolicyDet extends Policy{
	//Map between states and actions
	HashMap<GridState, GridAction> policy = new HashMap<GridState, GridAction>();
	
	/*
	 * Constructor
	 */
	public GridPolicyDet(){
		this.reset();
	}
	
	
	@Override
	public Action nextAction(State s) {
		//The cast is safe since a GridPolicy should only get GridStates and GridActions
		if(this.policy.containsKey(s)){
			return this.policy.get(s);
		}else{
			Set<? extends Action> actions = s.getActions();
			for(Action a : actions){
				this.policy.put((GridState)s, (GridAction)a);
				return a;
			}
		}
		return null;
	}

	@Override
	public void changePolicy(State s, Action a) {
		//The cast is safe since a GridPolicy should only get GridStates and GridActions
		this.policy.put((GridState)s, (GridAction)a);
	}

	@Override
	public void reset() {
		this.policy = new HashMap<GridState, GridAction>();
	}
}
