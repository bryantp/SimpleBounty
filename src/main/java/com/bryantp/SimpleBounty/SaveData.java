package com.bryantp.SimpleBounty;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import lib.PatPeter.SQLibrary.MySQL;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.bryantp.SimpleBounty.base.IDatabaseHandler;
import com.bryantp.SimpleBounty.resource.SimpleBountyResource;

/**
 * Responsible for the handling Data including saving and retrieval. 
 * @author Bryan
 *
 */
public class SaveData {
		
	private static File saveFile = new File(SimpleBountyResource.saveFile);
	private static File directory = new File(SimpleBountyResource.directory);
	private static File configFile = new File(SimpleBountyResource.configFile);
	
	private boolean useSQL; 
	private IDatabaseHandler databaseHandler;
	private Map<String, PlayerProfile> playerList;
	
	private Config conf; 
	
	public SaveData(Config conf){
		 this.conf = conf; 
		 this.useSQL = conf.getuseSQL(); 
	}
	
	/**
	 * Sets up the SQL connections needed to save
	 */
	public void setup(){
		 if(useSQL){
			 databaseHandler = new SimpleBountyMySQL(conf.getHost(),conf.getPort(),conf.getDatabase(),conf.getUserName(),conf.getPassword());
 		 }
		 
		 createFileandDirectory(); 	
	}
	
	/**
	 * Converts between SQL and flatfiles. 
	 */
	@SuppressWarnings("unchecked")
	public void convertDB(Player player){
		//Code to convert from Flatfile to SQL and vice versa. 
		if(saveFile.exists() && useSQL){
			SimpleBounty.logger.log(Level.INFO,SimpleBountyResource.getConvertingToSQLMessage());
			FileInputStream input;
			try {
				input = new FileInputStream(saveFile);
				ObjectInputStream objStream = new ObjectInputStream(input); 
				Object inputObj = objStream.readObject();
				databaseHandler.push((HashMap<String, PlayerProfile>) inputObj);
				player.sendMessage(SimpleBountyResource.positiveMessageColor + SimpleBountyResource.getConvertedToSQLMessageMessage()); 
				input.close();
				objStream.close();
			} catch (Exception e) {
				player.sendMessage(SimpleBountyResource.negativeMessageColor + SimpleBountyResource.getErrorConvertingToSQLMessage()); 
				e.printStackTrace();
			}
			
			if(saveFile.renameTo(new File(SimpleBountyResource.saveFileRename))){
				player.sendMessage(SimpleBountyResource.positiveMessageColor + SimpleBountyResource.getRenameFlatFileSuccessMessage());  
			}else{
				player.sendMessage(SimpleBountyResource.negativeMessageColor + SimpleBountyResource.getRenameFlatFileFailure()); 
			}
			
		}else if(!saveFile.exists() && !useSQL){
			SimpleBounty.logger.log(Level.INFO,SimpleBountyResource.getConvertingToFlatFileMessage());
			playerList = databaseHandler.pull();
			try {
				saveFile.createNewFile();
				save(); 
				player.sendMessage(SimpleBountyResource.positiveMessageColor+ SimpleBountyResource.getConvertedToFlatFileMessage()); 

			} catch (IOException e) {
				player.sendMessage(SimpleBountyResource.negativeMessageColor + SimpleBountyResource.getErrorConvertingToFlatFileMessage()); 
				e.printStackTrace();
			} 
		}
	}
	
	/**
	 * Create the Save File
	 */
	public void createFileandDirectory(){
		
		
		if(!directory.exists()){
			directory.mkdir(); 
			 
		}
		
		if(!saveFile.exists() && !useSQL){
			try {
				saveFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			} 
		}
		
		if(!configFile.exists()){
			try{
				configFile.createNewFile();
			}
			catch(Exception e){
				e.printStackTrace(); 
			}
		}
		
	}
		
	/**
	 * Loads the Object from the data file, and assigns the hashmap to it. 
	 */
	@SuppressWarnings("unchecked")
	public void load(){
		if(useSQL){
			playerList = databaseHandler.pull();
		}
		else{
			try {
				FileInputStream input = new FileInputStream(saveFile);
				ObjectInputStream objStream = new ObjectInputStream(input); 
				Object inputObj = objStream.readObject();
				if(inputObj instanceof Map){
					playerList = (HashMap<String, PlayerProfile>) inputObj; 
				}
				
				else{ 
					playerList = new HashMap<String, PlayerProfile>(); 
				}
				input.close();
				objStream.close(); 
			
			} 
			catch (EOFException e) {
				//First start. The save data has nothing in it
				playerList = new HashMap<String, PlayerProfile>();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} 
		}
	}
	
	/**
	 * Saves the HashMap to disk. 
	 */
	public void save(){
		ObjectOutputStream objStream = null;
		if(useSQL){
			databaseHandler.push(playerList); 
		}
		
		else{
			try{
				FileOutputStream output = new FileOutputStream(saveFile);
				objStream = new ObjectOutputStream(output); 
				objStream.writeObject(playerList);
			}
			
			catch (Exception e){
				e.printStackTrace(); 
			}
			finally{
				try {
					objStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	
	/**
	 * Saves the current HashMap to Disk. Deletes it. Then reloads it from Disk. 
	 */
	public void reload(){
		save();
		playerList = null;
		load();
	}
		
	public boolean playerExists(String playerName){
		return playerList.containsKey(playerName);
	}
	
	public PlayerProfile getPlayerProfile(String playerName){
		return playerList.get(playerName);
	}
	
	public void addPlayerProfile(String playerName){
		playerList.put(playerName,new PlayerProfile(playerName));
	}
	
	public Map<String,PlayerProfile> getPlayerList(){
		return playerList;
	}
	
	/**
	 * Returns the number of players who have a bounty > 0
	 * @return
	 */
	public int getNumberOfBounties(){
		int num = 0;
		for(Map.Entry<String, PlayerProfile> entry : playerList.entrySet()){
			if(entry.getValue().getTotalBounty().signum() > 0){
				num++;
			}
		}
		return num;
	}
	
	
	//Going to implement SQLite as well as flat files and MysQL
	@SuppressWarnings("unused")
	private class SimpleBountySQLite implements IDatabaseHandler{

		@Override
		public void setupConnection() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void push(Map<String, PlayerProfile> data) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public Map<String, PlayerProfile> pull() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void closeConnection() {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	private class SimpleBountyMySQL implements IDatabaseHandler{
		
		private String host;
		private int port; 
		private String database;
		private String username;
		private String password;
		private MySQL mysql; 
		
		public SimpleBountyMySQL(String host,int port,String database,String username,String password){
			this.host = host;
			this.port = port; 
			this.database = database;
			this.username = username;
			this.password = password; 
		}
		
		public  void setupConnection(){
			mysql = new MySQL(SimpleBounty.logger, "[SimpleBounty] ", host, port, database, username, password);
			if(!mysql.isOpen()){
				mysql.open();
			}
			
			if(!mysql.isTable("simplebounty")){
				String create = SimpleBountyResource.createTableSQL;
				createTable(create);
			}
		}
		
		private boolean createTable(String query){
			PreparedStatement ps;
			try {
				ps = mysql.prepare(query);
				mysql.query(ps);
				return true;
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return false;
		}
		
		/**
		 * Pushes data to the database 
		 * @param data
		 */
		public void push(Map<String,PlayerProfile> data){
			String name = null;
			BigDecimal communalBounty = BigDecimal.ZERO, psBounty = BigDecimal.ZERO; 
			for(Map.Entry<String, PlayerProfile> entry : data.entrySet()){
				name = entry.getKey(); 
				 communalBounty = entry.getValue().getCommunalBounty();
				 psBounty = entry.getValue().getPlayerSetBounty(); 
				 String updateQuery = String.format(SimpleBountyResource.updateOrInsertMySQL,name,communalBounty,psBounty,communalBounty,psBounty);
				 PreparedStatement ps;
				 try {
					 ps = mysql.prepare(updateQuery);
					 mysql.query(ps);
				 } catch (SQLException e) {
					 e.printStackTrace();
				 }
			} 
		}
		
		/**
		 * Pulls data from the database
		 */
		public Map<String, PlayerProfile> pull(){
			Map<String, PlayerProfile> returnMap = new HashMap<String, PlayerProfile>();
			PreparedStatement ps;
			try {
				ps = mysql.prepare(SimpleBountyResource.getAllFromMySQL);
				ResultSet data = mysql.query(ps);
				data.beforeFirst(); 
			
				while(data.next()){
					PlayerProfile player = new PlayerProfile(data.getString(2)); 
					player.setCommunalBounty(new BigDecimal(data.getString(3)));
					player.setPlayerSetBounty(new BigDecimal(data.getString(4)));
					returnMap.put(data.getString(2), player);  
				 } 	
			} catch (SQLException e) {
				e.printStackTrace();
			} 
			return returnMap;
		}
		
		/** 
		 * Closes the SQL connection. 
		 */
		public void closeConnection(){
			mysql.close(); 
		}

	}

}
