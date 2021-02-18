/*     */ package com.koletar.jj.mineresetlite;
/*     */ 
/*     */ import java.io.BufferedWriter;
/*     */ import java.io.File;
/*     */ import java.io.FileWriter;
/*     */ import java.io.IOException;
/*     */ import org.bukkit.configuration.file.YamlConfiguration;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Config
/*     */ {
/*     */   private static boolean broadcastInWorldOnly = false;
/*     */   private static boolean broadcastNearbyOnly = false;
/*     */   private static boolean checkForUpdates = true;
/*  18 */   private static String locale = "en";
/*     */   
/*     */   public static boolean getBroadcastInWorldOnly() {
/*  21 */     return broadcastInWorldOnly;
/*     */   }
/*     */   
/*     */   public static boolean getBroadcastNearbyOnly() {
/*  25 */     return broadcastNearbyOnly;
/*     */   }
/*     */   
/*     */   private static void setBroadcastInWorldOnly(boolean broadcastInWorldOnly) {
/*  29 */     Config.broadcastInWorldOnly = broadcastInWorldOnly;
/*     */   }
/*     */   
/*     */   private static void setBroadcastNearbyOnly(boolean broadcastNearbyOnly) {
/*  33 */     Config.broadcastNearbyOnly = broadcastNearbyOnly;
/*     */   }
/*     */   
/*     */   public static void writeBroadcastInWorldOnly(BufferedWriter out) throws IOException {
/*  37 */     out.write("# If you have multiple worlds, and wish for only the worlds in which your mine resides to receive");
/*  38 */     out.newLine();
/*  39 */     out.write("# reset notifications, and automatic reset warnings, set this to true.");
/*  40 */     out.newLine();
/*  41 */     out.write("broadcast-in-world-only: false");
/*  42 */     out.newLine();
/*     */   }
/*     */   
/*     */   public static void writeBroadcastNearbyOnly(BufferedWriter out) throws IOException {
/*  46 */     out.write("# If you only want players nearby the mines to receive reset notifications,");
/*  47 */     out.newLine();
/*  48 */     out.write("# and automatic reset warnings, set this to true. Note: Currently only broadcasts to players in the mine");
/*  49 */     out.newLine();
/*  50 */     out.write("broadcast-nearby-only: false");
/*  51 */     out.newLine();
/*     */   }
/*     */   
/*     */   public static boolean getCheckForUpdates() {
/*  55 */     return checkForUpdates;
/*     */   }
/*     */   
/*     */   private static void setCheckForUpdates(boolean checkForUpdates) {
/*  59 */     Config.checkForUpdates = checkForUpdates;
/*     */   }
/*     */   
/*     */   public static void writeCheckForUpdates(BufferedWriter out) throws IOException {
/*  63 */     out.write("# When true, this config option enables update alerts. I do not send any extra information along when ");
/*  64 */     out.newLine();
/*  65 */     out.write("# checking, and query a static file hosted on Dropbox. ");
/*  66 */     out.newLine();
/*  67 */     out.write("check-for-updates: true");
/*  68 */     out.newLine();
/*     */   }
/*     */   
/*     */   public static String getLocale() {
/*  72 */     return locale;
/*     */   }
/*     */   
/*     */   protected static void setLocale(String locale) {
/*  76 */     Config.locale = locale;
/*     */   }
/*     */   
/*     */   public static void writeLocale(BufferedWriter out) throws IOException {
/*  80 */     out.write("# MineResetLite supports multiple languages. Indicate the language to be used here.");
/*  81 */     out.newLine();
/*  82 */     out.write("# Languages available at the time this config was generated: Danish (thanks Beijiru), Spanish (thanks enetocs), Portuguese (thanks FelipeMarques14), Italian (thanks JoLong)");
/*  83 */     out.newLine();
/*  84 */     out.write("# Use the following values for these languages: English: 'en', Danish: 'da', Spanish: 'es', Portuguese: 'pt', Italian: 'it', French: 'fr', Dutch: 'nl', Polish: 'pl'");
/*  85 */     out.newLine();
/*  86 */     out.write("# A fully up-to-date list of languages is available at http://dev.bukkit.org/server-mods/mineresetlite/pages/internationalization/");
/*  87 */     out.newLine();
/*  88 */     out.write("locale: en");
/*  89 */     out.newLine();
/*     */   }
/*     */   
/*     */   public static void initConfig(File dataFolder) throws IOException {
/*  93 */     if (!dataFolder.exists()) {
/*  94 */       dataFolder.mkdir();
/*     */     }
/*  96 */     File configFile = new File(dataFolder, "config.yml");
/*  97 */     if (!configFile.exists()) {
/*  98 */       configFile.createNewFile();
/*  99 */       BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(configFile));
/* 100 */       bufferedWriter.write("# MineResetLite Configuration File");
/* 101 */       bufferedWriter.newLine();
/* 102 */       writeBroadcastInWorldOnly(bufferedWriter);
/* 103 */       writeBroadcastNearbyOnly(bufferedWriter);
/* 104 */       writeCheckForUpdates(bufferedWriter);
/* 105 */       writeLocale(bufferedWriter);
/* 106 */       bufferedWriter.close();
/*     */     } 
/* 108 */     YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);
/* 109 */     BufferedWriter out = new BufferedWriter(new FileWriter(configFile, true));
/* 110 */     if (config.contains("broadcast-in-world-only")) {
/* 111 */       setBroadcastInWorldOnly(config.getBoolean("broadcast-in-world-only"));
/*     */     } else {
/* 113 */       writeBroadcastInWorldOnly(out);
/*     */     } 
/* 115 */     if (config.contains("broadcast-nearby-only")) {
/* 116 */       setBroadcastNearbyOnly(config.getBoolean("broadcast-nearby-only"));
/*     */     } else {
/* 118 */       writeBroadcastNearbyOnly(out);
/*     */     } 
/* 120 */     if (config.contains("check-for-updates")) {
/* 121 */       setCheckForUpdates(config.getBoolean("check-for-updates"));
/*     */     } else {
/* 123 */       writeCheckForUpdates(out);
/*     */     } 
/* 125 */     if (config.contains("locale")) {
/* 126 */       setLocale(config.getString("locale"));
/*     */     } else {
/* 128 */       writeLocale(out);
/*     */     } 
/* 130 */     out.close();
/*     */   }
/*     */ }


/* Location:              C:\Users\zacha\Downloads\MineResetLite.jar!\com\koletar\jj\mineresetlite\Config.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */