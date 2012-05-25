package me.kevin.protectmycobble.API;

import org.bukkit.entity.Player;

public class Permission{
	public static String getNode(Type permission){
		if(permission == Type.Break){
			return "PMC.BreakAll";
		}else if(permission == Type.AdminStick){
			return "PMC.AdminStick";
		}else if(permission == Type.OpenContainer){
			return "PMC.OpenContainer";
		}else{
			return null;
		}
	}
	public enum Type{
		Break, AdminStick, OpenContainer
	}
	public interface PermissionHandler{
		public boolean hasPermission(Type permission, Player player);
		public void init();
	}
}
