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
	
	private SaveData saveData;
	private Economy econ= null;
	private Config conf;
	 
	
	
	public BountyListener(Economy econ, SimpleBounty sb, Config conf){
		this.econ = econ; 
		this.sb = sb; 
		this.conf = conf;
	}
	
	public SaveData getSaveData(){
		return this.saveData;
	}
	
	public void setSaveData(SaveData saveData){
		this.saveData = saveData;
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
		if(conf.getcommunalEnable()){//Checks to see if communal bounty is enabled or not. 
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
	 * Handles all the logic of a communal bounty transaction. When a player kills another player
	 * @param killer
	 * @param victim
	 */
	public void bountyHandler(Player killer, Player victim){
		
		//Give the killer the bounty
		PlayerProfile killerProfile = saveData.getPlayerProfile(killer.getName());
		PlayerProfile victimProfile = saveData.getPlayerProfile(victim.getName());
		
		BigDecimal bounty = saveData.getPlayerProfile(victim.getName()).getTotalBounty();
		 
		if(bounty.signum() > 0){ //Only gives money if the bounty is greater than 0. Stopped a bug where 1 gold was given even if bounty was 0. 
			//Do the new transaction here. 
			transactionHandler(killer.getName(), BigDecimal.ZERO); 
		 }
		
		if(conf.getNeedBountyLicense()){
			/**If you need a bounty license, then you need to check for bounty hunter permission. If they don't have it, they don't get the reward and they get a bounty placed on 
			* them even if the person they killed had a bounty. The person who died has no change in their bounty. 
			* 
			* Players can have their bounty go down if they kill people with bounties as well.
			*/
			
			if(victimProfile.hasBounty() && killer.hasPermission("bounty.bountyhunter")){
				//Give them the bounty if the person they killed has a bounty and they are licensed
				transactionHandler(killerProfile.getName(),victimProfile.getTotalBounty());
				victimProfile.clearBounties();
				if(conf.getBountyDecreaseOnKill()){
					//Your bounty goes down as you kill people with bounties
					killerProfile.subtractCommunalBounty(conf.getDecrement());
				}

			}else{
				//You need a license or you killed an innocent. Penalty!
				killerProfile.addCommunalBounty(conf.getIncrement());
			}
		}else{
			
		}
			
	}
	
	
	/**
	 * Returns the players total bounty 
	 * @param player
	 * @return
	 */
	public BigDecimal getplayerBounty(String player){
		return saveData.getPlayerProfile(player).getTotalBounty();
	}
	
	/**
	 * Sets the communal bounty for a player
	 * @param player
	 * @param d
	 */
	public void setcommunalBounty(String player,BigDecimal d){
		saveData.getPlayerProfile(player).setCommunalBounty(d); 
	}
	
	/**
	 * Sets the player's player set bounty
	 * @param player
	 * @param bounty
	 */
	public void setplayersetBounty(String player, BigDecimal bounty){
		saveData.getPlayerProfile(player).setPlayerSetBounty(bounty);
	}
		
	/**
	 * Adds to the player's communal Bounty 
	 * @param player
	 * @param increment
	 */
	public void addtocommunalBounty(String player, BigDecimal increment){
		saveData.getPlayerProfile(player).addCommunalBounty(increment);
	}
	
	/**
	 * Adds a player set bounty to a player. 
	 * @param player
	 * @param increment
	 */
	public void addtoplayersetBounty(String player, BigDecimal increment){
		saveData.getPlayerProfile(player).addPlayerSetBounty(increment); 
	}
	
	/**
	 * Places a bounty on a player. 
	 * @param placer
	 * @param victim
	 * @param bounty
	 */
	public void placeBounty(Player placer, String victim, BigDecimal bounty){
		
		if(placebountyHandler(placer, victim, bounty)){ 
			
			if(conf.getshowkillerMessage()){
				placer.sendMessage(SimpleBountyResource.positiveMessageColor + String.format(SimpleBountyResource.getPlaceBountyMessageOnPlayer(),bounty.doubleValue(),victim)); 
			}
			
			Player victimPlayer = Bukkit.getPlayer(victim);
			if(victimPlayer != null){
				if(conf.getshowvictimMessage()){
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
					BigDecimal totalBounty = unSortedList.get(i).getTotalBounty();
					if(!totalBounty.equals(SimpleBountyResource.SimpleBountyBigDecimalZero)){
						output.add(unSortedList.get(i).getName() + ": " + unSortedList.get(i).getTotalBounty()); 
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
	 * Handles the actual transaction of bounties. Takes into account the economy system. Gives the recipient money
	 * @param recipient
	 * @param amount
	 */
	public void transactionHandler(String recipient, BigDecimal amount){
		
		if(!conf.getuseEcon()){ 
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
	private boolean placebountyHandler(Player placer, String victim, BigDecimal amount){
		PlayerProfile victimProfile = saveData.getPlayerProfile(victim);
		if(!conf.getuseEcon()){
			Inventory placerInv = placer.getInventory(); //Creates a new item stack with the bounty, then checks to see if they have the amount listed. 
			ItemStack cost = new ItemStack(Material.GOLD_INGOT,amount.intValue());
			if(!placerInv.contains(Material.GOLD_INGOT, amount.intValue())){ 
				return false;
			}  
			
			victimProfile.addPlayerSetBounty(amount);
			placerInv.removeItem(cost); 
			return true; 
		}else{
			
			if(!econ.has(placer.getName(), amount.doubleValue())){ 
				return false; 
			}
			
			EconomyResponse placerEcon = econ.withdrawPlayer(placer.getName(),amount.doubleValue());
			if(!placerEcon.transactionSuccess()){
				SimpleBounty.logger.log(Level.SEVERE,String.format(SimpleBountyResource.getTransactionErrorMessage(), victim, placerEcon.errorMessage));
				return false; 
			}
			
			victimProfile.addPlayerSetBounty(amount); 
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



