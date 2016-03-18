package com.github.keyzou.villagerrun.rooms;

import com.github.keyzou.villagerrun.entities.PNJ;
import com.github.keyzou.villagerrun.game.VillagerRun;
import net.minecraft.server.v1_9_R1.BlockPosition;
import net.minecraft.server.v1_9_R1.Entity;
import net.minecraft.server.v1_9_R1.World;
import net.samagames.api.games.GamePlayer;
import net.samagames.tools.ParticleEffect;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
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
        roomsPlaying.forEach(room -> {if(room.errors > 2) roomsRemove.add(room);});
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
        roomsPlaying.forEach(room -> room.pnjList.stream().filter(pnj -> !room.pnjToRemove.contains(pnj)).forEach(pnj->{
                if ((pnj.motX == 0) && (pnj.motZ == 0) && (pnj.getLife() > 10)) {
                    Location pnjLoc = new Location(Bukkit.getServer().getWorld("world"), pnj.locX, pnj.locY, pnj.locZ);
                    BlockPosition pnjPos = new BlockPosition(pnj.locX, pnj.locY, pnj.locZ);
                    BlockPosition objPos = new BlockPosition(pnj.getObjective().getBlockX() + 0.5, pnj.getObjective().getBlockY(), pnj.getObjective().getBlockZ() + 0.5);
                    if(compare(pnjPos, objPos, room, pnj)) {
                        ParticleEffect.HEART.display(0.3f, 1, 0.3f, 1, 5, pnjLoc, 5);
                        room.getRoomPlayer().getPlayerIfOnline().playSound(pnjLoc, Sound.BLOCK_NOTE_HARP, 1, 1.5f);
                        room.getRoomPlayer().getPlayerIfOnline().playSound(pnjLoc, Sound.BLOCK_NOTE_HARP, 1, 1.7f);
                    }else {
                        ParticleEffect.CLOUD.display(0.3f, 0.3f, 0.3f, 0.2f, 10, new Location(Bukkit.getServer().getWorld("world"), pnj.locX, pnj.locY, pnj.locZ), 5);
                        room.getRoomPlayer().getPlayerIfOnline().playSound(pnjLoc, Sound.BLOCK_NOTE_BASS, 1, 0.7f);
                        room.getRoomPlayer().getPlayerIfOnline().playSound(pnjLoc, Sound.BLOCK_NOTE_BASS, 1, 0.2f);
                    }
                    room.removePNJ(pnj);
                }
        }));
    }

    private boolean compare(BlockPosition pnjPos, BlockPosition objPos, Room room, PNJ pnj){
        if (pnjPos.equals(objPos)) { // Si il a atteint sa destination on marque un point..
            if (pnj.isGood()) { // Seulement si c'est un blanc
                room.score++;
                return true;
            }
            else {
                room.errors++; // Sinon c'est une erreur
                return false;
            }
        } else { // S'il a pas atteint sa destination..
            if (pnj.isGood()) { // mais que c'est un blanc on lui retire un point
                room.errors++;
                return false;
            }else
                return false;
        }
    }

    public GamePlayer getRoomPlayer(int roomID){
        return roomsPlaying.get(roomID).getRoomPlayer();
    }

    public void clearRooms(){
        roomsPlaying.forEach(room -> {
            room.pnjList.forEach(Entity::die);
            room.pnjToRemove.forEach(Entity::die);
            room.pnjList.clear();
            room.pnjToRemove.clear();
        });
    }

    public void updateRooms() {
        roomsPlaying.forEach(Room::updateRoom);
    }
}
