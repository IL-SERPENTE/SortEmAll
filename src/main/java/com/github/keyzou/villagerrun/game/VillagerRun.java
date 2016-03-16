package com.github.keyzou.villagerrun.game;

import net.samagames.api.games.Game;
import net.samagames.api.games.GamePlayer;

/**
 * Created by Dean on 14/03/2016.
 */
public class VillagerRun extends Game<GamePlayer> {

    public VillagerRun(String gameCodeName, String gameName, String gameDescription, Class<GamePlayer> gamePlayerClass) {
        super(gameCodeName, gameName, gameDescription, gamePlayerClass);
    }

    @Override
    public void startGame(){
        super.startGame();

    }
}
