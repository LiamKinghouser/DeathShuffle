package juvoo.deathshuffle;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.entity.minecart.ExplosiveMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.projectiles.BlockProjectileSource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class DeathShuffle extends JavaPlugin implements Listener {

    public static Boolean isStarted;
    public static Plugin plugin;
    public static Integer timeTask;
    public static Integer seconds;
    public static Integer roundCompletions;
    public static ArrayList<DeathTask> enabledTasks;
    public static HashMap<UUID, DeathTask> deathTasks;
    public static HashMap<UUID, ArrayList<DeathTask>> unusedTasks;
    public static Integer gameTimeInMinutes;
    public static Boolean progressiveTaskDifficulty;
    public static Boolean continueGameAfterEnd;

    @Override
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(this, this);
        isStarted = false;
        plugin = this;
        roundCompletions = 0;
        enabledTasks = new ArrayList<>();
        deathTasks = new HashMap<>();
        unusedTasks = new HashMap<>();
        gameTimeInMinutes = 5;
        progressiveTaskDifficulty = false;
        continueGameAfterEnd = false;
        Utils.configure();
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (isStarted && event.getEntity() instanceof Player) {
            Player p = (Player)event.getEntity();
            if (event.getFinalDamage() >= p.getHealth()) {
                DeathTask task = deathTasks.get(p.getUniqueId());
                EntityDamageEvent.DamageCause cause = event.getCause();
                boolean completedTask = false;
                if (task != null) {
                    switch (task) {
                        case FALL:
                            if (cause == EntityDamageEvent.DamageCause.FALL) {
                                completedTask = true;
                            }
                            break;
                        case SWEET_BERRIES:
                            if (cause == EntityDamageEvent.DamageCause.CONTACT && p.getLocation().getBlock().getType() == Material.SWEET_BERRY_BUSH) {
                                completedTask = true;
                            }
                            break;
                        case CACTUS:
                            if (cause == EntityDamageEvent.DamageCause.CONTACT) {
                                completedTask = true;
                            }
                            break;
                        case FIRE:
                            if (cause == EntityDamageEvent.DamageCause.FIRE || cause == EntityDamageEvent.DamageCause.FIRE_TICK) {
                                completedTask = true;
                            }
                            break;
                        case LIGHTNING:
                            if (cause == EntityDamageEvent.DamageCause.LIGHTNING) {
                                completedTask = true;
                            }
                            break;
                        case ENTITY_CRAMMING:
                            if (cause == EntityDamageEvent.DamageCause.CRAMMING) {
                                completedTask = true;
                            }
                            break;
                        case VOID:
                            if (cause == EntityDamageEvent.DamageCause.VOID) {
                                completedTask = true;
                            }
                            break;
                        case LAVA:
                            if (cause == EntityDamageEvent.DamageCause.LAVA) {
                                completedTask = true;
                            }
                            break;
                        case SUFFOCATION:
                            if (cause == EntityDamageEvent.DamageCause.SUFFOCATION) {
                                completedTask = true;
                            }
                            break;
                        case DROWN:
                            if (cause == EntityDamageEvent.DamageCause.DROWNING) {
                                completedTask = true;
                            }
                            break;
                        case POINTED_DRIPSTONE:
                            if (cause == EntityDamageEvent.DamageCause.CONTACT && p.getLocation().getBlock().getType() == Material.POINTED_DRIPSTONE) {
                                completedTask = true;
                            }
                            break;
                        case FREEZE:
                            if (cause == EntityDamageEvent.DamageCause.FREEZE) {
                                completedTask = true;
                            }
                            break;
                        case WITHER:
                            if (cause == EntityDamageEvent.DamageCause.WITHER) {
                                completedTask = true;
                            }
                            break;
                        case MAGMA:
                            if (cause == EntityDamageEvent.DamageCause.HOT_FLOOR) {
                                completedTask = true;
                            }
                            break;
                    }
                }
                if (completedTask) {
                    Utils.taskCompleted(p);
                }
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (isStarted && event.getEntity() instanceof Player) {
            Player p = (Player) event.getEntity();
            if (event.getFinalDamage() >= p.getHealth()) {
                DeathTask task = deathTasks.get(p.getUniqueId());
                boolean completedTask = false;
                Entity damager = event.getDamager();
                if (task != null) {
                    switch (task) {
                        case CREEPER_EXPLOSION:
                            if (damager instanceof Creeper) {
                                completedTask = true;
                            }
                            break;
                        case TNT_EXPLOSION:
                            if (damager instanceof TNTPrimed) {
                                completedTask = true;
                            }
                            break;
                        case PILLAGER:
                            if (damager instanceof Arrow) {
                                Arrow arrow = (Arrow) damager;
                                if (arrow.getShooter() instanceof Pillager) {
                                    completedTask = true;
                                }
                            }
                            break;
                        case FIREBALL:
                            if (damager instanceof Fireball) {
                                completedTask = true;
                            }
                            break;
                        case IRON_GOLEM:
                            if (damager instanceof IronGolem) {
                                completedTask = true;
                                break;
                            }
                        case SKELETON:
                            if (damager instanceof Arrow) {
                                Arrow arrow = (Arrow) damager;
                                if (arrow.getShooter() instanceof Skeleton) {
                                    completedTask = true;
                                }
                            }
                            break;
                        case ZOMBIE:
                            if (damager instanceof Zombie) {
                                completedTask = true;
                            }
                            break;
                        case POINTED_DRIPSTONE:
                            if (damager instanceof FallingBlock) {
                                FallingBlock fb = (FallingBlock) damager;
                                if (fb.getMaterial() == Material.POINTED_DRIPSTONE) {
                                    completedTask = true;
                                }
                            }
                            break;
                        case ANVIL:
                            if (damager instanceof FallingBlock) {
                                FallingBlock fb = (FallingBlock) damager;
                                if (fb.getMaterial() == Material.ANVIL) {
                                    completedTask = true;
                                }
                            }
                            break;
                        case TRIDENT:
                            if (damager instanceof Trident) {
                                completedTask = true;
                            }
                            break;
                        case LLAMA_SPIT:
                            if (damager instanceof LlamaSpit) {
                                completedTask = true;
                            }
                            break;
                        case BEE:
                            if (damager instanceof Bee) {
                                completedTask = true;
                            }
                            break;
                        case BABY_HOGLIN:
                            if (damager instanceof Hoglin) {
                                Hoglin hoglin = (Hoglin) damager;
                                if (!hoglin.isAdult()) {
                                    completedTask = true;
                                }
                            }
                            break;
                        case SPIDER:
                            if (damager instanceof Spider) {
                                completedTask = true;
                            }
                            break;
                        case ENDERMAN:
                            if (damager instanceof Enderman) {
                                completedTask = true;
                            }
                            break;
                        case PUFFERFISH:
                            if (damager instanceof PufferFish) {
                                completedTask = true;
                            }
                            break;
                        case WOLF:
                            if (damager instanceof Wolf) {
                                completedTask = true;
                            }
                            break;
                        case TNT_MINECART:
                            if (damager instanceof ExplosiveMinecart) {
                                completedTask = true;
                            }
                            break;
                        case FIREWORK_ROCKET:
                            if (damager instanceof Firework) {
                                completedTask = true;
                            }
                            break;
                        case ZOMBIFIED_PIGLIN:
                            if (damager instanceof PigZombie) {
                                completedTask = true;
                            }
                            break;
                        case DISPENSER:
                            if (damager instanceof Arrow) {
                                Arrow arrow = (Arrow) damager;
                                if (arrow.getShooter() instanceof BlockProjectileSource) {
                                    completedTask = true;
                                }
                            }
                            break;
                    }
                }
                if (completedTask) {
                    Utils.taskCompleted(p);
                }
            }
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        if (isStarted && deathTasks.get(p.getUniqueId()) != null) {
            String message = Utils.retrieveMessage(deathTasks.get(p.getUniqueId()));
            p.sendMessage(ChatColor.GREEN + "Reminder:\nYou must complete the death:\n" + ChatColor.GREEN + "→ " + ChatColor.RESET + ChatColor.YELLOW + message);
        }
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 1 && label.equalsIgnoreCase("deathshuffle") || label.equalsIgnoreCase("deathshuffle:deathshuffle")) {
            if (args[0].equalsIgnoreCase("start")) {
                if (!isStarted) {
                    if (enabledTasks != null && enabledTasks.size() >= 2) {
                        if (gameTimeInMinutes >= 1) {
                            sender.sendMessage(ChatColor.GREEN + "Death Race started.");
                            Utils.newRound();
                        }
                        else {
                            sender.sendMessage(ChatColor.RED + "Game time must be 1+ minutes!");
                            sender.sendMessage(ChatColor.RED + "Change the game time by editing the config.");
                        }
                    }
                    else {
                        sender.sendMessage(ChatColor.RED + "Must have 2+ Death Tasks enabled!");
                        sender.sendMessage(ChatColor.RED + "Enable more Death Tasks by editing the config.");
                    }
                }
                else if (isStarted) {
                    sender.sendMessage(ChatColor.RED + "Death Race already started!");
                }
            }
            else if (args[0].equalsIgnoreCase("stop")) {
                if (isStarted) {
                    Utils.stopGame();
                    sender.sendMessage(ChatColor.GREEN + "Death Race stopped.");
                }
                else if (!isStarted) {
                    sender.sendMessage(ChatColor.RED + "Death Race not started!");
                }
            }
            else if (!args[0].equalsIgnoreCase("start") && !args[0].equalsIgnoreCase("stop")) {
                sender.sendMessage(ChatColor.RED + "Invalid Usage. Please try:");
                sender.sendMessage(ChatColor.GREEN + "/deathshuffle start");
                sender.sendMessage(ChatColor.GREEN + "/deathshuffle stop");
            }
        }
        else if (args.length != 1) {
            sender.sendMessage(ChatColor.RED + "Invalid Usage. Please try:");
            sender.sendMessage(ChatColor.GREEN + "/deathshuffle start");
            sender.sendMessage(ChatColor.GREEN + "/deathshuffle stop");
        }
        return true;
    }
}
