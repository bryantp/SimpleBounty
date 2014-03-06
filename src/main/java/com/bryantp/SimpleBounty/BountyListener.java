package com.bryantp.SimpleBounty;

import java.math.BigDecimal;
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

import com.bryantp.SimpleBounty.resource.SimpleBountyResource;

import net.milkbowl.vault.economy.Economy; 
import net.milkbowl.vault.economy.EconomyResponse; 


public class BountyListener implements Listener{

	private SimpleBounty sb = null; 
	private BigDecimal increment, decrement; 
	private boolean  showkillerMessage, showvictimMessage;
	
	private SaveData saveData;
	private Economy econ= null;
	private boolean useEcon; 
	private boolean useSQL; 
	private boolean incrementnoBounty;
	private boolean communalEnable;
	private Config conf;
	 
	
	
	public BountyListener(Economy econ, SimpleBounty sb, Config conf){
		this.econ = econ; 
		this.sb = sb; 
		this.conf = conf;
	}
	
	public void setConfigVariables(){
		this.increment = conf.getIncrement();
		this.decrement = conf.getDecrement();
		this.showkillerMessage = conf.getshowkillerMessage();
		this.showvictimMessage = conf.getshowkillerMessage();
		this.useSQL = conf.getuseSQL();
		this.useEcon = conf.getuseEcon();
		this.incrementnoBounty = conf.getincrementnoBounty();
		this.communalEnable = conf.getcommunalEnable();
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
		
		BigDecimal bounty = saveData.getPlayerProfile(victim.getName()).gettotalBounty();
		 
		if(bounty.signum() > 0){ //Only gives money if the bounty is greater than 0. Stopped a bug where 1 gold was given even if bounty was 0. 
			//Do the new transaction here. 
			transactionHandler(killer.getName(), BigDecimal.ZERO); 
		 } 
		
		
		//recalculate the bounty of the victim
		BigDecimal newVictimBounty = victimProfile.getcommunalBounty().subtract(decrement);
		if(newVictimBounty.signum() < 0){
			newVictimBounty = BigDecimal.ZERO;  
		}
			
		//recalculate the bounty of the killer. Look into this logic more. 
		BigDecimal victimTotalBounty = victimProfile.gettotalBounty();
		if(incrementnoBounty && victimTotalBounty.signum() <= 0){
			BigDecimal newKillerBounty = killerProfile.getcommunalBounty().add(increment); //Old bounty + increment
			newKillerBounty = (newKillerBounty.signum() < 0) ? BigDecimal.ZERO : newKillerBounty; //Makes sure that the bounty cannot go below 0. 
		
			killerProfile.setcommunalBounty(newKillerBounty);
			if(showkillerMessage){
				killer.sendMessage(String.format(SimpleBountyResource.getVictimMessage(), victim.getName(), bounty.doubleValue())); 
			}
			
			if(showvictimMessage){
				victim.sendMessage(String.format(SimpleBountyResource.getKillerMessage(),killer.getName(),bounty.doubleValue()));
			}
		} 
		
		else if(!incrementnoBounty){
			BigDecimal newKillerBounty = killerProfile.getcommunalBounty().add(increment); //Old bounty + increment
			newKillerBounty = (newKillerBounty.signum() < 0) ?  BigDecimal.ZERO : newKillerBounty;

			killerProfile.setcommunalBounty(newKillerBounty);
			if(showkillerMessage){
				killer.sendMessage(String.format(SimpleBountyResource.getVictimMessage(), victim.getName(), bounty.doubleValue())); 
			}
			
			if(showvictimMessage){
				victim.sendMessage(String.format(SimpleBountyResource.getKillerMessage(),killer.getName(),bounty.doubleValue()));
			}
		}
		
		else{
			killer.sendMessage(SimpleBountyResource.positiveMessageColor + SimpleBountyResource.getBountyNotPlacedMessage()); 
		}
		
		victimProfile.setcommunalBounty(newVictimBounty); //Sets the communal bounty of the victim. This always changes regardless of settings. 
		victimProfile.setplayersetBounty(BigDecimal.ZERO); //Set to 0 because the killer is going to receive the bounty. 
	}
	
	
	/**
	 * Returns the players total bounty 
	 * @param player
	 * @return
	 */
	public BigDecimal getplayerBounty(String player){
		return saveData.getPlayerProfile(player).gettotalBounty();
	}
	
	/**
	 * Sets the communal bounty for a player
	 * @param player
	 * @param d
	 */
	public void setcommunalBounty(String player,BigDecimal d){
		saveData.getPlayerProfile(player).setcommunalBounty(d); 
	}
	
	/**
	 * Sets the player's player set bounty
	 * @param player
	 * @param bounty
	 */
	public void setplayersetBounty(String player, BigDecimal bounty){
		saveData.getPlayerProfile(player).setplayersetBounty(bounty);
	}
		
	/**
	 * Adds to the player's communal Bounty 
	 * @param player
	 * @param increment
	 */
	public void addtocommunalBounty(String player, BigDecimal increment){
		saveData.getPlayerProfile(player).addcommunalBounty(increment);
	}
	
	/**
	 * Adds a player set bounty to a player. 
	 * @param player
	 * @param increment
	 */
	public void addtoplayersetBounty(String player, BigDecimal increment){
		saveData.getPlayerProfile(player).addplayersetBounty(increment); 
	}
	
	/**
	 * Places a bounty on a player. 
	 * @param placer
	 * @param victim
	 * @param bounty
	 */
	public void placeBounty(Player placer, String victim, BigDecimal bounty){
		
		if(placebountyHandler(placer, victim, bounty)){ 
			
			if(showkillerMessage){
				placer.sendMessage(SimpleBountyResource.positiveMessageColor + String.format(SimpleBountyResource.getPlaceBountyMessageOnPlayer(),bounty.doubleValue(),victim)); 
			}
			
			Player victimPlayer = Bukkit.getPlayer(victim);
			if(victimPlayer != null){
				if(showvictimMessage){
					victimPlayer.sendMessage(SimpleBountyResource.negativeMessageColor + String.format(SimpleBountyResource.getBountyPlacedOnYouMessage(),placer.getName(),bounty.doubleValue())); 
				}
			} 
			 
		}else{
			placer.sendMessage(SimpleBountyResource.negativeMessageColor + SimpleBountyResource.getNotEnoughMoneyToPlaceBountyMessage()); 
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
					BigDecimal totalBounty = unSortedList.get(i).gettotalBounty();
					if(!totalBounty.equals(SimpleBountyResource.SimpleBountyBigDecimalZero)){
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
	public void transactionHandler(String recipient, BigDecimal amount){
		
		if(!useEcon){ 
			PlayerInventory inventory = Bukkit.getPlayer(recipient).getInventory();
			ItemStack goldStack = new ItemStack(Material.GOLD_INGOT,amount.intValue()); 
			inventory.addItem(goldStack);
		}else{
			EconomyResponse r = econ.depositPlayer(recipient,amount.doubleValue()); 
			
			if(!r.transactionSuccess()){
				SimpleBounty.logger.log(Level.SEVERE,String.format(SimpleBountyResource.getTransactionErrorMessage(),recipient,r.errorMessage));
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
	public boolean placebountyHandler(Player placer, String victim, BigDecimal amount){
		PlayerProfile victimProfile = saveData.getPlayerProfile(victim);
		if(!useEcon){
			Inventory placerInv = placer.getInventory(); //Creates a new item stack with the bounty, then checks to see if they have the amount listed. 
			ItemStack cost = new ItemStack(Material.GOLD_INGOT,amount.intValue());
			if(!placerInv.contains(Material.GOLD_INGOT, amount.intValue())){ 
				return false;
			}  
			
			victimProfile.addplayersetBounty(amount);
			placerInv.removeItem(cost); 
			return true; 
			
		}
		
		else{
			
			if(!econ.has(placer.getName(), amount.doubleValue())){ 
				return false; 
			}
			
			
			EconomyResponse placerEcon = econ.withdrawPlayer(placer.getName(),amount.doubleValue());
			if(!placerEcon.transactionSuccess()){
				SimpleBounty.logger.log(Level.SEVERE,String.format(SimpleBountyResource.getTransactionErrorMessage(), victim, placerEcon.errorMessage));
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



