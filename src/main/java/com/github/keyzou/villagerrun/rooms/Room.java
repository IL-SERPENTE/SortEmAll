package com.github.keyzou.villagerrun.rooms;

import com.github.keyzou.villagerrun.entities.PNJ;
import com.github.keyzou.villagerrun.entities.VillagerPlayer;
import com.sk89q.worldedit.*;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.data.DataException;
import com.sk89q.worldedit.schematic.MCEditSchematicFormat;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;


public class Room {

    private VillagerPlayer attachedPlayer;
    private Location spawnPoint;

    protected List<Location> villagerSpawnPoints = new ArrayList<>();
    protected List<Location> fencesLocations = new ArrayList<>();

    protected List<PNJ> pnjList = new ArrayList<>();

    protected int score;
    protected int errors;

    protected Room(Location loc, VillagerPlayer player){
        attachedPlayer = player;
        spawnPoint = loc;
    }

    protected VillagerPlayer getRoomPlayer(){
        return attachedPlayer;
    }

    protected void startGame(RoomManager roomManager){
        generateRoom();
        for(int i = 0; i < roomManager.currentGame.getVillagersPerRoom(); i++){
            villagerSpawnPoints.add(new Location(spawnPoint.getWorld(), spawnPoint.getBlockX()+8, spawnPoint.getBlockY(), spawnPoint.getBlockZ() -2 + i));
            fencesLocations.add(new Location(spawnPoint.getWorld(), spawnPoint.getBlockX()+2, spawnPoint.getBlockY(), spawnPoint.getBlockZ() -2 + i));
        }
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
        // Add to Waiting List

        attachedPlayer = null;
        // Reset scores
        errors = 0;
        score = 0;
    }

    protected void addPNJ(PNJ pnj){
        pnjList.add(pnj);
    }

}
