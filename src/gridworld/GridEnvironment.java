package gridworld;

import java.util.*;

import concepts.*;


public class GridEnvironment extends Environment{
	//Current state
	private State currState;
	//Last state
	private State lastState;
	//Last action
	private Action lastAction;
	//Last reward
	private double lastRew;
	//Set of possible states
	private Set<State> states;
	//Set of possible actions
	private Set<Action> actions;
	//Grid width
	private int dimX;
	//Grid height
	private int dimY;
	//True if king moves are used
	private boolean kingMoves;
	private Set<State> termStates = new HashSet<State>();
	GridState initState;
	//HashMap using Cantor pairing for efficient searching of states
	HashMap<Long, GridState> gridPairedStates = new HashMap<Long, GridState>();
	//HashMap of Actions, keyed on their name
	HashMap<String, GridAction> gridActions = new HashMap<String, GridAction>();
	
	/*
	 * Constructor
	 * 
	 * parameters decide dimensions of the grid and type of movement,
	 * 4-way or 8-way
	 */
	public GridEnvironment(int x, int y, boolean kMoves){
		//Set dimensions
		this.dimX = x;
		this.dimY = y;
		this.kingMoves = kMoves;
		//Create all possible actions
		HashSet<Action> actions = new HashSet<Action>();
		if(kingMoves){
			GridAction act = new GridAction("N", "S");
			actions.add(act);
			gridActions.put("N", act);
			act = new GridAction("S", "N");
			actions.add(act);
			gridActions.put("S", act);
			act = new GridAction("W", "E");
			actions.add(act);
			gridActions.put("W", act);
			act = new GridAction("E", "W");
			actions.add(act);
			gridActions.put("E", act);
			act = new GridAction("NE", "SW");
			actions.add(act);
			gridActions.put("NE", act);
			act = new GridAction("NW", "SE");
			actions.add(act);
			gridActions.put("NW", act);
			act = new GridAction("SW", "NE");
			actions.add(act);
			gridActions.put("SW", act);
			act = new GridAction("SE", "NW");
			actions.add(act);
			gridActions.put("SE", act);
		} else {
			GridAction act = new GridAction("N", "S");
			actions.add(act);
			gridActions.put("N", act);
			act = new GridAction("S", "N");
			actions.add(act);
			gridActions.put("S", act);
			act = new GridAction("W", "E");
			actions.add(act);
			gridActions.put("W", act);
			act = new GridAction("E", "W");
			actions.add(act);
			gridActions.put("E", act);
		}
		this.actions = actions;
		//Create all possible states
		HashSet<State> gridStates = new HashSet<State>();
		for(int i = 0; i < this.dimX; i++){
			for(int j = 0; j < this.dimY; j++){
				//New State
				GridState s = new GridState(i, j);
				//From each state, all actions are possible
				s.setActions(this.getActions());
				//Add state to environment states
				gridStates.add(s);
				//Add state to gridPairedStates
				this.gridPairedStates.put(GridEnvironment.CantorPair(i, j), s);
			}
		}
		//For each state get the neighborhood
		for(State s : gridStates){
			//Cast is safe since a GridEnvironment should only get GridStates
			GridState s1 = (GridState)s;
			int sX = s1.getX();
			int sY = s1.getY();
			//Check if out of bounds
			//If so, remain in the state
			if(sY+1 < this.dimY)
				s1.addNeighState("N", this.gridPairedStates.get(GridEnvironment.CantorPair(sX, sY+1)));
			else
				s1.addNeighState("N", this.gridPairedStates.get(GridEnvironment.CantorPair(sX, sY)));
			if(sY-1 >= 0)
				s1.addNeighState("S", this.gridPairedStates.get(GridEnvironment.CantorPair(sX, sY-1)));
			else
				s1.addNeighState("S", this.gridPairedStates.get(GridEnvironment.CantorPair(sX, sY)));
			if(sX+1 < this.dimX)
				s1.addNeighState("E", this.gridPairedStates.get(GridEnvironment.CantorPair(sX+1, sY)));
			else
				s1.addNeighState("E", this.gridPairedStates.get(GridEnvironment.CantorPair(sX, sY)));
			if(sX-1 >= 0)
				s1.addNeighState("W", this.gridPairedStates.get(GridEnvironment.CantorPair(sX-1, sY)));
			else
				s1.addNeighState("W", this.gridPairedStates.get(GridEnvironment.CantorPair(sX, sY)));
			//If kingMoves enabled
			if(this.kingMoves){
				//Check if state is in a corner or on a border
				if(sY+1 < this.dimY && sX+1 < this.dimX)
					s1.addNeighState("NE", this.gridPairedStates.get(GridEnvironment.CantorPair(sX+1, sY+1)));
				else
					s1.addNeighState("NE", this.gridPairedStates.get(GridEnvironment.CantorPair(sX, sY)));
				if(sY-1 >= 0 && sX+1 < this.dimX)
					s1.addNeighState("SE", this.gridPairedStates.get(GridEnvironment.CantorPair(sX+1, sY-1)));
				else
					s1.addNeighState("SE", this.gridPairedStates.get(GridEnvironment.CantorPair(sX, sY)));
				if(sY+1 < this.dimY && sX-1 >= 0)
					s1.addNeighState("NW", this.gridPairedStates.get(GridEnvironment.CantorPair(sX-1, sY+1)));
				else
					s1.addNeighState("NW", this.gridPairedStates.get(GridEnvironment.CantorPair(sX, sY)));
				if(sY-1 >= 0 && sX-1 >= 0)
					s1.addNeighState("SW", this.gridPairedStates.get(GridEnvironment.CantorPair(sX-1, sY-1)));
				else
					s1.addNeighState("SW", this.gridPairedStates.get(GridEnvironment.CantorPair(sX, sY)));
			}
			
		}
		//Save state set
		this.states = gridStates;
	}
	//Check coordinates
	private boolean validCoords(int x, int y){
		if(x < this.dimX && y < this.dimY && x >= 0 && y >= 0)
			return true;
		else
			return false;
	}
	//Add a wall section
	public void setWall(int x, int y, String move1){
		//Check if state exists
		if(validCoords(x, y)){
			//Opposite move name
			String move2 = this.gridActions.get(move1).getOppAct();
			//States interested by changes
			GridState s1 = this.getState(x, y);
			GridState s2 = s1.getNeighState(move1);
			if((s1.getX() != s2.getX()) || (s1.getY() != s2.getY())){
				//Modify states
				s1.addNeighState(move1, s1);
				s2.addNeighState(move2, s2);
				if(this.kingMoves){
					GridState stmp = null;
					switch(move1){
						case "N":
							stmp = s1.getNeighState("NE");
							stmp.addNeighState("SW", stmp);
							s1.addNeighState("NE", s1);
							stmp = s1.getNeighState("NW");
							stmp.addNeighState("SE", stmp);
							s1.addNeighState("NW", s1);
							stmp = s2.getNeighState("SE");
							stmp.addNeighState("NW", stmp);
							s2.addNeighState("SE", s2);
							stmp = s2.getNeighState("SW");
							stmp.addNeighState("NE", stmp);
							s2.addNeighState("SW", s2);
							break;
						case "S":
							stmp = s1.getNeighState("SE");
							stmp.addNeighState("NW", stmp);
							s1.addNeighState("SE", s1);
							stmp = s1.getNeighState("SW");
							stmp.addNeighState("NE", stmp);
							s1.addNeighState("SW", s1);
							stmp = s2.getNeighState("NE");
							stmp.addNeighState("SW", stmp);
							s2.addNeighState("NE", s2);
							stmp = s2.getNeighState("NW");
							stmp.addNeighState("SE", stmp);
							s2.addNeighState("NW", s2);
							break;
						case "E":
							stmp = s1.getNeighState("SE");
							stmp.addNeighState("NW", stmp);
							s1.addNeighState("SE", s1);
							stmp = s1.getNeighState("NE");
							stmp.addNeighState("SW", stmp);
							s1.addNeighState("NE", s1);
							stmp = s2.getNeighState("NW");
							stmp.addNeighState("SE", stmp);
							s2.addNeighState("NW", s2);
							stmp = s2.getNeighState("SW");
							stmp.addNeighState("NE", stmp);
							s2.addNeighState("SW", s2);
							break;
						case "W":
							stmp = s1.getNeighState("SW");
							stmp.addNeighState("NE", stmp);
							s1.addNeighState("SW", s1);					
							stmp = s1.getNeighState("NW");
							stmp.addNeighState("SE", stmp);
							s1.addNeighState("NW", s1);
							stmp = s2.getNeighState("NE");
							stmp.addNeighState("SW", stmp);
							s2.addNeighState("NE", s2);
							stmp = s2.getNeighState("SE");
							stmp.addNeighState("NW", stmp);
							s2.addNeighState("SE", s2);
							break;
					}
				}
			}
		}
	}
	@Override
	public Map<State, Double> allNextStates(Action act) {
		if(act.getClass().getName().equals("gridworld.GridAction"))
			return allNSs((GridAction)act);
		else{
			System.out.println("Error while casting problem-specific class.");
			return null;
		}
	}
	//Implementation of allNextStates - return all possible next states for action act
	private HashMap<State, Double> allNSs(GridAction act){
		//Get name of the action
		String actName = act.getName();
		HashMap<State, Double> result = new HashMap<State, Double>();
		GridState s = (GridState)this.getState();
		if(!this.termState(s)){
			result.put(s.getNeighState(actName), 1.0);
			return result;
		} else {
			return null;
		}
	}
	@Override
	public boolean termState(State s){
		if(this.termStates.contains(s))
			return true;
		else
			return false;
	}
	@Override
	public double getReward(Action act, State s1) {
		if(!this.termState(this.getState())){
			if(act.getClass().getName().equals("gridworld.GridAction") && s1.getClass().getName().equals("gridworld.GridState"))
				return getR((GridAction)act, (GridState)s1);
			else{
				System.out.println("Error while casting problem-specific class.");
				return 0;
			}
		} else {
			//Terminal state, return 0
			return 0;
		}
	}
	//Implementation of getReward - return the reward for the transition to state s1
	private double getR(GridAction Act, GridState s1){
		return -1;
	}
	@Override
	public void resetETraces(){
		for(State s : this.states){
			s.reseteTraces();
		}
	}
	@Override
	public State getState() {
		return this.currState;
	}
	@Override
	public void setState(State s) {
		this.currState = s;
	}
	@Override
	public Set<State> getStates() {
		return this.states;
	}
	@Override
	public Set<Action> getActions() {
		return this.actions;
	}
	@Override
	public State getLastState() {
		return this.lastState;
	}
	@Override
	protected void setLastState(State s){
		this.lastState = s;
	}
	@Override
	public Action getLastAction() {
		return this.lastAction;
	}
	@Override
	protected void setLastAction(Action a){
		this.lastAction = a;
	}
	@Override
	public double getLastRew() {
		return this.lastRew;
	}
	@Override
	protected void setLastReward(double rew){
		this.lastRew = rew;
	}
	@Override
	public void addTermState(State s){
		this.termStates.add((GridState)s);
	}
	@Override
	public void removeTermState(State s){
		this.termStates.remove(s);
	}
	@Override
	public State getInitState(){
		return this.initState;
	}
	@Override
	public void setInitState(State s){
		this.initState = (GridState)s;
	}
	//Getter and setter methods
	public int getDimX() {
		return this.dimX;
	}
	public int getDimY() {
		return this.dimY;
	}
	public boolean useKingMoves(){
		return this.kingMoves;
	}
	public GridState getState(int x, int y){
		long cant = GridEnvironment.CantorPair(x, y);
		return this.gridPairedStates.get(cant);
	}
	public Set<State> getTermStates(){
		return this.termStates;
	}
	//Cantor pairing: useful to index states
	public static long CantorPair(int x, int y){
		return ((x + y) * (x + y + 1)) / 2 + y;
	}
	public static int[] Reverse(long z){
		int[] pair = new int[2];
		int t = (int)Math.floor((-1D + Math.sqrt(1D + 8 * z))/2D);
		long x = t * (t + 3) / 2 - z;
		long y = z - t * (t + 1) / 2;
		pair[0] = (short)x;
		pair[1] = (short)y;
		return pair;
	}
}
