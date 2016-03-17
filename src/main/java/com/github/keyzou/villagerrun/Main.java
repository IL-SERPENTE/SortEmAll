package com.github.keyzou.villagerrun;

import com.github.keyzou.villagerrun.entities.VillagerPlayer;
import com.github.keyzou.villagerrun.game.VillagerRun;
import net.samagames.api.SamaGamesAPI;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    @Override
    public void onEnable(){
        VillagerRun game = new VillagerRun("id", "Villager Run", "Sauvez les bons villageois !", VillagerPlayer.class, this);
        SamaGamesAPI.get().getGameManager().registerGame(game);
    }

}
