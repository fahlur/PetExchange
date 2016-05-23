package me.Fahlur.PetExchange;

import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Horse.Variant;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Tameable;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

public class PetExchange extends JavaPlugin implements Listener {

	PetExchange plugin;
	private Logger log;
	Permission permission;
	PluginDescriptionFile pdfFile = this.getDescription();

	
	public static HashMap<String, HashMap<String, String>> petAction = new HashMap<String, HashMap<String, String>>();
	
	
	@Override
	public void onEnable(){
		plugin = this;
		this.log = getLogger();
		setupPermissions();
		Bukkit.getServer().getPluginManager().registerEvents(this, this);
	}
	
	
	public static int randInt(int min, int max) {
	    Random rand = new Random();
	    int randomNum = rand.nextInt((max - min) + 1) + min;
	    return randomNum;
	}
	
	

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (!(sender instanceof Player)){
			log.warning("Commands for this plugin may only be executed by a player!");
			return true;
		}
		
		Player player = (Player) sender;
		
		if (!permission.playerHas(player, "pe.exchangepets")) {
	      player.sendMessage(ChatColor.RED + "Sorry, you don't have permission to use this command.");
	      return true;
	    }
		
		
		if (cmd.getName().equalsIgnoreCase("petexchange")){
			
			player.sendMessage(ChatColor.GREEN + "petExchange version: " + ChatColor.GOLD + pdfFile.getVersion());
			
			
		}
		
		
		
		if (cmd.getName().equalsIgnoreCase("setowner")){
			if (args.length != 1) {
				sender.sendMessage(ChatColor.RED + "Please specify a player to transfer ownership to");
				return true;
			}
			
			if (Bukkit.getPlayerExact(args[0]) == null){
				sender.sendMessage(ChatColor.RED + "Specified player is not online!");
				return true;
			}
			
			sender.sendMessage(ChatColor.GOLD + "Right click a pet within 10 seconds to transfer ownership");
			
			HashMap<String, String> options = new HashMap<String, String>();
			options.put("type", "set");
			options.put("new-owner", args[0]);
			
			petAction.put(sender.getName(), options);
			
			getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
				public void run() {
					if (petAction.containsKey(sender.getName())) {
						HashMap<String, String> options = petAction.get(sender.getName());
						
						if (options.get("type").equals("set")) {
							petAction.remove(sender.getName());
							sender.sendMessage(ChatColor.YELLOW + "Pet ownership transfer expired");
						}
					}
				}
			}, 10 * 20L);
		}
		

		
		if (cmd.getName().equalsIgnoreCase("tame")){
			if (!permission.has(player, "pe.admin")){
				sender.sendMessage(ChatColor.RED + "You do not haver permission to use this command!");
				return true;
			}
			if (args.length != 1) {
				sender.sendMessage(ChatColor.RED + "Please specify a player to set ownership to");
				return true;
			}
			
			if (Bukkit.getPlayerExact(args[0]) == null){
				sender.sendMessage(ChatColor.RED + "Specified player is not online!");
				return true;
			}
			
			sender.sendMessage(ChatColor.GOLD + "Right click a pet within 10 seconds to set ownership");
			
			HashMap<String, String> options = new HashMap<String, String>();
			options.put("type", "tame");
			options.put("new-owner", args[0]);
			
			petAction.put(sender.getName(), options);
			
			getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
				public void run() {
					if (petAction.containsKey(sender.getName())) {
						HashMap<String, String> options = petAction.get(sender.getName());
						
						if (options.get("type").equals("tame")) {
							petAction.remove(sender.getName());
							sender.sendMessage(ChatColor.YELLOW + "Pet instant taming expired");
						}
					}
				}
			}, 10 * 20L);
		}
		
		if (cmd.getName().equalsIgnoreCase("untame")){
			if (!permission.has(player, "pe.admin")){
				sender.sendMessage(ChatColor.RED + "You do not haver permission to use this command!");
				return true;
			}
			
			sender.sendMessage(ChatColor.GOLD + "Right click a pet within 10 seconds to remove ownership");
			
			HashMap<String, String> options = new HashMap<String, String>();
			options.put("type", "untame");
			
			petAction.put(sender.getName(), options);
			
			getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
				public void run() {
					if (petAction.containsKey(sender.getName())) {
						HashMap<String, String> options = petAction.get(sender.getName());
						
						if (options.get("type").equals("untame")) {
							petAction.remove(sender.getName());
							sender.sendMessage(ChatColor.YELLOW + "Pet instant taming expired");
						}
					}
				}
			}, 10 * 20L);
		}
		
		
		if (cmd.getName().equalsIgnoreCase("checkowner")){
			sender.sendMessage(ChatColor.GOLD + "Right click a pet within 10 seconds to check ownership");
			
			HashMap<String, String> options = new HashMap<String, String>();
			options.put("type", "check");
			
			petAction.put(sender.getName(), options);
			
			getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
				public void run() {
					if (petAction.containsKey(sender.getName())) {
						HashMap<String, String> options = petAction.get(sender.getName());
						
						if (options.get("type").equals("check")) {
							petAction.remove(sender.getName());
							sender.sendMessage(ChatColor.YELLOW + "Pet check expired");
						}
					}
				}
			}, 10 * 20L);}
		return true;
	}


	
	
	@EventHandler
	public void EntityDamageByEntity(EntityDamageByEntityEvent event){
		
		Entity damager = event.getDamager();
		Entity ent = event.getEntity();
		String returnMessage = ChatColor.RED + "[petExchange] " + ChatColor.GRAY + "Sorry that animal is protected!";

		if (damager instanceof Projectile){
			Projectile projectile = (Projectile) damager;
			if (projectile.getShooter() instanceof Player){
				
				if (ent instanceof Horse || ent instanceof Wolf || ent instanceof Ocelot){
					if (((Tameable) ent).getOwner() != null && ((Tameable) ent).getOwner() != projectile.getShooter()){
						((CommandSender) projectile.getShooter()).sendMessage(returnMessage);
						event.setCancelled(true);
					}
				}
			}
		}
		
		
		
		if (damager instanceof Player){
			if (ent instanceof Horse || ent instanceof Wolf || ent instanceof Ocelot){
				if (((Tameable) ent).getOwner() != null && ((Tameable) ent).getOwner() != damager){
					damager.sendMessage(returnMessage);
					event.setCancelled(true);
				}
			}
		}
		
	}
	
	
	long time = 0;
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPlayerRightClickEntity(PlayerInteractEntityEvent event){
		
		if (!petAction.containsKey(event.getPlayer().getName())) {
			Player p = event.getPlayer();
		    Entity h = event.getRightClicked();
		    
		    if (h instanceof Horse){
					Horse horse = (Horse) h;
					
					if (time == System.currentTimeMillis()){
						return;
					}
					if (horse.getOwner() != null){
						if (horse.getOwner() != p){
							p.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Hey!" + ChatColor.GRAY + " That is not your horse/mule/donkey!");
							time = System.currentTimeMillis();
							event.setCancelled(true);
							return;
						}
					}
					
					
					
					
					
					if (horse.getAge() != 0){
						horse.setPassenger(p);
						horse.setAgeLock(true);
						return;
					}
					
					
					
					
					if (horse.getVariant().equals(Variant.SKELETON_HORSE)){
						
						if (horse.getOwner() == null){
							int rand = randInt(1,10);
							if (rand < 8){
								horse.getLocation().getWorld().playEffect(horse.getEyeLocation().add(0D, 0D, 0.0D), Effect.VILLAGER_THUNDERCLOUD, null);
								p.getLocation().getWorld().playSound(p.getLocation(), Sound.ENTITY_ZOMBIE_HORSE_DEATH, 0.5F, 1.0F);
								p.sendMessage(ChatColor.DARK_GRAY + "This Skeleton Horse does not accept you as its master!");
							}else{
								p.getLocation().getWorld().playEffect(horse.getLocation().add(0.0D, 0D, 0.0D), Effect.HEART, null);
								p.getLocation().getWorld().playSound(h.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.8F, 0.5F);
								p.sendMessage(ChatColor.DARK_GREEN + "This Skeleton Horse has accepted you as its master!");	
								horse.setOwner(p);
							}
							
						}
						
					}
					return;
		    }
			return;
		}
		
		List<EntityType> validPets = Arrays.asList(EntityType.HORSE, EntityType.OCELOT, EntityType.WOLF);
		
		
		
		// Check pet ownership
		if (validPets.contains(event.getRightClicked().getType())) {
			Player player = event.getPlayer();
			HashMap<String, String> options = petAction.get(player.getName());
			Tameable pet = (Tameable) event.getRightClicked();
			
			// Check pet ownership
			if (options.get("type").equals("check")) {
				event.setCancelled(true);
				if (pet.getOwner() == null) {
					player.sendMessage(ChatColor.GRAY + "This animal is untamed");
				} else {
					player.sendMessage(ChatColor.GREEN + "This pet belongs to " + ChatColor.LIGHT_PURPLE + pet.getOwner().getName());
					
				}
				
				petAction.remove(player.getName());
				time = System.currentTimeMillis();
			}
	
			// Transfer pet ownership
			if (options.get("type").equals("set") && options.get("new-owner") != null) {
				event.setCancelled(true);
				if (pet.getOwner() == player || permission.has(player, "pe.admin")) {
					OfflinePlayer newOwner = getServer().getOfflinePlayer(options.get("new-owner"));
					
					pet.setOwner(newOwner);
					
					player.sendMessage(ChatColor.GOLD + String.format("This pet has been transferred to %s", newOwner.getName()));
				} else {
					player.sendMessage(ChatColor.RED + "You don't have permission to set ownership for other pets");
				}
				
				petAction.remove(player.getName());
				time = System.currentTimeMillis();
			}	
			
			// Set pet ownership
			if (options.get("type").equals("tame") && options.get("new-owner") != null && permission.has(player, "pe.admin")) {
				event.setCancelled(true);
				if (pet.getOwner() == null) {
					OfflinePlayer newOwner = getServer().getOfflinePlayer(options.get("new-owner"));
					
					if (event.getRightClicked() instanceof Ocelot){
						Ocelot cat = (Ocelot) event.getRightClicked();
						cat.setCatType(Ocelot.Type.values()[new Random().nextInt(Ocelot.Type.values().length)]);
					}
					pet.setOwner(newOwner);
					
					player.sendMessage(ChatColor.GOLD + String.format("This pet has been tamed to %s", newOwner.getName()));
				} else {
					player.sendMessage(ChatColor.RED + "This animal is already tamed!");
				}
				
				petAction.remove(player.getName());
				time = System.currentTimeMillis();
			}	
			
			// Remove pet ownership
			if (options.get("type").equals("untame") && permission.has(player, "pe.admin")) {
				event.setCancelled(true);
				if (pet.getOwner() != null) {					
					pet.setOwner(null);
					player.sendMessage(ChatColor.GOLD + String.format("This animal is no longer tame!"));
				} else {
					player.sendMessage(ChatColor.RED + "This animal is already untamed!");
				}
				petAction.remove(player.getName());
				time = System.currentTimeMillis();
			}	
						
			
			
			
			
			
			
		}
	
	}
	
	
	  private Boolean setupPermissions()
	  {
	    RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(Permission.class);
	    if (permissionProvider != null) {
	      permission = (Permission)permissionProvider.getProvider();
	    }
	    if (permission != null) {
	      return Boolean.valueOf(true);
	    }
	    return Boolean.valueOf(false);
	  }
	
	
	
}
