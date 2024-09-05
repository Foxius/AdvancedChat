package com.saikonohack.advancedChat.gui;

import com.saikonohack.advancedChat.AdvancedChat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class AdminGUI implements Listener {

    private final AdvancedChat plugin;
    private final Inventory adminMenu;

    public AdminGUI(AdvancedChat plugin) {
        this.plugin = plugin;
        this.adminMenu = Bukkit.createInventory(null, 27, ChatColor.DARK_GREEN + "Admin Settings");

        initializeItems();
    }

    // Инициализация элементов меню
    private void initializeItems() {
        adminMenu.setItem(10, createMenuItem(Material.PAPER, ChatColor.YELLOW + "Clear Chat"));
        adminMenu.setItem(12, createMenuItem(Material.BOOK, ChatColor.YELLOW + "Toggle Global Chat"));
        adminMenu.setItem(14, createMenuItem(Material.BOOKSHELF, ChatColor.YELLOW + "Toggle Local Chat"));
        adminMenu.setItem(16, createMenuItem(Material.COMPASS, ChatColor.YELLOW + "Geolocate Player"));
    }

    private ItemStack createMenuItem(Material material, String name) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            item.setItemMeta(meta);
        }
        return item;
    }

    // Открытие меню для игрока
    public void openAdminMenu(Player player) {
        player.openInventory(adminMenu);
    }

    // Обработка кликов в инвентаре
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals(ChatColor.DARK_GREEN + "Admin Settings")) {
            event.setCancelled(true); // Отменяем перемещение предметов в меню

            Player player = (Player) event.getWhoClicked();
            ItemStack clickedItem = event.getCurrentItem();

            if (clickedItem == null || !clickedItem.hasItemMeta()) {
                return;
            }

            switch (clickedItem.getType()) {
                case PAPER:
                    player.performCommand("clearchat");
                    player.sendMessage(ChatColor.GREEN + "Чат был очищен.");
                    break;
                case BOOK:
                    player.performCommand("chatsettings global toggle");
                    player.sendMessage(ChatColor.GREEN + "Глобальный чат был переключен.");
                    break;
                case BOOKSHELF:
                    player.performCommand("chatsettings local toggle");
                    player.sendMessage(ChatColor.GREEN + "Локальный чат был переключен.");
                    break;
                case COMPASS:
                    player.performCommand("geolocate <player>");
                    player.sendMessage(ChatColor.GREEN + "Введите команду /geolocate <player> в чате для слежки.");
                    break;
                default:
                    break;
            }

            player.closeInventory(); // Закрываем меню после выполнения действия
        }
    }
}
