package com.bryantp.SimpleBounty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;



import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import net.milkbowl.vault.economy.Economy; 
import net.milkbowl.vault.economy.EconomyResponse; 


public class BountyListener implements Listener{

	private SimpleBounty sb = null; 
	private int increment, decrement; 
	private boolean  showkillerMessage, showvictimMessage;
	
	private SaveData saveData;
	private Economy econ= null;
	private boolean useEcon; 
	private boolean useSQL; 
	private boolean incrementnoBounty;
	private boolean communalEnable;
	 
	
	
	public BountyListener(Economy econ, SimpleBounty sb){
		this.econ = econ; 
		this.sb = sb; 
		 
	}
	
	public SaveData getSaveData(){
		return this.saveData;
	}
	
	public void setSaveData(SaveData saveData){
		this.saveData = saveData;
	}
	
	/**
	 * Used for bounty info command
	 */
	public boolean  usingSQL(){
		return useSQL; 
	}
	
	/**
	 * Used for bounty info command
	 */
	public boolean getUseEcon(){
		return useEcon; 
	}
	
	public void setUseEcon(boolean useEcon){
		this.useEcon = useEcon;
	}
	
	public void setConfig(Config conf){ 
		useEcon= conf.getuseEcon(); 
		useSQL = conf.getuseSQL();
		incrementnoBounty = conf.getincrementnoBounty(); 
		communalEnable = conf.getcommunalEnable(); 
		increment = conf.getIncrement(); 
		decrement = conf.getDecrement();
		showvictimMessage = conf.getshowvictimMessage();  
		showkillerMessage = conf.getshowkillerMessage();
	}
	

	/**
	 * Checks to see if the player has a profile yet. If they don't, we create them one. 
	 * @param evt
	 */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent evt){
	   Player player = evt.getPlayer();
	   if(!saveData.playerExists(player.getName())){
		   saveData.addPlayerProfile(player.getName());
		   saveData.reload();
	   }
    }
	
	/**
	 * Death Handler. Makes sure that the entity that did the killing is a Player. 
	 */
	@EventHandler
	public void onDeath(EntityDeathEvent event){
		if(communalEnable){//Checks to see if communal bounty is enabled or not. 
			if(event.getEntity() instanceof Player && !(event.getEntity() instanceof Monster)){
				final Player victim = (Player) event.getEntity();
				Player killer = null; 
			 
				if(event.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent){
					EntityDamageByEntityEvent nEvent = (EntityDamageByEntityEvent) event.getEntity().getLastDamageCause();
				 
					if(nEvent.getDamager() instanceof Player){
						killer = (Player)nEvent.getDamager(); 
					}
				 
					if(nEvent.getDamager() instanceof Arrow){
						Projectile arrow = (Arrow) nEvent.getDamager(); 
						if(arrow.getShooter() instanceof Player){
							killer = (Player)arrow.getShooter();
						} 
					}
				 
					//Might do it this way from now on. 
					if(nEvent.getDamager().getType() == EntityType.SPLASH_POTION){
						Projectile potion = (Projectile) nEvent.getDamager(); 
						killer = (Player) potion.getShooter(); 
					 
					}
				}
			 
				if(killer != null && victim != null){
					bountyHandler(killer,victim); 
				} 
			  
			}
		} 
		
	}
	
	/**
	 * Handles all the logic of a communal bounty transaction. 
	 * @param killer
	 * @param victim
	 */
	public void bountyHandler(Player killer, Player victim){
		
		//Give the killer the bounty
		PlayerProfile killerProfile = saveData.getPlayerProfile(killer.getName());
		PlayerProfile victimProfile = saveData.getPlayerProfile(victim.getName());
		
		int bounty = saveData.getPlayerProfile(victim.getName()).gettotalBounty();
		 
		if(bounty > 0){ //Only gives money if the bounty is greater than 0. Stopped a bug where 1 gold was given even if bounty was 0. 
			//Do the new transaction here. 
			transactionHandler(killer.getName(), bounty); 
		 } 
		
		
		//recalculate the bounty of the victim
		int newVictimBounty = victimProfile.getcommunalBounty() - decrement;
		if(newVictimBounty < 0){
			newVictimBounty = 0;  
		}
			
		//recalculate the bounty of the killer. Look into this logic more. 
		int victimTotalBounty = victimProfile.gettotalBounty();
		if(incrementnoBounty && victimTotalBounty <= 0){
			int newKillerBounty = killerProfile.getcommunalBounty() + increment; //Old bounty + increment
			if(newKillerBounty < 0){
				newKillerBounty = 0; //Makes sure that the bounty cannot go below 0. 
			}
			
			killerProfile.setcommunalBounty(newKillerBounty);
			if(showkillerMessage) killer.sendMessage("You killed " + victim.getName() + " for a bounty of " + bounty); 
			if(showvictimMessage) victim.sendMessage("You were killed by " + killer.getName() + " for a bounty of " + bounty);
		} 
		
		else if(!incrementnoBounty){
			int newKillerBounty = killerProfile.getcommunalBounty() + increment; //Old bounty + increment
			newKillerBounty = (newKillerBounty < 0) ?  0 : newKillerBounty;

			killerProfile.setcommunalBounty(newKillerBounty);
			if(showkillerMessage) killer.sendMessage("You killed " + victim.getName() + " for a bounty of " + bounty); 
			if(showvictimMessage) victim.sendMessage("You were killed by " + killer.getName() + " for a bounty of " + bounty);
		}
		
		else{
			killer.sendMessage(ChatColor.GREEN + "A bounty was not placed on you."); 
		}
		
		victimProfile.setcommunalBounty(newVictimBounty); //Sets the communal bounty of the victim. This always changes regardless of settings. 
		victimProfile.setplayersetBounty(0); //Set to 0 because the killer is going to receive the bounty. 
	}
	
	
	/**
	 * Sets the configuration parameters 
	 * @param increment
	 * @param decrement
	 * @param showvictimMessage
	 * @param showkillerMessage
	 */
	public void configSet(boolean  incrementnoBounty, boolean  communalEnable, int increment ,
			int decrement, boolean  showvictimMessage, boolean  showkillerMessage, 
			boolean  useSQL, boolean  useEcon, boolean  psEnable){
		this.increment = increment;
		this.decrement = decrement;
		this.showvictimMessage = showvictimMessage; 
		this.showkillerMessage = showkillerMessage;
		this.useSQL= useSQL; 
		this.useEcon = useEcon; 
		this.incrementnoBounty = incrementnoBounty; 
		this.communalEnable = communalEnable; 
		 
	}
	
	/**
	 * Returns the players total bounty 
	 * @param player
	 * @return
	 */
	public int getplayerBounty(String player){
		return saveData.getPlayerProfile(player).gettotalBounty();
	}
	
	/**
	 * Sets the communal bounty for a player
	 * @param player
	 * @param d
	 */
	public void setcommunalBounty(String player,int d){
		saveData.getPlayerProfile(player).setcommunalBounty(d); 
	}
	
	/**
	 * Sets the player's player set bounty
	 * @param player
	 * @param bounty
	 */
	public void setplayersetBounty(String player, int bounty){
		saveData.getPlayerProfile(player).setplayersetBounty(bounty);
	}
		
	/**
	 * Adds to the player's communal Bounty 
	 * @param player
	 * @param increment
	 */
	public void addtocommunalBounty(String player, int increment){
		saveData.getPlayerProfile(player).addcommunalBounty(increment);
	}
	
	/**
	 * Adds a player set bounty to a player. 
	 * @param player
	 * @param increment
	 */
	public void addtoplayersetBounty(String player, int increment){
		saveData.getPlayerProfile(player).addplayersetBounty(increment); 
	}
	
	/**
	 * Places a bounty on a player. 
	 * @param placer
	 * @param victim
	 * @param bounty
	 */
	public void placeBounty(Player placer, String victim, int bounty){
		
		if(placebountyHandler(placer, victim, bounty)){ 
			
			if(showkillerMessage) placer.sendMessage(ChatColor.DARK_GRAY + "Placed a bounty of " + bounty + " on " + victim); //Toggles the messages that the killer sees 
			Player victimPlayer = Bukkit.getPlayer(victim);
			if(victimPlayer != null){ //Doesn't try and send a message if the player is Null. 
				if(showvictimMessage) victimPlayer.sendMessage(ChatColor.DARK_GRAY + placer.getName() + " placed a bounty of " + bounty + " on you"); //Toggles the message that the victim sees. 
			} 
			 
			}
		
		else{
			placer.sendMessage(ChatColor.RED + "Not enough money to place bounty"); 
		}
	}
	
	/**
	 * Calculates the top 10 player's total bounties. 
	 * @return
	 */
	public ArrayList<String> calculateTop(){
		ArrayList<PlayerProfile> unSortedList = new ArrayList<PlayerProfile>(); 
		ArrayList<String> output = new ArrayList<String>();
		Map<String,PlayerProfile> playerList = saveData.getPlayerList();
		if(playerList != null && playerList.size() > 0){ 
			for(Map.Entry<String, PlayerProfile> entry : playerList.entrySet()){
				if(entry.getValue() != null){
					unSortedList.add(entry.getValue()); 
				}
			}
			
			Collections.sort(unSortedList); 
			Collections.reverse(unSortedList); 

			
			if(unSortedList.size() != 0){
				for(int i = 0; i < unSortedList.size(); i++){
					if(!(unSortedList.get(i).gettotalBounty() == 0)){
						output.add(unSortedList.get(i).getName() + ": " + unSortedList.get(i).gettotalBounty()); 
					}
				}
			
				return output; 
			} else{
				output.add("There are no bounties to display!");
				return output; 
			}
		} else{
			output.add("There are no bounties to display!");
			return output; 
		}
		
	} 
	

	/**
	 * Handles the actual transaction of bounties. Takes into account the economy system. 
	 * @param recipient
	 * @param amount
	 */
	public void transactionHandler(String recipient, int amount){
		
		if(!useEcon){//If not using the economy system. 
			PlayerInventory inventory = Bukkit.getPlayer(recipient).getInventory();
			ItemStack goldStack = new ItemStack(Material.GOLD_INGOT,amount); 
			inventory.addItem(goldStack);
		}
		
		else{
			EconomyResponse r = econ.depositPlayer(recipient,(double)amount); 
			
			if(!r.transactionSuccess()){
				SimpleBounty.logger.log(Level.SEVERE,"There was an error with the transaction for " + recipient + " " + r.errorMessage);
			}
		}
		
	}
	
	/**
	 * Handles the placing of bounties with the placebounty command. 
	 * @param placer
	 * @param victim
	 * @param amount
	 * @return
	 */
	public boolean placebountyHandler(Player placer, String victim, int amount){
		PlayerProfile victimProfile = saveData.getPlayerProfile(victim);
		if(!useEcon){
			Inventory placerInv = placer.getInventory(); //Creates a new item stack with the bounty, then checks to see if they have the amount listed. 
			ItemStack cost = new ItemStack(Material.GOLD_INGOT,amount);
			if(!placerInv.contains(Material.GOLD_INGOT, amount)) { return false; }  
			
			victimProfile.addplayersetBounty(amount);
			placerInv.removeItem(cost); 
			return true; 
			
		}
		
		else{
			
			if(!econ.has(placer.getName(), (double)amount)){ 
				return false; 
			}
			
			
			EconomyResponse placerEcon = econ.withdrawPlayer(placer.getName(),(double)amount);
			if(!placerEcon.transactionSuccess()){
				SimpleBounty.logger.log(Level.SEVERE,"There was an error with the transaction for " + placer.getName() + " " + placerEcon.errorMessage);
				return false; 
			}
			
			victimProfile.addplayersetBounty(amount); 
			return true; 
		}
	}

	
	/**
	 * Returns the economy object.  
	 * @return
	 */
	public Economy getEcon(){
		return econ; 
	}
	
	/**
	 * Adds a player to the ArrayList. 
	 * @param player
	 */
	public void addPlayer(Player player){
		saveData.addPlayerProfile(player.getName());
	}
	

} 



