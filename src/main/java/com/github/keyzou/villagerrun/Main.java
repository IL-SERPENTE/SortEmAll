package com.github.keyzou.villagerrun;

import com.github.keyzou.villagerrun.game.VillagerRun;
import net.samagames.api.SamaGamesAPI;
import net.samagames.api.games.GamePlayer;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by Dean on 14/03/2016.
 */
public class Main extends JavaPlugin {

    @Override
    public void onEnable(){
        VillagerRun game = new VillagerRun("id", "Villager Run!", "Sauvez les bons villageois !", GamePlayer.class);
        SamaGamesAPI.get().getGameManager().registerGame(game);
    }
}
