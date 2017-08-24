package com.github.keyzou.villagerrun.entities;

import com.github.keyzou.villagerrun.entities.ai.PathfinderGoalWalk;
import com.google.common.collect.Sets;
import net.minecraft.server.v1_9_R1.EntityVillager;
import net.minecraft.server.v1_9_R1.PathfinderGoalSelector;
import net.minecraft.server.v1_9_R1.World;
import net.samagames.tools.Reflection;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.lang.reflect.Field;
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
public class PNJ extends EntityVillager {
    /**
     * PNJ's destination
     */
    private Location objective;
    /**
     * If it's either a good or bad PNJ
     */
    private boolean good;
    /**
     * How many ticks did the PNJ live
     */
    private int life;

    public PNJ(World world, Location obj, boolean good) {
        super(world);
        this.objective = obj;
        this.good = good;
        /*
        Par la suite on utilise la reflection pour récupérer l'AI du PNJ et la redéfinir.
         */
        try {
            Field bField = Reflection.getField(PathfinderGoalSelector.class, "b");
            bField.setAccessible(true);
            Field cField = Reflection.getField(PathfinderGoalSelector.class, "c");
            cField.setAccessible(true);
            bField.set(goalSelector, Sets.newLinkedHashSet());
            bField.set(targetSelector, Sets.newLinkedHashSet());
            cField.set(goalSelector, Sets.newLinkedHashSet());
            cField.set(targetSelector, Sets.newLinkedHashSet());
        } catch (Exception e) {
            Bukkit.getLogger().log(Level.SEVERE, "Erreur Reflection PNJ", e);
        }
        this.setProfession(good ? 1 : 2); // 1 = vêtement blanc / 2 = vêtement violet
        this.goalSelector.a(0, new PathfinderGoalWalk(this, objective)); // On rend notre PNJ intelligent
    }

    /**
     * Get the PNJ's destination
     * @return PNJ's destination
     */
    public Location getObjective() {
        return objective;
    }

    public int getLife() {
        return life;
    }

    public boolean isGood() {
        return good;
    }

    /**
     * Adds "life" to the PNJ (increments by one each tick)
     * @param life
     */
    public void addLife(int life) {
        this.life += life;
    }


}
