package rip.tilly.bedwars.menus.shop;

import lombok.AllArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import rip.tilly.bedwars.BedWars;
import rip.tilly.bedwars.playerdata.PlayerData;
import rip.tilly.bedwars.utils.CC;
import rip.tilly.bedwars.utils.ItemBuilder;
import rip.tilly.bedwars.utils.PlayerUtil;
import rip.tilly.bedwars.utils.menu.Button;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class ShopButton extends Button {

    private final String name;
    private final Material material;
    private final int data;
    private final int amount;
    private final Material costType;
    private final String costTypeName;
    private final ChatColor costTypeColor;
    private final int cost;
    private final boolean color;

    @Override
    public ItemStack getButtonItem(Player player) {
        List<String> loreList = new ArrayList<>();

        loreList.add(" ");
        loreList.add("&9Cost: " + costTypeColor + cost + " " + costTypeName);
        loreList.add("&9Amount: &e" + amount + "x");

        int costItems = 0;
        for (ItemStack contents : player.getInventory().getContents()) {
            if (contents != null) {
                if (contents.getType() == costType) {
                    costItems += contents.getAmount();
                }
            }
        }

        loreList.add(" ");
        loreList.add(costItems >= cost ? "&aClick to purchase!" : "&cYou don't have enough " + costTypeName + "!");

        PlayerData playerData = BedWars.getInstance().getPlayerDataManager().getPlayerData(player.getUniqueId());
        if (color) {
            return new ItemBuilder(material)
                    .name((costItems >= cost ? "&a" : "&c") + name)
                    .lore(loreList)
                    .amount(amount)
                    .durability(playerData.getPlayerTeam().getColorData())
                    .hideFlags()
                    .build();
        } else {
            return new ItemBuilder(material)
                    .name((costItems >= cost ? "&a" : "&c") + name)
                    .lore(loreList)
                    .amount(amount)
                    .durability(data)
                    .hideFlags()
                    .build();
        }
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
        if (clickType.isShiftClick()) {
            return;
        }

        int costItems = 0;
        for (ItemStack contents : player.getInventory().getContents()) {
            if (contents != null) {
                if (contents.getType() == costType) {
                    costItems += contents.getAmount();
                }
            }
        }

        if (costItems < cost) {
            player.sendMessage(CC.translate("&cYou don't have enough " + costTypeName + "!"));
            playFail(player);
            return;
        }

        PlayerData playerData = BedWars.getInstance().getPlayerDataManager().getPlayerData(player.getUniqueId());

        int finalCost = cost;
        for (ItemStack i : player.getInventory().getContents()) {
            if (i == null) {
                continue;
            }
            if (i.getType() == costType) {
                if (i.getAmount() < finalCost) {
                    finalCost -= i.getAmount();
                    PlayerUtil.minusAmount(player, i, i.getAmount());
                    player.updateInventory();
                } else {
                    PlayerUtil.minusAmount(player, i, finalCost);
                    player.updateInventory();
                    break;
                }
            }
        }

        if (color) {
            player.getInventory().addItem(new ItemBuilder(material)
                    .amount(amount)
                    .durability(playerData.getPlayerTeam().getColorData())
                    .hideFlags()
                    .build());
        } else {
            player.getInventory().addItem(new ItemBuilder(material)
                    .amount(amount)
                    .durability(data)
                    .hideFlags()
                    .build());
        }

        playNeutral(player);
    }
}
