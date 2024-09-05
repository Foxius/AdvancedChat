package com.saikonohack.advancedChat.tabcompleter;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class MentionTabCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        // Проверяем, начинается ли последнее слово с @
        if (args.length > 0 && args[args.length - 1].startsWith("@")) {
            String prefix = args[args.length - 1].substring(1).toLowerCase(); // Убираем символ '@' и приводим к нижнему регистру

            // Ищем игроков, имена которых начинаются с введенного текста после '@'
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.getName().toLowerCase().startsWith(prefix)) {
                    completions.add("@" + player.getName()); // Добавляем '@' перед ником игрока
                }
            }
        }

        return completions;
    }
}
