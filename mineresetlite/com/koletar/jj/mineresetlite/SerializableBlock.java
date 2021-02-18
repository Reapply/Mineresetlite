/*    */ package com.koletar.jj.mineresetlite;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class SerializableBlock
/*    */ {
/*    */   private int blockId;
/*    */   private byte data;
/*    */   
/*    */   public SerializableBlock(int blockId) {
/* 11 */     this.blockId = blockId;
/* 12 */     this.data = 0;
/*    */   }
/*    */   
/*    */   public SerializableBlock(int blockId, byte data) {
/* 16 */     this.blockId = blockId;
/* 17 */     this.data = data;
/*    */   }
/*    */   
/*    */   public SerializableBlock(String self) {
/* 21 */     String[] bits = self.split(":");
/* 22 */     if (bits.length != 2) {
/* 23 */       throw new IllegalArgumentException("String form of SerializableBlock didn't have exactly 2 numbers");
/*    */     }
/*    */     try {
/* 26 */       this.blockId = Integer.valueOf(bits[0]).intValue();
/* 27 */       this.data = Byte.valueOf(bits[1]).byteValue();
/* 28 */     } catch (NumberFormatException nfe) {
/* 29 */       throw new IllegalArgumentException("Unable to convert id to integer and data to byte");
/*    */     } 
/*    */   }
/*    */   
/*    */   public int getBlockId() {
/* 34 */     return this.blockId;
/*    */   }
/*    */   
/*    */   public byte getData() {
/* 38 */     return this.data;
/*    */   }
/*    */   
/*    */   public String toString() {
/* 42 */     return this.blockId + ":" + this.data;
/*    */   }
/*    */   
/*    */   public boolean equals(Object o) {
/* 46 */     return (o instanceof SerializableBlock && this.blockId == ((SerializableBlock)o).blockId && this.data == ((SerializableBlock)o).data);
/*    */   }
/*    */ }


/* Location:              C:\Users\zacha\Downloads\MineResetLite.jar!\com\koletar\jj\mineresetlite\SerializableBlock.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */