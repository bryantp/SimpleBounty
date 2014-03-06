package com.bryantp.SimpleBounty;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

import com.bryantp.SimpleBounty.resource.SimpleBountyResource;

public class PlayerProfile implements Serializable, Comparable<PlayerProfile>{
	
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * Profile object of a player and their bounty
	 */
	
	private String name;
	private BigDecimal communalBounty,playersetBounty;
	
	public PlayerProfile(String name){
		this.name = name;
		communalBounty = new BigDecimal("0").setScale(2, SimpleBountyResource.rounding);
		playersetBounty = new BigDecimal("0").setScale(2,SimpleBountyResource.rounding); 	
	}
	
	public String getName(){
		return name;
	}
	
	public BigDecimal getcommunalBounty(){
		return communalBounty; 
	}
	
	public BigDecimal getplayersetBounty(){
		return playersetBounty; 
	}
	
	public void addcommunalBounty(BigDecimal bounty){
		communalBounty = communalBounty.add(bounty);  
	}
	
	public void addplayersetBounty(BigDecimal bounty){
		playersetBounty = playersetBounty.add(bounty); 
	}
	
	public BigDecimal gettotalBounty(){
		return communalBounty.add(playersetBounty);
		
	}
	
	public void setcommunalBounty(BigDecimal bounty){
		communalBounty = bounty; 
	}
	
	public void setplayersetBounty(BigDecimal bounty){
		playersetBounty = bounty; 
	}


	@Override
	public int compareTo(PlayerProfile obj) {
		if(obj == null) return 0; 
		
		return this.gettotalBounty().compareTo(obj.gettotalBounty());
	}
	
	
	

}
