/*    */ package com.koletar.jj.mineresetlite;
/*    */ 
/*    */ import java.util.Locale;
/*    */ import java.util.Properties;
/*    */ import java.util.ResourceBundle;
/*    */ import java.util.logging.Logger;
/*    */ import org.bukkit.Material;
/*    */ import org.bukkit.World;
/*    */ import org.bukkit.command.BlockCommandSender;
/*    */ import org.bukkit.entity.Player;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class Phrases
/*    */ {
/*    */   private static Phrases instance;
/*    */   private ResourceBundle phrases;
/*    */   private Properties overrides;
/*    */   
/*    */   public static Phrases getInstance() {
/* 25 */     if (instance == null) {
/* 26 */       instance = new Phrases();
/*    */     }
/* 28 */     return instance;
/*    */   }
/*    */   
/*    */   public void initialize(Locale l) {
/* 32 */     this.phrases = ResourceBundle.getBundle("phrases", l);
/*    */   }
/*    */   
/*    */   public void overrides(Properties overrides) {
/* 36 */     this.overrides = overrides;
/*    */   }
/*    */   
/*    */   public static String phrase(String key, Object... replacements) {
/* 40 */     if (getInstance() == null) {
/* 41 */       return "";
/*    */     }
/* 43 */     if ((getInstance()).phrases == null) {
/* 44 */       return "§4Phrase Error! Did you /reload? Don't!";
/*    */     }
/* 46 */     if (!(getInstance()).phrases.containsKey(key)) {
/* 47 */       Logger.getLogger("Minecraft").warning("[MineResetLite] Unknown phrase key! '" + key + "'");
/* 48 */       return "";
/*    */     } 
/*    */     
/* 51 */     if ((getInstance()).overrides != null && (getInstance()).overrides.containsKey(key)) {
/* 52 */       format = (getInstance()).overrides.getProperty(key);
/*    */     } else {
/* 54 */       format = (getInstance()).phrases.getString(key);
/*    */     } 
/* 56 */     for (int i = 0; i < replacements.length; i++) {
/* 57 */       format = format.replace("%" + i + "%", findName(replacements[i]));
/*    */     }
/* 59 */     String format = format.replace("&", "§").replace("§§", "&");
/* 60 */     return format;
/*    */   }
/*    */   
/*    */   public static String findName(Object o) {
/* 64 */     if (o instanceof Mine)
/* 65 */       return ((Mine)o).getName(); 
/* 66 */     if (o instanceof Player)
/* 67 */       return ((Player)o).getName(); 
/* 68 */     if (o instanceof World)
/* 69 */       return ((World)o).getName(); 
/* 70 */     if (o instanceof SerializableBlock)
/* 71 */       return Material.getMaterial(((SerializableBlock)o).getBlockId()).toString() + ((((SerializableBlock)o).getData() != 0) ? (":" + ((SerializableBlock)o).getData()) : ""); 
/* 72 */     if (o instanceof org.bukkit.command.ConsoleCommandSender)
/* 73 */       return phrase("console", new Object[0]); 
/* 74 */     if (o instanceof BlockCommandSender) {
/* 75 */       return ((BlockCommandSender)o).getBlock().getType().toString();
/*    */     }
/* 77 */     return o.toString();
/*    */   }
/*    */ }


/* Location:              C:\Users\zacha\Downloads\MineResetLite.jar!\com\koletar\jj\mineresetlite\Phrases.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */