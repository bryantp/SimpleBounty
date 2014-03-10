/**
 * @author Bryan YDD
 * @version 1.6
 * 
 *  A bounty plugin for Minecraft 
 */
package com.bryantp.SimpleBounty;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.bryantp.SimpleBounty.resource.Resource;


public class SimpleBounty extends JavaPlugin{
	
	private SaveData savedata = null;
	private Config conf;  
	private BountyListener bountyListener = null; 
	private SimpleBountyCommandExecutor exec = null;
	public static boolean  useEcon = true; 
	public boolean forcegoldecon; 
	public boolean useSQL; 
	public boolean psEnable; 
	
	public static final Logger logger = Logger.getLogger("SimpleBounty");
	
	public void onEnable(){
		Resource.loadResources();
		conf = new Config(this);
		if(!conf.setup()){
			return;
		}
		
		savedata = new SaveData(conf); 
		bountyListener = new BountyListener(conf);

		getLogger().info(Resource.getPluginEnabledMessage()); 
        bountyListener.setSaveData(savedata);

        this.getServer().getPluginManager().registerEvents(bountyListener,this); 
	
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
				logger.log(Level.INFO, Resource.getReloadingDataMessage());
				savedata.reload(); 
			}
		}, 6000L, 6000L); 
		
		savedata.setup();
		savedata.load();
	}
	
	
	public void onDisable(){
		savedata.save();
		getLogger().info(Resource.getPluginDisabledMessage()); 
	}
		
	
	/**
	 * Checks to see if a player is stored in either offline players or current online players.
	 * @param playerName
	 * @return
	 */
	public boolean checkExists(String playerName){
		OfflinePlayer[] offPlayer = Bukkit.getServer().getOfflinePlayers();
		Player[] onLinePlayer = Bukkit.getServer().getOnlinePlayers(); 
		if(savedata.getPlayerProfile(playerName) == null){
			return false;
		}

		for(OfflinePlayer player : offPlayer){
			if(player.getName().equalsIgnoreCase(playerName)){
				return true;
			}
		}
		
		for(Player player : onLinePlayer){
			if(player.getName().equalsIgnoreCase(playerName)){
				return true; 
			}
		}
		
		return false; 	
	}
}
