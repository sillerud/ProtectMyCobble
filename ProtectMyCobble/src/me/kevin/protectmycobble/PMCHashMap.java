package me.kevin.protectmycobble;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;

import me.kevin.protectmycobble.API.SQLHandler;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class PMCHashMap implements SQLHandler, Runnable{
	File file;
	Plugin plugin;
	HashMap<Block, String> save = new HashMap<Block, String>();
	ArrayList<String> ignoreWorlds = new ArrayList<String>();
	public PMCHashMap(Plugin plugin) {
		//Trenger bare denne for saveloopen og basefolderen til pluginen :)
		this.plugin = plugin;
	}
	@Override
	public void protectBlock(Location loc, Player player) {
		save.put(loc.getBlock(), player.getName());
	}

	@Override
	public void protectBlock(int x, int y, int z, String world, Player player) {
		protectBlock(new Location(Bukkit.getWorld(world), x, y, z), player);
	}

	@Override
	public boolean canBreakBlock(Location loc, Player player) {
		String name = save.get(loc.getBlock());
		if(name == null)return true;
		return player.getName().equals(name);
	}

	@Override
	public boolean canBreakBlock(int x, int y, int z, String world,
			Player player) {
		return canBreakBlock(new Location(Bukkit.getWorld(world), x, y, z), player);
	}

	@Override
	public String getOwner(Location loc) {
		return save.get(loc.getBlock());
	}

	@Override
	public String getOwner(int x, int y, int z, String world) {
		return getOwner(new Location(Bukkit.getWorld(world), x, y, z));
	}

	@Override
	public void connect() {
		file = new File(new File(plugin.getDataFolder(), "protection"), "PMC.PROTECTION");
		file.getParentFile().mkdirs();
		if(file.exists()){
			HashMap<Block, String> map = readFile();
			if(map != null){
				save = map;
			}
		}
		Bukkit.getScheduler().scheduleAsyncRepeatingTask(plugin, this, 60l, 12000l);
	}

	@Override
	public void disconnect() {
		Bukkit.getLogger().info(ChatColor.RED + "Saving block protection, please wait");
		write();
		Bukkit.getLogger().info(ChatColor.RED + "Done!");
	}

	@Override
	public void ignoreWorld(String worldname) {
		ignoreWorlds.add(worldname);
	}

	@Override
	public void removeIngoreWorld(String worldname) {
		ignoreWorlds.remove(worldname);
	}
	boolean firstloop = true;
	@Override
	public void run() {
		if(firstloop){
			firstloop = false;
			return;
		}
		if(write()){
			save = readFile();
		}else{
			Bukkit.getLogger().severe("Could not save block file!");
		}
	}
	private HashMap<Block, String> readFile(){
		HashMap<Block, String> map = new HashMap<Block, String>();
		try{
			BufferedReader instream = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			String line;
			while((line = instream.readLine()) != null){
				String[] lines = line.split(",");
				int x = 0;
				int y = 0;
				int z = 0;
				try{
					x = Integer.parseInt(lines[0]);
					y = Integer.parseInt(lines[1]);
					z = Integer.parseInt(lines[2]);
				}catch(NumberFormatException e){
					e.printStackTrace();
					return null;
				}
				String world = lines[3];
				String player = lines[4];
				map.put(new Location(Bukkit.getWorld(world), x, y, z).getBlock(), player);
			}
			instream.close();
		}catch(IOException e){
			e.printStackTrace();
		}
		return map;
	}
	private boolean write(){
		try {
			PrintStream stream = new PrintStream(new FileOutputStream(file));
			for(Block block : save.keySet()){
				String name = save.get(block);
				stream.println(block.getX() + "," + block.getY() + "," + block.getZ() + "," + block.getWorld().getName() + "," + name);
			}
			stream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		}
		return false;
	}

}
