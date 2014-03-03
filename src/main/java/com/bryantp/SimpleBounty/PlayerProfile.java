package com.bryantp.SimpleBounty;

import java.io.Serializable;

public class PlayerProfile implements Serializable, Comparable<PlayerProfile>{
	
	
	private static final long serialVersionUID = 1L;
	/**
	 * Profile object of a player and their bounty
	 */
	
	private String name;
	private int communalBounty,playersetBounty;
	
	public PlayerProfile(String name){
		this.name = name;
		communalBounty = 0;
		playersetBounty = 0; 
	
		
	}
	
	public String getName(){
		return name;
	}
	
	public int getcommunalBounty(){
		return communalBounty; 
	}
	
	public int getplayersetBounty(){
		return playersetBounty; 
	}
	
	public void addcommunalBounty(int bounty){
		communalBounty += bounty;  
	}
	
	public void addplayersetBounty(int bounty){
		playersetBounty += bounty; 
	}
	
	public int gettotalBounty(){
		return communalBounty + playersetBounty; 
		
	}
	
	public void setcommunalBounty(int bounty){
		communalBounty = bounty; 
	}
	
	public void setplayersetBounty(int bounty){
		playersetBounty = bounty; 
	}


	@Override
	public int compareTo(PlayerProfile obj) {
		if(obj == null) return 0; 
		
		if(obj.gettotalBounty() > gettotalBounty()) return -1;
		
		else if(obj.gettotalBounty() < gettotalBounty()) return 1; 
		
		else return 0; 
		
		
	}
	
	
	

}
