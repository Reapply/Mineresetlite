/*    */ package com.koletar.jj.mineresetlite;
/*    */ 
/*    */ import java.util.List;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class StringTools
/*    */ {
/*    */   public static String buildSpacedArgument(String[] args, int start, int stop) {
/* 18 */     StringBuilder sb = new StringBuilder();
/* 19 */     for (int i = start; i < args.length - stop; i++) {
/* 20 */       sb.append(args[i]);
/* 21 */       sb.append(" ");
/*    */     } 
/* 23 */     if (sb.length() > 1) {
/* 24 */       sb.deleteCharAt(sb.length() - 1);
/*    */     }
/* 26 */     return sb.toString();
/*    */   }
/*    */   
/*    */   public static String buildSpacedArgument(String[] args, int stop) {
/* 30 */     return buildSpacedArgument(args, 0, stop);
/*    */   }
/*    */   
/*    */   public static String buildSpacedArgument(String[] args) {
/* 34 */     return buildSpacedArgument(args, 0);
/*    */   }
/*    */   
/*    */   public static String buildList(Object[] items, String prefix, String suffix) {
/* 38 */     StringBuilder sb = new StringBuilder();
/* 39 */     for (int i = 0; i < items.length; i++) {
/* 40 */       sb.append(prefix);
/* 41 */       sb.append(Phrases.findName(items[i]));
/* 42 */       if (i < items.length - 1) {
/* 43 */         sb.append(suffix);
/*    */       }
/*    */     } 
/* 46 */     return sb.toString();
/*    */   }
/*    */   
/*    */   public static String buildList(List<?> items, String prefix, String suffix) {
/* 50 */     return buildList(items.toArray(), prefix, suffix);
/*    */   }
/*    */ }


/* Location:              C:\Users\zacha\Downloads\MineResetLite.jar!\com\koletar\jj\mineresetlite\StringTools.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */