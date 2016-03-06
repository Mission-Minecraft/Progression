package joshie.progression.network;

import joshie.progression.handlers.RemappingHandler;
import joshie.progression.helpers.ClientHelper;
import joshie.progression.helpers.PlayerHelper;
import joshie.progression.json.Options;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class PacketReload extends PacketAction implements IMessageHandler<PacketReload, IMessage> {
    @Override
    public IMessage onMessage(PacketReload message, MessageContext ctx) {
        PacketReload.handle();
        return null;
    }
    
    public static void handle() {
        if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
            ClientHelper.getPlayer().addChatComponentMessage(new ChatComponentText("Progression data was reloaded."));
        } else {
            if (Options.editor) {
                //Perform a reset of all the data serverside
                RemappingHandler.reloadServerData();
                for (EntityPlayer player: PlayerHelper.getAllPlayers()) {
                    RemappingHandler.onPlayerConnect((EntityPlayerMP) player);
                }
            }
        }
    }
}
