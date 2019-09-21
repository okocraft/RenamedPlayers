package net.okocraft.playermanager.command;

import java.util.UUID;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import net.okocraft.playermanager.PlayerManager;
import net.okocraft.playermanager.database.Database;
import net.okocraft.playermanager.utilities.ConfigManager;

public class Commands implements CommandExecutor {
    private final ConfigManager configManager;

    public Commands(PlayerManager plugin, Database database) {
        this.configManager = plugin.getConfigManager();

        PluginCommand command = plugin.getCommand("playermanager");
        if (command != null) {
            command.setExecutor(this);
            command.setTabCompleter(new PlayerManagerTabCompleter(database));
        } else {
            plugin.getLog().warning("/playermanager が null でした。コマンドは使用できません。");
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            return errorOccurred(sender, configManager.getNoEnoughArgMsg());
        }

        final String subCommand = args[0].toLowerCase();

        switch (subCommand) {
        case "inventory":
            if (!hasPermission(sender, "playermanager." + subCommand))
                return false;
            return InventoryCommands.inventoryCommands(sender, args, false);
        case "enderchest":
            if (!hasPermission(sender, "playermanager." + subCommand))
                return false;
            return InventoryCommands.inventoryCommands(sender, args, true);
        case "database":
            if (!hasPermission(sender, "playermanager." + subCommand))
                return false;
            return DatabaseCommands.databaseCommands(sender, args);
        case "alt":
            if (!hasPermission(sender, "playermanager." + subCommand))
                return false;
            return AltCommands.altCommands(sender, args);
        case "namechange":
            if (!hasPermission(sender, "playermanager." + subCommand))
                return false;
            return NameChangeCommands.nameChangeCommands(sender, args);
        case "test":
            if (!hasPermission(sender, "playermanager." + subCommand))
                return false;
            return test(sender);
        }
        return errorOccurred(sender, configManager.getCommandNotExistMsg().replaceAll("%command%", subCommand));
    }

    private static boolean test(CommandSender sender) {
        sender.sendMessage(((Player) sender).getAddress().getAddress().getHostAddress());
        return true;
    }

    /**
     * If entry is form of UUID, this returns string "uuid". Otherwise, this returns
     * string "player".
     * 
     * @param entry エントリー
     * @return "uuid" or "player"
     */
    public static String checkEntryType(String entry) {
        try {
            UUID.fromString(entry);
            return "uuid";
        } catch (IllegalArgumentException e) {
            return "player";
        }
    }

    public static boolean errorOccurred(CommandSender sender, String msg) {
        sender.sendMessage(msg);
        return false;
    }

    public static boolean hasPermission(CommandSender sender, String permission) {
        if (!sender.hasPermission(permission))
            return errorOccurred(sender, PlayerManager.getInstance().getConfigManager().getNoPermMsg());
        return true;
    }
}
