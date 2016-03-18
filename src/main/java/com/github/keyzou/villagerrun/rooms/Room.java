package com.github.keyzou.villagerrun.rooms;

import com.github.keyzou.villagerrun.entities.PNJ;
import com.sk89q.worldedit.*;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.data.DataException;
import com.sk89q.worldedit.schematic.MCEditSchematicFormat;
import net.samagames.api.games.GamePlayer;
import net.samagames.tools.scoreboards.ObjectiveSign;
import net.samagames.tools.scoreboards.VObjective;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;


public class Room {

    private GamePlayer attachedPlayer;
    private Location spawnPoint;

    protected List<Location> villagerSpawnPoints = new ArrayList<>();
    protected List<Location> fencesLocations = new ArrayList<>();

    protected List<PNJ> pnjList = new ArrayList<>();
    protected List<PNJ> pnjToRemove = new ArrayList<>();

    protected int score;
    protected int errors;

    protected ObjectiveSign scoreBoard;

    protected Room(Location loc, GamePlayer player){
        attachedPlayer = player;
        spawnPoint = loc;
    }

    protected GamePlayer getRoomPlayer(){
        return attachedPlayer;
    }

    protected void startGame(RoomManager roomManager){
        generateRoom();
        for(int i = 0; i < roomManager.currentGame.getVillagersPerRoom(); i++){
            villagerSpawnPoints.add(new Location(spawnPoint.getWorld(), spawnPoint.getBlockX()+8, spawnPoint.getBlockY(), spawnPoint.getBlockZ() -1.5 + i));
            fencesLocations.add(new Location(spawnPoint.getWorld(), spawnPoint.getBlockX()+3.5, spawnPoint.getBlockY(), spawnPoint.getBlockZ() -2 + i + 0.5));
        }
        scoreBoard = new ObjectiveSign("villagerRun", ChatColor.AQUA+""+ChatColor.BOLD+"     Villager Run     ");
        scoreBoard.addReceiver(attachedPlayer.getPlayerIfOnline());
        scoreBoard.setLocation(VObjective.ObjectiveLocation.SIDEBAR);
        scoreBoard.setLine(1, ChatColor.GOLD+""+ChatColor.BOLD+"Score:");
        scoreBoard.setLine(2, "0");
        scoreBoard.setLine(4, ChatColor.GOLD+""+ChatColor.BOLD+"Erreurs:");
        attachedPlayer.getPlayerIfOnline().teleport(spawnPoint);
    }

    private void generateRoom(){
        org.bukkit.World world = Bukkit.getWorld("world");
        EditSessionFactory esf = WorldEdit.getInstance().getEditSessionFactory();
        EditSession es = esf.getEditSession(new BukkitWorld(world), -1);
        File file = new File("room.schematic");
        Vector loc = new Vector(spawnPoint.getBlockX(), spawnPoint.getBlockY(), spawnPoint.getBlockZ());
        try {
            MCEditSchematicFormat.getFormat(file).load(file).paste(es, loc, false);
        } catch (MaxChangedBlocksException | IOException | DataException e) {
            Bukkit.getLogger().log(Level.SEVERE, "Erreur [VR]", e);
        }
    }

    protected void lose(){
        // clear pnj list
        pnjList.forEach(this::removePNJ);
        // Add to Waiting List
        attachedPlayer.setSpectator();
        attachedPlayer = null;
        // Reset scores
        errors = 0;
        score = 0;
    }

    protected void addPNJ(PNJ pnj){
        pnjList.add(pnj);
    }
    protected void removePNJ(PNJ pnj){
        pnjToRemove.add(pnj);
        pnj.die();
    }

    protected void updateRoom(){
        scoreBoard.setLine(2, String.valueOf(score));
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < errors; i++){
            sb.append("✖");
        }
        scoreBoard.setLine(5, ChatColor.RED+sb.toString());
        scoreBoard.updateLines();
    }

}
