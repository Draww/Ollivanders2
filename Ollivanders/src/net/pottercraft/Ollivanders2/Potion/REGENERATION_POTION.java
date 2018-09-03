package net.pottercraft.Ollivanders2.Potion;

import net.pottercraft.Ollivanders2.Player.O2Player;
import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.Spell.INFORMOUS;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * The regeneration potion restores a player's experience after death if deathExpLoss is enabled.
 *
 * @author Azami7
 */
public final class REGENERATION_POTION extends O2Potion
{
   public REGENERATION_POTION (Ollivanders2 plugin, O2PotionType potionType)
   {
      super(plugin, potionType);

      ingredients.put(IngredientType.BONE, 1);
      ingredients.put(IngredientType.BLOOD, 1);
      ingredients.put(IngredientType.ROTTEN_FLESH, 1);
      ingredients.put(IngredientType.SALAMANDER_FIRE, 1);
      ingredients.put(IngredientType.STANDARD_POTION_INGREDIENT, 4);

      name = "Regeneration Potion";
      text = "This potion will heal a player." + getIngredientsText();
      flavorText.add("\"Bone of the father, unknowingly given, you will renew your son! Flesh of the servant, willingly sacrificed, you will revive your master. Blood of the enemy, forcibly taken, you will resurrect your foe.\" -Peter Pettigrew");

      name = "Regeneration Potion";
      text = "This potion will heal a player." + getIngredientsText();
      flavorText.add("\"Bone of the father, unknowingly given, you will renew your son! Flesh of the servant, willingly sacrificed, you will revive your master. Blood of the enemy, forcibly taken, you will resurrect your foe.\" -Peter Pettigrew");

      name = "Regeneration Potion";
      text = "This potion will heal a player." + getIngredientsText();
      flavorText.add("\"Bone of the father, unknowingly given, you will renew your son! Flesh of the servant, willingly sacrificed, you will revive your master. Blood of the enemy, forcibly taken, you will resurrect your foe.\" -Peter Pettigrew");

      effect = new PotionEffect(PotionEffectType.REGENERATION, duration, 1);
      potionColor = Color.WHITE;
   }

   public void drink (O2Player o2p, Player player) { }
}
