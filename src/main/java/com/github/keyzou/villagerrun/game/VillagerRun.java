package com.github.keyzou.villagerrun.game;

import com.github.keyzou.villagerrun.entities.VillagerPlayer;
import com.github.keyzou.villagerrun.rooms.RoomManager;
import net.samagames.api.games.Game;

public class VillagerRun extends Game<VillagerPlayer> {

    private RoomManager roomManager;

    public VillagerRun(String gameCodeName, String gameName, String gameDescription, Class<VillagerPlayer> gamePlayerClass) {
        super(gameCodeName, gameName, gameDescription, gamePlayerClass);
        roomManager = new RoomManager(this);
    }

    @Override
    public void startGame(){
        super.startGame();
        roomManager.startGame();
    }
}
