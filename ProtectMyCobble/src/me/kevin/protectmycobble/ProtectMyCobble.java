package me.kevin.protectmycobble;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.kevin.protectmycobble.API.Permission;
import me.kevin.protectmycobble.API.SQLHandler;
import me.kevin.protectmycobble.API.Permission.PermissionHandler;
import me.kevin.protectmycobble.permissionhandlers.PMCPermissionsBukkit;
import me.kevin.protectmycobble.permissionhandlers.PMC_OP;
import me.kevin.protectmycobble.permissionhandlers.PMC_PEX;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class ProtectMyCobble extends JavaPlugin{
	PMCBlockListener blocklistener = new PMCBlockListener(this);
	private SQLHandler sqlHandler;
	public boolean useInternalDatabaseHandler = true;
	public boolean showOwner = true;
	Permission.PermissionHandler permissionHandler;
	String MySQLPass = "password";
	String MySQLUser = "Username";
	String MySQLHost = "localhost";
	String MySQLPort = "3306";
	String MySQLDBName = "PMC";
	String MySQLTablePrefix = "PMC_";
	String DBtype = "MySQL";
	String protectedMessage = "&CThe player !PlayerName! owns this block!";
	String permissionType;
	private HashMap<String, PermissionHandler> permissionHandlers = new HashMap<String, PermissionHandler>();
	List<Integer> notProtected = new ArrayList<Integer>();
	@Override
	public void onEnable() {
		notProtected.add(3);
		notProtected.add(2);
		getConfig().options().copyDefaults(true);
		getConfig().options().header("How to use the config:\n" +
				"UseInternalSQLhandler enables the built in SQLHandlers, you can youreself choose the one you want\n" +
				"UseDBType is the type of SQL you want to use, original it uses a textfile, but it supports MySQL, values: MySQL, Textfile, Text\n" +
				"ShowOwner if this is true then the name of the player who placed the block + a message is sendt to the player who tried to destroy it\n" +
				"ProtectMessage the message to display if showowner is enabled, !PlayerName! is changed with the owners name\n" +
				"IgnoreBlocks is a list of block type id's that is not protected by the plugin\n" +
				"PermissionHandler is the permissionhandler to use, supported original is OP, PermissionsEx and PermissionsBukkit\n\n" +
				"MySQL.Host is the host name the server is going to connect to if you use the built in MySQL\n" +
				"MySQL.Port is the port the server is going to connect to if you use the built in MySQL\n" +
				"MySQL.User is the MySQL username if you use the built in MySQL\n" +
				"MySQL.Pass is the MySQL password if you use the built in MySQL\n" +
				"MySQL.DBName is the name of the Database the plugin is using if you use the built in MySQL\n" +
				"TablePrefix is the prefix that comes before the tablename if you use the built in MySQL");
		getConfig().addDefault("SQL.UseInternalSQLHandler", true);
		getConfig().addDefault("SQL.UseDBType", "TextFile");
		getConfig().addDefault("SQL.ShowOwner", true);
		getConfig().addDefault("SQL.ProtectedMessage", "&CThe player !PlayerName! owns this block!");
		getConfig().addDefault("SQL.IgnoreBlocks", notProtected);
		getConfig().addDefault("SQL.PermissionHandler", "PEX");
		getConfig().addDefault("MySQL.Host", "IP_or_host_url_here");
		getConfig().addDefault("MySQL.Port", "3306");
		getConfig().addDefault("MySQL.User", "Username");
		getConfig().addDefault("MySQL.Pass", "Password");
		getConfig().addDefault("MySQL.DBName", "Database_name");
		getConfig().addDefault("MySQL.TablePrefix", "PMC_");
		saveConfig();
		MySQLHost = getConfig().getString("MySQL.Host");
		MySQLPort = getConfig().getString("MySQL.Port");
		MySQLPass = getConfig().getString("MySQL.Pass");
		MySQLUser = getConfig().getString("MySQL.User");
		MySQLTablePrefix = getConfig().getString("MySQL.TablePrefix");
		MySQLDBName = getConfig().getString("MySQL.DBName");
		notProtected = getConfig().getIntegerList("SQL.IgnoreBlocks");
		DBtype = getConfig().getString("SQL.UseDBType");
		showOwner = getConfig().getBoolean("SQL.ShowOwner");
		useInternalDatabaseHandler = getConfig().getBoolean("SQL.UseInternalSQLHandler");
		protectedMessage = getConfig().getString("&CThe player !PlayerName! owns this block!");
		permissionType = getConfig().getString("SQL.PermissionHandler");

		if(useInternalDatabaseHandler){
			if(DBtype.equalsIgnoreCase("MySQL")){
				sqlHandler = new PMCMySQL(MySQLHost, MySQLUser, MySQLPass, MySQLDBName, MySQLPort, MySQLTablePrefix);
				sqlHandler.connect();
			}
			if(DBtype.equalsIgnoreCase("text")){
				sqlHandler = new PMCHashMap(this);
				sqlHandler.connect();
			}
			if(DBtype.equalsIgnoreCase("textfile")){
				sqlHandler = new PMCHashMap(this);
				sqlHandler.connect();
			}
			if(DBtype.equalsIgnoreCase("hashmap")){
				sqlHandler = new PMCHashMap(this);
				sqlHandler.connect();
			}
		}
		addPermissionHandler(new PMC_PEX(), "PEX");
		addPermissionHandler(new PMC_PEX(), "PermissionsEx");
		addPermissionHandler(new PMCPermissionsBukkit(), "PermissionsBukkit");
		addPermissionHandler(new PMCPermissionsBukkit(), "Bukkit");
		addPermissionHandler(new PMCPermissionsBukkit(), "Vanilla");
		addPermissionHandler(new PMC_OP(), "OP");
		
		if(permissionHandlers.containsKey(permissionType)){
			getPermissionHandler(permissionType).init();
		}
		
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(blocklistener, this);
	}
	@Override
	public void onDisable() {
		getSQL().disconnect();
	}
	public void setSQLHandler(SQLHandler sql){
		if(useInternalDatabaseHandler){
			sql.connect();
			this.sqlHandler = sql;
		}
	}
	public SQLHandler getSQL(){
		return sqlHandler;
	}
	public void addPermissionHandler(Permission.PermissionHandler handler, String handlername){
		permissionHandlers.put(handlername.toUpperCase(), handler);
	}
	public Permission.PermissionHandler getPermissionHandler(String handlername){
		return permissionHandlers.get(handlername.toUpperCase());
	}
	public PermissionHandler getCurrentPermissionHandler(){
		if(permissionHandler == null)return getPermissionHandler("OP");
		return permissionHandler;
	}
}
