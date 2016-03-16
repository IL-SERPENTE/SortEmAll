package com.github.keyzou.villagerrun.rooms;

import com.github.keyzou.villagerrun.entities.VillagerPlayer;
import org.bukkit.Location;


public class Room {

    private VillagerPlayer attachedPlayer;
    private Location spawnPoint;

    protected Room(Location loc, VillagerPlayer player){
        attachedPlayer = player;
        spawnPoint = loc;
    }

    protected VillagerPlayer getRoomPlayer(){
        return attachedPlayer;
    }

    protected void startGame(){
        generateRoom();
        attachedPlayer.getPlayerIfOnline().teleport(spawnPoint);
    }

    private void generateRoom(){

    }

    protected void endGame(){

    }
}
