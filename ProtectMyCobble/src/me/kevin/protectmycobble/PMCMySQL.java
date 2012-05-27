package me.kevin.protectmycobble;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import me.kevin.protectmycobble.API.DatabaseAPI;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;


public class PMCMySQL implements DatabaseAPI, Listener{
	private Connection con;
	private String pass;
	String user;
	String Host;
	String DB;
	String port;
	String tableprefix;
	ArrayList<String> ignoreworldlist = new ArrayList<String>();

	public PMCMySQL(String Host, String user, String pass, String DB, String port, String tablePrefix) {
		this.Host = Host;
		this.user = user;
		this.pass = pass;
		this.DB = DB;
		this.port = port;
		this.tableprefix = tablePrefix;
	}

	@Override
	public void protectBlock(Location loc, Player player) {
		protectBlock(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), loc.getWorld().getName(), player);
	}

	@Override
	public void protectBlock(int x, int y, int z, String world, Player player) {
		try {
			query("REPLACE INTO " + getTableName(world) + "(X, Y, Z, Player) VALUES(" + x + ", " + y + ", " + z + ", '" + player.getName() + "');");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean canBreakBlock(Location loc, Player player) {
		return canBreakBlock(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), loc.getWorld().getName(), player);
	}

	@Override
	public boolean canBreakBlock(int x, int y, int z, String world, Player player) {
		String owner = getOwner(x, y, z, world);
		if(owner == null){
			return false;
		}
		return player.getName().equals(owner);
	}

	@Override
	public String getOwner(Location loc) {
		return getOwner(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), loc.getWorld().getName());
	}

	@Override
	public String getOwner(int x, int y, int z, String world) {
		try {
			ResultSet result = query("SELECT Player FROM " + getTableName(world) + " WHERE X=" + x + " AND Y=" + y + " AND Z=" + z + ";");
			if(result.next())
			return result.getString("Player");
			return null;
		} catch (SQLException e) {
			e.printStackTrace();
			return "Protection system has failed, all blocks are protected!";
		}
	}

	@Override
	public void connect() {

		for(World world : Bukkit.getWorlds()){
			if(!ignoreworldlist.contains(world.getName())){
				addWorldProtect(world.getName());
			}
		}
	}

	@Override
	public void disconnect() {
		try{
			if(con != null){
				if(!con.isClosed()){
					con.close();
				}
			}
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	@Override
	public void ignoreWorld(String worldname) {
		ignoreworldlist.add(worldname);
	}
	@Override
	public void removeIngoreWorld(String worldname) {
		ignoreworldlist.remove(worldname);
	}
	public void addWorldProtect(String worldname) {
		try {
			query("CREATE TABLE IF NOT EXISTS " + getTableName(worldname)  + "(X INT, Y INT, Z INT, Player VARCHAR(100), UNIQUE INDEX(X, Y, Z));");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public String getTableName(World world){
		return tableprefix + world.getName();
	}
	public String getTableName(String world){
		return tableprefix + world;
	}

	private ResultSet query(String query) throws SQLException{
		if(query.toUpperCase().startsWith("SELECT ")){
			return getConnection().createStatement().executeQuery(query);
		}else{
			getConnection().createStatement().execute(query);
			return null;
		}
	}
	public boolean hasDatabaseConnection(){
		try {
			Class.forName("com.mysql.jdbc.Driver");
			return true;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return false;
		}
	}
	private Connection getConnection() throws SQLException{
		if(con == null){
			String URL = "jdbc:mysql://" + Host + ":" + port + "/" + DB;
			con = DriverManager.getConnection(URL, user, pass);
		}
		return con;
	}
}
