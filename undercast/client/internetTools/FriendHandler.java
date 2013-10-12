package undercast.client.internetTools;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.minecraft.src.Minecraft;
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
        if(message.contains("joined the game") || message.contains("left the game")){
            return true;
        }
        if (isListening) {
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
                        String server;
                        if (message.contains(",")) {
                            if (message.contains(", EU")) {
                                server = message.substring(message.lastIndexOf(" is online on ") + 14, message.indexOf(",")) + UndercastData.locationNames[1];
                            } else {
                                server = message.substring(message.lastIndexOf(" is online on ") + 14, message.indexOf(",")) + UndercastData.locationNames[0];
                            }
                        } else {
                            server = message.substring(message.lastIndexOf(" is online on ") + 14) + (UndercastData.isEU ? UndercastData.locationNames[1] : UndercastData.locationNames[0]);
                        }
                        UndercastData.friends.put(friend, server);
                    } else {
                        UndercastData.friends.put(friend, "offline");
                    }
                }
            } else if(message.contains(" is online")) {
                String friend = message.split(" ")[0].replace("*", "");
                if (!UndercastData.friends.containsKey(friend)) {
                    UndercastData.friends.put(friend, UndercastData.server + (UndercastData.isEU ? UndercastData.locationNames[1] : UndercastData.locationNames[0]));
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
        boolean flag = message.contains("------------  Your Friends") || (message.contains(" is online on ") || (message.contains(" seen ") && message.contains(" on ")));
        return !(isListening && flag);
    }
    private static void removeChatMessage(){
        if(!UndercastData.forgeDetected) {
            try {
                List chatLines;
                // get the lines
                chatLines = (List)ModLoader.getPrivateValue(GuiNewChat.class, Minecraft.getMinecraft().ingameGUI.getChatGUI(), 3);
                // remove the message (20 most recent chat messages are enough)
                for(int c = 0; c < 20; c++) {
                    ChatLine line = (ChatLine)chatLines.get(c);
                    if(StringUtils.stripControlCodes(line.getChatLineString()).contains("Your Friends") || StringUtils.stripControlCodes(line.getChatLineString()).contains(" is online") || (StringUtils.stripControlCodes(line.getChatLineString()).contains(" seen ") && (StringUtils.stripControlCodes(line.getChatLineString()).contains(" on ")))) {
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
}