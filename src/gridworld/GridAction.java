package gridworld;

import concepts.*;

public class GridAction extends Action{
	private String name;
	private String oppAct;
	public GridAction(String n, String oppAct){
		this.name = n;
		this.oppAct = oppAct;
	}
	
	//Getter and setter methods
	public String getName() {
		return name;
	}
	public String getOppAct(){
		return this.oppAct;
	}
}
