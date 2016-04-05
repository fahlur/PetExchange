package me.Fahlur.PetExchange;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

public class PetExchange
  extends JavaPlugin
  implements Listener
{
  PetExchange plugin;
  private Logger log;
  Permission permission;
  PluginDescriptionFile pdfFile = getDescription();
  public static HashMap<String, HashMap<String, String>> petAction = new HashMap();
  
  public void onEnable()
  {
    this.plugin = this;
    this.log = getLogger();
    setupPermissions();
    Bukkit.getServer().getPluginManager().registerEvents(this, this);
  }
  
  public boolean onCommand(final CommandSender sender, Command cmd, String label, String[] args)
  {
    if (!(sender instanceof Player))
    {
      this.log.warning("Commands for this plugin may only be executed by a player!");
      return true;
    }
    Player player = (Player)sender;
    if (!this.permission.playerHas(player, "pe.exchangepets"))
    {
      player.sendMessage(ChatColor.RED + "Sorry, you don't have permission to use this command.");
      return true;
    }
    if (cmd.getName().equalsIgnoreCase("petexchange")) {
      player.sendMessage(ChatColor.GREEN + "petExchange version: " + ChatColor.GOLD + this.pdfFile.getVersion());
    }
    if (cmd.getName().equalsIgnoreCase("setowner"))
    {
      if (args.length != 1)
      {
        sender.sendMessage(ChatColor.RED + "Please specify a player to transfer ownership to");
        return true;
      }
      if (Bukkit.getPlayerExact(args[0]) == null)
      {
        sender.sendMessage(ChatColor.RED + "Specified player is not online!");
        return true;
      }
      sender.sendMessage(ChatColor.GOLD + "Right click a pet within 10 seconds to transfer ownership");
      
      HashMap<String, String> options = new HashMap();
      options.put("type", "set");
      options.put("new-owner", args[0]);
      
      petAction.put(sender.getName(), options);
      
      getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable()
      {
        public void run()
        {
          if (PetExchange.petAction.containsKey(sender.getName()))
          {
            HashMap<String, String> options = (HashMap)PetExchange.petAction.get(sender.getName());
            if (((String)options.get("type")).equals("set"))
            {
              PetExchange.petAction.remove(sender.getName());
              sender.sendMessage(ChatColor.YELLOW + "Pet ownership transfer expired");
            }
          }
        }
      }, 200L);
    }
    if (cmd.getName().equalsIgnoreCase("checkowner"))
    {
      sender.sendMessage(ChatColor.GOLD + "Right click a pet within 10 seconds to check ownership");
      
      HashMap<String, String> options = new HashMap();
      options.put("type", "check");
      
      petAction.put(sender.getName(), options);
      
      getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable()
      {
        public void run()
        {
          if (PetExchange.petAction.containsKey(sender.getName()))
          {
            HashMap<String, String> options = (HashMap)PetExchange.petAction.get(sender.getName());
            if (((String)options.get("type")).equals("check"))
            {
              PetExchange.petAction.remove(sender.getName());
              sender.sendMessage(ChatColor.YELLOW + "Pet check expired");
            }
          }
        }
      }, 200L);
    }
    return true;
  }
  
  @EventHandler
  public void onPlayerRightClickEntity(PlayerInteractEntityEvent event)
  {
    if (!petAction.containsKey(event.getPlayer().getName())) {
      return;
    }
    List<EntityType> validPets = Arrays.asList(new EntityType[] { EntityType.HORSE, EntityType.OCELOT, EntityType.WOLF });
    if (validPets.contains(event.getRightClicked().getType()))
    {
      Player player = event.getPlayer();
      HashMap<String, String> options = (HashMap)petAction.get(player.getName());
      Tameable pet = (Tameable)event.getRightClicked();
      if (((String)options.get("type")).equals("check"))
      {
        if (pet.getOwner() == null) {
          player.sendMessage(ChatColor.GRAY + "This animal is untamed");
        } else {
          player.sendMessage(ChatColor.GREEN + "This pet belongs to " + ChatColor.LIGHT_PURPLE + pet.getOwner().getName());
        }
        petAction.remove(player.getName());
      }
      if ((((String)options.get("type")).equals("set")) && (options.get("new-owner") != null))
      {
        if (pet.getOwner() == player)
        {
          OfflinePlayer newOwner = getServer().getOfflinePlayer((String)options.get("new-owner"));
          
          pet.setOwner(newOwner);
          
          player.sendMessage(ChatColor.GOLD + String.format("This pet has been transferred to %s", new Object[] { newOwner.getName() }));
        }
        else
        {
          player.sendMessage(ChatColor.RED + "You don't have permission to set ownership for other pets");
        }
        petAction.remove(player.getName());
      }
    }
  }
  
  private Boolean setupPermissions()
  {
    RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(Permission.class);
    if (permissionProvider != null) {
      this.permission = ((Permission)permissionProvider.getProvider());
    }
    if (this.permission != null) {
      return Boolean.valueOf(true);
    }
    return Boolean.valueOf(false);
  }
}
