package nl.stoux.SlapGames.Games.Sonic;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import nl.stoux.SlapGames.Games.Sonic.Models.SonicRun;
import nl.stoux.SlapGames.Players.GamePlayer;
import nl.stoux.SlapGames.Util.PlayerUtil;
import nl.stoux.SlapGames.Util.Schedule;
import nl.stoux.SlapGames.Util.Util;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffectType;

import java.util.stream.Stream;

/**
 * Created by Stoux on 23/01/2015.
 */
public class SonicPlayer extends GamePlayer<Sonic> {

    /** This player's current/latest run */
    @Getter(AccessLevel.PUBLIC)
    private SonicRun run;

    /** The player is busy with a run */
    @Getter(AccessLevel.PUBLIC)
    @Setter(AccessLevel.PUBLIC)
    private boolean running;

    public SonicPlayer(Player player, Sonic game) {
        super(player, game);
    }

    /** Add potions boosts & boots */
    public void becomeSonic() {
        //Stack potions
        giveInfinitePotion(PotionEffectType.SPEED);
        giveInfinitePotion(PotionEffectType.JUMP);
        giveInfinitePotion(PotionEffectType.REGENERATION, 99999);
        giveInfinitePotion(PotionEffectType.HEALTH_BOOST, 99999); //TODO Not sure if this works as intended
        giveInfinitePotion(PotionEffectType.DAMAGE_RESISTANCE, 99999); //TODO Has this effect on the blast radius? If not put this higher

        //Create the sonic boots
        ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);
        //=> Add enchants
        Stream.of(Enchantment.PROTECTION_ENVIRONMENTAL, Enchantment.PROTECTION_EXPLOSIONS, Enchantment.PROTECTION_FALL)
                .forEach(e -> boots.addUnsafeEnchantment(e, 10));
        //=> Set color & name
        LeatherArmorMeta meta = (LeatherArmorMeta) boots.getItemMeta();
        meta.setColor(Color.BLUE);
        meta.setDisplayName(ChatColor.AQUA + "" + ChatColor.ITALIC + "Sonic Shoes");
        boots.setItemMeta(meta);

        //Set the boots
        player.getInventory().setBoots(boots);
    }

    /** The player has started a new run */
    public void startNewRun() {
        run = new SonicRun(PlayerUtil.getUserID(player));
        running = true;
    }

    /** The player has finished their run */
    public void finishedRun() {
        run.finished();
        running = false;

        //Insert the run into the DB
        Schedule.insertASync(run);
    }

}
