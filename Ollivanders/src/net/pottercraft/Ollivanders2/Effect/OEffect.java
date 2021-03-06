package net.pottercraft.Ollivanders2.Effect;

import java.io.Serializable;
import java.util.UUID;

import org.bukkit.entity.Player;

@Deprecated
public class OEffect implements Serializable
{

   /**
    * Effect object stored in OPlayer
    */
   public O2EffectType name;
   private UUID casterUUID;
   public int duration;
   public boolean kill;

   public OEffect (Player sender, O2EffectType effect, int duration)
   {
      casterUUID = sender.getUniqueId();
      this.duration = duration;
      name = effect;
      kill = false;
   }

   /**
    * Ages the OEffect
    */
   public void age (int i)
   {
      duration -= i;
      if (duration < 0)
      {
         kill();
      }
   }

   /**
    * This kills the effect.
    */
   public void kill ()
   {
      kill = true;
   }

   /**
    * Returns the caster's UUID
    *
    * @return UUID
    */
   public UUID getCasterUUID ()
   {
      return casterUUID;
   }
}