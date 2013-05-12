package undercast.client;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.IChatListener;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.packet.NetHandler;
import net.minecraft.network.packet.Packet3Chat;
import net.minecraft.util.StringUtils;

/**
 * @author Flv92
 */
public class ChatListener implements IChatListener {

    public ChatListener() {
    }

    @Override
    public Packet3Chat serverChat(NetHandler handler, Packet3Chat message) {
        return message;
    }

    @Override
    public Packet3Chat clientChat(NetHandler handler, Packet3Chat packet) {
        try {
            Minecraft mc = FMLClientHandler.instance().getClient();
            EntityPlayer player = mc.thePlayer;
            UndercastModClass.getInstance().username = mc.thePlayer.username;
            String message = StringUtils.stripControlCodes(packet.message);
            // stop global msg to go through
            if (!message.startsWith("<") && UndercastData.isPlayingOvercastNetwork()) {
                addLineToChatLines(message);
                new UndercastChatHandler(message, UndercastModClass.getInstance().username, player);
                if (UndercastConfig.showAchievements) {
                    new UndercastKillsHandler(message, UndercastModClass.getInstance().username, player);
                }
            }
        } catch (Exception e) {
        }
        packet.message = UndercastChatHandler.handleTip(packet);
        return packet;
    }

    public void addLineToChatLines(String line) {
        System.arraycopy(UndercastModClass.lastChatLines, 0, UndercastModClass.lastChatLines, 1, 99);
        UndercastModClass.lastChatLines[0] = line;
    }
}
