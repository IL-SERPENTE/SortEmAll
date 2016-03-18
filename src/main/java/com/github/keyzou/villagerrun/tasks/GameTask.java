package com.github.keyzou.villagerrun.tasks;

import com.github.keyzou.villagerrun.game.VillagerRun;
import net.md_5.bungee.api.ChatColor;
import net.samagames.tools.GameUtils;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class GameTask extends BukkitRunnable {

    private VillagerRun game;

    public GameTask(VillagerRun game){
        this.game = game;
    }

    @Override
    public void run() {
        if(game.mustEnd()){
            Bukkit.getScheduler().cancelTask(game.getVerifTaskID());
            this.cancel();
            return;
        }

        game.incrementSecondsElapsed();
        if(game.getSecondsElapsed() % 30 == 0 && game.getSpawnFrequency() > 20){
            game.reduceSpawnFrequency();
            GameUtils.broadcastMessage(ChatColor.GOLD+"On accélère la cadence !");
        }

        game.getRoomManager().updateRooms();
        game.getRoomManager().checkErrors();
        game.getRoomManager().cleanRooms();

        if(game.getRoomManager().getRoomsPlayingCount() <= 1 && !game.mustEnd()){
            game.setWinner(game.getRoomManager().getRoomPlayer(0));
            game.endGame();
        }
    }
}
