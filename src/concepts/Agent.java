package concepts;

/*
 * Class defining aspect and behaviour of Agents
 */
public abstract class Agent {
	//Agent environment
	private Environment env;
	//Agent policy
	private Policy pol;
	
	public Agent(Environment e, Policy p){
		this.env = e;
		this.pol = p;
	}
	//Make the agent go to the next state
	public double nextStep(){
		//Get current state from environment
		State s = this.getEnv().getState();
		//Get next action from policy
		Action a = this.getPol().nextAction(s);
		//Use action a on environment, making it change state
		//Then return the reward obtained
		return this.getEnv().nextState(a);
	}
	//Make agent go to the next state given an action a
	//Useful for certain algorithms
	public double nextStep(Action a){
		//Use action a on environment, making it change state
		//Then return the reward obtained
		return this.getEnv().nextState(a);
	}
	/*
	 * Getter and Setter methods
	 */
	//Get environment
	public Environment getEnv() {
		return env;
	}
	//Set environment
	public void setEnv(Environment env) {
		this.env = env;
	}
	//Get policy
	public Policy getPol() {
		return pol;
	}
	//Set environment
	public void setPol(Policy pol) {
		this.pol = pol;
	}
}
