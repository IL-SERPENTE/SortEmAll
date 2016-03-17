package com.github.keyzou.villagerrun.game;

import com.github.keyzou.villagerrun.Main;
import com.github.keyzou.villagerrun.entities.VillagerPlayer;
import com.github.keyzou.villagerrun.rooms.RoomManager;
import com.github.keyzou.villagerrun.tasks.GameTask;
import com.github.keyzou.villagerrun.tasks.SpawnTask;
import net.samagames.api.SamaGamesAPI;
import net.samagames.api.games.Game;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class VillagerRun extends Game<VillagerPlayer> {

    private RoomManager roomManager;
    private List<UUID> players = new ArrayList<>();

    private int roomXSize;
    private int roomZSize;
    private int villagersPerRoom;

    private long spawnFrequency = 40L;

    private Main plugin;

    public VillagerRun(String gameCodeName, String gameName, String gameDescription, Class<VillagerPlayer> gamePlayerClass, Main plugin) {
        super(gameCodeName, gameName, gameDescription, gamePlayerClass);
        roomManager = new RoomManager(this);
        this.plugin = plugin;
    }

    @Override
    public void startGame(){
        super.startGame();
        for(int i = 0; i < players.size(); i++){
            roomManager.createRoom(new Location(Bukkit.getWorld("world"), 100,100,100+i*(roomZSize + 2)), this.getPlayer(players.get(i)));
        }
        roomManager.startGame();

        GameTask gameTask = new GameTask(this);
        gameTask.runTaskTimer(this.plugin, 0L, 20L);
        // Task vérif
        SpawnTask spawnTask = new SpawnTask(this);
        spawnTask.runTaskLater(this.plugin, spawnFrequency);
        // Task spawn variable
    }

    public void endGame(){
        this.endGame();
    }

    @Override
    public void handleLogin(Player player){
        super.handleLogin(player);
        players.add(player.getUniqueId());
    }

    @Override
    public void handlePostRegistration(){
        super.handlePostRegistration();
        roomXSize = SamaGamesAPI.get().getGameManager().getGameProperties().getOptions().get("width").getAsInt();
        roomZSize = SamaGamesAPI.get().getGameManager().getGameProperties().getOptions().get("height").getAsInt();
        villagersPerRoom = SamaGamesAPI.get().getGameManager().getGameProperties().getOptions().get("spawnPerRoom").getAsInt();
    }

    public int getRoomXSize(){
        return roomXSize;
    }

    public int getRoomZSize(){
        return roomZSize;
    }

    public int getVillagersPerRoom(){
        return villagersPerRoom;
    }

    public long getSpawnFrequency(){
        return spawnFrequency;
    }

    public RoomManager getRoomManager(){
        return roomManager;
    }

    public Main getPlugin(){
        return plugin;
    }
}
