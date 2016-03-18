package com.github.keyzou.villagerrun.game;

import com.github.keyzou.villagerrun.Main;
import com.github.keyzou.villagerrun.rooms.RoomManager;
import com.github.keyzou.villagerrun.tasks.GameTask;
import com.github.keyzou.villagerrun.tasks.SpawnTask;
import net.samagames.api.SamaGamesAPI;
import net.samagames.api.games.Game;
import net.samagames.api.games.GamePlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class VillagerRun extends Game<GamePlayer> {

    private RoomManager roomManager;
    private List<UUID> players = new ArrayList<>();

    private int roomZSize;
    private int villagersPerRoom;
    private int pathLength;

    private long spawnFrequency = 40L;

    private int verifTaskID;
    private int secondsElapsed;

    private Main plugin;

    private GamePlayer winner;

    private boolean end;

    public VillagerRun(String gameCodeName, String gameName, String gameDescription, Class<GamePlayer> gamePlayerClass, Main plugin) {
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
        verifTaskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> roomManager.checkRoomsPNJ(), 0L, 3L);
    }

    public void endGame(){
        this.end = true;
        this.roomManager.clearRooms();
        SamaGamesAPI.get().getGameManager().getCoherenceMachine().getTemplateManager().getPlayerWinTemplate().execute(winner.getPlayerIfOnline());
        winner.addCoins(25, "Partie gagnée");
        winner.addStars(3, "Partie gagnée");
        this.handleGameEnd();
    }

    @Override
    public void handleLogin(Player player){
        super.handleLogin(player);
        players.add(player.getUniqueId());
    }

    @Override
    public void handlePostRegistration(){
        super.handlePostRegistration();
        roomZSize = SamaGamesAPI.get().getGameManager().getGameProperties().getOptions().get("roomZSize").getAsInt();
        villagersPerRoom = SamaGamesAPI.get().getGameManager().getGameProperties().getOptions().get("spawnPerRoom").getAsInt();
        pathLength = SamaGamesAPI.get().getGameManager().getGameProperties().getOptions().get("pathLength").getAsInt();
    }

    public int getVillagersPerRoom(){
        return villagersPerRoom;
    }

    public int getPathLength(){
        return pathLength;
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

    public int getVerifTaskID(){
        return verifTaskID;
    }

    public void setWinner(GamePlayer player){
        winner = player;
    }

    public boolean mustEnd(){
        return end;
    }

    public int getSecondsElapsed() {
        return secondsElapsed;
    }

    public void incrementSecondsElapsed() {
        this.secondsElapsed++;
    }

    public void reduceSpawnFrequency(){
        spawnFrequency -= spawnFrequency <= 30 ? 5 : 10;
        if(spawnFrequency < 20)
            spawnFrequency = 20;
    }
}
