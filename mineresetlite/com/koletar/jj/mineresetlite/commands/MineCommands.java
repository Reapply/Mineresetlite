/*     */ package com.koletar.jj.mineresetlite.commands;
/*     */ 
/*     */ import com.koletar.jj.mineresetlite.Command;
/*     */ import com.koletar.jj.mineresetlite.InvalidCommandArgumentsException;
/*     */ import com.koletar.jj.mineresetlite.Mine;
/*     */ import com.koletar.jj.mineresetlite.MineResetLite;
/*     */ import com.koletar.jj.mineresetlite.Phrases;
/*     */ import com.koletar.jj.mineresetlite.SerializableBlock;
/*     */ import com.koletar.jj.mineresetlite.StringTools;
/*     */ import com.sk89q.worldedit.bukkit.WorldEditPlugin;
/*     */ import com.sk89q.worldedit.bukkit.selections.Selection;
/*     */ import java.util.HashMap;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import org.bukkit.Location;
/*     */ import org.bukkit.Material;
/*     */ import org.bukkit.World;
/*     */ import org.bukkit.command.CommandSender;
/*     */ import org.bukkit.entity.Player;
/*     */ import org.bukkit.util.Vector;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class MineCommands
/*     */ {
/*     */   private MineResetLite plugin;
/*     */   private Map<Player, Location> point1;
/*     */   private Map<Player, Location> point2;
/*     */   
/*     */   public MineCommands(MineResetLite plugin) {
/*  35 */     this.plugin = plugin;
/*  36 */     this.point1 = new HashMap<>();
/*  37 */     this.point2 = new HashMap<>();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Command(aliases = {"list", "l"}, description = "List the names of all Mines", permissions = {"mineresetlite.mine.list"}, help = {"List the names of all Mines currently created, across all worlds."}, min = 0, max = 0, onlyPlayers = false)
/*     */   public void listMines(CommandSender sender, String[] args) {
/*  46 */     sender.sendMessage(Phrases.phrase("mineList", new Object[] { StringTools.buildList(this.plugin.mines, "&c", "&d, ") }));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Command(aliases = {"pos1", "p1"}, description = "Change your first selection point", help = {"Run this command to set your first selection point to the block you are looking at.", "Use /mrl pos1 -feet to set your first point to the location you are standing on."}, usage = "(-feet)", permissions = {"mineresetlite.mine.create", "mineresetlite.mine.redefine"}, min = 0, max = 1, onlyPlayers = true)
/*     */   public void setPoint1(CommandSender sender, String[] args) throws InvalidCommandArgumentsException {
/*  57 */     Player player = (Player)sender;
/*  58 */     if (args.length == 0) {
/*     */       
/*  60 */       this.point1.put(player, player.getTargetBlock((Set)null, 100).getLocation());
/*  61 */       player.sendMessage(Phrases.phrase("firstPointSet", new Object[0])); return;
/*     */     } 
/*  63 */     if (args[0].equalsIgnoreCase("-feet")) {
/*     */       
/*  65 */       this.point1.put(player, player.getLocation());
/*  66 */       player.sendMessage(Phrases.phrase("firstPointSet", new Object[0]));
/*     */       
/*     */       return;
/*     */     } 
/*  70 */     throw new InvalidCommandArgumentsException();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Command(aliases = {"pos2", "p2"}, description = "Change your first selection point", help = {"Run this command to set your second selection point to the block you are looking at.", "Use /mrl pos2 -feet to set your second point to the location you are standing on."}, usage = "(-feet)", permissions = {"mineresetlite.mine.create", "mineresetlite.mine.redefine"}, min = 0, max = 1, onlyPlayers = true)
/*     */   public void setPoint2(CommandSender sender, String[] args) throws InvalidCommandArgumentsException {
/*  81 */     Player player = (Player)sender;
/*  82 */     if (args.length == 0) {
/*     */       
/*  84 */       this.point2.put(player, player.getTargetBlock((Set)null, 100).getLocation());
/*  85 */       player.sendMessage(Phrases.phrase("secondPointSet", new Object[0])); return;
/*     */     } 
/*  87 */     if (args[0].equalsIgnoreCase("-feet")) {
/*     */       
/*  89 */       this.point2.put(player, player.getLocation());
/*  90 */       player.sendMessage(Phrases.phrase("secondPointSet", new Object[0]));
/*     */       
/*     */       return;
/*     */     } 
/*  94 */     throw new InvalidCommandArgumentsException();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Command(aliases = {"create", "save"}, description = "Create a mine from either your WorldEdit selection or by manually specifying the points", help = {"Provided you have a selection made via either WorldEdit or selecting the points using MRL,", "an empty mine will be created. This mine will have no composition and default settings."}, usage = "<mine name>", permissions = {"mineresetlite.mine.create"}, min = 1, max = -1, onlyPlayers = true)
/*     */   public void createMine(CommandSender sender, String[] args) {
/* 106 */     Player player = (Player)sender;
/* 107 */     World world = null;
/* 108 */     Vector p1 = null;
/* 109 */     Vector p2 = null;
/*     */     
/* 111 */     if (this.point1.containsKey(player) && this.point2.containsKey(player)) {
/* 112 */       world = ((Location)this.point1.get(player)).getWorld();
/* 113 */       if (!world.equals(((Location)this.point2.get(player)).getWorld())) {
/* 114 */         player.sendMessage(Phrases.phrase("crossWorldSelection", new Object[0]));
/*     */         return;
/*     */       } 
/* 117 */       p1 = ((Location)this.point1.get(player)).toVector();
/* 118 */       p2 = ((Location)this.point2.get(player)).toVector();
/*     */     } 
/*     */     
/* 121 */     if (this.plugin.hasWorldEdit() && this.plugin.getWorldEdit().getSelection(player) != null) {
/* 122 */       WorldEditPlugin worldEdit = this.plugin.getWorldEdit();
/* 123 */       Selection selection = worldEdit.getSelection(player);
/* 124 */       world = selection.getWorld();
/* 125 */       p1 = selection.getMinimumPoint().toVector();
/* 126 */       p2 = selection.getMaximumPoint().toVector();
/*     */     } 
/* 128 */     if (p1 == null) {
/* 129 */       player.sendMessage(Phrases.phrase("emptySelection", new Object[0]));
/*     */       
/*     */       return;
/*     */     } 
/* 133 */     String name = StringTools.buildSpacedArgument(args);
/*     */     
/* 135 */     Mine[] mines = this.plugin.matchMines(name);
/* 136 */     if (mines.length > 0) {
/* 137 */       player.sendMessage(Phrases.phrase("nameInUse", new Object[] { name }));
/*     */       
/*     */       return;
/*     */     } 
/* 141 */     if (p1.getX() > p2.getX()) {
/*     */       
/* 143 */       double x = p1.getX();
/* 144 */       p1.setX(p2.getX());
/* 145 */       p2.setX(x);
/*     */     } 
/* 147 */     if (p1.getY() > p2.getY()) {
/* 148 */       double y = p1.getY();
/* 149 */       p1.setY(p2.getY());
/* 150 */       p2.setY(y);
/*     */     } 
/* 152 */     if (p1.getZ() > p2.getZ()) {
/* 153 */       double z = p1.getZ();
/* 154 */       p1.setZ(p2.getZ());
/* 155 */       p2.setZ(z);
/*     */     } 
/*     */     
/* 158 */     Mine newMine = new Mine(p1.getBlockX(), p1.getBlockY(), p1.getBlockZ(), p2.getBlockX(), p2.getBlockY(), p2.getBlockZ(), name, world);
/* 159 */     this.plugin.mines.add(newMine);
/* 160 */     player.sendMessage(Phrases.phrase("mineCreated", new Object[] { newMine }));
/* 161 */     this.plugin.buffSave();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Command(aliases = {"info", "i"}, description = "List information about a mine", usage = "<mine name>", permissions = {"mineresetlite.mine.info"}, min = 1, max = -1, onlyPlayers = false)
/*     */   public void mineInfo(CommandSender sender, String[] args) {
/* 170 */     Mine[] mines = this.plugin.matchMines(StringTools.buildSpacedArgument(args));
/* 171 */     if (mines.length > 1) {
/* 172 */       sender.sendMessage(Phrases.phrase("tooManyMines", new Object[0])); return;
/*     */     } 
/* 174 */     if (mines.length == 0) {
/* 175 */       sender.sendMessage(Phrases.phrase("noMinesMatched", new Object[0]));
/*     */       return;
/*     */     } 
/* 178 */     sender.sendMessage(Phrases.phrase("mineInfoName", new Object[] { mines[0] }));
/* 179 */     sender.sendMessage(Phrases.phrase("mineInfoWorld", new Object[] { mines[0].getWorld() }));
/*     */     
/* 181 */     StringBuilder csb = new StringBuilder();
/* 182 */     for (Map.Entry<SerializableBlock, Double> entry : (Iterable<Map.Entry<SerializableBlock, Double>>)mines[0].getComposition().entrySet()) {
/* 183 */       csb.append(((Double)entry.getValue()).doubleValue() * 100.0D);
/* 184 */       csb.append("% ");
/* 185 */       csb.append(Material.getMaterial(((SerializableBlock)entry.getKey()).getBlockId()).toString());
/* 186 */       if (((SerializableBlock)entry.getKey()).getData() != 0) {
/* 187 */         csb.append(":");
/* 188 */         csb.append(((SerializableBlock)entry.getKey()).getData());
/*     */       } 
/* 190 */       csb.append(", ");
/*     */     } 
/* 192 */     if (csb.length() > 2) {
/* 193 */       csb.delete(csb.length() - 2, csb.length() - 1);
/*     */     }
/* 195 */     sender.sendMessage(Phrases.phrase("mineInfoComposition", new Object[] { csb }));
/* 196 */     if (mines[0].getResetDelay() != 0) {
/* 197 */       sender.sendMessage(Phrases.phrase("mineInfoResetDelay", new Object[] { Integer.valueOf(mines[0].getResetDelay()) }));
/* 198 */       sender.sendMessage(Phrases.phrase("mineInfoTimeUntilReset", new Object[] { Integer.valueOf(mines[0].getTimeUntilReset()) }));
/*     */     } 
/* 200 */     sender.sendMessage(Phrases.phrase("mineInfoSilence", new Object[] { Boolean.valueOf(mines[0].isSilent()) }));
/* 201 */     if (mines[0].getResetWarnings().size() > 0) {
/* 202 */       sender.sendMessage(Phrases.phrase("mineInfoWarningTimes", new Object[] { StringTools.buildList(mines[0].getResetWarnings(), "", ", ") }));
/*     */     }
/* 204 */     if (mines[0].getSurface() != null) {
/* 205 */       sender.sendMessage(Phrases.phrase("mineInfoSurface", new Object[] { mines[0].getSurface() }));
/*     */     }
/* 207 */     if (mines[0].getFillMode()) {
/* 208 */       sender.sendMessage(Phrases.phrase("mineInfoFillMode", new Object[0]));
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Command(aliases = {"set", "add", "+"}, description = "Set the percentage of a block in the mine", help = {"This command will always overwrite the current percentage for the specified block,", "if a percentage has already been set. You cannot set the percentage of any specific", "block, such that the percentage would then total over 100%."}, usage = "<mine name> <block>:(data) <percentage>%", permissions = {"mineresetlite.mine.composition"}, min = 3, max = -1, onlyPlayers = false)
/*     */   public void setComposition(CommandSender sender, String[] args) {
/* 221 */     Mine[] mines = this.plugin.matchMines(StringTools.buildSpacedArgument(args, 2));
/* 222 */     if (mines.length > 1) {
/* 223 */       sender.sendMessage(Phrases.phrase("tooManyMines", new Object[0])); return;
/*     */     } 
/* 225 */     if (mines.length == 0) {
/* 226 */       sender.sendMessage(Phrases.phrase("noMinesMatched", new Object[0]));
/*     */       
/*     */       return;
/*     */     } 
/* 230 */     String[] bits = args[args.length - 2].split(":");
/* 231 */     Material m = this.plugin.matchMaterial(bits[0]);
/* 232 */     if (m == null) {
/* 233 */       sender.sendMessage(Phrases.phrase("unknownBlock", new Object[0]));
/*     */       return;
/*     */     } 
/* 236 */     if (!m.isBlock()) {
/* 237 */       sender.sendMessage(Phrases.phrase("notABlock", new Object[0]));
/*     */       return;
/*     */     } 
/* 240 */     byte data = 0;
/* 241 */     if (bits.length == 2) {
/*     */       try {
/* 243 */         data = Byte.valueOf(bits[1]).byteValue();
/* 244 */       } catch (NumberFormatException nfe) {
/* 245 */         sender.sendMessage(Phrases.phrase("unknownBlock", new Object[0]));
/*     */         
/*     */         return;
/*     */       } 
/*     */     }
/* 250 */     String percentageS = args[args.length - 1];
/* 251 */     if (!percentageS.endsWith("%")) {
/* 252 */       sender.sendMessage(Phrases.phrase("badPercentage", new Object[0]));
/*     */       return;
/*     */     } 
/* 255 */     StringBuilder psb = new StringBuilder(percentageS);
/* 256 */     psb.deleteCharAt(psb.length() - 1);
/* 257 */     double percentage = 0.0D;
/*     */     try {
/* 259 */       percentage = Double.valueOf(psb.toString()).doubleValue();
/* 260 */     } catch (NumberFormatException nfe) {
/* 261 */       sender.sendMessage(Phrases.phrase("badPercentage", new Object[0]));
/*     */       return;
/*     */     } 
/* 264 */     if (percentage > 100.0D || percentage <= 0.0D) {
/* 265 */       sender.sendMessage(Phrases.phrase("badPercentage", new Object[0]));
/*     */       return;
/*     */     } 
/* 268 */     percentage /= 100.0D;
/* 269 */     SerializableBlock block = new SerializableBlock(m.getId(), data);
/* 270 */     Double oldPercentage = (Double)mines[0].getComposition().get(block);
/* 271 */     double total = 0.0D;
/* 272 */     for (Map.Entry<SerializableBlock, Double> entry : (Iterable<Map.Entry<SerializableBlock, Double>>)mines[0].getComposition().entrySet()) {
/* 273 */       if (!((SerializableBlock)entry.getKey()).equals(block)) {
/* 274 */         total += ((Double)entry.getValue()).doubleValue(); continue;
/*     */       } 
/* 276 */       block = entry.getKey();
/*     */     } 
/*     */     
/* 279 */     total += percentage;
/* 280 */     if (total > 1.0D) {
/* 281 */       sender.sendMessage(Phrases.phrase("insaneCompositionChange", new Object[0]));
/* 282 */       if (oldPercentage == null) {
/* 283 */         mines[0].getComposition().remove(block);
/*     */       } else {
/* 285 */         mines[0].getComposition().put(block, oldPercentage);
/*     */       } 
/*     */       return;
/*     */     } 
/* 289 */     mines[0].getComposition().put(block, Double.valueOf(percentage));
/* 290 */     sender.sendMessage(Phrases.phrase("mineCompositionSet", new Object[] { mines[0], Double.valueOf(percentage * 100.0D), block, Double.valueOf((1.0D - mines[0].getCompositionTotal()) * 100.0D) }));
/* 291 */     this.plugin.buffSave();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Command(aliases = {"unset", "remove", "-"}, description = "Remove a block from the composition of a mine", usage = "<mine name> <block>:(data)", permissions = {"mineresetlite.mine.composition"}, min = 2, max = -1, onlyPlayers = false)
/*     */   public void unsetComposition(CommandSender sender, String[] args) {
/* 300 */     Mine[] mines = this.plugin.matchMines(StringTools.buildSpacedArgument(args, 1));
/* 301 */     if (mines.length > 1) {
/* 302 */       sender.sendMessage(Phrases.phrase("tooManyMines", new Object[0])); return;
/*     */     } 
/* 304 */     if (mines.length == 0) {
/* 305 */       sender.sendMessage(Phrases.phrase("noMinesMatched", new Object[0]));
/*     */       
/*     */       return;
/*     */     } 
/* 309 */     String[] bits = args[args.length - 1].split(":");
/* 310 */     Material m = this.plugin.matchMaterial(bits[0]);
/* 311 */     if (m == null) {
/* 312 */       sender.sendMessage(Phrases.phrase("unknownBlock", new Object[0]));
/*     */       return;
/*     */     } 
/* 315 */     if (!m.isBlock()) {
/* 316 */       sender.sendMessage(Phrases.phrase("notABlock", new Object[0]));
/*     */       return;
/*     */     } 
/* 319 */     byte data = 0;
/* 320 */     if (bits.length == 2) {
/*     */       try {
/* 322 */         data = Byte.valueOf(bits[1]).byteValue();
/* 323 */       } catch (NumberFormatException nfe) {
/* 324 */         sender.sendMessage(Phrases.phrase("unknownBlock", new Object[0]));
/*     */         
/*     */         return;
/*     */       } 
/*     */     }
/* 329 */     SerializableBlock block = new SerializableBlock(m.getId(), data);
/* 330 */     for (Map.Entry<SerializableBlock, Double> entry : (Iterable<Map.Entry<SerializableBlock, Double>>)mines[0].getComposition().entrySet()) {
/* 331 */       if (((SerializableBlock)entry.getKey()).equals(block)) {
/* 332 */         mines[0].getComposition().remove(entry.getKey());
/* 333 */         sender.sendMessage(Phrases.phrase("blockRemovedFromMine", new Object[] { mines[0], block, Double.valueOf((1.0D - mines[0].getCompositionTotal()) * 100.0D) }));
/*     */         return;
/*     */       } 
/*     */     } 
/* 337 */     sender.sendMessage(Phrases.phrase("blockNotInMine", new Object[] { mines[0], block }));
/* 338 */     this.plugin.buffSave();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Command(aliases = {"reset", "r"}, description = "Reset a mine", help = {"If you supply the -s argument, the mine will silently reset. Resets triggered via", "this command will not show a 1 minute warning, unless this mine is flagged to always", "have a warning. If the mine's composition doesn't equal 100%, the composition will be", "padded with air until the total equals 100%."}, usage = "<mine name> (-s)", permissions = {"mineresetlite.mine.reset"}, min = 1, max = -1, onlyPlayers = false)
/*     */   public void resetMine(CommandSender sender, String[] args) {
/* 351 */     Mine[] mines = this.plugin.matchMines(StringTools.buildSpacedArgument(args).replace(" -s", ""));
/* 352 */     if (mines.length > 1) {
/* 353 */       sender.sendMessage(Phrases.phrase("tooManyMines", new Object[0])); return;
/*     */     } 
/* 355 */     if (mines.length == 0) {
/* 356 */       sender.sendMessage(Phrases.phrase("noMinesMatched", new Object[0]));
/*     */       return;
/*     */     } 
/* 359 */     if (args[args.length - 1].equalsIgnoreCase("-s")) {
/*     */       
/* 361 */       mines[0].reset();
/*     */     } else {
/* 363 */       MineResetLite.broadcast(Phrases.phrase("mineResetBroadcast", new Object[] { mines[0], sender }), mines[0]);
/* 364 */       mines[0].reset();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Command(aliases = {"flag", "f"}, description = "Set various properties of a mine, including automatic resets", help = {"Available flags:", "resetDelay: An integer number of minutes specifying the time between automatic resets. Set to 0 to disable automatic resets.", "resetWarnings: A comma separated list of integer minutes to warn before the automatic reset. Warnings must be less than the reset delay.", "surface: A block that will cover the entire top surface of the mine when reset, obscuring surface ores. Set surface to air to clear the value.", "fillMode: An alternate reset algorithm that will only \"reset\" air blocks inside your mine. Set to true or false.", "isSilent: A boolean (true or false) of whether or not this mine should broadcast a reset notification when it is reset *automatically*"}, usage = "<mine name> <setting> <value>", permissions = {"mineresetlite.mine.flag"}, min = 3, max = -1, onlyPlayers = false)
/*     */   public void flag(CommandSender sender, String[] args) {
/* 380 */     Mine[] mines = this.plugin.matchMines(StringTools.buildSpacedArgument(args, 2));
/* 381 */     if (mines.length > 1) {
/* 382 */       sender.sendMessage(Phrases.phrase("tooManyMines", new Object[0])); return;
/*     */     } 
/* 384 */     if (mines.length == 0) {
/* 385 */       sender.sendMessage(Phrases.phrase("noMinesMatched", new Object[0]));
/*     */       return;
/*     */     } 
/* 388 */     String setting = args[args.length - 2];
/* 389 */     String value = args[args.length - 1];
/* 390 */     if (setting.equalsIgnoreCase("resetEvery") || setting.equalsIgnoreCase("resetDelay")) {
/*     */       int delay;
/*     */       try {
/* 393 */         delay = Integer.valueOf(value).intValue();
/* 394 */       } catch (NumberFormatException nfe) {
/* 395 */         sender.sendMessage(Phrases.phrase("badResetDelay", new Object[0]));
/*     */         return;
/*     */       } 
/* 398 */       if (delay < 0) {
/* 399 */         sender.sendMessage(Phrases.phrase("badResetDelay", new Object[0]));
/*     */         return;
/*     */       } 
/* 402 */       mines[0].setResetDelay(delay);
/* 403 */       if (delay == 0) {
/* 404 */         sender.sendMessage(Phrases.phrase("resetDelayCleared", new Object[] { mines[0] }));
/*     */       } else {
/* 406 */         sender.sendMessage(Phrases.phrase("resetDelaySet", new Object[] { mines[0], Integer.valueOf(delay) }));
/*     */       } 
/* 408 */       this.plugin.buffSave(); return;
/*     */     } 
/* 410 */     if (setting.equalsIgnoreCase("resetWarnings") || setting.equalsIgnoreCase("resetWarning")) {
/* 411 */       String[] bits = value.split(",");
/* 412 */       List<Integer> warnings = mines[0].getResetWarnings();
/* 413 */       List<Integer> oldList = new LinkedList<>(warnings);
/* 414 */       warnings.clear();
/* 415 */       for (String bit : bits) {
/*     */         try {
/* 417 */           warnings.add(Integer.valueOf(bit));
/* 418 */         } catch (NumberFormatException nfe) {
/* 419 */           sender.sendMessage(Phrases.phrase("badWarningList", new Object[0]));
/*     */           
/*     */           return;
/*     */         } 
/*     */       } 
/* 424 */       for (Integer warning : warnings) {
/* 425 */         if (warning.intValue() >= mines[0].getResetDelay()) {
/* 426 */           sender.sendMessage(Phrases.phrase("badWarningList", new Object[0]));
/* 427 */           mines[0].setResetWarnings(oldList);
/*     */           return;
/*     */         } 
/*     */       } 
/* 431 */       if (warnings.contains(Integer.valueOf(0)) && warnings.size() == 1) {
/* 432 */         warnings.clear();
/* 433 */         sender.sendMessage(Phrases.phrase("warningListCleared", new Object[] { mines[0] })); return;
/*     */       } 
/* 435 */       if (warnings.contains(Integer.valueOf(0))) {
/* 436 */         sender.sendMessage(Phrases.phrase("badWarningList", new Object[0]));
/* 437 */         mines[0].setResetWarnings(oldList);
/*     */         return;
/*     */       } 
/* 440 */       sender.sendMessage(Phrases.phrase("warningListSet", new Object[] { mines[0] }));
/* 441 */       this.plugin.buffSave(); return;
/*     */     } 
/* 443 */     if (setting.equalsIgnoreCase("surface")) {
/*     */       
/* 445 */       String[] bits = value.split(":");
/* 446 */       Material m = this.plugin.matchMaterial(bits[0]);
/* 447 */       if (m == null) {
/* 448 */         sender.sendMessage(Phrases.phrase("unknownBlock", new Object[0]));
/*     */         return;
/*     */       } 
/* 451 */       if (!m.isBlock()) {
/* 452 */         sender.sendMessage(Phrases.phrase("notABlock", new Object[0]));
/*     */         return;
/*     */       } 
/* 455 */       byte data = 0;
/* 456 */       if (bits.length == 2) {
/*     */         try {
/* 458 */           data = Byte.valueOf(bits[1]).byteValue();
/* 459 */         } catch (NumberFormatException nfe) {
/* 460 */           sender.sendMessage(Phrases.phrase("unknownBlock", new Object[0]));
/*     */           return;
/*     */         } 
/*     */       }
/* 464 */       if (m.equals(Material.AIR)) {
/* 465 */         mines[0].setSurface(null);
/* 466 */         sender.sendMessage(Phrases.phrase("surfaceBlockCleared", new Object[] { mines[0] }));
/* 467 */         this.plugin.buffSave();
/*     */         return;
/*     */       } 
/* 470 */       SerializableBlock block = new SerializableBlock(m.getId(), data);
/* 471 */       mines[0].setSurface(block);
/* 472 */       sender.sendMessage(Phrases.phrase("surfaceBlockSet", new Object[] { mines[0] }));
/* 473 */       this.plugin.buffSave(); return;
/*     */     } 
/* 475 */     if (setting.equalsIgnoreCase("fill") || setting.equalsIgnoreCase("fillMode")) {
/*     */       
/* 477 */       if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("yes") || value.equalsIgnoreCase("enabled")) {
/* 478 */         mines[0].setFillMode(true);
/* 479 */         sender.sendMessage(Phrases.phrase("fillModeEnabled", new Object[0]));
/* 480 */         this.plugin.buffSave(); return;
/*     */       } 
/* 482 */       if (value.equalsIgnoreCase("false") || value.equalsIgnoreCase("no") || value.equalsIgnoreCase("disabled")) {
/* 483 */         mines[0].setFillMode(false);
/* 484 */         sender.sendMessage(Phrases.phrase("fillModeDisabled", new Object[0]));
/* 485 */         this.plugin.buffSave();
/*     */         return;
/*     */       } 
/* 488 */       sender.sendMessage(Phrases.phrase("invalidFillMode", new Object[0]));
/* 489 */     } else if (setting.equalsIgnoreCase("isSilent") || setting.equalsIgnoreCase("silent") || setting.equalsIgnoreCase("silence")) {
/* 490 */       if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("yes") || value.equalsIgnoreCase("enabled")) {
/* 491 */         mines[0].setSilence(true);
/* 492 */         sender.sendMessage(Phrases.phrase("mineIsNowSilent", new Object[] { mines[0] }));
/* 493 */         this.plugin.buffSave(); return;
/*     */       } 
/* 495 */       if (value.equalsIgnoreCase("false") || value.equalsIgnoreCase("no") || value.equalsIgnoreCase("disabled")) {
/* 496 */         mines[0].setSilence(false);
/* 497 */         sender.sendMessage(Phrases.phrase("mineIsNoLongerSilent", new Object[] { mines[0] }));
/* 498 */         this.plugin.buffSave();
/*     */         return;
/*     */       } 
/* 501 */       sender.sendMessage(Phrases.phrase("badBoolean", new Object[0]));
/*     */     } 
/* 503 */     sender.sendMessage(Phrases.phrase("unknownFlag", new Object[0]));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Command(aliases = {"erase"}, description = "Completely erase a mine", help = {"Like most erasures of data, be sure you don't need to recover anything from this mine before you delete it."}, usage = "<mine name>", permissions = {"mineresetlite.mine.erase"}, min = 1, max = -1, onlyPlayers = false)
/*     */   public void erase(CommandSender sender, String[] args) {
/* 512 */     Mine[] mines = this.plugin.matchMines(StringTools.buildSpacedArgument(args));
/* 513 */     if (mines.length > 1) {
/* 514 */       sender.sendMessage(Phrases.phrase("tooManyMines", new Object[0])); return;
/*     */     } 
/* 516 */     if (mines.length == 0) {
/* 517 */       sender.sendMessage(Phrases.phrase("noMinesMatched", new Object[0]));
/*     */       return;
/*     */     } 
/* 520 */     this.plugin.eraseMine(mines[0]);
/* 521 */     sender.sendMessage(Phrases.phrase("mineErased", new Object[] { mines[0] }));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Command(aliases = {"reschedule"}, description = "Synchronize all automatic mine resets", help = {"This command will set the 'start time' of the mine resets to the same point."}, usage = "", permissions = {"mineresetlite.mine.flag"}, min = 0, max = 0, onlyPlayers = false)
/*     */   public void reschedule(CommandSender sender, String[] args) {
/* 530 */     for (Mine mine : this.plugin.mines) {
/* 531 */       mine.setResetDelay(mine.getResetDelay());
/*     */     }
/* 533 */     this.plugin.buffSave();
/* 534 */     sender.sendMessage(Phrases.phrase("rescheduled", new Object[0]));
/*     */   }
/*     */ }


/* Location:              C:\Users\zacha\Downloads\MineResetLite.jar!\com\koletar\jj\mineresetlite\commands\MineCommands.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */