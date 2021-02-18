/*    */ package com.koletar.jj.mineresetlite.commands;
/*    */ 
/*    */ import com.koletar.jj.mineresetlite.Command;
/*    */ import com.koletar.jj.mineresetlite.MineResetLite;
/*    */ import com.koletar.jj.mineresetlite.Phrases;
/*    */ import org.bukkit.command.CommandSender;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class PluginCommands
/*    */ {
/*    */   private MineResetLite plugin;
/*    */   
/*    */   public PluginCommands(MineResetLite plugin) {
/* 16 */     this.plugin = plugin;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @Command(aliases = {"about"}, description = "List version and project information about MRL", permissions = {}, help = {"Show version information about this installation of MRL, in addition", "to the authors of the plugin."}, min = 0, max = 0, onlyPlayers = false)
/*    */   public void about(CommandSender sender, String[] args) {
/* 25 */     sender.sendMessage(Phrases.phrase("aboutTitle", new Object[0]));
/* 26 */     sender.sendMessage(Phrases.phrase("aboutAuthors", new Object[0]));
/* 27 */     sender.sendMessage(Phrases.phrase("aboutVersion", new Object[] { this.plugin.getDescription().getVersion() }));
/*    */   }
/*    */ }


/* Location:              C:\Users\zacha\Downloads\MineResetLite.jar!\com\koletar\jj\mineresetlite\commands\PluginCommands.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */