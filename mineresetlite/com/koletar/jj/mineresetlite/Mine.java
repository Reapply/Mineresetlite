/*     */ package com.koletar.jj.mineresetlite;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Random;
/*     */ import java.util.logging.Logger;
/*     */ import org.bukkit.Bukkit;
/*     */ import org.bukkit.Location;
/*     */ import org.bukkit.World;
/*     */ import org.bukkit.configuration.serialization.ConfigurationSerializable;
/*     */ import org.bukkit.entity.Player;
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Mine
/*     */   implements ConfigurationSerializable
/*     */ {
/*     */   private int minX;
/*     */   private int minY;
/*     */   private int minZ;
/*     */   private int maxX;
/*     */   private int maxY;
/*     */   private int maxZ;
/*     */   private World world;
/*     */   private Map<SerializableBlock, Double> composition;
/*     */   private int resetDelay;
/*     */   private List<Integer> resetWarnings;
/*     */   private String name;
/*     */   private SerializableBlock surface;
/*     */   private boolean fillMode;
/*     */   private int resetClock;
/*     */   private boolean isSilent;
/*     */   
/*     */   public Mine(int minX, int minY, int minZ, int maxX, int maxY, int maxZ, String name, World world) {
/*  38 */     this.minX = minX;
/*  39 */     this.minY = minY;
/*  40 */     this.minZ = minZ;
/*  41 */     this.maxX = maxX;
/*  42 */     this.maxY = maxY;
/*  43 */     this.maxZ = maxZ;
/*  44 */     this.name = name;
/*  45 */     this.world = world;
/*  46 */     this.composition = new HashMap<>();
/*  47 */     this.resetWarnings = new LinkedList<>();
/*     */   }
/*     */   
/*     */   public Mine(Map<String, Object> me) {
/*     */     try {
/*  52 */       this.minX = ((Integer)me.get("minX")).intValue();
/*  53 */       this.minY = ((Integer)me.get("minY")).intValue();
/*  54 */       this.minZ = ((Integer)me.get("minZ")).intValue();
/*  55 */       this.maxX = ((Integer)me.get("maxX")).intValue();
/*  56 */       this.maxY = ((Integer)me.get("maxY")).intValue();
/*  57 */       this.maxZ = ((Integer)me.get("maxZ")).intValue();
/*  58 */     } catch (Throwable t) {
/*  59 */       throw new IllegalArgumentException("Error deserializing coordinate pairs");
/*     */     } 
/*     */     try {
/*  62 */       this.world = Bukkit.getServer().getWorld((String)me.get("world"));
/*  63 */     } catch (Throwable t) {
/*  64 */       throw new IllegalArgumentException("Error finding world");
/*     */     } 
/*  66 */     if (this.world == null) {
/*  67 */       Logger l = Bukkit.getLogger();
/*  68 */       l.severe("[MineResetLite] Unable to find a world! Please include these logger lines along with the stack trace when reporting this bug!");
/*  69 */       l.severe("[MineResetLite] Attempted to load world named: " + me.get("world"));
/*  70 */       l.severe("[MineResetLite] Worlds listed: " + StringTools.buildList(Bukkit.getWorlds(), "", ", "));
/*  71 */       throw new IllegalArgumentException("World was null!");
/*     */     } 
/*     */     try {
/*  74 */       Map<String, Double> sComposition = (Map<String, Double>)me.get("composition");
/*  75 */       this.composition = new HashMap<>();
/*  76 */       for (Map.Entry<String, Double> entry : sComposition.entrySet()) {
/*  77 */         this.composition.put(new SerializableBlock(entry.getKey()), entry.getValue());
/*     */       }
/*  79 */     } catch (Throwable t) {
/*  80 */       throw new IllegalArgumentException("Error deserializing composition");
/*     */     } 
/*  82 */     this.name = (String)me.get("name");
/*  83 */     this.resetDelay = ((Integer)me.get("resetDelay")).intValue();
/*  84 */     List<String> warnings = (List<String>)me.get("resetWarnings");
/*  85 */     this.resetWarnings = new LinkedList<>();
/*  86 */     for (String warning : warnings) {
/*     */       try {
/*  88 */         this.resetWarnings.add(Integer.valueOf(warning));
/*  89 */       } catch (NumberFormatException nfe) {
/*  90 */         throw new IllegalArgumentException("Non-numeric reset warnings supplied");
/*     */       } 
/*     */     } 
/*  93 */     if (me.containsKey("surface") && 
/*  94 */       !me.get("surface").equals("")) {
/*  95 */       this.surface = new SerializableBlock((String)me.get("surface"));
/*     */     }
/*     */     
/*  98 */     if (me.containsKey("fillMode")) {
/*  99 */       this.fillMode = ((Boolean)me.get("fillMode")).booleanValue();
/*     */     }
/* 101 */     if (me.containsKey("resetClock")) {
/* 102 */       this.resetClock = ((Integer)me.get("resetClock")).intValue();
/*     */     }
/*     */     
/* 105 */     if (this.resetDelay > 0 && this.resetClock == 0) {
/* 106 */       this.resetClock = this.resetDelay;
/*     */     }
/* 108 */     if (me.containsKey("isSilent")) {
/* 109 */       this.isSilent = ((Boolean)me.get("isSilent")).booleanValue();
/*     */     }
/*     */   }
/*     */   
/*     */   public Map<String, Object> serialize() {
/* 114 */     Map<String, Object> me = new HashMap<>();
/* 115 */     me.put("minX", Integer.valueOf(this.minX));
/* 116 */     me.put("minY", Integer.valueOf(this.minY));
/* 117 */     me.put("minZ", Integer.valueOf(this.minZ));
/* 118 */     me.put("maxX", Integer.valueOf(this.maxX));
/* 119 */     me.put("maxY", Integer.valueOf(this.maxY));
/* 120 */     me.put("maxZ", Integer.valueOf(this.maxZ));
/* 121 */     me.put("world", this.world.getName());
/*     */     
/* 123 */     Map<String, Double> sComposition = new HashMap<>();
/* 124 */     for (Map.Entry<SerializableBlock, Double> entry : this.composition.entrySet()) {
/* 125 */       sComposition.put(((SerializableBlock)entry.getKey()).toString(), entry.getValue());
/*     */     }
/* 127 */     me.put("composition", sComposition);
/* 128 */     me.put("name", this.name);
/* 129 */     me.put("resetDelay", Integer.valueOf(this.resetDelay));
/* 130 */     List<String> warnings = new LinkedList<>();
/* 131 */     for (Integer warning : this.resetWarnings) {
/* 132 */       warnings.add(warning.toString());
/*     */     }
/* 134 */     me.put("resetWarnings", warnings);
/* 135 */     if (this.surface != null) {
/* 136 */       me.put("surface", this.surface.toString());
/*     */     } else {
/* 138 */       me.put("surface", "");
/*     */     } 
/* 140 */     me.put("fillMode", Boolean.valueOf(this.fillMode));
/* 141 */     me.put("resetClock", Integer.valueOf(this.resetClock));
/* 142 */     me.put("isSilent", Boolean.valueOf(this.isSilent));
/* 143 */     return me;
/*     */   }
/*     */   
/*     */   public boolean getFillMode() {
/* 147 */     return this.fillMode;
/*     */   }
/*     */   
/*     */   public void setFillMode(boolean fillMode) {
/* 151 */     this.fillMode = fillMode;
/*     */   }
/*     */   
/*     */   public void setResetDelay(int minutes) {
/* 155 */     this.resetDelay = minutes;
/* 156 */     this.resetClock = minutes;
/*     */   }
/*     */   
/*     */   public void setResetWarnings(List<Integer> warnings) {
/* 160 */     this.resetWarnings = warnings;
/*     */   }
/*     */   
/*     */   public List<Integer> getResetWarnings() {
/* 164 */     return this.resetWarnings;
/*     */   }
/*     */   
/*     */   public int getResetDelay() {
/* 168 */     return this.resetDelay;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getTimeUntilReset() {
/* 178 */     return this.resetClock;
/*     */   }
/*     */   
/*     */   public SerializableBlock getSurface() {
/* 182 */     return this.surface;
/*     */   }
/*     */   
/*     */   public void setSurface(SerializableBlock surface) {
/* 186 */     this.surface = surface;
/*     */   }
/*     */   
/*     */   public World getWorld() {
/* 190 */     return this.world;
/*     */   }
/*     */   
/*     */   public String getName() {
/* 194 */     return this.name;
/*     */   }
/*     */   
/*     */   public Map<SerializableBlock, Double> getComposition() {
/* 198 */     return this.composition;
/*     */   }
/*     */   
/*     */   public boolean isSilent() {
/* 202 */     return this.isSilent;
/*     */   }
/*     */   
/*     */   public void setSilence(boolean isSilent) {
/* 206 */     this.isSilent = isSilent;
/*     */   }
/*     */   
/*     */   public double getCompositionTotal() {
/* 210 */     double total = 0.0D;
/* 211 */     for (Double d : this.composition.values()) {
/* 212 */       total += d.doubleValue();
/*     */     }
/* 214 */     return total;
/*     */   }
/*     */   
/*     */   public boolean isInside(Player p) {
/* 218 */     Location l = p.getLocation();
/* 219 */     return (l.getWorld().equals(this.world) && l
/* 220 */       .getX() >= this.minX && l.getX() <= this.maxX && l
/* 221 */       .getY() >= this.minY && l.getY() <= this.maxY && l
/* 222 */       .getZ() >= this.minZ && l.getZ() <= this.maxZ);
/*     */   }
/*     */ 
/*     */   
/*     */   public void reset() {
/* 227 */     List<CompositionEntry> probabilityMap = mapComposition(this.composition);
/*     */     
/* 229 */     for (Player p : Bukkit.getServer().getOnlinePlayers()) {
/* 230 */       Location l = p.getLocation();
/* 231 */       if (isInside(p)) {
/* 232 */         p.teleport(new Location(this.world, l.getX(), this.maxY + 2.0D, l.getZ()));
/*     */       }
/*     */     } 
/*     */     
/* 236 */     Random rand = new Random();
/* 237 */     for (int x = this.minX; x <= this.maxX; x++) {
/* 238 */       for (int y = this.minY; y <= this.maxY; y++) {
/* 239 */         for (int z = this.minZ; z <= this.maxZ; z++) {
/* 240 */           if (!this.fillMode || this.world.getBlockTypeIdAt(x, y, z) == 0)
/* 241 */             if (y == this.maxY && this.surface != null) {
/* 242 */               this.world.getBlockAt(x, y, z).setTypeIdAndData(this.surface.getBlockId(), this.surface.getData(), false);
/*     */             } else {
/*     */               
/* 245 */               double r = rand.nextDouble();
/* 246 */               for (CompositionEntry ce : probabilityMap) {
/* 247 */                 if (r <= ce.getChance()) {
/* 248 */                   this.world.getBlockAt(x, y, z).setTypeIdAndData(ce.getBlock().getBlockId(), ce.getBlock().getData(), false);
/*     */                   break;
/*     */                 } 
/*     */               } 
/*     */             }  
/*     */         } 
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public void cron() {
/* 259 */     if (this.resetDelay == 0) {
/*     */       return;
/*     */     }
/* 262 */     if (this.resetClock > 0) {
/* 263 */       this.resetClock--;
/*     */     }
/* 265 */     if (this.resetClock == 0) {
/* 266 */       if (!this.isSilent) {
/* 267 */         MineResetLite.broadcast(Phrases.phrase("mineAutoResetBroadcast", new Object[] { this }), this);
/*     */       }
/* 269 */       reset();
/* 270 */       this.resetClock = this.resetDelay;
/*     */       return;
/*     */     } 
/* 273 */     for (Integer warning : this.resetWarnings) {
/* 274 */       if (warning.intValue() == this.resetClock)
/* 275 */         MineResetLite.broadcast(Phrases.phrase("mineWarningBroadcast", new Object[] { this, warning }), this); 
/*     */     } 
/*     */   }
/*     */   
/*     */   public static class CompositionEntry
/*     */   {
/*     */     private SerializableBlock block;
/*     */     private double chance;
/*     */     
/*     */     public CompositionEntry(SerializableBlock block, double chance) {
/* 285 */       this.block = block;
/* 286 */       this.chance = chance;
/*     */     }
/*     */     
/*     */     public SerializableBlock getBlock() {
/* 290 */       return this.block;
/*     */     }
/*     */     
/*     */     public double getChance() {
/* 294 */       return this.chance;
/*     */     }
/*     */   }
/*     */   
/*     */   public static ArrayList<CompositionEntry> mapComposition(Map<SerializableBlock, Double> compositionIn) {
/* 299 */     ArrayList<CompositionEntry> probabilityMap = new ArrayList<>();
/* 300 */     Map<SerializableBlock, Double> composition = new HashMap<>(compositionIn);
/* 301 */     double max = 0.0D;
/* 302 */     for (Map.Entry<SerializableBlock, Double> entry : composition.entrySet()) {
/* 303 */       max += ((Double)entry.getValue()).doubleValue();
/*     */     }
/*     */     
/* 306 */     if (max < 1.0D) {
/* 307 */       composition.put(new SerializableBlock(0), Double.valueOf(1.0D - max));
/* 308 */       max = 1.0D;
/*     */     } 
/* 310 */     double i = 0.0D;
/* 311 */     for (Map.Entry<SerializableBlock, Double> entry : composition.entrySet()) {
/* 312 */       double v = ((Double)entry.getValue()).doubleValue() / max;
/* 313 */       i += v;
/* 314 */       probabilityMap.add(new CompositionEntry(entry.getKey(), i));
/*     */     } 
/* 316 */     return probabilityMap;
/*     */   }
/*     */ }


/* Location:              C:\Users\zacha\Downloads\MineResetLite.jar!\com\koletar\jj\mineresetlite\Mine.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */