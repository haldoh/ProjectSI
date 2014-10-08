package concepts;

/*
 * Class defining aspect and behaviour of Policies
 */
public abstract class Policy {
	/*
	 * Abstract methods - to be implemented by subclasses
	 */
	//Gives the next action from state s
	public abstract Action nextAction(State s);
	//Change policy - deterministic version
	public abstract void changePolicy(State s, Action a);
	//Reset to default (if any), or do nothing (depends on type of policy)
	public abstract void reset();
}
