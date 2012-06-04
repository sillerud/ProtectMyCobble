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

import me.kevin.protectmycobble.API.DatabaseAPI;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class PMCHashMap implements DatabaseAPI, Runnable{
	File file;
	Plugin plugin;
	HashMap<Block, String> save = new HashMap<Block, String>();
	ArrayList<String> ignoreWorlds = new ArrayList<String>();
	public PMCHashMap(Plugin plugin) {
		//Trenger bare denne for saveloopen og basefolderen til pluginen :)
		this.plugin = plugin;
	}
	/**
	 * Beskytter blokken som er ved kordinatene som er gitt.
	 * @param loc Blokken sine kordinater
	 * @param player spilleren som plasserte blokken
	 */
	@Override
	public void protectBlock(Location loc, Player player) {
		save.put(loc.getBlock(), player.getName());
	}

	/**
	 * Beskytter blokken somer ved kordinatene som er gitt
	 * @param x x-kordinatet til blokken
	 * @param y y-kordinatet til blokken
	 * @param z z-kordinatet til blokken
	 * @param world navnet til verden hvor blokken er plassert
	 * @param player Spilleren som plasserte blokken.
	 */
	@Override
	public void protectBlock(int x, int y, int z, String world, Player player) {
		protectBlock(new Location(Bukkit.getWorld(world), x, y, z), player);
	}

	/**
	 * Sjekker om spilleren kan ødelegge blokken ved kordinatene som er gitt.
	 * @return om spilleren kan ødelegge blokken.
	 * @param loc Stedet til blokken som er plassert.
	 * @param player Spilleren som skal sjekkes.
	 */
	@Override
	public boolean canBreakBlock(Location loc, Player player) {
		if(ignoreWorlds.contains(loc.getWorld()))return true;
		String name = save.get(loc.getBlock());
		if(name == null)return true;
		return player.getName().equalsIgnoreCase(name);
	}

	/**
	 * Sjekker om spilleren kan ødelegge blokken ved kordinatene som er gitt.
	 * @return om spilleren kan ødelegge blokken.
	 * @param x x-kordinatet til blokken
	 * @param y y-kordinatet til blokken
	 * @param z z-kordinatet til blokken
	 */
	@Override
	public boolean canBreakBlock(int x, int y, int z, String world,
			Player player) {
		return canBreakBlock(new Location(Bukkit.getWorld(world), x, y, z), player);
	}

	/**
	 * Gir deg navnet til spilleren som plasserte blokken ved de kordinatene
	 * @return Navnet til spilleren som plasserte blokken til kordinatene.
	 * @param loc kordinatene til blokken.
	 */
	@Override
	public String getOwner(Location loc) {
		return save.get(loc.getBlock());
	}

	/**
	 * Gir deg navnet til spilleren som plasserte blokken ved de kordinatene
	 * @return Navnet til spilleren som plasserte blokken til kordinatene
	 * @param x x-kordinatet til blokken
	 * @param y y-kordinatet til blokken
	 * @param z z-kordinatet til blokken
	 * @param world verden til blokken
	 */
	@Override
	public String getOwner(int x, int y, int z, String world) {
		return getOwner(new Location(Bukkit.getWorld(world), x, y, z));
	}

	/**
	 * initialiserer beskyttelsen
	 */
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

	/**
	 * stopper beskyttelsen.
	 */
	@Override
	public void disconnect() {
		Bukkit.getLogger().info(ChatColor.RED + "Saving block protection, please wait");
		write();
		Bukkit.getLogger().info(ChatColor.RED + "Done!");
	}

	/**
	 * gjør at en verden ikke er beskyttet.
	 * @param worldname navnet til verden.
	 */
	@Override
	public void ignoreWorld(String worldname) {
		ignoreWorlds.add(worldname);
	}

	/**
	 * gjør at en verden som ikke er beskyttet er beskyttet igjen
	 * @param worldname navnet til verden.
	 */
	@Override
	public void removeIngoreWorld(String worldname) {
		ignoreWorlds.remove(worldname);
	}
	private boolean firstloop = true;
	/**
	 * Save loopen.
	 */
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
	/**
	 * Leser blokk database filen.
	 * @return ett hashmap med alle blokker som er lagret
	 */
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
	/**
	 * Skriver hashmappet med blokker til databasen
	 * @return om det var velykket.
	 */
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
