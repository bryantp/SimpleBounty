package com.bryantp.SimpleBounty;

import java.io.File;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.logging.Level;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;

import com.bryantp.SimpleBounty.resource.Resource;

public class Config {
	
	private SimpleBounty simplebounty; 
	
	private FileConfiguration fileConfig; 
	
	//Config Settings
	private boolean useEcon = true; 
	private boolean forcegoldecon; 
	private boolean useSQL;   
	private boolean communalEnable, psEnable;;
	private boolean needBountyLicense;
	private boolean bountyDecreaseOnKill; //Killing other players with bounties decreases your own. You also get to keep the gold. 
	private boolean showvictimMessage,showkillerMessage; 
	private BigDecimal increment, decrement; 
	private BigDecimal max,min; 
	
	//Vault
	private Economy econ = null;
	
	//SQl Settings
	private String host;
	private int port;
	private String database;
	private String userName;
	private String password;
	
	private File directory = new File("plugins/SimpleBounty"); 
	
	public Config(SimpleBounty simplebounty){
		this.simplebounty = simplebounty;
		this.simplebounty.getConfig().options().copyDefaults(true);
		loadConfig();
	}
	
	public void loadConfig(){
		this.fileConfig = this.simplebounty.getConfig();
		this.forcegoldecon = fileConfig.getBoolean("forcegoldecon");
		this.showvictimMessage = fileConfig.getBoolean("showvictimmessages");
		this.showkillerMessage = fileConfig.getBoolean("showkillermessages");
		this.increment = new BigDecimal(fileConfig.getDouble("communalbounty.increment")).setScale(2,Resource.rounding);
		this.decrement = new BigDecimal(fileConfig.getDouble("communalbounty.decrement")).setScale(2, Resource.rounding); 
		this.max = new BigDecimal(fileConfig.getDouble("psbounty.max")).setScale(2,Resource.rounding);
		this.min = new BigDecimal(fileConfig.getString("psbounty.min")).setScale(2,Resource.rounding); 
		this.communalEnable = fileConfig.getBoolean("communalbounty.enable");
		this.psEnable = fileConfig.getBoolean("psbounty.enable");
		this.useSQL = fileConfig.getBoolean("MySQL.enabled");
		this.needBountyLicense = fileConfig.getBoolean("needbountylicense");
		this.bountyDecreaseOnKill = fileConfig.getBoolean("communalbounty.bountydecreaseonkill");
		
		if(useSQL){
			loadMySQL();
		}
	}
		
	public void loadMySQL(){
		this.host = fileConfig.getString("MySQL.host");
		this.port = Integer.parseInt(fileConfig.getString("MySQL.port"));
		this.database = fileConfig.getString("MySQL.database");
		this.userName = fileConfig.getString("MySQL.username");
		this.password =  fileConfig.getString("MySQL.password");
		this.useSQL = fileConfig.getBoolean("MySQL.enabled");
	}
	
	/**
	 * Sets up the Economy and Permissions and checks the Config directory
	 * @return
	 */
	public boolean setup(){
		if(!setupEconomy()){
			SimpleBounty.logger.log(Level.INFO,Resource.getNoEconomyFoundMessage());
			useEcon = false; 
		}
		  
		if(forcegoldecon){
			useEcon = false;
		}
		  
		if(directory.exists() && directory.isDirectory()){
			SimpleBounty.logger.log(Level.INFO,"Loading Data");
			fileConfig.get("plugins/SimpleBounty/config.yml"); 
		}
			
		else{
			directory.mkdir();
			fileConfig.options().copyDefaults(true);
			simplebounty.saveConfig();
		}
		
		return true;
	}
	
	/**
	 * Sets up the economy system for the plugin
	 * @return
	 */
	public boolean setupEconomy(){
		if(simplebounty.getServer().getPluginManager().getPlugin("Vault") == null){
			return false; 
		}
		
		RegisteredServiceProvider<Economy> rsp = simplebounty.getServer().getServicesManager().getRegistration(Economy.class); 
		if(rsp == null) return false; 
		
		econ = rsp.getProvider(); 
		return econ != null; 
	}
	
	public Economy getEconomy(){
		return this.econ;
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
	
	public boolean getcommunalEnable(){
		return communalEnable; 
	}
	
	public boolean getshowvictimMessage(){
		return showvictimMessage; 
	}
	
	public boolean getshowkillerMessage(){
		return showkillerMessage; 
	}
	
	public BigDecimal getIncrement(){
		return increment;
	}
	
	public BigDecimal getDecrement(){
		return decrement; 
	}
	
	public BigDecimal getMax(){
		if(max.signum() < 0) {
			return BigDecimal.ZERO; 
		}
		return max;
	}
	
	public BigDecimal getMin(){
		if(min.signum() < 0) {
			return BigDecimal.ZERO;
		}
		
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
		map.put("host", fileConfig.getString("mySQL.host"));
		map.put("port", fileConfig.getString("mySQL.port"));
		map.put("database", fileConfig.getString("mySQL.database"));
		map.put("username", fileConfig.getString("mySQL.username"));
		map.put("password", fileConfig.getString("mySQL.password"));
		return map; 
	}
	
	public boolean getNeedBountyLicense(){
		return this.needBountyLicense;
	}
	
	public boolean getBountyDecreaseOnKill(){
		return this.bountyDecreaseOnKill;
	}

}
