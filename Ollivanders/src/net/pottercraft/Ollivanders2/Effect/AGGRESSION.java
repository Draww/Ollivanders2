package net.pottercraft.Ollivanders2.Effect;

import java.util.Collection;

import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.Ollivanders2Common;

import org.bukkit.entity.Creature;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

/**
 * This effect causes the player to impulsively harm those nearby and causes them to provoke attacks by
 * nearby mobs. This effect does not age but must be removed explicitly.
 *
 * @author Azami7
 * @since 2.2.8
 */
public class AGGRESSION extends O2Effect
{
   // Value from 1-10 for how aggressive this player will be with 1 being lowest level
   int aggressionLevel = 5;

   /**
    * Constructor
    *
    * @param plugin a callback to the MC plugin
    * @param effect the effect cast
    * @param duration the duration of the effect
    * @param player the player this effect acts on
    */
   public AGGRESSION (Ollivanders2 plugin, O2EffectType effect, Integer duration, Player player)
   {
      super(plugin, effect, duration, player);
   }

   /**
    * Attack random entity every 15 seconds and provoke nearby Creatures to attack.
    */
   @Override
   public void checkEffect ()
   {
      // only take action once per 10 seconds, which is every 120 ticks
      long curTime = target.getWorld().getTime();
      if ((curTime % 120) == 0)
      {
         int rand = Math.abs(Ollivanders2.random.nextInt()) % 10;

         Ollivanders2Common common = new Ollivanders2Common(p);
         if (rand < aggressionLevel)
         {
            // damage nearby entity
            Collection<LivingEntity> nearby = common.getLivingEntitiesInRadius(target.getLocation(), 3);
            damageRandomEntity(nearby);

            // provoke nearby Creatures to attack
            nearby = common.getLivingEntitiesInRadius(target.getLocation(), 6);
            provoke(nearby);
         }
      }
   }

   /**
    * Damage a random nearby entity.
    *
    * @param nearby a collection of nearby entities
    */
   private void damageRandomEntity (Collection<LivingEntity> nearby)
   {
      if (nearby != null && !nearby.isEmpty())
      {
         int rand = Math.abs(Ollivanders2.random.nextInt());
         LivingEntity[] nArray = nearby.toArray(new LivingEntity[nearby.size()]);

         LivingEntity toDamage = nArray[rand % nearby.size()];

         double curHealth = toDamage.getHealth();
         // damage is entities current health divided by 2, 3, or 4
         rand = Math.abs(Ollivanders2.random.nextInt());
         double damage = curHealth / ((rand % 3) + 2);
         toDamage.damage(damage, target);
      }
   }

   /**
    * Provoke nearby Creatures to target this player.
    *
    * @param nearby
    */
   private void provoke (Collection<LivingEntity> nearby)
   {
      if (nearby != null && !nearby.isEmpty())
      {
         for (LivingEntity entity : nearby)
         {
            if (entity instanceof Creature)
            {
               ((Creature) entity).setTarget(target);
            }
         }
      }
   }

   /**
    * Set the aggression level for this player
    *
    * @param level 1-10 where 1 is the lowest
    */
   public void setAggressionLevel (int level)
   {
      if (level < 1)
         level = 1;
      else if (level > 10)
         level = 10;

      aggressionLevel = level;
   }
}
