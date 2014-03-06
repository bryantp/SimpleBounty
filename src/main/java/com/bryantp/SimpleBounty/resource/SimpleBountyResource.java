package com.bryantp.SimpleBounty.resource;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Properties;

import org.bukkit.ChatColor;

/**
 * Used to store resources
 * @author Bryan
 *
 */
public class SimpleBountyResource {
	
	//Rounding
	public static final RoundingMode rounding = RoundingMode.HALF_EVEN;
	
	//Special Currency Zero
	public static final BigDecimal SimpleBountyBigDecimalZero = BigDecimal.ZERO.setScale(2, rounding);
	
	//File Locations
	public static final String saveFile = "plugins/SimpleBounty/data.dat";
	public static final String directory = "plugins/SimpleBounty";
	public static final String configFile = "plugins/SimpleBounty/config.yml";
	public static final String saveFileRename = "plugins/SimpleBounty/data.dat.OLD";
	
	//MySQl Queries
	public static final String getAllFromMySQL = "SELECT * FROM %s";
	public static final String updateOrInsertMySQL = "INSERT INTO simplebounty (name, communal_bounty, ps_bounty) VALUES (%s,%d,%d) ON DUPLICATE KEY UPDATE communal_bounty=%d, ps_bounty=%d";
	public static final String createTableSQL = "CREATE TABLE simplebounty (id INT NOT NULL AUTO_INCREMENT, name VARCHAR(255), communal_bounty DOUBLE, ps_bounty DOUBLE,primary key (id),UNIQUE (id),UNIQUE (name))";
	
	//Chat Colors
	public static final ChatColor positiveMessageColor = ChatColor.GREEN;
	public static final ChatColor negativeMessageColor = ChatColor.RED;
	public static final ChatColor informationMessageColor = ChatColor.DARK_GRAY;

	/**
	 * Loads the Changeable String resources. Messages for example.
	 */
	public static void loadResources(){
		Properties defaultProps = new Properties();
		try {
			defaultProps.load(SimpleBountyResource.class.getResourceAsStream("/resources/en_US.prop"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		victimMessage = defaultProps.getProperty("victimMessage", "You were Killed for a bounty");
		killerMessage = defaultProps.getProperty("killerMessage", "You killed someone for a bounty");
		bountyNotPlacedMessage = defaultProps.getProperty("bountyNotPlaced","A bounty was not placed on you");
		placeBountyMessageOnPlayer = defaultProps.getProperty("placeBountyMessage","You placed a bounty");
		gotBountyPlacedOnYouMessage = defaultProps.getProperty("gotBountyPlaced","You got a bounty placed on you");
		notEnoughMoneyToPlaceBountyMessage = defaultProps.getProperty("notEnoughMoneyPlaceBounty","Not enough money to place bounty");
		errorTransactionMessage = defaultProps.getProperty("transactionError","There was an error with the transaction");
		convertingToFlatFileMessage = defaultProps.getProperty("convertingToFlatFile","Converting to FlatFile");
		convertingToSQLMessage = defaultProps.getProperty("convertingToSQL","Converting FlatFile to SQL");
		convertedToFlatFileMessage = defaultProps.getProperty("convertedToFlatFile","Converting SQL to FlatFile");
		convertedToSQLMessage = defaultProps.getProperty("convertedToSQL","Converted SQL to FlatFile");
		errorConvertingToSQLMessage = defaultProps.getProperty("errorConvertingToSQL","There was an error converting to SQL");
		renameFlatFileSuccessMessage = defaultProps.getProperty("renameFlatFileSuccess","Renamed FlatFile");
		renameFlatFileFailureMessage = defaultProps.getProperty("renameFlatFileFailure","Unable to rename FlatFile");
		errorConvertingToFlatFileMessage = defaultProps.getProperty("errorConvertingToFlatFile","There was an error converting to FlatFile");
		pluginEnabledMessage = defaultProps.getProperty("pluginEnabled","SimpleBounty has been enabled");
		pluginDisabledMessage = defaultProps.getProperty("pluginDisabled","SimpleBounty has been disabled");
		reloadingDataMessage = defaultProps.getProperty("reloadingData","Reloading Data");
		noEconomyFoundMessage = defaultProps.getProperty("noEconomyFound","No economy system detected, defaulting to gold ingots");
		pluginDisabledDependencyMessage = defaultProps.getProperty("pluginDisabledDependency","[%s] - Disabled due to no Vault dependency found!");
	}
	
	private static String victimMessage;
	private static String killerMessage;
	private static String bountyNotPlacedMessage;
	private static String placeBountyMessageOnPlayer;
	private static String gotBountyPlacedOnYouMessage;
	private static String notEnoughMoneyToPlaceBountyMessage;
	private static String convertingToFlatFileMessage;
	private static String convertingToSQLMessage;
	private static String convertedToFlatFileMessage;
	private static String convertedToSQLMessage;
	private static String errorConvertingToSQLMessage;
	private static String renameFlatFileSuccessMessage;
	private static String renameFlatFileFailureMessage;
	private static String errorConvertingToFlatFileMessage;
	private static String pluginEnabledMessage;
	private static String pluginDisabledMessage;
	private static String reloadingDataMessage;
	private static String noEconomyFoundMessage;
	private static String pluginDisabledDependencyMessage;
	
	//Logger Messages
	private static String errorTransactionMessage;
	
	public static String getVictimMessage(){
		return victimMessage;
	}
	
	public static String getKillerMessage(){
		return killerMessage;
	}
	
	public static String getBountyNotPlacedMessage(){
		return bountyNotPlacedMessage;
	}
	
	public static String getPlaceBountyMessageOnPlayer(){
		return placeBountyMessageOnPlayer;
	}
	
	public static String getBountyPlacedOnYouMessage(){
		return gotBountyPlacedOnYouMessage;
	}
	
	public static String getNotEnoughMoneyToPlaceBountyMessage(){
		return notEnoughMoneyToPlaceBountyMessage;
	}
	
	public static String getTransactionErrorMessage(){
		return errorTransactionMessage;
	}
	
	public static String getConvertingToFlatFileMessage(){
		return convertingToFlatFileMessage;
	}
	
	public static String getConvertingToSQLMessage(){
		return convertingToSQLMessage;
	}
	
	public static String getConvertedToFlatFileMessage(){
		return convertedToFlatFileMessage;
	}
	
	public static String getConvertedToSQLMessageMessage(){
		return convertedToSQLMessage;
	}
	
	public static String getErrorConvertingToSQLMessage(){
		return errorConvertingToSQLMessage;
	}
	
	public static String getRenameFlatFileSuccessMessage(){
		return renameFlatFileSuccessMessage;
	}
	
	public static String getRenameFlatFileFailure(){
		return renameFlatFileFailureMessage;
	}
	
	public static String getErrorConvertingToFlatFileMessage(){
		return errorConvertingToFlatFileMessage;
	}
	
	public static String getPluginEnabledMessage(){
		return pluginEnabledMessage;
	}
	
	public static String getPluginDisabledMessage(){
		return pluginDisabledMessage;
	}
	
	public static String getReloadingDataMessage(){
		return reloadingDataMessage;
	}
	
	public static String getNoEconomyFoundMessage(){
		return noEconomyFoundMessage;
	}
	
	public static String getPluginDisabledDependencyMessage(){
		return pluginDisabledDependencyMessage;
	}

}
