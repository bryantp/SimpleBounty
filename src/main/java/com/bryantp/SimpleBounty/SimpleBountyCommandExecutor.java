package com.bryantp.SimpleBounty;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginDescriptionFile;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;


public class SimpleBountyCommandExecutor implements CommandExecutor {
	private SimpleBounty sp; 
	private Config config; 
	private SaveData saveData; 
	private final BountyListener eListener; 
	private final Permission perms; 
	private boolean  psEnable,incrementnoBounty; 
	private BigDecimal max, min; 

	
	public SimpleBountyCommandExecutor(SimpleBounty sp, BountyListener eListener, Config config, SaveData saveData){
		this.sp = sp; 
		this.eListener = eListener;
		this.saveData = saveData; 
		perms = config.getPermissions(); 
		this.config = config; 
		psEnable = config.getpsEnable();
	}
	
	public void setConfigVariables(){
		this.psEnable = config.getpsEnable();
		this.incrementnoBounty = config.getincrementnoBounty();
		this.min = config.getMin();
		this.max = config.getMax();
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label,
			String[] args) {  
		
		
		Player player = null;
		
		if (!(sender instanceof Player)){
			return false;  
		}
		
		player = (Player) sender; 
		//Bounty command 
		if(cmd.getName().equalsIgnoreCase("bounty")){
			return bountyCommand(player, args);
		}
		
		//Bounty list command 
		else if(cmd.getName().equalsIgnoreCase("bountylist")){
			return bountylistCommand(player, args); 
		}
		
		//Place bounty command
		else if(cmd.getName().equalsIgnoreCase("placebounty")){
			return placebountyCommand(player, args); 
		}
		
		//Set set communal bounty command 
		else if(cmd.getName().equalsIgnoreCase("setcommunalbounty")){
			return setcommunalbountyCommand(player, args); 
		}
		
		//Set player set bounty command 
		else if(cmd.getName().equalsIgnoreCase("setplayersetbounty")){
			return setplayersetbountyCommand(player, args); 
		}
		
		//Add Bounty command
		else if(cmd.getName().equalsIgnoreCase("addcommunalbounty")){
			return addcommunalbountyCommand(player,args); 
		}
		
		//Add player set bounty command 
		else if(cmd.getName().equalsIgnoreCase("addplayersetbounty")){
			return addplayersetbountyCommand(player,args); 
		}
		
		//Bounty Reload Command
		else if(cmd.getName().equalsIgnoreCase("bountyreload")){
			return bountyreloadCommand(player, args);
			}
		
		//Bounty info command
		else if(cmd.getName().equalsIgnoreCase("bountyinfo")){
			return bountyinfoCommand(player);
		}
		
		//Convert DB command 
		else if(cmd.getName().equalsIgnoreCase("convertdb")){
			return convertdbCommand(player); 
		}
		
		//Bounty reload command 
		else if(cmd.getName().equalsIgnoreCase("bountyload")){
			return bountyloadCommand(player); 
		}
		
		//Pay bounty command 
		else if(cmd.getName().equalsIgnoreCase("paybounty")){
			return paybountyCommand(player, args); 
		}
		
		return false; 
			
	}
	
	//Bounty command 
	public boolean bountyCommand(Player player, String[] args){
		
		if(!(perms.has(player,"bounty.bounty")) || !(perms.has(player, "bounty.*"))){
			noPermissions(player);
			
			return true; 
		}
		
		if(args.length == 0){
			player.sendMessage(ChatColor.DARK_GRAY + "Your bounty is: " + eListener.getplayerBounty(player.getName()));
		}
		
		else if(args.length == 1){
			
			if(args[0].equalsIgnoreCase("ps") || args[0].equalsIgnoreCase("playerset")){
				player.sendMessage(ChatColor.DARK_GRAY + "Your player set bounty " + saveData.getPlayerProfile(player.getName()).getplayersetBounty());
			}
			
			else if(args[0].equalsIgnoreCase("c") || args[0].equalsIgnoreCase("communal")){
				player.sendMessage(ChatColor.DARK_GRAY + "Your communal set bounty " + saveData.getPlayerProfile(player.getName()).getcommunalBounty());
			}
			
			else if(sp.checkExists(args[0])){
				player.sendMessage(ChatColor.DARK_GRAY + "The bounty for " + args[0] + " is " + eListener.getplayerBounty(args[0]));
			}
				
			else player.sendMessage(ChatColor.RED + args[0] + " doesn't exist");
				
		}
		
		else if(args.length == 2){
			if(sp.checkExists(args[1])){
				if(args[0].equalsIgnoreCase("ps") || args[0].equals("playerset")){
					player.sendMessage(ChatColor.DARK_GRAY + "The player set bounty for " + args[1] + " is " + saveData.getPlayerProfile(args[1]).getplayersetBounty());
				}
				
				else if(args[0].equalsIgnoreCase("communal") || args[0].equals("c")){
					player.sendMessage(ChatColor.DARK_GRAY + "The communal bounty for " + args[1] + " is " + saveData.getPlayerProfile(args[1]).getcommunalBounty());
				}
			
				else player.sendMessage(ChatColor.RED + "Please either enter playerset (ps) or communal(c)");
			}
			
			else player.sendMessage(ChatColor.RED + "Player doesn't exist");
		}
		
		else{
			player.sendMessage(ChatColor.RED + "Wrong amount of arguments");
			return false;
		}
		
			return true; 
		} 
	
	//Bounty list command (blist) 
	public boolean bountylistCommand(Player player, String[] args){
		if(perms.has(player,"bounty.bountylist") || perms.has(player,"bounty.*")){
			ArrayList<String> top = eListener.calculateTop(); 
			if(top.size() == 0 || top.get(0).equalsIgnoreCase("There are no bounties to display!")){
				player.sendMessage(ChatColor.DARK_GRAY + "There are no bounties to display!"); 
				return true; 
			}
			player.sendMessage(ChatColor.DARK_GRAY + "Top ten players:"); 
			for(String items : top){
				player.sendMessage(ChatColor.DARK_GRAY + items); 
			}
			return true; 
		}
		else player.sendMessage(ChatColor.RED + "You do not have permission"); 
		return true; 
	}
	
	//Place bounty command 
	public boolean placebountyCommand(Player player, String[] args){
		if(!psEnable){
			player.sendMessage(ChatColor.RED + "Player placed bounties have been disabled!");
			return true; //Going to return true since this is a config issue. 
		}
		
		else{
			if(perms.has(player,"bounty.placebounty") || perms.has(player, "bounty.*")){
				if(args.length != 2){
					player.sendMessage(ChatColor.RED + "Incorrect number of arguments");
					return false; 
				}
		
				else{
					if(sp.checkExists(args[0])){
						try{
							if(player.getName().equals(args[0])){
								player.sendMessage(ChatColor.RED + "You can't place a bounty on yourself"); 
							}
							else if(Integer.parseInt(args[1]) <= 0){
								player.sendMessage(ChatColor.RED + "Please enter a value above 0"); 
							}
							else if(new BigDecimal(Integer.parseInt(args[1])).compareTo(min) == -1 && min != BigDecimal.ZERO){
								player.sendMessage(ChatColor.RED + "Please enter a value greater than " + min); 
							}
							else if(new BigDecimal(Integer.parseInt(args[1])).compareTo(max) == 1){
								player.sendMessage(ChatColor.RED + "Please enter a value less than " + max); 
							}

							return true;
						}
						catch(Exception e){
							player.sendMessage(ChatColor.RED + "Please enter a number E:" + e.getMessage());
							SimpleBounty.logger.log(Level.INFO, "Can't convert " + args[1] + " " + e.getMessage());
							return true;
						}
					}
					else{
						player.sendMessage(ChatColor.RED + args[0] + " doesn't exist"); 
						return true;
					}
			
				}
		
			}
			else player.sendMessage(ChatColor.RED + "You do not have permission"); 
			return true;
		} 
	}
	
	//Set communal bounty command. 
	public boolean setcommunalbountyCommand(Player player, String[] args){
		if(perms.has(player,"bounty.admin.*") || perms.has(player,"bounty.admin.setbounty")){
			
			if(args.length != 2){
				player.sendMessage("Wrong amount of arguments");
				return false; 
			}
			
			else if(sp.checkExists(args[0])){
				try{
					if(Integer.parseInt(args[1]) < 0) player.sendMessage(ChatColor.RED + "Please enter a value at or above 0"); 
					else{
						eListener.setcommunalBounty(args[0], new BigDecimal(Integer.parseInt(args[1]))); //Going to have to change this. If the player is offline, it will throw an error. 
						player.sendMessage(ChatColor.GREEN + "Set " + args[0] +"'s bounty to " + args[1]);
					} 
					return true; 
				}
				catch(Exception e){
					player.sendMessage(ChatColor.RED + "Please enter a number");
					return true;
				}
			}
			
			else{
				player.sendMessage(ChatColor.RED + "Player doesn't exist"); 
			}
			
		}
		
		else{
			player.sendMessage(ChatColor.RED + "You do not have permission to do that");
			return true; 
		}
		return false;
	}
	
	//set player set bounty command. 
	public boolean setplayersetbountyCommand(Player player, String[] args){
		if(perms.has(player,"bounty.admin.*") || perms.has(player,"bounty.admin.setbounty")){
			
			if(args.length != 2){
				player.sendMessage("Wrong amount of arguments");
				return false; 
			}
			
			else if(sp.checkExists(args[0])){
				try{
					if(Integer.parseInt(args[1]) < 0) player.sendMessage(ChatColor.RED + "Please enter a value at or above 0");
					else{
						eListener.setplayersetBounty(args[0],new BigDecimal(Integer.parseInt(args[1]))); 
						player.sendMessage(ChatColor.GREEN + "Set " + args[0] +"'s bounty to " + args[1]);
					} 
				return true; 
				}
				catch(Exception e){
					player.sendMessage(ChatColor.RED + "Please enter a number");
					return true;
				}
			}
			
			else{
				player.sendMessage(ChatColor.RED + "Player doesn't exist"); 
				return true; 
			}
			
		}
		
		else{
			player.sendMessage(ChatColor.RED + "You do not have permission to do that");
			return true; 
		}
	}
	
	//Add communal bounty command. 
	public boolean addcommunalbountyCommand(Player player, String[] args){
		if(perms.has(player,"bounty.admin.*") || perms.has(player,"bounty.admin.addbounty:")){
			
			if(args.length != 2){
				player.sendMessage(ChatColor.RED + "Wrong amount of arguments");
				return false; 
			}
			
			else if(sp.checkExists(args[0])){
				try{
				if(Integer.parseInt(args[1]) <= 0) player.sendMessage(ChatColor.RED + "Please enter a value above 0");
				else{
					eListener.addtocommunalBounty(args[0],new BigDecimal(Integer.parseInt(args[1]))); 
					player.sendMessage(ChatColor.GREEN + "Added " + args[1] + " to " + args[0]  + "'s bounty");
					} 
					return true;
				}
				catch(Exception e){
					player.sendMessage(ChatColor.RED + "Please enter a number");
					return true; 
					}
			}
			
			else{
				player.sendMessage(ChatColor.RED + "Player doesn't exist"); 
				return true; 
			}
			
		}
		
		else{
			player.sendMessage(ChatColor.RED + "You do not have permission to do that");
			return true; 
		}
	}
	
	//Add player set bounty command
	public boolean addplayersetbountyCommand(Player player, String[] args){
		if(perms.has(player,"bounty.admin.*") || perms.has(player,"bounty.admin.addbounty:")){
			
			if(args.length != 2){
				player.sendMessage(ChatColor.RED + "Wrong amount of arguments");
				return false; 
			}
			
			else if(sp.checkExists(args[0])){
				try{
					if(Integer.parseInt(args[1]) <= 0) player.sendMessage(ChatColor.RED + "Please enter a value above 0");
					else{
					
							eListener.addtoplayersetBounty(args[0],new BigDecimal(Integer.parseInt(args[1]))); 
							player.sendMessage(ChatColor.GREEN + "Added " + args[1] + " to " + args[0]  + "'s bounty");
					
					} 
					return true; 
				}
				catch(Exception e){
					player.sendMessage(ChatColor.RED + "Please enter a number");
					return true; 
				}
			}
			
			
			else{
				player.sendMessage(ChatColor.RED + "Player doesn't exist"); 
				return true; 
			}
			
		}
		
		else{
			player.sendMessage(ChatColor.RED + "You do not have permission to do that");
			return true; 
		}
	}
	
	
	//Reloads simpleBounty
	public boolean bountyreloadCommand(Player player, String[] args){
		if(perms.has(player,"bounty.admin.*") || perms.has(player,"bounty.admin.addbounty:")){
			saveData.reload(); 
			sp.reloadConfig(); 
			//savedata.setConfig();
			//savedata.setupDB(false); 
			player.sendMessage(ChatColor.GREEN + "Reloaded SimpleBounty"); 
			return true; 
		}
	
	
		return false;
	} 
	
	//Displays SimpleBounty information 
	public boolean bountyinfoCommand(Player player){
		if(perms.has(player,"bounty.admin.*") || perms.has(player,"bounty.admin.bountyinfo:")){
		
			PluginDescriptionFile spDescrip = sp.getDescription(); 
			player.sendMessage(ChatColor.GREEN + "You are running SimpleBounty V" + spDescrip.getVersion());
			player.sendMessage(ChatColor.GREEN + "Using Economy: " + eListener.getUseEcon()); 
			player.sendMessage(ChatColor.GREEN + "Using SQL: " + eListener.usingSQL());
			player.sendMessage(ChatColor.GREEN + "Player Set Bounties enabled?: " + psEnable); 
			player.sendMessage(ChatColor.GREEN + "Communal Bounties enabled?: " + config.getcommunalEnable()); 
			player.sendMessage(ChatColor.GREEN + "The killer's bounty will not change if they kill someone with a bounty on their head: " + incrementnoBounty);
			player.sendMessage(ChatColor.GREEN + "Total number of Bounties: " + saveData.getNumberOfBounties());
			for(Map.Entry<String, PlayerProfile> entry : saveData.getplayerList().entrySet()){
				if(entry.getValue() != null) {
					PlayerProfile p = entry.getValue();
					if(p.gettotalBounty().signum() > 0){
						player.sendMessage(ChatColor.GREEN + p.getName() + " Communal: " + p.getcommunalBounty() + " Playeset: " + p.getplayersetBounty() + " Total: " + p.gettotalBounty());
					}
				}
			}
		} 
		
		else{
			noPermissions(player); 
		}
		
		return true; 
	}
	
	//Displayes the no permissions error message. 
	public void noPermissions(Player player){ //Need to convert over other commands to use this. 
		player.sendMessage(ChatColor.RED + "You do not have permission to do that."); 
	}
	
	//Convert DB command. 
	public boolean convertdbCommand(Player player){
		if(perms.has(player,"bounty.admin.*") || perms.has(player,"bounty.admin.convertdb:")){
			saveData.convertDB(player); 
			return true; 
		} 
		player.sendMessage(ChatColor.RED + "You do not have permission to do that"); 
		return false; 
	}
	
	
	//Loads from the Database of FlatFile. 
	public boolean bountyloadCommand(Player player){
		if(perms.has(player, "bounty.admin.*") || perms.has(player, "bounty.admin.bountyload")){
		saveData.load(); 
		if(eListener.usingSQL()) player.sendMessage(ChatColor.GREEN + "Loaded from SQL");
		else player.sendMessage(ChatColor.GREEN + "Loaded from Save File");
		return true; 
		}
		player.sendMessage(ChatColor.RED + "You do not have permission to do that"); 
		return false; 
	}
	
	//paybounty command 
	public boolean paybountyCommand(Player player, String[] args){
		if(perms.has(player, "bounty.paybounty") || perms.has(player, "bounty.*")){
						
			Economy econ = eListener.getEcon(); 
			boolean useEcon = config.getuseEcon();   
			PlayerProfile p = saveData.getPlayerProfile(player.getName()); //Get a playerProfile object 
			BigDecimal amount;
		
			try{
				amount = new BigDecimal(Integer.parseInt(args[1]));
			}catch(Exception e){
				player.sendMessage(ChatColor.RED + "Please enter a number"); 
				return false; 
			}
			
		
			if(useEcon){
				if(args[0].equals("c")){
						
					if(p.getplayersetBounty().compareTo(amount) == -1){
						amount = p.getcommunalBounty();  //If a person tries to pay more than what their bounty is, only make their bounty 0. 
					}
					
					if(!econ.has(player.getName(), amount.doubleValue())){
						player.sendMessage(ChatColor.RED + "You do not have enough money"); 
						return true; 
					}
					p.setcommunalBounty(p.getcommunalBounty().subtract(amount)); //Subtract the amount the player wants from their bounty. 
					econ.withdrawPlayer(player.getName(),amount.doubleValue()); //Withdraws from the player's account. 
					player.sendMessage(ChatColor.GREEN + "You paid " + amount + " on your bounty.\nYour current communal bounty is " + p.getcommunalBounty()); 
					return true; 
				} 
					
				else if(args[0].equalsIgnoreCase("ps")){
						
					if(p.getplayersetBounty().compareTo(amount) == -1){
						amount = p.getplayersetBounty(); 
					}
					
					if(!econ.has(player.getName(), amount.doubleValue())){
						player.sendMessage(ChatColor.RED + "You do not have enough money"); 
						return true; 
					}
					p.setplayersetBounty(p.getplayersetBounty().subtract(amount)); 
					econ.withdrawPlayer(player.getName(),amount.doubleValue()); //Withdraws from the player's account. 
					player.sendMessage(ChatColor.GREEN + "You paid " + amount + " on your bounty.\nYour current player set bounty is " + p.getplayersetBounty()); 
					return true; 
				}
					
				return false; 
			
			}
		
			else{
				Inventory playerInv = player.getInventory(); //Creates a new item stack with the bounty, then checks to see if they have the amount listed. 
				int amountInt = amount.intValue();
				if(args[0].equalsIgnoreCase("c")){
					
					if(p.getcommunalBounty().intValue() < amountInt){
						amount = p.getcommunalBounty(); 
					}
					
					ItemStack cost = new ItemStack(Material.GOLD_INGOT,amountInt);
					
					if(!playerInv.contains(Material.GOLD_INGOT, amountInt)){
						player.sendMessage(ChatColor.RED + "You do not have enough money"); 
						return true; 
					}
					
					p.setcommunalBounty(new BigDecimal(p.getcommunalBounty().intValue() - amountInt));
					playerInv.remove(cost); 
					player.sendMessage(ChatColor.GREEN + "You paid " + amount + " on your bounty.\nYour current communal bounty is " + p.getcommunalBounty()); 
					return true; 
				}
			
				if(args[0].equalsIgnoreCase("ps")){
					
					if(p.getplayersetBounty().intValue() < amountInt){
						amount = p.getplayersetBounty(); 
					}
					
					ItemStack cost = new ItemStack(Material.GOLD_INGOT,(int)amountInt);
					if(!playerInv.contains(Material.GOLD_INGOT, amountInt)){
						player.sendMessage(ChatColor.RED + "You do not have enough money"); 
						return false; 
					}  
					
					p.setplayersetBounty(new BigDecimal(p.getplayersetBounty().intValue() - amountInt)); 
					playerInv.remove(cost); 
					player.sendMessage(ChatColor.GREEN + "You paid " + amount + " on your bounty.\nYour current player set bounty is " + p.getplayersetBounty()); 
					return true; 
				}
				
				return false; 
			}
			
			
		}
		
		else{
			player.sendMessage(ChatColor.RED + "You do not have permission to do that"); 
			return true; 
		}
		
	}
	
	
	/*
	 * Used for the bounty info command. 
	 */
	public void setIncrementNoBounty(boolean incrementnoBounty){
		this.incrementnoBounty = incrementnoBounty;
	}
	
	
}
