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
	private BigDecimal communalBounty,playerSetBounty;
	
	public PlayerProfile(String name){
		this.name = name;
		communalBounty = new BigDecimal("0").setScale(2, SimpleBountyResource.rounding);
		playerSetBounty = new BigDecimal("0").setScale(2,SimpleBountyResource.rounding); 	
	}
	
	public String getName(){
		return name;
	}
	
	public BigDecimal getCommunalBounty(){
		return communalBounty; 
	}
	
	public BigDecimal getPlayerSetBounty(){
		return playerSetBounty; 
	}
	
	public void addCommunalBounty(BigDecimal bounty){
		communalBounty = communalBounty.add(bounty);  
	}
	
	public void subtractCommunalBounty(BigDecimal bounty){
		communalBounty = communalBounty.subtract(bounty);
		if(communalBounty.signum() < 0){
			communalBounty = BigDecimal.ZERO;
		}
	}
	
	public void addPlayerSetBounty(BigDecimal bounty){
		playerSetBounty = playerSetBounty.add(bounty); 
	}
	
	public BigDecimal getTotalBounty(){
		return communalBounty.add(playerSetBounty);
		
	}
	
	public void setCommunalBounty(BigDecimal bounty){
		communalBounty = bounty; 
	}
	
	public void setPlayerSetBounty(BigDecimal bounty){
		playerSetBounty = bounty; 
	}
	
	/**
	 * Determines if the player has any bounty
	 * @return
	 */
	public boolean hasBounty(){
		BigDecimal totalBounty = this.communalBounty.add(this.playerSetBounty);
		return (totalBounty != BigDecimal.ZERO);
	}
	
	/**
	 * Wipes all bounty;
	 */
	public void clearBounties(){
		this.communalBounty = BigDecimal.ZERO;
		this.playerSetBounty = BigDecimal.ZERO;
	}


	@Override
	public int compareTo(PlayerProfile obj) {
		if(obj == null) return 0; 
		return this.getTotalBounty().compareTo(obj.getTotalBounty());
	}
}
