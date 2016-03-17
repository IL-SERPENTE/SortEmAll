package com.github.keyzou.villagerrun.tasks;

import com.github.keyzou.villagerrun.game.VillagerRun;
import org.bukkit.scheduler.BukkitRunnable;

public class GameTask extends BukkitRunnable {

    private VillagerRun game;

    public GameTask(VillagerRun game){
        this.game = game;
    }

    @Override
    public void run() {
        game.getRoomManager().checkErrors();
        game.getRoomManager().cleanRooms();

        if(game.getRoomManager().getRoomsPlayingCount() <= 0){
            game.endGame();
        }
    }
}
