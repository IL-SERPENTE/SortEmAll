package com.github.keyzou.villagerrun.rooms;

import com.github.keyzou.villagerrun.entities.PNJ;
import com.github.keyzou.villagerrun.entities.VillagerPlayer;
import com.github.keyzou.villagerrun.game.VillagerRun;
import net.minecraft.server.v1_9_R1.World;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_9_R1.CraftWorld;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class RoomManager {

    private List<Room> rooms = new ArrayList<>();
    private List<Room> roomsPlaying = new ArrayList<>();
    private List<Room> roomsRemove = new ArrayList<>();

    protected VillagerRun currentGame;

    public RoomManager(VillagerRun game){
        this.currentGame = game;
    }

    public void createRoom(Location loc, VillagerPlayer attachedPlayer) {
        rooms.add(new Room(loc, attachedPlayer));
    }

    public void startGame(){
        roomsPlaying.addAll(rooms);
        roomsPlaying.forEach(room -> room.startGame(this));
    }

    public void spawnNPC(int spawnerID, boolean isGood){
        roomsPlaying.forEach(room -> {
            Location loc = room.villagerSpawnPoints.get(spawnerID);
            World mcWorld = ((CraftWorld) loc.getWorld()).getHandle();
            PNJ pnj = new PNJ(mcWorld, room.fencesLocations.get(spawnerID), isGood);
            pnj.setLocation(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), loc.getYaw(), loc.getPitch());
            mcWorld.addEntity(pnj);
            room.addPNJ(pnj);
        });
    }

    public void checkErrors(){
        roomsPlaying.forEach(room -> {if(room.errors > 3) roomsRemove.add(room);});
    }

    public void cleanRooms(){
        roomsRemove.forEach(room -> {
            Bukkit.getServer().broadcastMessage(ChatColor.RED + room.getRoomPlayer().getPlayerIfOnline().getDisplayName() +" a perdu !");
            Player p = room.getRoomPlayer().getPlayerIfOnline();
            p.sendMessage(ChatColor.RED+"Vous avez perdu !");
            room.lose();
        });
        roomsPlaying.removeAll(roomsRemove);
        roomsRemove.clear();
    }

    public int getRoomsPlayingCount(){
        return roomsPlaying.size();
    }


}
