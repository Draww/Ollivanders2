package net.pottercraft.Ollivanders2.Book;

import net.pottercraft.Ollivanders2.O2MagicBranch;
import net.pottercraft.Ollivanders2.Spell.O2SpellType;
import net.pottercraft.Ollivanders2.Ollivanders2;

/**
 * Magick Moste Evile - O2Book of Dark Magic written in the Middle Ages
 *
 * Missing O2SpellType:
 * Animagus - https://github.com/Azami7/Ollivanders2/issues/87
 * Homorphus Charm - https://github.com/Azami7/Ollivanders2/issues/39
 *
 * @since 2.2.4
 * @author Azami7
 */
public class MAGICK_MOSTE_EVILE extends O2Book
{
   public MAGICK_MOSTE_EVILE (Ollivanders2 plugin)
   {
      super(plugin);

      shortTitle = title = "Magick Moste Evile";
      author = "Godelot";
      branch = O2MagicBranch.DARK_ARTS;

      spells.add(O2SpellType.FIENDFYRE);
      spells.add(O2SpellType.AVADA_KEDAVRA);
      spells.add(O2SpellType.FLAGRANTE);
      spells.add(O2SpellType.LEGILIMENS);

      closingPage = "\n\nOf the Horcrux, wickedest of magical inventions, we shall not speak nor give direction.";
   }
}
