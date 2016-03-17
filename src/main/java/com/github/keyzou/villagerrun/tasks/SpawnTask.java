package com.github.keyzou.villagerrun.tasks;

import com.github.keyzou.villagerrun.game.VillagerRun;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.Random;


public class SpawnTask extends BukkitRunnable {

    private VillagerRun game;

    private Random random;

    public SpawnTask(VillagerRun game){
        this.game = game;
        random = new Random();
    }

    @Override
    public void run() {
        int spawnID = random.nextInt(game.getVillagersPerRoom());
        game.getRoomManager().spawnNPC(spawnID, random.nextBoolean());
        SpawnTask nextTask = new SpawnTask(game);
        nextTask.runTaskLater(game.getPlugin(), game.getSpawnFrequency());
    }
}
