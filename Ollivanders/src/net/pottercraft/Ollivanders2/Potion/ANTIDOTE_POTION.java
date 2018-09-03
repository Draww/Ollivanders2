package net.pottercraft.Ollivanders2.Potion;

import net.pottercraft.Ollivanders2.Player.O2Player;
import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * The Antidote for Common Poisons is a potion which counteracts ordinary poisons, such as creature bites and stings.
 *
 * http://harrypotter.wikia.com/wiki/Antidote_to_Common_Poisons
 *
 * @since 2.2.7
 * @author Azami7
 */
public final class ANTIDOTE_POTION extends O2Potion
{
   public ANTIDOTE_POTION (Ollivanders2 plugin, O2PotionType potionType)
   {
      super(plugin, potionType);

      ingredients.put(IngredientType.MISTLETOE_BERRIES, 2);
      ingredients.put(IngredientType.BEZOAR, 1);
      ingredients.put(IngredientType.UNICORN_HAIR, 1);
      ingredients.put(IngredientType.STANDARD_POTION_INGREDIENT, 2);

      name = "Common Antidote Potion";
      text = "Counteracts ordinary poisons, such as creature bites and stings." + getIngredientsText();

      name = "Common Antidote Potion";
      text = "Counteracts ordinary poisons, such as creature bites and stings." + getIngredientsText();

      name = "Common Antidote Potion";
      text = "Counteracts ordinary poisons, such as creature bites and stings." + getIngredientsText();

      effect = new PotionEffect(PotionEffectType.HEAL, duration, 1);
      potionColor = Color.TEAL;
   }

   public void drink (O2Player o2p, Player player)
   {
   }
}
