package com.serene.avatarduels;

import com.serene.avatarduels.AvatarDuels;
import org.bukkit.Bukkit;

 import com.serene.avatarduels.ability.fire.LightningBurst;
 import com.serene.avatarduels.ability.water.HealingWaters;
 import com.serene.avatarduels.ability.water.IcePassive;
 import com.serene.avatarduels.util.RegenTempBlock;

public class JCManager implements Runnable {

	public AvatarDuels plugin;
	
	public JCManager(AvatarDuels plugin) {
		this.plugin = plugin;
	}
	
	public void run() {
		LightningBurst.progressAll();
		
		HealingWaters.heal(Bukkit.getServer());
		IcePassive.handleSkating();
//		IceWall.progressAll();
		
		RegenTempBlock.manage();
	}
}