package com.github.keyzou.villagerrun;

import com.github.keyzou.villagerrun.entities.PNJ;
import com.github.keyzou.villagerrun.game.VillagerRun;
import net.minecraft.server.v1_9_R1.EntityInsentient;
import net.minecraft.server.v1_9_R1.EntityTypes;
import net.samagames.api.SamaGamesAPI;
import net.samagames.api.games.GamePlayer;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

/*
 * This file is part of PersistanceAPI.
 *
 * PersistanceAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PersistanceAPI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with PersistanceAPI.  If not, see <http://www.gnu.org/licenses/>.
 */
public class Main extends JavaPlugin {

    @Override
    public void onEnable(){
        VillagerRun game = new VillagerRun("id", "Villager Run", "Sauvez les bons villageois !", GamePlayer.class, this);
        SamaGamesAPI.get().getGameManager().registerGame(game);
        registerEntity("CustomVillager", 120, PNJ.class);
    }

    /**
     * Using reflection to add a custom entity to Minecraft's entity list
     * @param name Name of the Entity
     * @param id id of the entity (120 is EntityVillager)
     * @param customClass {@link PNJ} class
     */
    private void registerEntity(String name, int id, Class<? extends EntityInsentient> customClass){
        try {
            List<Map<?, ?>> dataMap = new ArrayList<>();
            for (Field f : EntityTypes.class.getDeclaredFields()){
                if (Map.class.isAssignableFrom(f.getType())){
                    f.setAccessible(true);
                    dataMap.add((Map<?, ?>) f.get(null));
                }
            }

            if (dataMap.get(2).containsKey(id)){
                dataMap.get(0).remove(name);
                dataMap.get(2).remove(id);
            }

            Method method = EntityTypes.class.getDeclaredMethod("a", Class.class, String.class, int.class);
            method.setAccessible(true);
            method.invoke(null, customClass, name, id);
        } catch (Exception e){
            Bukkit.getLogger().log(Level.SEVERE, "Erreur !", e);
        }


    }

}
