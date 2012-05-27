package me.kevin.protectmycobble.API;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface DatabaseAPI {
	public void protectBlock(Location loc, Player player);
	public void protectBlock(int x, int y, int z, String world, Player player);
	public boolean canBreakBlock(Location loc, Player player);
	public boolean canBreakBlock(int x, int y, int z, String world, Player player);
	public String getOwner(Location loc);
	public String getOwner(int x, int y, int z, String world);
	public void connect();
	public void disconnect();
	public void ignoreWorld(String worldname);
	public void removeIngoreWorld(String worldname);
}
