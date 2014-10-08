package gridworld;

import java.util.*;

import concepts.*;

public class GridState extends State{
	//state-v funct
	private double vFunct;
	//action-v funct
	private HashMap<Action, Double> qFunct;
	//Elegibility trace
	private double eTrace;
	//Elegibility traces for state-action pairs
	private HashMap<Action, Double> eTraces;
	//possible actions from this state
	private Set<Action> actions;
	//coordinates
	private int x;
	private int y;
	//neighbours given actions
	private HashMap<String, GridState> neighStates = new HashMap<String, GridState>();
	
	public GridState(int x, int y){
		super();
		this.x = x;
		this.y = y;
	}
	@Override
	public double getvFunct() {
		return this.vFunct;
	}
	@Override
	public void setvFunct(double value) {
		this.vFunct = value;
	}
	@Override
	public void resetvFunct() {
		this.vFunct = 0;
	}
	@Override
	public double getqFunct(Action a) {
		if(!this.qFunct.containsKey(a))
			this.qFunct.put(a, (double)0);
		return this.qFunct.get(a);
	}
	@Override
	public void setqFunct(Action a, double value) {
		this.qFunct.put(a, value);
	}
	@Override
	public void resetqFunct() {
		this.qFunct = new HashMap<Action, Double>();
	}
	@Override
	public Set<Action> getActions() {
		return this.actions;
	}
	@Override
	public void setActions(Set<Action> actions) {
		this.actions = actions;
	}
	@Override
	public void resetActions() {
		this.actions = new HashSet<Action>();
	}
	@Override
	public double geteTrace() {
		return this.eTrace;
	}
	@Override
	public void seteTrace(double eT) {
		this.eTrace = eT;
	}
	@Override
	public double geteTrace(Action a) {
		if(!this.eTraces.containsKey(a))
			this.eTraces.put(a, (double)0);
		return this.eTraces.get(a);
	}
	@Override
	public void seteTrace(Action a, double eT) {
		this.eTraces.put(a, eT);
	}
	@Override
	public void reseteTraces(){
		this.eTrace = 0;
		this.eTraces = new HashMap<Action, Double>();
	}
	//Getter and setter methods
	public int getX() {
		return x;
	}
	public int getY() {
		return y;
	}
	public GridState getNeighState(String move){
		return neighStates.get(move);
	}
	public void addNeighState(String move, GridState state){
		//System.out.println("	Added neighboor " + move + " coord: " + state.getX() + "," + state.getY());
		this.neighStates.put(move, state);
	}
	public void remNeighState(String move){
		this.neighStates.remove(move);
	}
}
