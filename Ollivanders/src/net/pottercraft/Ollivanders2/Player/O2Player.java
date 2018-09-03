package net.pottercraft.Ollivanders2.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import net.pottercraft.Ollivanders2.Book.INTERMEDIATE_TRANSFIGURATION;
import net.pottercraft.Ollivanders2.Effect.O2Effect;
import net.pottercraft.Ollivanders2.Effect.O2EffectType;
import net.pottercraft.Ollivanders2.Effect.ShapeShiftSuper;
import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.Potion.IngredientType;
import net.pottercraft.Ollivanders2.Spell.SpellProjectile;
import net.pottercraft.Ollivanders2.Spell.Spells;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

/**
 * Ollivanders2 player
 *
 * This adds additional functionality beyond the original OPlayer. Since the original
 * file-based save used serialization, a new class was created for backwards compatibility.
 *
 * @author Azami7
 * @author autumnwoz
 * @since 2.5.2
 */

public class O2Player
{
   /**
    * Wand wood material effectType
    */
   private String wandWood = null;

   /**
    * Wand core material effectType
    */
   private String wandCore = null;

   /**
    * Player display effectType
    */
   private String playerName = null;

   /**
    * Player minecraft UUID, this is their primary identifier in Ollivanders2
    */
   private UUID pid = null;

   /**
    * The MC plugin callback
    */
   private Ollivanders2 p = null;

   /**
    * A map of all the spells a player knows and the cast count.
    */
   private Map<Spells, Integer> knownSpells = new HashMap<>();

   /**
    * A map of all the potions a player knows and the brew count.
    */
   private Map<String, Integer> knownPotions = new HashMap<>();

   /**
    * A map of the recent spells a player has cast and their cast timestamp
    */
   private Map<Spells, Long> recentSpells = new HashMap<>();

   /**
    * A list of all effects currently on this player
    */
   private Map<O2EffectType, O2Effect> effects = new HashMap<>();

   /**
    * The spell loaded into the wand for casting with left click
    */
   private Spells wandSpell = null;

   /**
    * The mastered spell set for silent casting - is cast anytime a player left-clicks their wand in their primary hand.
    */
   private Spells masterSpell = null;

   /**
    * The list of mastered spells - spells with > 100 cast count
    */
   private ArrayList<Spells> masteredSpells = new ArrayList<>();

   /**
    * The number of souls this user has collected
    */
   private int souls = 0;

   /**
    * Whether the player is currently invisible
    */
   private boolean invisible = false;

   /**
    * Whether the player is in a Repello Muggleton area
    */
   private boolean inRepelloMuggleton = false;

   /**
    * Whether the player has found their destined wand yet
    */
   private boolean foundWand = false;

   /**
    * The player'ss animagus form, if they are an animagus
    */
   private EntityType animagusForm = null;

   /**
    * The color variant for the animagus form
    */
   private String animagusColor = null;

   /**
    * Whether the player is a Muggle.
    */
   private boolean muggle = true;

   /**
    * The player's year in school
    */
   private Year year = Year.YEAR_1;

   /**
    * Effects to add to this player at join
    */
   private HashMap<O2EffectType, Integer> effectsAtJoin = new HashMap<>();

   /**
    * Constructor.
    *
    * @param id the UUID of the player
    * @param name the effectType of the player
    * @param plugin a reference to the plugin
    */
   public O2Player (UUID id, String name, Ollivanders2 plugin)
   {
      p = plugin;
      playerName = name;
      pid = id;

      // set destined wand
      initDestinedWand();
   }

   /**
    * Initialize the player's destined wand seeded with their pid
    */
   private void initDestinedWand ()
   {
      // set destined wand
      int seed = Math.abs(pid.hashCode()%16);
      int wood = seed/4;
      int core = seed%4;

      wandWood = O2PlayerCommon.woodArray.get(wood);
      wandCore = O2PlayerCommon.coreArray.get(core);
   }

   /**
    * Determine if a wand matches the player's destined wand type.
    *
    * @param stack the wand to check
    * @return true if is a wand and it matches, false otherwise
    */
   public boolean isDestinedWand (ItemStack stack)
   {
      if (wandWood == null || wandCore == null)
         return false;

      if (p.common.isWand(stack))
      {
         List<String> lore = stack.getItemMeta().getLore();
         String[] comps = lore.get(0).split(O2PlayerCommon.wandLoreConjunction);

         if (wandWood.equalsIgnoreCase(comps[0]) && wandCore.equalsIgnoreCase(comps[1]))
         {
            foundWand = true;
            muggle = false;
            return true;
         }
         else
         {
            return false;
         }
      }
      else
      {
         return false;
      }
   }

   /**
    * Get the player's destined wand lore.
    *
    * @return the wand lore for the players destined wand
    */
   public String getDestinedWandLore ()
   {
      return wandWood + O2PlayerCommon.wandLoreConjunction + wandCore;
   }

   /**
    * Get the player's destined wand wood type.
    *
    * @return the player's destined wand wood type
    */
   public String getWandWood ()
   {
      return wandWood;
   }

   /**
    * Get the player's destined wand core type.
    *
    * @return the player's destined wand core type
    */
   public String getWandCore ()
   {
      return wandCore;
   }

   /**
    * Set the player's destined wand wood type. This overrides the current value.
    *
    * @param wood sets the destined wand wood type
    */
   public void setWandWood (String wood)
   {
      if (O2PlayerCommon.woodArray.contains(wood))
      {
         wandWood = wood;
      }
   }

   /**
    * Set the player's destined wand core type. This overrides the current value.
    *
    * @param core set the destined wand core type
    */
   public void setWandCore (String core)
   {
      if (O2PlayerCommon.coreArray.contains(core))
      {
         wandCore = core;
      }
   }

   /**
    * Get the effectType of this player for use in commands like listing out house membership. Since player names
    * can change, this should not be used to identify a player. Instead, use the UUID of player and the O2Players
    * map to find their O2Player object.
    *
    * @return the player's effectType
    */
   public String getPlayerName ()
   {
      return playerName;
   }

   /**
    * Sets the effectType of this player for use in commands like listing out house membership.
    *
    * @param name the effectType to set for this player
    */
   public void setPlayerName (String name)
   {
      playerName = name;
   }

   /**
    * Get the casting count for a spell
    *
    * @param spell the spell to get a count for
    * @return the number of times a player has cast this spell
    */
   public int getSpellCount (Spells spell)
   {
      int count = 0;

      if (knownSpells.containsKey(spell))
      {
         count = knownSpells.get(spell);
      }

      return count;
   }

   /**
    * Get the brewing count for a potion
    *
    * @param potion the spell to get a count for
    * @return the number of times a player has cast this spell
    */
   public int getPotionCount (String potion)
   {
      int count = 0;

      if (knownPotions.containsKey(potion))
      {
         count = knownPotions.get(potion);
      }

      return count;
   }

   /**
    * Get the casting count for a spell
    *
    * @param spell the spell to get a count for
    * @return the number of times a player has cast this spell
    */
   public Long getSpellLastCastTime (Spells spell)
   {
      Long count = new Long(0);

      if (recentSpells.containsKey(spell))
      {
         count = recentSpells.get(spell);
      }

      return count;
   }

   /**
    * Get the list of known spells for this player.
    *
    * @return a map of all the known spells and the spell count for each.
    */
   public Map<Spells, Integer> getKnownSpells ()
   {
      return knownSpells;
   }

   public Map<String, Integer> getKnownPotions ()
   {
      return knownPotions;
   }

   /**
    * Get the list of recently cast spells for this player.
    *
    * @return a map of all recent spells and the time they were cast.
    */
   public Map<Spells, Long> getRecentSpells () { return recentSpells; }

   /**
    * Set the spell count for a spell. This will override the existing values for this spell and should
    * not be used when increment is intended.
    *
    * @param spell the spell to set the count for
    * @param count the count to set
    */
   public void setSpellCount (Spells spell, int count)
   {
      if (count >= 1)
      {
         if (knownSpells.containsKey(spell))
         {
            knownSpells.replace(spell, count);
         }
         else
         {
            knownSpells.put(spell, count);
         }
      }
      else
      {
         if (knownSpells.containsKey(spell))
            knownSpells.remove(spell);
      }

      // remove spell from mastered list if level is less than 100
      if (count < 100)
      {
         removeMasteredSpell(spell);
      }
      // add spell to mastered list if level is at or over 100
      else
      {
         addMasteredSpell(spell);
      }
   }

   /**
    * Set the potion count for a potion. This will override the existing values for this potion and should
    * not be used when increment is intended.
    *
    * @param potion the potion to set the count for
    * @param count the count to set
    */
   public void setPotionCount (String potion, int count)
   {
      if (count >= 1)
      {
         if (knownPotions.containsKey(potion))
         {
            knownPotions.replace(potion, count);
         }
         else
         {
            knownPotions.put(potion, count);
         }
      }
      else
      {
         if (knownPotions.containsKey(potion))
            knownPotions.remove(potion);
      }
   }

   /**
    * Set the most recent cast time for a spell. This will override the existing values for this spell.
    *
    * @param spell the spell to set the time for
    */
   public void setSpellRecentCastTime (Spells spell)
   {
      String spellClass = "net.pottercraft.Ollivanders2.Spell." + spell.toString();
      @SuppressWarnings("rawtypes")
      Constructor c;
      try
      {
         c = Class.forName(spellClass).getConstructor();
         SpellProjectile s = (SpellProjectile) c.newInstance();
         if (recentSpells.containsKey(spell))
         {
            recentSpells.replace(spell, System.currentTimeMillis() + s.getCoolDown());
         }
         else
         {
            recentSpells.put(spell, System.currentTimeMillis() + s.getCoolDown());
         }
      }
      catch (InvocationTargetException e)
      {
         e.getCause().printStackTrace();
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   /**
    * Set the wand's mastered spell.
    *
    * @param spell the mastered spell
    */
   public void setMasterSpell (Spells spell)
   {
      masterSpell = spell;
   }

   /**
    * Increment the spell count by 1.
    *
    * @param spell the spell to increment
    */
   public void incrementSpellCount (Spells spell)
   {
      if (knownSpells.containsKey(spell))
      {
         int curCount = knownSpells.get(spell);
         knownSpells.replace(spell, curCount + 1);

         if (curCount + 1 >= 100)
         {
            addMasteredSpell(spell);
         }
      }
      else
      {
         knownSpells.put(spell, 1);
      }
   }

   /**
    * Increment the potion count by 1.
    *
    * @param potion the potion to increment
    */
   public void incrementPotionCount (String potion)
   {
      if (knownPotions.containsKey(potion))
      {
         int curCount = knownPotions.get(potion);
         knownPotions.replace(potion, curCount + 1);
      }
      else
      {
         knownPotions.put(potion, 1);
      }
   }

   /**
    * Resets the known spells for this player to none.
    */
   public void resetSpellCount ()
   {
      knownSpells.clear();
      masteredSpells.clear();
   }

   /**
    * Resets the known spells for this player to none.
    */
   public void resetPotionCount ()
   {
      knownPotions.clear();
   }

   /**
    * Get the spell currently loaded in to the player's wand.
    *
    * @return the loaded spell
    */
   public Spells getWandSpell ()
   {
      if (wandSpell == null && masterSpell != null && Ollivanders2.nonVerbalCasting)
         return masterSpell;

      return wandSpell;
   }

   /**
    * Loads a spell in to the player's wand.
    *
    * @param spell the spell to load
    */
   public void setWandSpell (Spells spell)
   {
      if (Ollivanders2.debug)
      {
         if (spell == null)
            p.getLogger().info("Setting wand spell to null");
         else
            p.getLogger().info("Setting wand spell to " + spell.toString());
      }

      wandSpell = spell;
   }

   /**
    * Determine if this player is invisible.
    *
    * @return true if the player is invisible, false otherwise.
    */
   public boolean isInvisible ()
   {
      return invisible;
   }

   /**
    * Set whether a player is invisible
    *
    * @param isInvisible true if the player is invisible, false if they are not
    */
   public void setInvisible(boolean isInvisible)
   {
      invisible = isInvisible;
   }

   /**
    * Determine if the player is in a Repello Muggleton.
    *
    * @return true if they are a in a repello muggleton, false otherwise.
    */
   public boolean isInRepelloMuggleton ()
   {
      return inRepelloMuggleton;
   }

   /**
    * Set if a player is in a repello muggleton.
    *
    * @param isInRepelloMuggleton true if the player is in a repello muggleton, false otherwise
    */
   public void setInRepelloMuggleton (boolean isInRepelloMuggleton)
   {
      inRepelloMuggleton = isInRepelloMuggleton;
   }

   /**
    * Determine if player is a muggle.
    *
    * @return true if they are a muggle, false otherwise
    */
   public boolean isMuggle () { return muggle; }

   /**
    * Set if a player is a muggle.
    *
    * @param isMuggle true if the player is a muggle
    */
   public void setMuggle (boolean isMuggle) { muggle = isMuggle; }

   /**
    * Get the number of souls this player has collected.
    *
    * @return the number of souls this player has collected
    */
   public int getSouls ()
   {
      return souls;
   }

   /**
    * Set the number of souls this player has collected.
    *
    * @param s the number of souls the player has collected
    */
   public void setSouls (int s)
   {
      souls = s;
   }

   /**
    * Add a soul to this player.
    */
   public void addSoul ()
   {
      souls++;
   }

   /**
    * Remove a soul from this player.
    */
   public void subtractSoul()
   {
      if (souls > 0)
      {
         souls--;
      }
   }

   /**
    * Get the year this player is in.
    * @return The year the player is in
    */
   public Year getYear() {
      return year;
   }

   /**
    * Set the year this player is in.
    * @param y The year to set them to
    */
   public void setYear(Year y) {
      if (year != null) {
         year = y;
      }
   }

   /**
    * Reset the soul count to zero.
    */
   public void resetSouls ()
   {
      souls = 0;
   }

   /**
    * Get a list of all the Ollivanders effects this user has on them.
    *
    * @return a list of the effects active on this player
    */
   public List<O2Effect> getEffects ()
   {
      List<O2Effect> effectsCopy = new ArrayList<>();

      effectsCopy.addAll(effects.values());

      return effectsCopy;
   }

   /**
    * Gets a specific effect on a player.
    *
    * @param effectType the effect type to get
    * @return the effect if the player has one, null otherwise
    */
   public O2Effect getEffect (O2EffectType effectType)
   {
      if (effects.containsKey(effectType))
         return effects.get(effectType);
      else
         return null;
   }

   /**
    * Add an effect to this player.
    *
    * @param e the effect to add to this player
    */
   public void addEffect (O2Effect e)
   {
      // do not allow multiple shape-shifting effects at the same time
      if (e instanceof ShapeShiftSuper)
      {
         for (O2Effect effect : effects.values())
         {
            if (effect instanceof ShapeShiftSuper)
            {
               return;
            }
         }
      }

      effects.put(e.effectType, e);

      if (Ollivanders2.debug)
         p.getLogger().info("Adding effect " + e.effectType.toString() + " to " + playerName);
   }

   /**
    * Remove an effect from this player.
    *
    * @param e the effect to remove from this player
    */
   public void removeEffect (O2Effect e)
   {
      removeEffect(e.effectType);
   }

   /**
    * Remove an effect from this player.
    *
    * @param effectType the effect to remove from this player
    */
   public void removeEffect (O2EffectType effectType)
   {
      O2Effect effect = effects.get(effectType);

      if (effect != null)
      {
         effect.kill();
         effects.remove(effectType);

         if (Ollivanders2.debug)
            p.getLogger().info("Removing effect " + effectType.toString() + " to " + playerName);
      }
   }

   /**
    * Remove all effects from this player.
    */
   public void resetEffects ()
   {
      effects.clear();
   }

   /**
    * Set whether the player has found their destined wand before.
    *
    * @param b set whether the player has found their destined wand
    */
   public void setFoundWand (boolean b)
   {
      foundWand = b;
   }

   /**
    * Has this player found their destined wand?
    *
    * @return true if they have, false if not
    */
   public boolean foundWand ()
   {
      return foundWand;
   }

   /**
    * Returns this player's spell journal, a book with all known spells and their level.
    *
    * @return the player's spell journal
    */
   public ItemStack getSpellJournal ()
   {
      ItemStack spellJournal = new ItemStack(Material.WRITTEN_BOOK, 1);

      BookMeta bookMeta = (BookMeta)spellJournal.getItemMeta();
      bookMeta.setAuthor(playerName);
      bookMeta.setTitle("Spell Journal");

      String content = "Spell Journal\n\n";
      int lineCount = 2;
      for (Entry <Spells, Integer> e : knownSpells.entrySet())
      {
         // if we have done 14 lines, make a new page
         if (lineCount == 14)
         {
            bookMeta.addPage(content);
            lineCount = 0;
            content = "";
         }

         // add a newline to all lines except the first
         if (lineCount != 0)
         {
            content = content + "\n";
         }

         String spell = p.common.firstLetterCapitalize(p.common.enumRecode(e.getKey().toString().toLowerCase()));
         String count = e.getValue().toString();
         String line = spell + " " + count;
         content = content + spell + " " + count;

         lineCount++;
         // ~18 characters per line, this will likely wrap
         if (line.length() > 18)
         {
            lineCount++;
         }
      }

      bookMeta.addPage(content);

      bookMeta.setGeneration(BookMeta.Generation.ORIGINAL);
      spellJournal.setItemMeta(bookMeta);

      return spellJournal;
   }

   /**
    * Add a spell to the list of spells that have a level of 100 or more. If this spell is the first mastered
    * spell then also load it to the wand master spell.
    *
    * @param spell the spell to add
    */
   private void addMasteredSpell (Spells spell)
   {
      if (!masteredSpells.contains(spell))
      {
         if (masteredSpells.size() < 1)
         {
            // this is their first mastered spell, set it on their wand
            masterSpell = spell;
         }
         masteredSpells.add(spell);
      }
   }

   /**
    * Remove a mastered spell when the level goes below 100 or is reset. If this spell is also set as the wand's
    * master spell, shift it to the next mastered spell or remove if there are none.
    *
    * @param spell the spell to remove
    */
   private void removeMasteredSpell (Spells spell)
   {
      if (masteredSpells.contains(spell))
      {
         // first remove this from the loaded master spell if it is that spell
         if (masterSpell == spell)
         {
            if (masteredSpells.size() > 1)
            {
               shiftMasterSpell(false);
            }
            else
            {
               masterSpell = null;
            }
         }

         masteredSpells.remove(spell);
      }
   }

   /**
    * Shift the wand's master spell to the next spell.
    */
   @Deprecated
   public void shiftMasterSpell ()
   {
      shiftMasterSpell(false);
   }

   /**
    * Shift the wand's master spell to the next spell.
    *
    * @param reverse if set to true, iterates backwards through spell list
    */
   public void shiftMasterSpell (boolean reverse)
   {
      // shift to the next spell if there is more than one mastered spell
      if (masteredSpells.size() >= 1)
      {
         if (masterSpell == null || masteredSpells.size() == 1)
         {
            masterSpell = masteredSpells.get(0);
         }
         else
         {
            int curSpellIndex = masteredSpells.indexOf(masterSpell);
            int nextSpellIndex;

            if (reverse)
            {
               nextSpellIndex = curSpellIndex + 1;
            }
            else
            {
               nextSpellIndex = curSpellIndex - 1;
            }

            // handle roll overs
            if (nextSpellIndex >= masteredSpells.size())
            {
               nextSpellIndex = 0;
            }
            else if (nextSpellIndex < 0)
            {
               nextSpellIndex = masteredSpells.size() - 1;
            }

            masterSpell = masteredSpells.get(nextSpellIndex);
         }
      }
      else
      {
         masterSpell = null;
      }
   }

   /**
    * Get the wand's master spell.
    *
    * @return the wand's master spell
    */
   public Spells getMasterSpell ()
   {
      return masterSpell;
   }

   /**
    * Sets that this player is an Animagus.
    */
   public void setIsAnimagus ()
   {
      if (animagusForm == null)
      {
         int form = 0;

         ArrayList<EntityType> animagusShapes = O2PlayerCommon.getAnimagusShapes();

         if (Ollivanders2.mcVersionCheck())
         {
            form = Math.abs(pid.hashCode() % animagusShapes.size());
         }
         else
         {
            // last 2 types are MC 1.12 and higher
            form = Math.abs(pid.hashCode() % (animagusShapes.size() - 2));
         }

         animagusForm = animagusShapes.get(form);
         if (Ollivanders2.debug)
            p.getLogger().info(playerName + " is an animagus type " + animagusForm.toString());

         // determine color variations for certain types
         if (animagusForm == EntityType.OCELOT)
         {
            animagusColor = p.common.randomOcelotType().toString();
         }
         else if (animagusForm == EntityType.WOLF)
         {
            animagusColor = p.common.randomSecondaryDyeColor().toString();
         }
         else if (animagusForm == EntityType.HORSE)
         {
            animagusColor = p.common.randomHorseColor().toString();
         }
         else if (Ollivanders2.mcVersionCheck() && animagusForm == EntityType.LLAMA)
         {
            animagusColor = p.common.randomLlamaColor().toString();
         }

         if (animagusColor != null && Ollivanders2.debug)
         {
            p.getLogger().info("Color variation " + animagusColor);
         }
      }
   }

   /**
    * Sets the Animagus form for this player.
    *
    * @param type the player's Animagus form.
    */
   public void setAnimagusForm (EntityType type)
   {
      ArrayList<EntityType> animagusShapes = O2PlayerCommon.getAnimagusShapes();

      if (animagusShapes.contains(type))
      {
         animagusForm = type;
      }
      else
      {
         // they do not have an allowed type, reset their type
         animagusForm = null;
         animagusColor = null;
         setIsAnimagus();
      }
   }

   void setAnimagusColor (String color)
   {
      animagusColor = color;
   }

   /**
    * Get the Animagus form for this player.
    *
    * @return the player's Animagus form or null if they are not an Animagus
    */
   public EntityType getAnimagusForm ()
   {
      return animagusForm;
   }

   /**
    * Get the color variation for this animagus.
    *
    * @return the color variation or null if not applicable
    */
   public String getAnimagusColor ()
   {
      // in case color was set when player has no animagus form
      if (animagusForm != null)
         return animagusColor;
      else
         return null;
   }

   /**
    * Determine if this player an Animagus.
    *
    * @return true if they have an Animagus form, false otherwise
    */
   public boolean isAnimagus ()
   {
      if (animagusForm != null)
         return true;

      return false;
   }

   /**
    * Determine if this player has the specified effect
    *
    * @param effectType the effect to check for
    * @return true if the player is in their animal form, false otherwise
    */
   public boolean hasEffect (O2EffectType effectType)
   {
      boolean has = false;

      if (effects.containsKey(effectType))
      {
         has = true;
      }

      return has;
   }

   /**
    * Get the player's UUID
    *
    * @return
    */
   public UUID getID ()
   {
      return pid;
   }

   /**
    * Effects to add to this player when they join. Since effects require a Player object, these cannot
    * be added at plugin load.
    *
    * @param effectType the effect type to add
    * @param duration the duration for this effect
    */
   void addJoinEffect (O2EffectType effectType, Integer duration)
   {
      if (!effectsAtJoin.containsKey(effectType))
      {
         effectsAtJoin.put(effectType, duration);
      }
   }

   /**
    * Effects to add to this player on join.
    */
   public void onJoinEffects ()
   {
      for (Entry<O2EffectType, Integer> entry : effectsAtJoin.entrySet())
      {
         O2EffectType effectType = entry.getKey();
         Integer duration = entry.getValue();

         Class effectClass = effectType.getClassName();
         Player player = p.getServer().getPlayer(pid);

         O2Effect effect;
         try
         {
            effect = (O2Effect)effectClass.getConstructor(Ollivanders2.class, O2EffectType.class, Integer.class, Player.class).newInstance(p, effectType, duration, player);
         }
         catch (Exception e)
         {
            if (Ollivanders2.debug)
            {
               p.getLogger().info("Failed to create class for " + effectType.toString());
               e.printStackTrace();
            }
            continue;
         }

         addEffect(effect);
      }
   }
}
