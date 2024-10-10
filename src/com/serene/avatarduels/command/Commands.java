package com.serene.avatarduels.command;


import com.serene.avatarduels.scoreboard.BendingBoard;

public class Commands {

	public Commands() {
		if (BendingBoard.enabled) {
			new BoardCommand();
		}
		new AvatarDuelsCommand();
	}
}
