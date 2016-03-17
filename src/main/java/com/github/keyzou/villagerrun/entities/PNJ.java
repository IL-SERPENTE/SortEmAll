package com.github.keyzou.villagerrun.entities;

import com.github.keyzou.villagerrun.entities.ai.PathfinderGoalWalk;
import com.google.common.collect.Sets;
import net.minecraft.server.v1_9_R1.EntityVillager;
import net.minecraft.server.v1_9_R1.PathfinderGoalSelector;
import net.minecraft.server.v1_9_R1.World;
import net.samagames.tools.Reflection;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_9_R1.util.UnsafeList;

import java.lang.reflect.Field;
import java.util.Set;
import java.util.logging.Level;

public class PNJ extends EntityVillager {
    /**
     * La destination du PNJ
     */
    private Location objective;
    /**
     * Pour savoir si c'est un bon ou un mauvais PNJ
     */
    private boolean good;
    /**
     * Nombre de ticks pendant lequel le PNJ a vécu
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
     * Permet de récupérer la destination du PNJ
     * @return la destination du pnj
     */
    public Location getObjective() {
        return objective;
    }

    /**
     * Permet de récupérer le nombre de ticks durant lequel le PNJ a vécu
     * @return la "vie" du pnj
     */
    public int getLife() {
        return life;
    }

    /**
     * Permet de savoir si un pnj est bon ou mauvais
     * @return true si bon sinon false
     */
    public boolean isGood() {
        return good;
    }

    /**
     * Ajoute de la "vie" au PNJ (s'incrémente de 1 à chaque tick)
     * @param life la vie à ajouter
     */
    public void addLife(int life) {
        this.life += life;
    }


}
