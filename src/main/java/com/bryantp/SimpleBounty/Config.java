package com.bryantp.SimpleBounty;

import java.io.File;
import java.util.HashMap;
import java.util.logging.Level;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;

public class Config {
	
	private SimpleBounty simplebounty; 
	
	private FileConfiguration config; 
	
	//Config Settings
	private static boolean  useEcon = true; 
	private boolean forcegoldecon; 
	private boolean useSQL;   
	private boolean incrementnoBounty;
	private boolean communalEnable, psEnable;;
	private boolean showvictimMessage,showkillerMessage; 
	private int increment, decrement; 
	private int max,min; 
	
	//Vault
	private static Economy econ = null;

	private static Permission perms = null; //Get rid of this and use Bukkit
	
	//SQl Settings
	private String host;
	private int port;
	private String database;
	private String userName;
	private String password;
	
	private File directory = new File("plugins\\SimpleBounty"); 
	
	public Config(SimpleBounty simplebounty){
		this.simplebounty = simplebounty;
		
		this.simplebounty.getConfig().options().copyDefaults(true);
		this.config = this.simplebounty.getConfig();
		this.incrementnoBounty = config.getBoolean("communalbounty.incrementnobounty");
		this.forcegoldecon = config.getBoolean("forcegoldecon");
		this.showvictimMessage = config.getBoolean("showvictimmessages");
		this.showkillerMessage = config.getBoolean("showkillermessages");
		this.increment = config.getInt("communalbounty.increment");
		this.decrement = config.getInt("communalbounty.decrement"); 
		this.max = config.getInt("psbounty.max");
		this.min = config.getInt("psbounty.min"); 
		this.communalEnable = config.getBoolean("communalbounty.enable");
		this.psEnable = config.getBoolean("psbounty.enable");
		this.useSQL = config.getBoolean("mySQL.enabled");
		
		if(useSQL){
			this.host = config.getString("mySQL.host");
			this.port = Integer.parseInt(config.getString("mySQL.port"));
			this.database = config.getString("mySQL.database");
			this.userName = config.getString("mySQL.username");
			this.password =  config.getString("mySQL.password");
			this.useSQL = config.getBoolean("mySQL.enabled");
		}
		
		if(!setupEconomy()){
			SimpleBounty.logger.log(Level.INFO,"No economy system detected, defaulting to gold ignots");
			useEcon = false; 
		}
		
		
		if(!setupPermissions()){
	        SimpleBounty.logger.info(String.format("[%s] - Disabled due to no Vault dependency found!", simplebounty.getDescription().getName()));
	        simplebounty.getServer().getPluginManager().disablePlugin(simplebounty);
	        return;
	    }
		  
		if(forcegoldecon) useEcon = false;
		  
	
		  
		  
		if(directory.exists() && directory.isDirectory()){
			SimpleBounty.logger.log(Level.INFO,"Loading Data");
			//bountylistener.load();  Load the save data separate. 
			config.get("plugins/SimpleBounty/config.yml"); 
		}
			
		else{
			directory.mkdir();
			config.options().copyDefaults(true);
			simplebounty.saveConfig();
		}
		
	}
	
	
	public boolean setupEconomy(){
		if(simplebounty.getServer().getPluginManager().getPlugin("Vault") == null){
			return false; 
		}
		RegisteredServiceProvider<Economy> rsp = simplebounty.getServer().getServicesManager().getRegistration(Economy.class); 
		if(rsp == null) return false; 
		
		econ = rsp.getProvider(); 
		return econ != null; 
		
	}
	
	
	
	public boolean setupPermissions(){
		if(simplebounty.getServer().getPluginManager().getPlugin("Vault") == null){
			return false; 
		}
		RegisteredServiceProvider<Permission> rsp = simplebounty.getServer().getServicesManager().getRegistration(Permission.class);
		perms = rsp.getProvider();
		return perms != null; 
	}
	
	
	
	
	
	public boolean getforcegoldecon(){
		return forcegoldecon; 
	}
	
	public boolean getuseEcon(){
		return useEcon; 
	}
	
	public boolean getuseSQL(){
		return useSQL; 
	}
	
	public boolean getpsEnable(){
		return psEnable; 
	}
	
	public boolean getincrementnoBounty(){
		return incrementnoBounty; 
	}
	
	public boolean getcommunalEnable(){
		return communalEnable; 
	}
	
	public boolean getshowvictimMessage(){
		return showvictimMessage; 
	}
	
	public boolean getshowkillerMessage(){
		return showkillerMessage; 
	}
	
	public int getIncrement(){
		return increment;
	}
	
	public int getDecrement(){
		return decrement; 
	}
	
	public int getMax(){
		if(max < 0) return 0; 
		return max;
	}
	
	public int getMin(){
		if(min < 0) return 0;
		return min; 
	}
	
	public String getHost(){
		return this.host;
	}
	
	public void setHost(String host){
		this.host = host;
	}
	
	public int getPort() {
		return port;
	}


	public void setPort(int port) {
		this.port = port;
	}


	public String getDatabase() {
		return database;
	}


	public void setDatabase(String database) {
		this.database = database;
	}


	public String getUserName() {
		return userName;
	}


	public void setUserName(String userName) {
		this.userName = userName;
	}


	public String getPassword() {
		return password;
	}


	public void setPassword(String password) {
		this.password = password;
	}
	
	
	public HashMap<String,String> dbInfo(){
		HashMap<String, String> map = new HashMap<String, String>();  
		map.put("host", config.getString("mySQL.host"));
		map.put("port", config.getString("mySQL.port"));
		map.put("database", config.getString("mySQL.database"));
		map.put("username", config.getString("mySQL.username"));
		map.put("password", config.getString("mySQL.password"));
		return map; 
	}
	
	public Permission getPermissions(){
		return perms; 
	}

}
