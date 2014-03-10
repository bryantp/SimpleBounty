package com.bryantp.SimpleBounty;

import java.math.BigDecimal;
import java.util.ArrayList;
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

import com.bryantp.SimpleBounty.resource.Resource;

public class SimpleBountyCommandExecutor implements CommandExecutor {
	private SimpleBounty sp; 
	private Config conf; 
	private SaveData saveData; 
	private final BountyListener eListener; 
	
	public SimpleBountyCommandExecutor(SimpleBounty sp, BountyListener eListener, Config conf, SaveData saveData){
		this.sp = sp; 
		this.eListener = eListener;
		this.saveData = saveData; 
		this.conf = conf; 
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {  
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
			return bountyListCommand(player, args); 
		}
		
		//Place bounty command
		else if(cmd.getName().equalsIgnoreCase("placebounty")){
			return placeBountyCommand(player, args); 
		}
		
		//Set set communal bounty command 
		else if(cmd.getName().equalsIgnoreCase("setcommunalbounty")){
			return setCommunalBountyCommand(player, args); 
		}
		
		//Set player set bounty command 
		else if(cmd.getName().equalsIgnoreCase("setplayersetbounty")){
			return setPlayerSetBountyCommand(player, args); 
		}
		
		//Add Bounty command
		else if(cmd.getName().equalsIgnoreCase("addcommunalbounty")){
			return addCommunalBountyCommand(player,args); 
		}
		
		//Add player set bounty command 
		else if(cmd.getName().equalsIgnoreCase("addplayersetbounty")){
			return addPlayerSetBountyCommand(player,args); 
		}
		
		//Bounty Reload Command
		else if(cmd.getName().equalsIgnoreCase("bountyreload")){
			return bountyReloadCommand(player, args);
			}
		
		//Bounty info command
		else if(cmd.getName().equalsIgnoreCase("bountyinfo")){
			return bountyInfoCommand(player);
		}
		
		//Convert DB command 
		else if(cmd.getName().equalsIgnoreCase("convertdb")){
			return convertDBCommand(player); 
		}
		
		//Bounty reload command 
		else if(cmd.getName().equalsIgnoreCase("bountyload")){
			return bountyLoadCommand(player); 
		}
		
		//Pay bounty command 
		else if(cmd.getName().equalsIgnoreCase("paybounty")){
			return payBountyCommand(player, args); 
		}
		
		return false; 
			
	}
	
	//Bounty command 
	private boolean bountyCommand(Player player, String[] args){
		
		if(!player.hasPermission("bounty.bounty")){
			noPermissions(player);
			return true; 
		}
		
		if(args.length == 0){
			player.sendMessage(ChatColor.DARK_GRAY + "Your bounty is: " + eListener.getplayerBounty(player.getName()));
		}
		
		else if(args.length == 1){
			
			if(args[0].equalsIgnoreCase("ps") || args[0].equalsIgnoreCase("playerset")){
				player.sendMessage(ChatColor.DARK_GRAY + "Your player set bounty " + saveData.getPlayerProfile(player.getName()).getPlayerSetBounty());
			}
			
			else if(args[0].equalsIgnoreCase("c") || args[0].equalsIgnoreCase("communal")){
				player.sendMessage(ChatColor.DARK_GRAY + "Your communal set bounty " + saveData.getPlayerProfile(player.getName()).getCommunalBounty());
			}
			
			else if(sp.checkExists(args[0])){
				player.sendMessage(ChatColor.DARK_GRAY + "The bounty for " + args[0] + " is " + eListener.getplayerBounty(args[0]));
			}
				
			else player.sendMessage(ChatColor.RED + args[0] + " doesn't exist");
				
		}
		
		else if(args.length == 2){
			if(sp.checkExists(args[1])){
				if(args[0].equalsIgnoreCase("ps") || args[0].equals("playerset")){
					player.sendMessage(ChatColor.DARK_GRAY + "The player set bounty for " + args[1] + " is " + saveData.getPlayerProfile(args[1]).getPlayerSetBounty());
				}
				
				else if(args[0].equalsIgnoreCase("communal") || args[0].equals("c")){
					player.sendMessage(ChatColor.DARK_GRAY + "The communal bounty for " + args[1] + " is " + saveData.getPlayerProfile(args[1]).getCommunalBounty());
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
	private boolean bountyListCommand(Player player, String[] args){
		if(player.hasPermission("bounty.bountylist")){
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
		else{
			player.sendMessage(ChatColor.RED + "You do not have permission"); 
		}
		return true; 
	}
	
	//Place bounty command 
	private boolean placeBountyCommand(Player player, String[] args){
		if(!conf.getpsEnable()){
			player.sendMessage(ChatColor.RED + "Player placed bounties have been disabled!");
			return true; //Going to return true since this is a config issue. 
		}
		
		else{
			if(player.hasPermission("bounty.placebounty")){
				if(args.length != 2){
					player.sendMessage(ChatColor.RED + "Incorrect number of arguments");
					return false; 
				}
		
				else{
					if(sp.checkExists(args[0])){
						BigDecimal value;
						try{
							value = new BigDecimal(args[1]);
						} catch(NumberFormatException  e){
							player.sendMessage(ChatColor.RED + "Please enter a number");
							SimpleBounty.logger.log(Level.INFO, "Can't convert " + args[1] + " " + e.getCause() + " " + e.getMessage());
							return true;
						}
					
						if(player.getName().equals(args[0])){
							player.sendMessage(ChatColor.RED + "You can't place a bounty on yourself"); 
						}else if(value.signum() < 0){
							player.sendMessage(ChatColor.RED + "Please enter a value above 0"); 
						}else if(value.compareTo(conf.getMin()) == -1 && conf.getMin() != BigDecimal.ZERO){
							player.sendMessage(ChatColor.RED + "Please enter a value greater than " + conf.getMin()); 
						}else if(value.compareTo(conf.getMax()) == 1){
							player.sendMessage(ChatColor.RED + "Please enter a value less than " + conf.getMax()); 
						}else{
							eListener.placeBounty(player,args[0], value);
						}
						return true;
					}
					else{
						player.sendMessage(ChatColor.RED + args[0] + " doesn't exist"); 
						return true;
					}			
				}
			}
			else{
				player.sendMessage(ChatColor.RED + "You do not have permission"); 
			}
			return true;
		} 
	}
	
	//Set communal bounty command. 
	private boolean setCommunalBountyCommand(Player player, String[] args){
		if(player.hasPermission("bounty.admin.setcbounty")){
			
			if(args.length != 2){
				player.sendMessage("Wrong amount of arguments");
				return false; 
			}
			
			else if(sp.checkExists(args[0])){
				try{
					if(Integer.parseInt(args[1]) < 0) player.sendMessage(ChatColor.RED + "Please enter a value at or above 0"); 
					else{
						eListener.setcommunalBounty(args[0], new BigDecimal(args[1])); //Going to have to change this. If the player is offline, it will throw an error. 
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
	private boolean setPlayerSetBountyCommand(Player player, String[] args){
		if(player.hasPermission("bounty.admin.setpsbounty")){
			
			if(args.length != 2){
				player.sendMessage("Wrong amount of arguments");
				return false; 
			}
			
			else if(sp.checkExists(args[0])){
				try{
					if(Integer.parseInt(args[1]) < 0) player.sendMessage(ChatColor.RED + "Please enter a value at or above 0");
					else{
						eListener.setplayersetBounty(args[0],new BigDecimal(args[1])); 
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
	private boolean addCommunalBountyCommand(Player player, String[] args){
		if(player.hasPermission("bounty.admin.addcbounty")){
			
			if(args.length != 2){
				player.sendMessage(ChatColor.RED + "Wrong amount of arguments");
				return false; 
			}
			
			else if(sp.checkExists(args[0])){
				try{
				if(Integer.parseInt(args[1]) <= 0) player.sendMessage(ChatColor.RED + "Please enter a value above 0");
				else{
					eListener.addtocommunalBounty(args[0],new BigDecimal(args[1])); 
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
	private boolean addPlayerSetBountyCommand(Player player, String[] args){
		if(player.hasPermission("bounty.admin.addpsbounty")){
			
			if(args.length != 2){
				player.sendMessage(ChatColor.RED + "Wrong amount of arguments");
				return false; 
			}
			
			else if(sp.checkExists(args[0])){
				try{
					if(Integer.parseInt(args[1]) <= 0) player.sendMessage(ChatColor.RED + "Please enter a value above 0");
					else{			
							eListener.addtoplayersetBounty(args[0],new BigDecimal(args[1])); 
							player.sendMessage(Resource.positiveMessageColor + String.format(Resource.getAddedToBountyMessage(),args[0],args[1]));
					} 
					return true; 
				}
				catch(Exception e){
					player.sendMessage(Resource.negativeMessageColor + Resource.getPleaseEnterNumberMessage());
					return true; 
				}
			}
			
			
			else{
				player.sendMessage(Resource.negativeMessageColor + Resource.getPlayerDoesntexistMessage()); 
				return true; 
			}
			
		}
		
		else{
			player.sendMessage(Resource.negativeMessageColor + Resource.getNoPermissionMessage());
			return true; 
		}
	}
	
	
	//Reloads simpleBounty
	private boolean bountyReloadCommand(Player player, String[] args){
		if(player.hasPermission("bounty.admin.reloadbounty")){
			saveData.reload(); 
			sp.reloadConfig();
			conf.loadConfig();
			player.sendMessage(ChatColor.GREEN + "Reloaded SimpleBounty"); 
			return true; 
		}
		noPermissions(player);
		return true;
	} 
	
	//Displays SimpleBounty information 
	private boolean bountyInfoCommand(Player player){
		if(player.hasPermission("bounty.admin.bountyinfo")){
		
			PluginDescriptionFile spDescrip = sp.getDescription(); 
			player.sendMessage(ChatColor.GREEN + "You are running SimpleBounty V" + spDescrip.getVersion());
			player.sendMessage(ChatColor.GREEN + "Using Economy: " + conf.getuseEcon()); 
			player.sendMessage(ChatColor.GREEN + "Using SQL: " + conf.getuseSQL());
			player.sendMessage(ChatColor.GREEN + "Player Set Bounties enabled?: " + conf.getpsEnable()); 
			player.sendMessage(ChatColor.GREEN + "Communal Bounties enabled?: " + conf.getcommunalEnable()); 
			player.sendMessage(ChatColor.GREEN + "Min PlayerSet Bounty: " + conf.getMin());
			player.sendMessage(ChatColor.GREEN + "Max PlayerSet Bounty: " + conf.getMax());
			player.sendMessage(ChatColor.GREEN + "Show Killer Message: " + conf.getshowkillerMessage());
			player.sendMessage(ChatColor.GREEN + "Show Victim Message: " + conf.getshowvictimMessage());
			player.sendMessage(ChatColor.GREEN + "Require Bounty Hunter License? (needbountylicense): " + conf.getNeedBountyLicense());
			player.sendMessage(ChatColor.GREEN + "Killing wanted men lowers your own bounty? (bountydecreaseonkill)" + conf.getBountyDecreaseOnKill());
			player.sendMessage(ChatColor.GREEN + "Total number of Bounties: " + saveData.getNumberOfBounties());
		}else{
			noPermissions(player); 
		}
		
		return true; 
	}
	
	private void noPermissions(Player player){
		player.sendMessage(ChatColor.RED + "You do not have permission to do that."); 
	}
	
	//Convert DB command. 
	private boolean convertDBCommand(Player player){
		if(player.hasPermission("bounty.admin.convertdb")){
			saveData.convertDB(player); 
			return true; 
		} 
		noPermissions(player); 
		return true; 
	}
	
	
	//Loads from the Database of FlatFile. 
	private boolean bountyLoadCommand(Player player){
		if(player.hasPermission("bounty.admin.bountyload")){
			saveData.load(); 
			if(conf.getuseSQL()) {
				player.sendMessage(ChatColor.GREEN + "Loaded from SQL");
			}else{
				player.sendMessage(ChatColor.GREEN + "Loaded from Save File");
			}
		
			return true; 
		}
		
		player.sendMessage(ChatColor.RED + "You do not have permission to do that"); 
		return false; 
	}
	
	//paybounty command 
	private boolean payBountyCommand(Player player, String[] args){
		if(player.hasPermission("bounty.paybounty")){
			boolean useEcon = conf.getuseEcon();   
			PlayerProfile p = saveData.getPlayerProfile(player.getName()); //Get a playerProfile object 
			BigDecimal amount;
		
			try{
				amount = new BigDecimal(args[1]);
			}catch(Exception e){
				player.sendMessage(ChatColor.RED + "Please enter a number"); 
				return false; 
			}
			
		
			if(useEcon){
				if(args[0].equals("c")){
						
					if(p.getPlayerSetBounty().compareTo(amount) == -1){
						amount = p.getCommunalBounty();  //If a person tries to pay more than what their bounty is, only make their bounty 0. 
					}
					
					if(!conf.getEconomy().has(player.getName(), amount.doubleValue())){
						player.sendMessage(ChatColor.RED + "You do not have enough money"); 
						return true; 
					}
					p.setCommunalBounty(p.getCommunalBounty().subtract(amount)); //Subtract the amount the player wants from their bounty. 
					conf.getEconomy().withdrawPlayer(player.getName(),amount.doubleValue()); //Withdraws from the player's account. 
					player.sendMessage(ChatColor.GREEN + "You paid " + amount + " on your bounty.\nYour current communal bounty is " + p.getCommunalBounty()); 
					return true; 
				} 
					
				else if(args[0].equalsIgnoreCase("ps")){
						
					if(p.getPlayerSetBounty().compareTo(amount) == -1){
						amount = p.getPlayerSetBounty(); 
					}
					
					if(!conf.getEconomy().has(player.getName(), amount.doubleValue())){
						player.sendMessage(ChatColor.RED + "You do not have enough money"); 
						return true; 
					}
					p.setPlayerSetBounty(p.getPlayerSetBounty().subtract(amount)); 
					conf.getEconomy().withdrawPlayer(player.getName(),amount.doubleValue()); //Withdraws from the player's account. 
					player.sendMessage(ChatColor.GREEN + "You paid " + amount + " on your bounty.\nYour current player set bounty is " + p.getPlayerSetBounty()); 
					return true; 
				}
					
				return false; 
			
			}
		
			else{
				Inventory playerInv = player.getInventory(); //Creates a new item stack with the bounty, then checks to see if they have the amount listed. 
				int amountInt = amount.intValue();
				if(args[0].equalsIgnoreCase("c")){
					
					if(p.getCommunalBounty().intValue() < amountInt){
						amount = p.getCommunalBounty(); 
					}
					
					ItemStack cost = new ItemStack(Material.GOLD_INGOT,amountInt);
					
					if(!playerInv.contains(Material.GOLD_INGOT, amountInt)){
						player.sendMessage(ChatColor.RED + "You do not have enough money"); 
						return true; 
					}
					
					p.setCommunalBounty(new BigDecimal(p.getCommunalBounty().intValue() - amountInt));
					playerInv.remove(cost); 
					player.sendMessage(ChatColor.GREEN + "You paid " + amount + " on your bounty.\nYour current communal bounty is " + p.getCommunalBounty()); 
					return true; 
				}
			
				if(args[0].equalsIgnoreCase("ps")){
					
					if(p.getPlayerSetBounty().intValue() < amountInt){
						amount = p.getPlayerSetBounty(); 
					}
					
					ItemStack cost = new ItemStack(Material.GOLD_INGOT,(int)amountInt);
					if(!playerInv.contains(Material.GOLD_INGOT, amountInt)){
						player.sendMessage(ChatColor.RED + "You do not have enough money"); 
						return false; 
					}  
					
					p.setPlayerSetBounty(new BigDecimal(p.getPlayerSetBounty().intValue() - amountInt)); 
					playerInv.remove(cost); 
					player.sendMessage(ChatColor.GREEN + "You paid " + amount + " on your bounty.\nYour current player set bounty is " + p.getPlayerSetBounty()); 
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
}
