package algorithms;

import java.util.Map;
import java.util.Set;

import concepts.Action;
import concepts.Agent;
import concepts.State;

public class PolicyIteration extends Algorithm{
	//Agent
	private Agent agent;
	//Discount rate
	private double dRate;
	//Threshold for evaluation step
	private double theta;
	
	/*
	 * Constructor
	 */
	public PolicyIteration(Agent a, double dr, double t){
		this.setAgent(a);
		this.setdRate(dr);
		this.setTheta(t);
	}
	//policy evaluation
	private void pEval(){
		//System.out.println("Policy Evaluation.");
		double delta = this.theta+1;
		//Keep going while delta greater than threshold theta
		while(delta >= theta){
			//Reset delta
			delta = 0;
			//Get set of all possible states from environment
			Set<? extends State> allStates = agent.getEnv().getStates();
			for(State s : allStates){
				//Save old V value
				double oldV = s.getvFunct();
				//Initialize new V
				double newV = 0;
				//Next action following policy
				Action act = agent.getPol().nextAction(s);
				//Set state in environment
				agent.getEnv().setState(s);
				//All possible next states given action act and their probabilities
				Map<? extends State, Double> allNextStates = agent.getEnv().allNextStates(act);
				//If result is null, or s is a terminal state, do nothing
				if(allNextStates != null && !this.agent.getEnv().termState(s)){
					//Key for iteration on map
					Set<? extends State> allNSKeys = allNextStates.keySet();
					for(State s1: allNSKeys){
						//Probability of this state following action act
						double probS1 = allNextStates.get(s1);
						//Reward for going in this state
						double rewS1 = agent.getEnv().getReward(act, s1);
						//State-v funct of state s1
						double vFunctS1 = s1.getvFunct();
						//Add value to the new state-v funct
						newV += probS1 * (rewS1 + (dRate * vFunctS1));
						//System.out.println(newV);
						//Compute difference between old and new value
						double dV = Math.abs((oldV - newV));
						//Update delta
						if(dV >= delta)
							delta = dV;
					}
				}
				//Save new state-v funct
				s.setvFunct(newV);
			}
		}
	}
	//policy improvement
	private boolean pImpr(){
		//System.out.println("Policy Improvement.");
		boolean stable = true;
		//Get all states from environment
		Set<? extends State> allStates = agent.getEnv().getStates();
		//For each possible state s
		for(State s : allStates){
			//Set state as actual state in environment
			agent.getEnv().setState(s);
			//Action selected by policy
			Action oldAct = agent.getPol().nextAction(s);
			//Initialize variable to compute state-v funct of the act selected by the policy
			double oldV = 0;
			//All possible next states given action act and their probabilities
			Map<? extends State, Double> allNextStates = agent.getEnv().allNextStates(oldAct);
			Set<? extends State> allNSKeys = null;
			//If result is null, s is a terminal state
			if(allNextStates != null){
				//Key for iteration on map
				allNSKeys = allNextStates.keySet();
				for(State s1 : allNSKeys){
					//Probability of this state following action act
					double probS1 = allNextStates.get(s1);
					//Reward for going in this state
					double rewS1 = agent.getEnv().getReward(oldAct, s1);
					//State-v funct of state s1
					double vFunctS1 = s1.getvFunct();
					//Add value to the state-v funct of this action
					oldV += probS1 * (rewS1 + (dRate * vFunctS1));
				}
				//Get all possible next actions from state s
				Set<? extends Action> allNextActions = s.getActions();
				for(Action act : allNextActions){
					//State-v funct for this action
					double newV = 0;
					//All possible next states given action act and their probabilities
					allNextStates = agent.getEnv().allNextStates(act);
					//Key for iteration on map
					allNSKeys = allNextStates.keySet();
					for(State s1 : allNSKeys){
						//Probability of this state following action act
						double probS1 = allNextStates.get(s1);
						//Reward for going in this state
						double rewS1 = agent.getEnv().getReward(act, s1);
						//State-v funct of state s1
						double vFunctS1 = s1.getvFunct();
						//Add value to the state-v funct of this action
						newV += probS1 * (rewS1 + (dRate * vFunctS1));
					}
					//Compare oldV and newV
					if(newV > oldV && !act.equals(oldAct)){
						//Found a new policy better than old one and diferent
						stable = false;
						oldV = newV;
						oldAct = act;
						agent.getPol().changePolicy(s, act);
					}
				}
			}
		}
		return stable;
	}
	//Run a single step of evaluation - improvement
	public boolean runPIStep(){
		boolean stable = false;
		this.pEval();
		stable = this.pImpr();
		//System.out.println("A step");
		return stable;
	}
	//Run the algorithm
	public int runPI(){
		boolean stable = false;
		int cnt = 0;
		//While policy isn't stable
		while(!stable){
			//Run step
			stable = this.runPIStep();
			cnt++;
		}
		return cnt;
	}
	@Override
	public int execAlg() {
		return this.runPI();
	}
	/*
	 * Getter and setter methods
	 */
	public Agent getAgent() {
		return this.agent;
	}
	public void setAgent(Agent agent) {
		this.agent = agent;
	}
	public double getdRate() {
		return this.dRate;
	}
	public void setdRate(double dRate) {
		this.dRate = dRate;
	}
	public double getTheta() {
		return this.theta;
	}
	public void setTheta(double theta) {
		this.theta = theta;
	}
}
