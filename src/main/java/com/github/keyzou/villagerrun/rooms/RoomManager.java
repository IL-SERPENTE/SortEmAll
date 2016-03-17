package com.github.keyzou.villagerrun.rooms;

import com.github.keyzou.villagerrun.entities.PNJ;
import com.github.keyzou.villagerrun.game.VillagerRun;
import net.minecraft.server.v1_9_R1.BlockPosition;
import net.minecraft.server.v1_9_R1.World;
import net.samagames.api.games.GamePlayer;
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

    public void createRoom(Location loc, GamePlayer attachedPlayer) {
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
        roomsPlaying.forEach(roomPlaying -> roomPlaying.pnjList.removeAll(roomPlaying.pnjToRemove));
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


    public void checkRoomsPNJ() {
        roomsPlaying.forEach(room -> {
            for(PNJ pnj : room.pnjList){
                if(room.pnjToRemove.contains(pnj))
                    continue;
                if ((pnj.motX == 0) && (pnj.motZ == 0) && (pnj.getLife() > 10)) {
                    BlockPosition pnjPos = new BlockPosition(pnj.locX, pnj.locY, pnj.locZ);
                    BlockPosition objPos = new BlockPosition(pnj.getObjective().getBlockX() + 0.5, pnj.getObjective().getBlockY(), pnj.getObjective().getBlockZ() + 0.5);
                    compare(pnjPos, objPos, room, pnj);
                    room.removePNJ(pnj);
                }
            }
        });
    }

    private void compare(BlockPosition pnjPos, BlockPosition objPos, Room room, PNJ pnj){
        if (pnjPos.equals(objPos)) { // Si il a atteint sa destination on marque un point..
            if (pnj.isGood()) // Seulement si c'est un blanc
                room.score++;
            else {
                room.errors++; // Sinon c'est une erreur
                room.getRoomPlayer().getPlayerIfOnline().sendMessage("Erreur !");
            }
        } else { // S'il a pas atteint sa destination..
            if (pnj.isGood()) { // mais que c'est un blanc on lui retire un point
                room.errors++;
                room.getRoomPlayer().getPlayerIfOnline().sendMessage("Erreur !");
            }
        }
    }

    public GamePlayer getRoomPlayer(int roomID){
        return roomsPlaying.get(roomID).getRoomPlayer();
    }

    public void clearRooms(){
        roomsPlaying.forEach(room -> {
            room.pnjList.forEach(pnj -> pnj.die());
            room.pnjToRemove.forEach(pnj -> pnj.die());
            room.pnjList.clear();
            room.pnjToRemove.clear();
        });
    }
}
