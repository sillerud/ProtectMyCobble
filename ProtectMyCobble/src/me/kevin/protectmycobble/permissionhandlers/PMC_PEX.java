package me.kevin.protectmycobble.permissionhandlers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import ru.tehkode.permissions.bukkit.PermissionsEx;

import me.kevin.protectmycobble.API.PermissionAPI;

public class PMC_PEX implements PermissionAPI.PermissionHandler{
	PermissionsEx pex;
	public boolean pexfound = false;
	@Override
	public boolean hasPermission(PermissionAPI.Type permission, Player player) {
		if(pexfound){
			return pex.has(player, PermissionAPI.getNode(permission));
		}else{
			return player.isOp();
		}
	}
	public boolean findPex(){
		try{
			Class.forName("ru.tehkode.permissions.bukkit.PermissionsEx");
			pexfound = true;
			return true;
		}catch (ClassNotFoundException e){
			Bukkit.getLogger().severe("Did not find PEX, defaulting to OP!");
			return false;
		}
	}
	@Override
	public void init() {
		if(findPex()){
			pex = (PermissionsEx) Bukkit.getPluginManager().getPlugin("PermissionsEX");
			Bukkit.getLogger().info("ProtectMyCobble's PEX binder started!");
		}
	}

}
