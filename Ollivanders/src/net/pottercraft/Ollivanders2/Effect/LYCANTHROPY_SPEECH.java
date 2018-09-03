package net.pottercraft.Ollivanders2.Effect;

import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.entity.Player;

import java.util.ArrayList;

/**
 * Change the the player's speech to dog sounds.
 *
 * @author Azami7
 * @since 2.2.8
 */
public class LYCANTHROPY_SPEECH extends BABBLING
{
   public LYCANTHROPY_SPEECH (Ollivanders2 plugin, O2EffectType effect, Integer duration, Player player)
   {
      super(plugin, effect, duration, player);

      dictionary = new ArrayList<String>() {{
         add("§oHOOOOOOWLLLLLL");
         add("§obark bark bark bark");
         add("§ogrowl");
         add("§osnarl");
      }};

      permanent = true;
      maxWords = 3;
   }
}
