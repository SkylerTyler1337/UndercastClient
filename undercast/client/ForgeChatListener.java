package undercast.client;

import net.minecraft.src.NetHandler;
import net.minecraft.src.Packet3Chat;
import cpw.mods.fml.common.network.IChatListener;

public class ForgeChatListener implements IChatListener {

    @Override
    public Packet3Chat serverChat(NetHandler handler, Packet3Chat message) {
        return message;
    }

    @Override
    public Packet3Chat clientChat(NetHandler handler, Packet3Chat message) {
        if (UndercastCustomMethods.handleChatMessage(message.message)) {
            message.message = null;
        }
        return message;
    }

}
