package com.github.keyzou.villagerrun.rooms;

import net.samagames.api.games.Game;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dean on 15/03/2016.
 */
public class RoomManager {

    private List<Room> rooms = new ArrayList<>();
    private List<Room> roomsPlaying = new ArrayList<>();

    private Game currentGame;

    public RoomManager(Game game){
        this.currentGame = game;
    }

    public void createRoom(Location loc, Player attachedPlayer){

    }

}
