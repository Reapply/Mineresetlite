/*     */ package com.koletar.jj.mineresetlite;
/*     */ 
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.HashMap;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.bukkit.ChatColor;
/*     */ import org.bukkit.command.CommandSender;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class CommandManager
/*     */ {
/*  28 */   private Map<String, Method> commands = new HashMap<>();
/*  29 */   private Map<Method, Object> instances = new HashMap<>();
/*     */ 
/*     */   
/*     */   public void register(Class<?> cls, Object obj) {
/*  33 */     for (Method method : cls.getMethods()) {
/*  34 */       if (method.isAnnotationPresent((Class)Command.class)) {
/*     */ 
/*     */ 
/*     */         
/*  38 */         Command command = method.<Command>getAnnotation(Command.class);
/*     */         
/*  40 */         for (String alias : command.aliases()) {
/*  41 */           this.commands.put(alias, method);
/*     */         }
/*  43 */         this.instances.put(method, obj);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   @Command(aliases = {"help", "?"}, description = "Provide information about MineResetLite commands", min = 0, max = -1)
/*     */   public void help(CommandSender sender, String[] args) {
/*  51 */     if (args.length >= 1)
/*     */     {
/*  53 */       if (this.commands.containsKey(args[0].toLowerCase())) {
/*  54 */         Command command = ((Method)this.commands.get(args[0].toLowerCase())).<Command>getAnnotation(Command.class);
/*  55 */         sender.sendMessage(Phrases.phrase("helpUsage", new Object[] { command.aliases()[0], command.usage() }));
/*  56 */         for (String help : command.help()) {
/*  57 */           sender.sendMessage(ChatColor.GRAY + help);
/*     */         }
/*     */         return;
/*     */       } 
/*     */     }
/*  62 */     List<Method> seenMethods = new LinkedList<>();
/*  63 */     for (Map.Entry<String, Method> entry : this.commands.entrySet()) {
/*  64 */       if (!seenMethods.contains(entry.getValue())) {
/*  65 */         seenMethods.add(entry.getValue());
/*  66 */         Command command = ((Method)entry.getValue()).<Command>getAnnotation(Command.class);
/*     */         
/*  68 */         if (command.onlyPlayers() && !(sender instanceof org.bukkit.entity.Player)) {
/*     */           continue;
/*     */         }
/*  71 */         boolean may = false;
/*  72 */         for (String perm : command.permissions()) {
/*  73 */           if (sender.hasPermission(perm)) {
/*  74 */             may = true;
/*     */           }
/*     */         } 
/*  77 */         if (!may) {
/*     */           continue;
/*     */         }
/*  80 */         sender.sendMessage(Phrases.phrase("helpUsage", new Object[] { command.aliases()[0], command.usage() }));
/*  81 */         sender.sendMessage(Phrases.phrase("helpDesc", new Object[] { command.description() }));
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void callCommand(String cmdName, CommandSender sender, String[] args) {
/*  89 */     Method method = this.commands.get(cmdName.toLowerCase());
/*  90 */     if (method == null) {
/*  91 */       sender.sendMessage(Phrases.phrase("unknownCommand", new Object[0]));
/*     */       
/*     */       return;
/*     */     } 
/*  95 */     Command command = method.<Command>getAnnotation(Command.class);
/*     */ 
/*     */     
/*  98 */     if (command.min() > args.length || (command.max() != -1 && command.max() < args.length)) {
/*  99 */       sender.sendMessage(Phrases.phrase("invalidArguments", new Object[0]));
/* 100 */       sender.sendMessage(Phrases.phrase("invalidArgsUsage", new Object[] { command.aliases()[0], command.usage() }));
/*     */       
/*     */       return;
/*     */     } 
/*     */     
/* 105 */     if (command.onlyPlayers() && !(sender instanceof org.bukkit.entity.Player)) {
/* 106 */       sender.sendMessage(Phrases.phrase("notAPlayer", new Object[0]));
/*     */       
/*     */       return;
/*     */     } 
/*     */     
/* 111 */     boolean may = false;
/* 112 */     if ((command.permissions()).length == 0) {
/* 113 */       may = true;
/*     */     }
/* 115 */     for (String perm : command.permissions()) {
/* 116 */       if (sender.hasPermission(perm)) {
/* 117 */         may = true;
/*     */       }
/*     */     } 
/* 120 */     if (!may) {
/* 121 */       sender.sendMessage(Phrases.phrase("noPermission", new Object[0]));
/*     */       
/*     */       return;
/*     */     } 
/*     */     
/* 126 */     Object[] methodArgs = { sender, args };
/*     */     try {
/* 128 */       method.invoke(this.instances.get(method), methodArgs);
/* 129 */     } catch (IllegalAccessException e) {
/* 130 */       e.printStackTrace();
/* 131 */       throw new RuntimeException("Invalid methods on command!");
/* 132 */     } catch (InvocationTargetException e) {
/* 133 */       if (e.getCause() instanceof InvalidCommandArgumentsException) {
/* 134 */         sender.sendMessage(Phrases.phrase("invalidArguments", new Object[0]));
/* 135 */         sender.sendMessage(Phrases.phrase("invalidArgsUsage", new Object[] { command.aliases()[0], command.usage() }));
/*     */       } else {
/* 137 */         e.printStackTrace();
/* 138 */         throw new RuntimeException("Invalid methods on command!");
/*     */       } 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\zacha\Downloads\MineResetLite.jar!\com\koletar\jj\mineresetlite\CommandManager.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */