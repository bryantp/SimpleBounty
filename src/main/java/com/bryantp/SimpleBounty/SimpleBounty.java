/**
 * @author Bryan YDD
 * @version 1.5
 * 
 *  A bounty plugin for Minecraft 
 */
package com.bryantp.SimpleBounty;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import net.milkbowl.vault.economy.Economy;

public class SimpleBounty extends JavaPlugin{
	
	SaveData savedata = null;
	Config conf;  
	BountyListener bountyListener = null; 
	SimpleBountyCommandExecutor exec = null;
	public static final Logger logger = Logger.getLogger("SimpleBounty");
	private static Economy econ = null;
	public static boolean  useEcon = true; 
	public boolean forcegoldecon; 
	public boolean useSQL; 
	public boolean psEnable; 
	
	
	
	public void onEnable(){
		setupEconomy();
		bountyListener = new BountyListener(econ, this);
		conf = new Config(this);
		savedata = new SaveData(conf); 
		
         
		getLogger().info("Bounty has been enabled"); 
        bountyListener.setSaveData(savedata);
		bountyListener.setConfig(conf); 

        this.getServer().getPluginManager().registerEvents(bountyListener,this); //Registers the DeathListener. 
	
		exec = new SimpleBountyCommandExecutor(this,bountyListener,conf, savedata);
		getCommand("bountylist").setExecutor(exec);
		getCommand("bounty").setExecutor(exec);
		getCommand("setcommunalbounty").setExecutor(exec);
		getCommand("setplayersetbounty").setExecutor(exec);
		getCommand("addcommunalbounty").setExecutor(exec);
		getCommand("addplayersetbounty").setExecutor(exec);
		getCommand("bountyreload").setExecutor(exec);
		getCommand("placebounty").setExecutor(exec);
		getCommand("bountyinfo").setExecutor(exec);
		getCommand("convertdb").setExecutor(exec); 
		getCommand("bountyload").setExecutor(exec);
		getCommand("paybounty").setExecutor(exec); 
		this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable(){ //Timed to reload the data every 5 minutes or so. 
			public void run(){
				logger.log(Level.INFO, "Reloading Data");
				savedata.reload(); 
			}
		}, 6000L, 6000L); 
		
		savedata.setup();
		savedata.load();
	}
	
	
	public void onDisable(){
		savedata.save();
		getLogger().info("Bounty has been disabled"); 
		
	}
	
	/**
	 * If the Database is enabled, it sets it up. 
	 * @param 
	 *  Determines if the use of a database should be forced. Used for the Conversion command
	 */

	
	/**
	 * If there is an economy plugin enabled, sets up the economy plugin. 
	 * @return
	 */
	public boolean setupEconomy(){
		if(getServer().getPluginManager().getPlugin("Vault") == null){
			return false; 
		}
		RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class); 
		if(rsp == null) return false; 
		
		econ = rsp.getProvider(); 
		return econ != null; 
		
	}

	
	/**
	 * Checks to see if a player is stored in either offline players or current online players.
	 * @param playerName
	 * @return
	 */
	public boolean checkExists(String playerName){
		OfflinePlayer[] offPlayer = Bukkit.getServer().getOfflinePlayers();
		Player[] onLinePlayer = Bukkit.getServer().getOnlinePlayers(); 
		
		for(OfflinePlayer player : offPlayer){
			if(player.getName().equalsIgnoreCase(playerName)) return true;
		}
		
		for(Player player : onLinePlayer){
			if(player.getName().equalsIgnoreCase(playerName)) return true; 
		}
		
		return false; 
		
	}
	


	
	
}
