package juvoo.deathshuffle;

import org.bukkit.*;
import org.bukkit.entity.Player;

import java.util.*;

public class Utils {

    public static void newRound() {
        DeathShuffle.isStarted = true;
        DeathShuffle.deathTasks.clear();
        for (Player p : Bukkit.getOnlinePlayers()) {
            assignTask(p);
        }
        DeathShuffle.seconds = DeathShuffle.gameTimeInMinutes * 60;
        if (DeathShuffle.timeTask != null) {
            Bukkit.getScheduler().cancelTask(DeathShuffle.timeTask);
        }
        for (World w : Bukkit.getWorlds()) {
            w.setGameRule(GameRule.KEEP_INVENTORY, true);
        }
        DeathShuffle.timeTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(DeathShuffle.plugin, new Runnable() {
            @Override
            public void run() {
                double dseconds = (double)DeathShuffle.seconds;
                dseconds = dseconds / 60;
                if (DeathShuffle.seconds <= 10) {
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        if (DeathShuffle.seconds != 0) {
                            p.sendTitle("" + ChatColor.RED + ChatColor.BOLD + DeathShuffle.seconds, "");
                        }
                        if (DeathShuffle.seconds == 0) {
                            if (DeathShuffle.continueGameAfterEnd) {
                                p.sendTitle("" + ChatColor.RED + ChatColor.BOLD + DeathShuffle.seconds, "");
                            }
                            else {
                                p.sendTitle("" + ChatColor.RED + ChatColor.BOLD + "Game over!", "");
                            }
                        }
                    }
                    if (DeathShuffle.seconds == 1) {
                        Bukkit.broadcastMessage("" + ChatColor.RED + ChatColor.BOLD + "You have " + DeathShuffle.seconds + " second to complete your death task!");
                    }
                    else if (DeathShuffle.seconds != 0) {
                        Bukkit.broadcastMessage("" + ChatColor.RED + ChatColor.BOLD + "You have " + DeathShuffle.seconds + " seconds to complete your death task!");
                    }
                }
                if (dseconds == Math.floor(dseconds)) {
                    String s = String.valueOf(dseconds);
                    int index = s.indexOf(".");
                    ArrayList<Character> chars = new ArrayList<>();
                    for (int i = 0; i < index; i++) {
                        chars.add(s.charAt(i));
                    }
                    StringBuilder string = new StringBuilder();
                    for (char c : chars) {
                        string.append(c);
                    }
                    int left = Integer.parseInt(string.toString());
                    if (left != DeathShuffle.gameTimeInMinutes && left != 0) {
                        Bukkit.broadcastMessage("" + ChatColor.RED + ChatColor.BOLD + "You have " + left + "m to complete your death task!");
                    }
                    if (DeathShuffle.seconds == 0) {
                        boolean losers = false;
                        for (UUID uuid : DeathShuffle.deathTasks.keySet()) {
                            if (DeathShuffle.deathTasks.get(uuid) != DeathTask.COMPLETE) {
                                losers = true;
                                break;
                            }
                        }
                        if (losers) {
                            List<String> losersList = new ArrayList<>();
                            for (UUID uuid : DeathShuffle.deathTasks.keySet()) {
                                if (DeathShuffle.deathTasks.get(uuid) != DeathTask.COMPLETE) {
                                    losersList.add(Bukkit.getOfflinePlayer(uuid).getName());
                                }
                            }
                            if (losersList.size() != 0) {
                                for (String str : losersList) {
                                    Bukkit.broadcastMessage("" + ChatColor.DARK_RED + ChatColor.BOLD + str + " failed to complete their death task!");
                                }
                            }
                            if (DeathShuffle.continueGameAfterEnd) {
                                DeathShuffle.plugin.getServer().getScheduler().cancelTask(DeathShuffle.timeTask);
                                Bukkit.getScheduler().scheduleSyncDelayedTask(DeathShuffle.plugin, new Runnable() {
                                    @Override
                                    public void run() {
                                        DeathShuffle.roundCompletions++;
                                        Utils.newRound();
                                    }
                                }, 20);
                            }
                            else {
                                stopGame();
                            }
                        }
                        else {
                            Bukkit.getScheduler().cancelTask(DeathShuffle.timeTask);
                        }
                    }
                }
                DeathShuffle.seconds--;
            }
        }, 0, 20);
    }

    private static void assignTask(Player p) {
        if (!DeathShuffle.unusedTasks.containsKey(p.getUniqueId()) || DeathShuffle.unusedTasks.get(p.getUniqueId()).size() == 0) {
            ArrayList<DeathTask> tasks = new ArrayList<>();
            for (DeathTask task : DeathTask.values()) {
                if (task != DeathTask.COMPLETE && DeathShuffle.enabledTasks.contains(task)) {
                    tasks.add(task);
                }
            }
            DeathShuffle.unusedTasks.put(p.getUniqueId(), tasks);
        }
        Random r = new Random();
        DeathTask assignment = null;
        if (DeathShuffle.progressiveTaskDifficulty) {
            Integer rounds = DeathShuffle.roundCompletions;
            int difficulty = 1;
            if (rounds > 4 && rounds <= 8) {
                difficulty = 2;
            }
            if (rounds > 8) {
                difficulty = 3;
            }
            ArrayList<DeathTask> primaryTasks = new ArrayList<>();
            ArrayList<DeathTask> secondaryTasks = new ArrayList<>();
            ArrayList<DeathTask> tertiaryTasks = new ArrayList<>();
            for (DeathTask task : DeathShuffle.unusedTasks.get(p.getUniqueId())) {
                Integer taskDifficulty = retrieveDifficulty(task);
                if (taskDifficulty == 1) {
                    primaryTasks.add(task);
                    secondaryTasks.add(task);
                    tertiaryTasks.add(task);
                }
                if (taskDifficulty == 2) {
                    secondaryTasks.add(task);
                    tertiaryTasks.add(task);
                }
                if (taskDifficulty == 3) {
                    tertiaryTasks.add(task);
                }
            }
            if (difficulty == 1) {
                if (primaryTasks.size() != 0) {
                    assignment = primaryTasks.get(r.nextInt(primaryTasks.size()));
                }
                else {
                    if (secondaryTasks.size() != 0) {
                        assignment = secondaryTasks.get(r.nextInt(secondaryTasks.size()));
                    }
                    else {
                        assignment = tertiaryTasks.get(r.nextInt(tertiaryTasks.size()));
                    }
                }
            }
            if (difficulty == 2) {
                if (secondaryTasks.size() != 0) {
                    assignment = primaryTasks.get(r.nextInt(primaryTasks.size()));
                }
                else {
                    assignment = tertiaryTasks.get(r.nextInt(tertiaryTasks.size()));
                }
            }
            if (difficulty == 3) {
                assignment = tertiaryTasks.get(r.nextInt(tertiaryTasks.size()));
            }
        }
        else {
            ArrayList<DeathTask> tasks = DeathShuffle.unusedTasks.get(p.getUniqueId());
            assignment = tasks.get(r.nextInt(tasks.size()));
        }
        DeathShuffle.unusedTasks.get(p.getUniqueId()).remove(assignment);
        DeathShuffle.deathTasks.remove(p.getUniqueId());
        DeathShuffle.deathTasks.put(p.getUniqueId(), assignment);
        String message = retrieveMessage(assignment);
        p.sendMessage(ChatColor.GREEN + "You must complete the death:\n" + ChatColor.GREEN + "→ " + ChatColor.RESET + ChatColor.YELLOW + message);
    }

    public static void taskCompleted(Player p) {
        Bukkit.broadcastMessage("" + ChatColor.GOLD + ChatColor.BOLD + p.getName() + " completed their death task!");
        p.playSound(p.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1.0F, 1.0F);
        DeathTask assignment = DeathShuffle.deathTasks.get(p.getUniqueId());
        DeathShuffle.unusedTasks.get(p.getUniqueId()).remove(assignment);
        DeathShuffle.deathTasks.remove(p.getUniqueId());
        DeathShuffle.deathTasks.put(p.getUniqueId(), DeathTask.COMPLETE);
        Integer numDone = 0;
        Integer total = 0;
        for (UUID uuid : DeathShuffle.deathTasks.keySet()) {
            total++;
            if (DeathShuffle.deathTasks.get(uuid) == DeathTask.COMPLETE) {
                numDone++;
            }
        }
        if (numDone.equals(total)) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(DeathShuffle.plugin, new Runnable() {
                @Override
                public void run() {
                    DeathShuffle.roundCompletions++;
                    Utils.newRound();
                }
            }, 20);
        }
    }

    public static void stopGame() {
        DeathShuffle.isStarted = false;
        Bukkit.getScheduler().cancelTask(DeathShuffle.timeTask);
        DeathShuffle.deathTasks.clear();
        DeathShuffle.unusedTasks.clear();
        DeathShuffle.roundCompletions = 0;
        Bukkit.broadcastMessage("" + ChatColor.GOLD + ChatColor.BOLD + "Game over!");
    }

    public static void configure() {
        DeathShuffle.plugin.saveDefaultConfig();
        try {
            DeathShuffle.gameTimeInMinutes = DeathShuffle.plugin.getConfig().getInt("game-time-minutes");
            DeathShuffle.progressiveTaskDifficulty = DeathShuffle.plugin.getConfig().getBoolean("progressive-task-difficulty");
            DeathShuffle.continueGameAfterEnd = DeathShuffle.plugin.getConfig().getBoolean("continue-game-after-end");
            for (DeathTask task : DeathTask.values()) {
                if (task != DeathTask.COMPLETE) {
                    String taskString = task.toString().toLowerCase().replace("_", "-");
                    if (DeathShuffle.plugin.getConfig().getBoolean(taskString)) {
                        DeathShuffle.enabledTasks.add(task);
                    }
                }
            }
        } catch (Exception e) {
            DeathShuffle.plugin.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Formatting error encountered in config.");
            DeathShuffle.plugin.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Try deleting the config and reloading the server.");
        }
    }

    public static String retrieveMessage(DeathTask task) {
        String message = "";
        switch (task) {
            case FALL:
                message = "Die from fall damage";
                break;
            case CACTUS:
                message = "Die to a cactus";
                break;
            case FIRE:
                message = "Die from fire";
                break;
            case LIGHTNING:
                message = "Die to lightning";
                break;
            case CREEPER_EXPLOSION:
                message = "Die to a creeper explosion";
                break;
            case TNT_EXPLOSION:
                message = "Die to a tnt explosion";
                break;
            case PILLAGER:
                message = "Die to a pillager's arrow";
                break;
            case FIREBALL:
                message = "Die to a fireball";
                break;
            case ENTITY_CRAMMING:
                message = "Die due to entity cramming";
                break;
            case IRON_GOLEM:
                message = "Die to an iron golem";
                break;
            case VOID:
                message = "Die in the void";
                break;
            case SKELETON:
                message = "Die to a skeleton's arrow";
                break;
            case ZOMBIE:
                message = "Die to a zombie";
                break;
            case LAVA:
                message = "Die to lava";
                break;
            case POINTED_DRIPSTONE:
                message = "Die to pointed dripstone";
                break;
            case SWEET_BERRIES:
                message = "Die to sweet berries";
                break;
            case ANVIL:
                message = "Die to a falling anvil";
                break;
            case SUFFOCATION:
                message = "Die from suffocation";
                break;
            case TRIDENT:
                message = "Die to a trident";
                break;
            case LLAMA_SPIT:
                message = "Die to llama spit";
                break;
            case DROWN:
                message = "Die from drowning";
                break;
            case BEE:
                message = "Die from a bee sting";
                break;
            case BABY_HOGLIN:
                message = "Die to a baby hoglin";
                break;
            case SPIDER:
                message = "Die to a spider";
                break;
            case ENDERMAN:
                message = "Die to an enderman";
                break;
            case PUFFERFISH:
                message = "Die to a pufferfish";
                break;
            case WOLF:
                message = "Die to a wolf";
                break;
            case FREEZE:
                message = "Freeze to death";
                break;
            case TNT_MINECART:
                message = "Die to a tnt minecart";
                break;
            case FIREWORK_ROCKET:
                message = "Die to a firework rocket";
                break;
            case ZOMBIFIED_PIGLIN:
                message = "Die to a zombified piglin";
                break;
            case WITHER:
                message = "Die to the wither effect";
                break;
            case MAGMA:
                message = "Die to a magma block";
                break;
            case DISPENSER:
                message = "Die to an arrow launched from a dispenser";
                break;
        }
        return message;
    }

    public static Integer retrieveDifficulty(DeathTask task) {
        int difficulty = 0;
        switch (task) {
            case FALL:
            case SPIDER:
            case DROWN:
            case SUFFOCATION:
            case SWEET_BERRIES:
            case LAVA:
            case ZOMBIE:
            case SKELETON:
            case CREEPER_EXPLOSION:
            case FIRE:
            case CACTUS:
                difficulty = 1;
                break;
            case LIGHTNING:
            case DISPENSER:
            case MAGMA:
            case FIREWORK_ROCKET:
            case TNT_MINECART:
            case WOLF:
            case PUFFERFISH:
            case ENDERMAN:
            case BEE:
            case LLAMA_SPIT:
            case POINTED_DRIPSTONE:
            case IRON_GOLEM:
            case PILLAGER:
            case TNT_EXPLOSION:
                difficulty = 2;
                break;
            case FIREBALL:
            case WITHER:
            case ZOMBIFIED_PIGLIN:
            case FREEZE:
            case BABY_HOGLIN:
            case TRIDENT:
            case ANVIL:
            case VOID:
            case ENTITY_CRAMMING:
                difficulty = 3;
                break;
        }
        return difficulty;
    }
}
