package net.pottercraft.Ollivanders2.Effect;

import java.util.ArrayList;

import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.Player.O2Player;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

/**
 * Turns player into a werewolf during the full moon. Doesn't go away until death.
 *
 * @author azami7
 * @since 2.2.8
 */
public class LYCANTHROPY extends ShapeShiftSuper
{
   ArrayList<O2EffectType> additionalEffects = new ArrayList<>();

   /**
    * Constructor
    *
    * @param plugin a callback to the MC plugin
    * @param effect the effect cast
    * @param duration the duration of the effect
    * @param player the player this effect acts on
    */
   public LYCANTHROPY (Ollivanders2 plugin, O2EffectType effect, Integer duration, Player player)
   {
      super(plugin, effect, duration, player);

      form = EntityType.WOLF;
      permanent = true;
      transformed = false;
   }

   /**
    * Transfigure the player back to human form and kill this effect.
    */
   @Override
   public void kill ()
   {
      restore();
      removeAdditionalEffect();

      kill = true;
   }

   /**
    * Change player in to a wolf for 1 day when the full moon occurs.
    *
    * See https://minecraft.gamepedia.com/Moon
    */
   @Override
   protected void upkeep ()
   {
      long curTime = target.getWorld().getTime();
      if (!transformed)
      {
         // only need to check after sunset
         if (curTime > 13000)
         {
            long day = target.getWorld().getFullTime()/24000;
            if ((day % 8) == 0)
            {
               // moonrise on a full moon day
               transform();

               addAdditionalEffects();

               target.playSound(target.getEyeLocation(), Sound.ENTITY_WOLF_HOWL, 1, 0);
            }
         }
      }
      else
      {
         long day = target.getWorld().getFullTime()/24000;
         boolean restore = false;

         if ((day % 8) == 0)
         {
            // if it is a full moon day before moonrise or after sunrise
            if (curTime < 13000 || curTime > 23500)
            {
               restore = true;
            }
         }
         else
         {
            // it is not a full moon day
            restore = true;
         }

         if (restore)
         {
            restore();
            removeAdditionalEffect();
         }
      }
   }

   /**
    * Add additional effects of lycanthropy such as aggression and speaking like a wolf
    */
   private void addAdditionalEffects ()
   {
      O2Player o2p = p.getO2Player(target);

      AGGRESSION effect = new AGGRESSION(p, O2EffectType.AGGRESSION, 5, target);
      effect.setAggressionLevel(10);
      o2p.addEffect(effect);
      additionalEffects.add(O2EffectType.AGGRESSION);

      o2p.addEffect(new LYCANTHROPY_SPEECH(p, O2EffectType.LYCANTHROPY_SPEECH, 5, target));
      additionalEffects.add(O2EffectType.LYCANTHROPY_SPEECH);
   }

   /**
    * Remove additional effects of Lycanthropy
    */
   private void removeAdditionalEffect ()
   {
      O2Player o2p = p.getO2Player(target);

      for (O2EffectType effect : additionalEffects)
      {
         o2p.removeEffect(effect);
      }
   }
}
