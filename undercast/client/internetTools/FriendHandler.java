package undercast.client.internetTools;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.minecraft.client.Minecraft;
import net.minecraft.src.ChatLine;
import net.minecraft.src.GuiNewChat;
import net.minecraft.src.ModLoader;
import net.minecraft.src.StringUtils;
import undercast.client.UndercastData;

/**
 *
 * @author Flv92
 */
public class FriendHandler {

    public int pages = -1;
    public int currentPage = -1;
    public boolean isListening = false;

    public FriendHandler() {
    }

    /**
     *
     * @param message
     * @return true to display the chat message
     */
    public boolean handleMessage(String message) {
        if (isListening) {
            if(!message.contains("Your Friends") && !(message.contains(" is online on ") || (message.contains(" seen ") && message.contains(" on ")))){
                isListening = false;
            }
            //Getting the number of pages if it is not already known
            if (pages == -1 && message.contains("------------  Your Friends")) {
                int lengthOfNumber = message.substring(message.lastIndexOf(" of ") + 4, message.lastIndexOf(")")).length();
                try {
                    pages = Integer.parseInt(message.substring(message.lastIndexOf(" of ") + 4, message.lastIndexOf(" of ") + 4 + lengthOfNumber));
                } catch (Exception e) {
                }
            }
            if (message.contains("------------  Your Friends")) {
                int lengthOfNumber = message.substring(message.lastIndexOf("(Page ") + 6, message.lastIndexOf(" of ")).length();
                try {
                    currentPage = Integer.parseInt(message.substring(message.lastIndexOf("(Page ") + 6, message.lastIndexOf("(Page ") + 6 + lengthOfNumber));
                } catch (Exception e) {
                }
            }
            if (message.contains(" is online on ") || (message.contains(" seen ") && message.contains(" on "))) {
                String friend = message.split(" ")[0];
                if (!UndercastData.friends.containsKey(friend)) {
                    if (message.contains(" is online on ")) {
                        UndercastData.friends.put(friend, message.substring(message.lastIndexOf(" is online on ") + 14));
                    } else {
                        UndercastData.friends.put(friend, "offline");
                    }
                }
            }
            if (UndercastData.friends.size() % 8 == 0 && currentPage < pages && !UndercastData.friends.isEmpty() && !message.contains("Your Friends")) {
                Thread t1 = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            isListening = false;
                            Thread.sleep(2000);
                            int nextPage = currentPage + 1;
                            isListening = true;
                            Minecraft.getMinecraft().thePlayer.sendChatMessage("/fr " + nextPage);
                        } catch (InterruptedException ex) {
                        }

                    }
                });
                t1.start();
            }
            if (currentPage == pages) {
                Thread t1 = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(1000);
                            isListening = false;
                        } catch (InterruptedException ex) {
                        }

                    }
                });
                t1.start();
            }

        }
        if(isListening) {
            removeChatMessage();
        }
        return !isListening;
    }
    private static void removeChatMessage(){
        try {
            List chatLines;
            // get the lines
            chatLines = (List)ModLoader.getPrivateValue(GuiNewChat.class, Minecraft.getMinecraft().ingameGUI.getChatGUI(), 3);
            // remove the message (20 most recent chat messages are enough)
            for(int c = 0; c < 20; c++) {
                ChatLine line = (ChatLine)chatLines.get(c);
                if(StringUtils.stripControlCodes(line.getChatLineString()).contains("Your Friends") || StringUtils.stripControlCodes(line.getChatLineString()).contains(" is online on ") || (StringUtils.stripControlCodes(line.getChatLineString()).contains(" seen ") && (StringUtils.stripControlCodes(line.getChatLineString()).contains(" on ")))) {
                    chatLines.remove(c);
                    break;
                }
            }
            // set them back
            ModLoader.setPrivateValue(GuiNewChat.class, Minecraft.getMinecraft().ingameGUI.getChatGUI(), 3, chatLines);
        } catch (Exception e) {
            System.out.println("[UndercastMod]: Getting a private value (chatLines) failed");
            System.out.println("[UndercastMod]: ERROR: " + e.toString());
        }
    }
}