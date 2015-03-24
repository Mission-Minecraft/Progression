package joshie.crafting.commands;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import joshie.crafting.api.ICraftingCommand;
import joshie.crafting.api.ICraftingCommandHandler;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.event.CommandEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class CommandManager extends CommandBase implements ICraftingCommandHandler {
    public static final ICraftingCommandHandler INSTANCE = new CommandManager();
    private HashMap<String, ICraftingCommand> commands = new HashMap();

    @Override
    public void registerCommand(ICraftingCommand command) {
        commands.put(command.getCommandName(), command);
    }

    @Override
    public Map getCommands() {
        return commands;
    }

    @Override
    public List getPossibleCommands(ICommandSender sender) {
        ArrayList list = new ArrayList();
        for (ICraftingCommand command: commands.values()) {
            if (sender.canCommandSenderUseCommand(command.getPermissionLevel().ordinal(), command.getCommandName())) {
                list.add(command);
            }
        }
        
        return list;
    }

    @Override
    public String getCommandName() {
        return "craftcontrol";
    }

    @SubscribeEvent
    public void onCommandSend(CommandEvent event) {
        if (event.command == this && event.parameters.length > 0) {
            String commandName = event.parameters[0];
            ICraftingCommand command = commands.get(commandName);
            if (command == null || !event.sender.canCommandSenderUseCommand(command.getPermissionLevel().ordinal(), commandName)) {
                event.setCanceled(true);
            } else {
                processCommand(event, command);
            }
        }
    }

    //Attempt to process the command, throw wrong usage otherwise
    private void processCommand(CommandEvent event, ICraftingCommand command) {
        String[] args = new String[event.parameters.length - 1];
        System.arraycopy(event.parameters, 1, args, 0, args.length);
        if (!command.processCommand(event.sender, args)) {
            throwError(event.sender, command);
        }
    }
    
    static void throwError(ICommandSender sender, ICraftingCommand command) {
        ChatComponentTranslation chatcomponenttranslation1 = new ChatComponentTranslation(getUsage(command), new Object[0]);
        chatcomponenttranslation1.getChatStyle().setColor(EnumChatFormatting.RED);
        sender.addChatMessage(chatcomponenttranslation1);
    }
    
    static String getUsage(ICraftingCommand command) {
        return "/" + "craftcontrol" + " " + command.getCommandName() + " " + command.getUsage();
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] parameters) {
        return new ArrayList();
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/" + getCommandName() + " help";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] values) {
        if(values.length == 0) {
            throwError(sender, new CommandHelp());
        }
    } //Do sweet nothing

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }
}