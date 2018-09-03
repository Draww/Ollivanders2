package net.pottercraft.Ollivanders2.Effect;

import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class SILENCIO extends O2Effect
{
   /**
    * Constructor
    *
    * @param plugin a callback to the MC plugin
    * @param effect the effect cast
    * @param duration the duration of the effect
    * @param player the player this effect acts on
    */
   public SILENCIO (Ollivanders2 plugin, O2EffectType effect, Integer duration, Player player)
   {
      super(plugin, effect, duration, player);
   }

   /**
    * Age the effect by 1 every game tick.
    */
   public void checkEffect ()
   {
      age(1);
   }

   /**
    * Remove all recepients from chat if the player.
    *
    * @param event the player chat event
    */
   public void doSilencio (AsyncPlayerChatEvent event)
   {
      Player sender = event.getPlayer();

      if (Ollivanders2.debug)
      {
         p.getLogger().info("onPlayerChat: SILENCIO");
      }

      if (sender.isPermissionSet("Ollivanders2.BYPASS"))
      {
         if (!sender.hasPermission("Ollivanders2.BYPASS"))
         {
            event.getRecipients().clear();
         }
      }
      else
      {
         event.getRecipients().clear();
      }
   }
}