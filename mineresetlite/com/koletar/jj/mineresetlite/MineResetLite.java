/*     */ package com.koletar.jj.mineresetlite;
/*     */ 
/*     */ import com.koletar.jj.mineresetlite.commands.MineCommands;
/*     */ import com.koletar.jj.mineresetlite.commands.PluginCommands;
/*     */ import com.koletar.jj.mineresetlite.org.mcstats.Metrics;
/*     */ import com.sk89q.worldedit.bukkit.WorldEditPlugin;
/*     */ import java.io.BufferedReader;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FilenameFilter;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStreamReader;
/*     */ import java.net.URL;
/*     */ import java.net.URLConnection;
/*     */ import java.util.ArrayList;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
/*     */ import java.util.Locale;
/*     */ import java.util.Properties;
/*     */ import java.util.logging.Logger;
/*     */ import org.bukkit.Bukkit;
/*     */ import org.bukkit.Material;
/*     */ import org.bukkit.command.Command;
/*     */ import org.bukkit.command.CommandSender;
/*     */ import org.bukkit.configuration.file.YamlConfiguration;
/*     */ import org.bukkit.configuration.serialization.ConfigurationSerialization;
/*     */ import org.bukkit.entity.Player;
/*     */ import org.bukkit.event.EventHandler;
/*     */ import org.bukkit.event.EventPriority;
/*     */ import org.bukkit.event.HandlerList;
/*     */ import org.bukkit.event.Listener;
/*     */ import org.bukkit.event.player.PlayerJoinEvent;
/*     */ import org.bukkit.plugin.Plugin;
/*     */ import org.bukkit.plugin.java.JavaPlugin;
/*     */ import org.bukkit.scheduler.BukkitScheduler;
/*     */ import org.bukkit.scheduler.BukkitTask;
/*     */ import org.json.simple.JSONArray;
/*     */ import org.json.simple.JSONObject;
/*     */ import org.json.simple.JSONValue;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class MineResetLite
/*     */   extends JavaPlugin
/*     */ {
/*     */   public List<Mine> mines;
/*     */   private Logger logger;
/*     */   private CommandManager commandManager;
/*  52 */   private WorldEditPlugin worldEdit = null;
/*  53 */   private Metrics metrics = null;
/*  54 */   private int saveTaskId = -1;
/*  55 */   private int resetTaskId = -1;
/*  56 */   private BukkitTask updateTask = null;
/*     */   private boolean needsUpdate;
/*     */   private boolean isUpdateCritical;
/*     */   
/*     */   static {
/*  61 */     ConfigurationSerialization.registerClass(Mine.class);
/*     */   }
/*     */   
/*     */   private static class IsMineFile implements FilenameFilter {
/*     */     public boolean accept(File file, String s) {
/*  66 */       return s.contains(".mine.yml");
/*     */     }
/*     */     private IsMineFile() {} }
/*     */   private class UpdateWarner implements Listener { private UpdateWarner() {}
/*     */     
/*     */     @EventHandler(priority = EventPriority.MONITOR)
/*     */     public void onJoin(PlayerJoinEvent event) {
/*  73 */       if (event.getPlayer().hasPermission("mineresetlite.updates") && MineResetLite.this.needsUpdate) {
/*  74 */         event.getPlayer().sendMessage(Phrases.phrase("updateWarning1", new Object[0]));
/*  75 */         event.getPlayer().sendMessage(Phrases.phrase("updateWarning2", new Object[0]));
/*  76 */         if (MineResetLite.this.isUpdateCritical) {
/*  77 */           event.getPlayer().sendMessage(Phrases.phrase("criticalUpdateWarningDecoration", new Object[0]));
/*  78 */           event.getPlayer().sendMessage(Phrases.phrase("criticalUpdateWarning", new Object[0]));
/*  79 */           event.getPlayer().sendMessage(Phrases.phrase("criticalUpdateWarningDecoration", new Object[0]));
/*     */         } 
/*     */       } 
/*     */     } }
/*     */   
/*     */   public void onEnable() {
/*  85 */     this.mines = new ArrayList<>();
/*  86 */     this.logger = getLogger();
/*  87 */     if (!setupConfig()) {
/*  88 */       this.logger.severe("Since I couldn't setup config files properly, I guess this is goodbye. ");
/*  89 */       this.logger.severe("Plugin Loading Aborted!");
/*     */       return;
/*     */     } 
/*  92 */     this.commandManager = new CommandManager();
/*  93 */     this.commandManager.register(MineCommands.class, new MineCommands(this));
/*  94 */     this.commandManager.register(CommandManager.class, this.commandManager);
/*  95 */     this.commandManager.register(PluginCommands.class, new PluginCommands(this));
/*  96 */     Locale locale = new Locale(Config.getLocale());
/*  97 */     Phrases.getInstance().initialize(locale);
/*  98 */     File overrides = new File(getDataFolder(), "phrases.properties");
/*  99 */     if (overrides.exists()) {
/* 100 */       Properties overridesProps = new Properties();
/*     */       try {
/* 102 */         overridesProps.load(new FileInputStream(overrides));
/* 103 */       } catch (IOException e) {
/* 104 */         e.printStackTrace();
/*     */       } 
/* 106 */       Phrases.getInstance().overrides(overridesProps);
/*     */     } 
/*     */     
/* 109 */     if (getServer().getPluginManager().isPluginEnabled("WorldEdit")) {
/* 110 */       this.worldEdit = (WorldEditPlugin)getServer().getPluginManager().getPlugin("WorldEdit");
/*     */     }
/*     */     
/*     */     try {
/* 114 */       this.metrics = new Metrics((Plugin)this);
/* 115 */       this.metrics.start();
/* 116 */     } catch (IOException e) {
/* 117 */       this.logger.warning("MineResetLite couldn't initialize metrics!");
/* 118 */       e.printStackTrace();
/*     */     } 
/*     */     
/* 121 */     File[] mineFiles = (new File(getDataFolder(), "mines")).listFiles(new IsMineFile());
/* 122 */     for (File file : mineFiles) {
/* 123 */       this.logger.info("Loading mine from file '" + file.getName() + "'...");
/* 124 */       YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);
/*     */       try {
/* 126 */         Object o = yamlConfiguration.get("mine");
/* 127 */         if (!(o instanceof Mine))
/* 128 */         { this.logger.severe("Mine wasn't a mine object! Something is off with serialization!"); }
/*     */         else
/*     */         
/* 131 */         { Mine mine = (Mine)o;
/* 132 */           this.mines.add(mine); } 
/* 133 */       } catch (Throwable t) {
/* 134 */         this.logger.severe("Unable to load mine!");
/*     */       } 
/*     */     } 
/* 137 */     this.resetTaskId = getServer().getScheduler().scheduleSyncRepeatingTask((Plugin)this, new Runnable() {
/*     */           public void run() {
/* 139 */             for (Mine mine : MineResetLite.this.mines) {
/* 140 */               mine.cron();
/*     */             }
/*     */           }
/*     */         },  1200L, 1200L);
/*     */     
/* 145 */     if (!getDescription().getVersion().contains("dev")) {
/* 146 */       this.updateTask = getServer().getScheduler().runTaskLaterAsynchronously((Plugin)this, new Runnable() {
/*     */             public void run() {
/* 148 */               MineResetLite.this.checkUpdates();
/*     */             }
/*     */           },  300L);
/*     */     }
/* 152 */     getServer().getPluginManager().registerEvents(new UpdateWarner(), (Plugin)this);
/* 153 */     this.logger.info("MineResetLite version " + getDescription().getVersion() + " enabled!");
/*     */   }
/*     */   
/*     */   private void checkUpdates() {
/*     */     try {
/* 158 */       URL updateFile = new URL("https://api.curseforge.com/servermods/files?projectIds=45520");
/* 159 */       URLConnection conn = updateFile.openConnection();
/* 160 */       conn.addRequestProperty("User-Agent", "MineResetLite/v" + getDescription().getVersion() + " by jjkoletar");
/* 161 */       String rv = (new BufferedReader(new InputStreamReader(conn.getInputStream()))).readLine();
/* 162 */       JSONArray resp = (JSONArray)JSONValue.parse(rv);
/* 163 */       if (resp.size() == 0)
/* 164 */         return;  String name = ((JSONObject)resp.get(resp.size() - 1)).get("name").toString();
/* 165 */       String[] bits = name.split(" ");
/* 166 */       String remoteVer = bits[bits.length - 1];
/* 167 */       int remoteVal = Integer.valueOf(remoteVer.replace(".", "")).intValue();
/* 168 */       int localVer = Integer.valueOf(getDescription().getVersion().replace(".", "")).intValue();
/* 169 */       if (remoteVal > localVer) {
/* 170 */         this.needsUpdate = true;
/*     */       }
/*     */     }
/* 173 */     catch (Throwable t) {
/* 174 */       t.printStackTrace();
/*     */     } 
/*     */   }
/*     */   
/*     */   public void onDisable() {
/* 179 */     getServer().getScheduler().cancelTask(this.resetTaskId);
/* 180 */     getServer().getScheduler().cancelTask(this.saveTaskId);
/* 181 */     if (this.updateTask != null) {
/* 182 */       this.updateTask.cancel();
/*     */     }
/* 184 */     HandlerList.unregisterAll((Plugin)this);
/* 185 */     save();
/* 186 */     this.logger.info("MineResetLite disabled");
/*     */   }
/*     */ 
/*     */   
/*     */   public Material matchMaterial(String name) {
/* 191 */     if (name.equalsIgnoreCase("diamondore"))
/* 192 */       return Material.DIAMOND_ORE; 
/* 193 */     if (name.equalsIgnoreCase("diamondblock"))
/* 194 */       return Material.DIAMOND_BLOCK; 
/* 195 */     if (name.equalsIgnoreCase("ironore"))
/* 196 */       return Material.IRON_ORE; 
/* 197 */     if (name.equalsIgnoreCase("ironblock"))
/* 198 */       return Material.IRON_BLOCK; 
/* 199 */     if (name.equalsIgnoreCase("goldore"))
/* 200 */       return Material.GOLD_ORE; 
/* 201 */     if (name.equalsIgnoreCase("goldblock"))
/* 202 */       return Material.GOLD_BLOCK; 
/* 203 */     if (name.equalsIgnoreCase("coalore"))
/* 204 */       return Material.COAL_ORE; 
/* 205 */     if (name.equalsIgnoreCase("cake") || name.equalsIgnoreCase("cakeblock"))
/* 206 */       return Material.CAKE_BLOCK; 
/* 207 */     if (name.equalsIgnoreCase("emeraldore"))
/* 208 */       return Material.EMERALD_ORE; 
/* 209 */     if (name.equalsIgnoreCase("emeraldblock"))
/* 210 */       return Material.EMERALD_BLOCK; 
/* 211 */     if (name.equalsIgnoreCase("lapisore"))
/* 212 */       return Material.LAPIS_ORE; 
/* 213 */     if (name.equalsIgnoreCase("lapisblock"))
/* 214 */       return Material.LAPIS_BLOCK; 
/* 215 */     if (name.equalsIgnoreCase("snowblock") || name.equalsIgnoreCase("snow"))
/* 216 */       return Material.SNOW_BLOCK; 
/* 217 */     if (name.equalsIgnoreCase("redstoneore")) {
/* 218 */       return Material.REDSTONE_ORE;
/*     */     }
/* 220 */     return Material.matchMaterial(name);
/*     */   }
/*     */   
/*     */   public Mine[] matchMines(String in) {
/* 224 */     List<Mine> matches = new LinkedList<>();
/* 225 */     for (Mine mine : this.mines) {
/* 226 */       if (mine.getName().toLowerCase().contains(in.toLowerCase())) {
/* 227 */         matches.add(mine);
/*     */       }
/*     */     } 
/* 230 */     return matches.<Mine>toArray(new Mine[matches.size()]);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void buffSave() {
/* 238 */     BukkitScheduler scheduler = getServer().getScheduler();
/* 239 */     if (this.saveTaskId != -1)
/*     */     {
/* 241 */       scheduler.cancelTask(this.saveTaskId);
/*     */     }
/*     */     
/* 244 */     final MineResetLite plugin = this;
/* 245 */     scheduler.scheduleSyncDelayedTask((Plugin)this, new Runnable() {
/*     */           public void run() {
/* 247 */             plugin.save();
/*     */           }
/*     */         },  1200L);
/*     */   }
/*     */   
/*     */   public void save() {
/* 253 */     for (Mine mine : this.mines) {
/* 254 */       File mineFile = getMineFile(mine);
/* 255 */       YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(mineFile);
/* 256 */       yamlConfiguration.set("mine", mine);
/*     */       try {
/* 258 */         yamlConfiguration.save(mineFile);
/* 259 */       } catch (IOException e) {
/* 260 */         this.logger.severe("Unable to serialize mine!");
/* 261 */         e.printStackTrace();
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   private File getMineFile(Mine mine) {
/* 267 */     return new File(new File(getDataFolder(), "mines"), mine.getName().replace(" ", "") + ".mine.yml");
/*     */   }
/*     */   
/*     */   public void eraseMine(Mine mine) {
/* 271 */     this.mines.remove(mine);
/* 272 */     getMineFile(mine).delete();
/*     */   }
/*     */   
/*     */   public boolean hasWorldEdit() {
/* 276 */     return (this.worldEdit != null);
/*     */   }
/*     */   
/*     */   public WorldEditPlugin getWorldEdit() {
/* 280 */     return this.worldEdit;
/*     */   }
/*     */   
/*     */   private boolean setupConfig() {
/* 284 */     File pluginFolder = getDataFolder();
/* 285 */     if (!pluginFolder.exists() && !pluginFolder.mkdir()) {
/* 286 */       this.logger.severe("Could not make plugin folder! This won't end well...");
/* 287 */       return false;
/*     */     } 
/* 289 */     File mineFolder = new File(getDataFolder(), "mines");
/* 290 */     if (!mineFolder.exists() && !mineFolder.mkdir()) {
/* 291 */       this.logger.severe("Could not make mine folder! Abort! Abort!");
/* 292 */       return false;
/*     */     } 
/*     */     try {
/* 295 */       Config.initConfig(getDataFolder());
/* 296 */     } catch (IOException e) {
/* 297 */       this.logger.severe("Could not make config file!");
/* 298 */       e.printStackTrace();
/* 299 */       return false;
/*     */     } 
/* 301 */     return true;
/*     */   }
/*     */   
/*     */   public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
/* 305 */     if (command.getName().equalsIgnoreCase("mineresetlite")) {
/* 306 */       if (args.length == 0) {
/* 307 */         String[] helpArgs = new String[0];
/* 308 */         this.commandManager.callCommand("help", sender, helpArgs);
/* 309 */         return true;
/*     */       } 
/*     */       
/* 312 */       String[] spoofedArgs = new String[args.length - 1];
/* 313 */       for (int i = 1; i < args.length; i++) {
/* 314 */         spoofedArgs[i - 1] = args[i];
/*     */       }
/* 316 */       this.commandManager.callCommand(args[0], sender, spoofedArgs);
/* 317 */       return true;
/*     */     } 
/* 319 */     return false;
/*     */   }
/*     */   
/*     */   public static void broadcast(String message, Mine mine) {
/* 323 */     if (Config.getBroadcastNearbyOnly()) {
/* 324 */       for (Player p : mine.getWorld().getPlayers()) {
/* 325 */         if (mine.isInside(p)) {
/* 326 */           p.sendMessage(message);
/*     */         }
/*     */       } 
/* 329 */       Bukkit.getLogger().info(message);
/* 330 */     } else if (Config.getBroadcastInWorldOnly()) {
/* 331 */       for (Player p : mine.getWorld().getPlayers()) {
/* 332 */         p.sendMessage(message);
/*     */       }
/* 334 */       Bukkit.getLogger().info(message);
/*     */     } else {
/* 336 */       Bukkit.getServer().broadcastMessage(message);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\zacha\Downloads\MineResetLite.jar!\com\koletar\jj\mineresetlite\MineResetLite.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */