package com.github.keyzou.villagerrun.entities.ai;

import com.github.keyzou.villagerrun.entities.PNJ;
import net.minecraft.server.v1_9_R1.*;
import net.samagames.tools.Reflection;
import org.bukkit.Location;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class PathfinderGoalWalk extends PathfinderGoal {
    /**
     * L'endroit où le PNJ doit aller (portillon)
     */
    private Location objective;
    /**
     * Le famous PNJ
     */
    private PNJ pnj;

    public PathfinderGoalWalk(PNJ pnj, Location objective){
        this.pnj = pnj;
        this.objective = objective;
    }

    /**
     * fonction shouldExecute()
     * @return toujours true parce qu'on veut toujours que le pnj aille droit au but
     */
    @Override
    public boolean a() {
        return true;
    }

    /**
     * Fonction startExecuting(), on défini la destination du pnj sur le portillon
     */
    @Override
    public void c(){
//        PathPoint pnjPoint = new PathPoint((int)pnj.locX, (int)pnj.locY, (int)pnj.locZ);
//        PathPoint objPoint = new PathPoint((int)objective.getX(),(int) objective.getY(),(int) objective.getZ());
//        PathEntity pe = new PathEntity(new PathPoint[]{pnjPoint, objPoint});
//        this.pnj.getNavigation().a(pe, 0.5f);
        this.pnj.getNavigation().a(objective.getBlockX()+0.5, objective.getBlockY(), objective.getBlockZ()+0.5, 0.5f );
    }

    /**
     * Rien ne doit arrêter ce PNJ
     * @return false
     */
    @Override
    public boolean b() {
        return false;
    }

    /**
     * Quand on met à jour son trajet, on le fait vieillir.
     */
    @Override
    public void e(){
        pnj.addLife(1);
        pnj.motZ = 0;
        pnj.motY = 0;
    }


}
