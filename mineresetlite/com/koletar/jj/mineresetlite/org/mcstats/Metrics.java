/*     */ package com.koletar.jj.mineresetlite.org.mcstats;
/*     */ 
/*     */ import java.io.BufferedReader;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStreamReader;
/*     */ import java.io.OutputStreamWriter;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.net.Proxy;
/*     */ import java.net.URL;
/*     */ import java.net.URLConnection;
/*     */ import java.net.URLEncoder;
/*     */ import java.util.Collections;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedHashSet;
/*     */ import java.util.Set;
/*     */ import java.util.UUID;
/*     */ import java.util.logging.Level;
/*     */ import org.bukkit.Bukkit;
/*     */ import org.bukkit.configuration.InvalidConfigurationException;
/*     */ import org.bukkit.configuration.file.YamlConfiguration;
/*     */ import org.bukkit.plugin.Plugin;
/*     */ import org.bukkit.plugin.PluginDescriptionFile;
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
/*     */ public class Metrics
/*     */ {
/*     */   private static final int REVISION = 5;
/*     */   private static final String BASE_URL = "http://mcstats.org";
/*     */   private static final String REPORT_URL = "/report/%s";
/*     */   private static final String CUSTOM_DATA_SEPARATOR = "~~";
/*     */   private static final int PING_INTERVAL = 10;
/*     */   private final Plugin plugin;
/* 104 */   private final Set<Graph> graphs = Collections.synchronizedSet(new HashSet<>());
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 109 */   private final Graph defaultGraph = new Graph("Default");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final YamlConfiguration configuration;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final File configurationFile;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final String guid;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 129 */   private final Object optOutLock = new Object();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 134 */   private volatile int taskId = -1;
/*     */   
/*     */   public Metrics(Plugin plugin) throws IOException {
/* 137 */     if (plugin == null) {
/* 138 */       throw new IllegalArgumentException("Plugin cannot be null");
/*     */     }
/*     */     
/* 141 */     this.plugin = plugin;
/*     */ 
/*     */     
/* 144 */     this.configurationFile = getConfigFile();
/* 145 */     this.configuration = YamlConfiguration.loadConfiguration(this.configurationFile);
/*     */ 
/*     */     
/* 148 */     this.configuration.addDefault("opt-out", Boolean.valueOf(false));
/* 149 */     this.configuration.addDefault("guid", UUID.randomUUID().toString());
/*     */ 
/*     */     
/* 152 */     if (this.configuration.get("guid", null) == null) {
/* 153 */       this.configuration.options().header("http://mcstats.org").copyDefaults(true);
/* 154 */       this.configuration.save(this.configurationFile);
/*     */     } 
/*     */ 
/*     */     
/* 158 */     this.guid = this.configuration.getString("guid");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Graph createGraph(String name) {
/* 169 */     if (name == null) {
/* 170 */       throw new IllegalArgumentException("Graph name cannot be null");
/*     */     }
/*     */ 
/*     */     
/* 174 */     Graph graph = new Graph(name);
/*     */ 
/*     */     
/* 177 */     this.graphs.add(graph);
/*     */ 
/*     */     
/* 180 */     return graph;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addGraph(Graph graph) {
/* 189 */     if (graph == null) {
/* 190 */       throw new IllegalArgumentException("Graph cannot be null");
/*     */     }
/*     */     
/* 193 */     this.graphs.add(graph);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addCustomData(Plotter plotter) {
/* 202 */     if (plotter == null) {
/* 203 */       throw new IllegalArgumentException("Plotter cannot be null");
/*     */     }
/*     */ 
/*     */     
/* 207 */     this.defaultGraph.addPlotter(plotter);
/*     */ 
/*     */     
/* 210 */     this.graphs.add(this.defaultGraph);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean start() {
/* 221 */     synchronized (this.optOutLock) {
/*     */       
/* 223 */       if (isOptOut()) {
/* 224 */         return false;
/*     */       }
/*     */ 
/*     */       
/* 228 */       if (this.taskId >= 0) {
/* 229 */         return true;
/*     */       }
/*     */ 
/*     */       
/* 233 */       this.taskId = this.plugin.getServer().getScheduler().scheduleAsyncRepeatingTask(this.plugin, new Runnable()
/*     */           {
/*     */             private boolean firstPost = true;
/*     */ 
/*     */             
/*     */             public void run() {
/*     */               try {
/* 240 */                 synchronized (Metrics.this.optOutLock) {
/*     */                   
/* 242 */                   if (Metrics.this.isOptOut() && Metrics.this.taskId > 0) {
/* 243 */                     Metrics.this.plugin.getServer().getScheduler().cancelTask(Metrics.this.taskId);
/* 244 */                     Metrics.this.taskId = -1;
/*     */                     
/* 246 */                     for (Metrics.Graph graph : Metrics.this.graphs) {
/* 247 */                       graph.onOptOut();
/*     */                     }
/*     */                   } 
/*     */                 } 
/*     */ 
/*     */ 
/*     */ 
/*     */                 
/* 255 */                 Metrics.this.postPlugin(!this.firstPost);
/*     */ 
/*     */ 
/*     */                 
/* 259 */                 this.firstPost = false;
/* 260 */               } catch (IOException e) {
/* 261 */                 Bukkit.getLogger().log(Level.INFO, "[Metrics] " + e.getMessage());
/*     */               } 
/*     */             }
/*     */           }0L, 12000L);
/*     */       
/* 266 */       return true;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isOptOut() {
/* 276 */     synchronized (this.optOutLock) {
/*     */       
/*     */       try {
/* 279 */         this.configuration.load(getConfigFile());
/* 280 */       } catch (IOException ex) {
/* 281 */         Bukkit.getLogger().log(Level.INFO, "[Metrics] " + ex.getMessage());
/* 282 */         return true;
/* 283 */       } catch (InvalidConfigurationException ex) {
/* 284 */         Bukkit.getLogger().log(Level.INFO, "[Metrics] " + ex.getMessage());
/* 285 */         return true;
/*     */       } 
/* 287 */       return this.configuration.getBoolean("opt-out", false);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void enable() throws IOException {
/* 298 */     synchronized (this.optOutLock) {
/*     */       
/* 300 */       if (isOptOut()) {
/* 301 */         this.configuration.set("opt-out", Boolean.valueOf(false));
/* 302 */         this.configuration.save(this.configurationFile);
/*     */       } 
/*     */ 
/*     */       
/* 306 */       if (this.taskId < 0) {
/* 307 */         start();
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void disable() throws IOException {
/* 319 */     synchronized (this.optOutLock) {
/*     */       
/* 321 */       if (!isOptOut()) {
/* 322 */         this.configuration.set("opt-out", Boolean.valueOf(true));
/* 323 */         this.configuration.save(this.configurationFile);
/*     */       } 
/*     */ 
/*     */       
/* 327 */       if (this.taskId > 0) {
/* 328 */         this.plugin.getServer().getScheduler().cancelTask(this.taskId);
/* 329 */         this.taskId = -1;
/*     */       } 
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
/*     */   public File getConfigFile() {
/* 345 */     File pluginsFolder = this.plugin.getDataFolder().getParentFile();
/*     */ 
/*     */     
/* 348 */     return new File(new File(pluginsFolder, "PluginMetrics"), "config.yml");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void postPlugin(boolean isPing) throws IOException {
/*     */     URLConnection connection;
/* 356 */     PluginDescriptionFile description = this.plugin.getDescription();
/*     */ 
/*     */     
/* 359 */     StringBuilder data = new StringBuilder();
/* 360 */     data.append(encode("guid")).append('=').append(encode(this.guid));
/* 361 */     encodeDataPair(data, "version", description.getVersion());
/* 362 */     encodeDataPair(data, "server", Bukkit.getVersion());
/* 363 */     encodeDataPair(data, "players", Integer.toString(Bukkit.getServer().getOnlinePlayers().size()));
/* 364 */     encodeDataPair(data, "revision", String.valueOf(5));
/*     */ 
/*     */     
/* 367 */     if (isPing) {
/* 368 */       encodeDataPair(data, "ping", "true");
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 373 */     synchronized (this.graphs) {
/* 374 */       Iterator<Graph> iter = this.graphs.iterator();
/*     */       
/* 376 */       while (iter.hasNext()) {
/* 377 */         Graph graph = iter.next();
/*     */         
/* 379 */         for (Plotter plotter : graph.getPlotters()) {
/*     */ 
/*     */ 
/*     */           
/* 383 */           String key = String.format("C%s%s%s%s", new Object[] { "~~", graph.getName(), "~~", plotter.getColumnName() });
/*     */ 
/*     */ 
/*     */           
/* 387 */           String value = Integer.toString(plotter.getValue());
/*     */ 
/*     */           
/* 390 */           encodeDataPair(data, key, value);
/*     */         } 
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 396 */     URL url = new URL("http://mcstats.org" + String.format("/report/%s", new Object[] { encode(this.plugin.getDescription().getName()) }));
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 403 */     if (isMineshafterPresent()) {
/* 404 */       connection = url.openConnection(Proxy.NO_PROXY);
/*     */     } else {
/* 406 */       connection = url.openConnection();
/*     */     } 
/*     */     
/* 409 */     connection.setDoOutput(true);
/*     */ 
/*     */     
/* 412 */     OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
/* 413 */     writer.write(data.toString());
/* 414 */     writer.flush();
/*     */ 
/*     */     
/* 417 */     BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
/* 418 */     String response = reader.readLine();
/*     */ 
/*     */     
/* 421 */     writer.close();
/* 422 */     reader.close();
/*     */     
/* 424 */     if (response == null || response.startsWith("ERR")) {
/* 425 */       throw new IOException(response);
/*     */     }
/*     */     
/* 428 */     if (response.contains("OK This is your first update this hour")) {
/* 429 */       synchronized (this.graphs) {
/* 430 */         Iterator<Graph> iter = this.graphs.iterator();
/*     */         
/* 432 */         while (iter.hasNext()) {
/* 433 */           Graph graph = iter.next();
/*     */           
/* 435 */           for (Plotter plotter : graph.getPlotters()) {
/* 436 */             plotter.reset();
/*     */           }
/*     */         } 
/*     */       } 
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean isMineshafterPresent() {
/*     */     try {
/* 451 */       Class.forName("mineshafter.MineServer");
/* 452 */       return true;
/* 453 */     } catch (Exception e) {
/* 454 */       return false;
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
/*     */ 
/*     */ 
/*     */   
/*     */   private static void encodeDataPair(StringBuilder buffer, String key, String value) throws UnsupportedEncodingException {
/* 472 */     buffer.append('&').append(encode(key)).append('=').append(encode(value));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static String encode(String text) throws UnsupportedEncodingException {
/* 482 */     return URLEncoder.encode(text, "UTF-8");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static class Graph
/*     */   {
/*     */     private final String name;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 499 */     private final Set<Metrics.Plotter> plotters = new LinkedHashSet<>();
/*     */     
/*     */     private Graph(String name) {
/* 502 */       this.name = name;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public String getName() {
/* 511 */       return this.name;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void addPlotter(Metrics.Plotter plotter) {
/* 520 */       this.plotters.add(plotter);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void removePlotter(Metrics.Plotter plotter) {
/* 529 */       this.plotters.remove(plotter);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Set<Metrics.Plotter> getPlotters() {
/* 538 */       return Collections.unmodifiableSet(this.plotters);
/*     */     }
/*     */ 
/*     */     
/*     */     public int hashCode() {
/* 543 */       return this.name.hashCode();
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean equals(Object object) {
/* 548 */       if (!(object instanceof Graph)) {
/* 549 */         return false;
/*     */       }
/*     */       
/* 552 */       Graph graph = (Graph)object;
/* 553 */       return graph.name.equals(this.name);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     protected void onOptOut() {}
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static abstract class Plotter
/*     */   {
/*     */     private final String name;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Plotter() {
/* 578 */       this("Default");
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Plotter(String name) {
/* 587 */       this.name = name;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public abstract int getValue();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public String getColumnName() {
/* 606 */       return this.name;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void reset() {}
/*     */ 
/*     */ 
/*     */     
/*     */     public int hashCode() {
/* 617 */       return getColumnName().hashCode();
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean equals(Object object) {
/* 622 */       if (!(object instanceof Plotter)) {
/* 623 */         return false;
/*     */       }
/*     */       
/* 626 */       Plotter plotter = (Plotter)object;
/* 627 */       return (plotter.name.equals(this.name) && plotter.getValue() == getValue());
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\zacha\Downloads\MineResetLite.jar!\com\koletar\jj\mineresetlite\org\mcstats\Metrics.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */