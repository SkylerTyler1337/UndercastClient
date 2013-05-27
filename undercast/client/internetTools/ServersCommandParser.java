package undercast.client.internetTools;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.src.ChatLine;
import net.minecraft.src.GuiNewChat;
import net.minecraft.src.ModLoader;
import net.minecraft.src.StringUtils;
import undercast.client.UndercastCustomMethods;
import undercast.client.UndercastData;
import undercast.client.UndercastData.MatchState;

public class ServersCommandParser {
    private static boolean isListening = false;
    //is set to true if the mod casts /servers in order to delete the messages
    public static boolean castedByMod = false;
    public static boolean nextCastedByMod = true;
    public static int pages = 0;
    
    public static void handleChatMessage(String message, String unstripedMessage) {
        if(isListening) {
            // check if the message belongs to the command
            boolean commandEnded = false;
            if (!message.contains("Online: ") && !message.contains("--------- Overcast Network Servers")) {
                isListening = false;
                castedByMod = nextCastedByMod;
                UndercastCustomMethods.sortServers();
                commandEnded = true;
            }

            // don't try to handle it if the command ended
            if(!commandEnded) {
                //our only interest is the MatchState
                if(message.contains("Current Map: ")) {
                    String name;
                    String map;
                    char matchStatusColor;
                    MatchState state;

                    name = message.substring(0, message.indexOf(": "));
                    map = message.substring(message.indexOf("Current Map: ") + 13);
                    matchStatusColor = unstripedMessage.charAt(unstripedMessage.indexOf("Current Map§f: ") + 16);

                    //c == red
                    //e == yellow
                    //a = green
                    switch(matchStatusColor) {
                    case 'a':
                        state = MatchState.Starting;
                        break;
                    case 'c':
                        state = MatchState.Finished;
                        break;
                    case 'e':
                        state = MatchState.Started;
                        break;
                    default:
                        state = MatchState.Unknown;
                    }

                    //insert the data
                    for(int c = 0; c < UndercastData.serverInformation.length; c++) {
                        if(!(UndercastData.serverInformation[c].name == null)) {
                            if(UndercastData.serverInformation[c].name.equals(name)) {
                                UndercastData.serverInformation[c].currentMap = map;
                                UndercastData.serverInformation[c].matchState = state;
                            }
                        }
                    }
                }
                // remove the message
                if(castedByMod) {
                    try {
                        List chatLines;
                        // get the lines
                        chatLines = (List)ModLoader.getPrivateValue(GuiNewChat.class, Minecraft.getMinecraft().ingameGUI.getChatGUI(), 3);
                        //remove the message (20 most recent chat messages are enough)
                        for(int c = 0; c < 20; c++) {
                            ChatLine line = (ChatLine)chatLines.get(c);
                            if(StringUtils.stripControlCodes(line.getChatLineString()).contains("Online: ") || StringUtils.stripControlCodes(line.getChatLineString()).contains("--------- Overcast Network Servers")) {
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

        if(!isListening && message.contains("--------- Overcast Network Servers (1 of ")) {
            // get the page count
            try {
                pages = Integer.parseInt(message.substring(message.indexOf("of ") + 3, message.indexOf("of ") + 4));
            } catch (Exception e) {
                pages = 3;
            }

            if(castedByMod) {
                // get the other pages
                for(int i = 2; i <= pages; i++) {
                                    Minecraft.getMinecraft().thePlayer.sendChatMessage("/servers " + i);
                }
                try {
                    List chatLines;
                    // get the lines
                    chatLines = (List)ModLoader.getPrivateValue(GuiNewChat.class, Minecraft.getMinecraft().ingameGUI.getChatGUI(), 3);
                    for(int c = 0; c < 20; c++) {
                        ChatLine line = (ChatLine)chatLines.get(c);
                        if(StringUtils.stripControlCodes(line.getChatLineString()).equals("--------- Overcast Network Servers (1 of 3) ---------")) {
                            chatLines.remove(c);
                            break;
                        }
                    }
                    ModLoader.setPrivateValue(GuiNewChat.class, Minecraft.getMinecraft().ingameGUI.getChatGUI(), 3, chatLines);
                } catch (Exception e) {
                    System.out.println("[UndercastMod]: Getting a private value (chatLines) failed");
                    System.out.println("[UndercastMod]: ERROR: " + e.toString());
                }
                isListening = true;
            }
        }
    }

    public static void castByMod() {
        if(!isListening) {
            castedByMod = true;
        } else {
            nextCastedByMod = true;
        }
    }
}
