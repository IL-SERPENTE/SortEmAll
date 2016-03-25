package com.github.keyzou.villagerrun.game;

import com.github.keyzou.villagerrun.Main;
import com.github.keyzou.villagerrun.rooms.RoomManager;
import com.github.keyzou.villagerrun.tasks.GameTask;
import com.github.keyzou.villagerrun.tasks.SpawnTask;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.samagames.api.SamaGamesAPI;
import net.samagames.api.games.Game;
import net.samagames.api.games.GamePlayer;
import net.samagames.tools.LocationUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class VillagerRun extends Game<GamePlayer> {

    /**
     * The only instance of RoomManager
     */
    private RoomManager roomManager;

    /**
     * Player List
     */
    private List<UUID> players = new ArrayList<>();

    /**
     * Gap between two rooms (from one player spawn to another)
     */
    private int gap;
    /**
     * Location of the waiting lobby
     */
    private Location waitingLobby;

    /**
     * Frequency at which villagers are spawning (ticks)
     */
    private long spawnFrequency = 40L;

    /**
     * Task's ID that checks every villager pos
     */
    private int verifTaskID;
    /**
     * Seconds elapsed since the game started
     */
    private int secondsElapsed;

    /**
     * Instance of the Main plugin
     */
    private Main plugin;

    /**
     * Player who won the game
     */
    private GamePlayer winner;

    /**
     * True if the game has to end.
     */
    private boolean end;

    /**
     * Class Constructor, extends from {@link Game<GamePlayer>}, creates the only instance of {@link RoomManager}
     * @param gameCodeName
     * @param gameName
     * @param gameDescription
     * @param gamePlayerClass
     * @param plugin
     */
    public VillagerRun(String gameCodeName, String gameName, String gameDescription, Class<GamePlayer> gamePlayerClass, Main plugin) {
        super(gameCodeName, gameName, gameDescription, gamePlayerClass);
        roomManager = new RoomManager();
        this.plugin = plugin;
    }

    /**
     * Called when the game starts
     */
    @Override
    public void startGame(){
        super.startGame();
        for(int i = 1; i < players.size(); i++){
            roomManager.duplicateRoom((gap)*(i+1)); // We duplicate the first room and moves the new room
        }
        for(int i = 0; i < players.size(); i++){
            roomManager.dispatchPlayer(i, getPlayer(players.get(i))); // Attach every player to a room
        }

        roomManager.startGame();

        GameTask gameTask = new GameTask(this);
        gameTask.runTaskTimer(this.plugin, 0L, 20L);
        // Task vérif
        SpawnTask spawnTask = new SpawnTask(this);
        spawnTask.runTaskLater(this.plugin, spawnFrequency);
        // Task spawn variable
        verifTaskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> roomManager.checkRoomsPNJ(), 0L, 3L);

        this.getPlugin().getServer().getWorld("world").setPVP(true);

    }

    /**
     * Called when the game can end
     */
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
        player.teleport(waitingLobby);
        player.setGameMode(GameMode.ADVENTURE);
        players.add(player.getUniqueId());
    }

    @Override
    public void handlePostRegistration(){
        super.handlePostRegistration();
        gap = SamaGamesAPI.get().getGameManager().getGameProperties().getConfig("gap", new JsonPrimitive(10)).getAsInt();
        waitingLobby = LocationUtils.str2loc(SamaGamesAPI.get().getGameManager().getGameProperties().getConfig("waiting-lobby", new JsonPrimitive("world, 0, 126, 0, 0, 0")).getAsString());
        JsonObject firstRoom = SamaGamesAPI.get().getGameManager().getGameProperties().getConfig("firstRoom", new JsonObject()).getAsJsonObject();
        Location playerSpawn = LocationUtils.str2loc(firstRoom.get("playerSpawn").getAsString());
        JsonArray paths = firstRoom.get("paths").getAsJsonArray();
        List<Location> originList = new ArrayList<>();
        List<Location> destinationList = new ArrayList<>();

        paths.forEach(jsonElement -> {
            JsonObject entry = jsonElement.getAsJsonObject();
            originList.add(LocationUtils.str2loc(entry.get("origin").getAsString()));
            destinationList.add(LocationUtils.str2loc(entry.get("destination").getAsString()));
        });
        roomManager.createFirstRoom(playerSpawn, originList, destinationList);
        this.getPlugin().getServer().getWorld("world").setPVP(false);

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

    /**
     * Reduces the spawn frequency, at first by 10 ticks then by 5, capped at 20 ticks
     */
    public void reduceSpawnFrequency(){
        spawnFrequency -= spawnFrequency <= 30 ? 5 : 10;
        if(spawnFrequency < 20)
            spawnFrequency = 20;
    }
}
