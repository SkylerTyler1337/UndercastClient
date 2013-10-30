package undercast.client.internetTools;

import java.util.List;

import net.minecraft.src.Minecraft;
import net.minecraft.src.ChatLine;
import net.minecraft.src.GuiNewChat;
import net.minecraft.src.ModLoader;
import net.minecraft.src.StringUtils;
import undercast.client.UndercastCustomMethods;
import undercast.client.UndercastData;
import undercast.client.UndercastData.MatchState;
import undercast.client.UndercastData.ServerLocation;

public class ServersCommandParser {
    private static boolean isListening = false;
    //is set to true if the mod casts /servers in order to delete the messages
    private static boolean castedByMod = false;
    private static boolean nextCastedByMod = false;
    
    private static int pages = 0;
    private static int currentPage = 0;
    
    private static final String PAGE_TITLE = "-------- Overcast Network Servers";
    private static final String FIRST_PAGE_TITLE = "-------- Overcast Network Servers (1 ";
    private static final String MESSAGE_CHARACTERISTIC = "Online: "; // string which all non title messges have in comon
    private static final String MAP_SEPARATOR = "Current Map: ";
    private static final String MAP_SEPARATOR_UNSTRIPED = "Current Map§f: ";
    
    public static boolean handleChatMessage(String message, String unstripedMessage) {
        boolean returnValue = false;
        if (isListening) {
            // check if the message belongs to the command
            boolean commandEnded = false;
            if ((!message.contains(MESSAGE_CHARACTERISTIC) && !message.contains(PAGE_TITLE)) || message.contains(FIRST_PAGE_TITLE)) {
                isListening = false;
                if(currentPage == pages) {
                    castedByMod = nextCastedByMod;
                    nextCastedByMod = false;
                    UndercastCustomMethods.sortAndFilterServers();
                }
                commandEnded = true;
            }

            // don't try to handle it if the command ended
            if (!commandEnded) {
                if (message.contains(PAGE_TITLE)) {
                    try {
                        currentPage = Integer.parseInt(message.substring(message.indexOf(" of ") - 2, message.indexOf(" of ")).replace("(", ""));
                    } catch (Exception e) {
                        currentPage = 1;
                    }
                }
                // our only interest is the MatchState
                if (message.contains(MAP_SEPARATOR)) {
                    String name;
                    String map;
                    char matchStatusColor;
                    MatchState state;

                    name = message.substring(0, message.indexOf(": "));
                    map = message.substring(message.indexOf(MAP_SEPARATOR) + 13);
                    matchStatusColor = unstripedMessage.charAt(unstripedMessage.indexOf(MAP_SEPARATOR_UNSTRIPED) + 16);

                    // c == red
                    // e == yellow
                    // a = green
                    // f = white
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
                    case 'f':
                        state = MatchState.Waiting;
                        break;
                    default:
                        state = MatchState.Unknown;
                    }

                    // insert the data
                    for (int c = 0; c < UndercastData.serverInformation.length; c++) {
                        if(!(UndercastData.serverInformation[c].name == null)) {
                            if(UndercastData.serverInformation[c].name.equals(name) && ((!UndercastData.isEU && UndercastData.serverInformation[c].location == ServerLocation.US) || (UndercastData.isEU && UndercastData.serverInformation[c].location == ServerLocation.EU))) {
                                UndercastData.serverInformation[c].currentMap = map;
                                UndercastData.serverInformation[c].matchState = state;
                                break;
                            }
                        }
                    }
                }
                removeChatMessage();
                returnValue = true;
            }
        }

        if (!isListening && message.contains(PAGE_TITLE)) {
            // get the page count
            try {
                pages = Integer.parseInt(message.substring(message.indexOf("of ") + 3, message.indexOf("of ") + 5).replace(")", ""));
            } catch (Exception e) {
                pages = 10;
            }

            try {
                currentPage = Integer.parseInt(message.substring(message.indexOf(" of ") - 2, message.indexOf(" of ")).replace("(", ""));
            } catch (Exception e) {
                currentPage = 1;
            }

            if (castedByMod) {
                if(message.contains(FIRST_PAGE_TITLE)) {
                    for (int c = 1; c < (UndercastData.isEU ? UndercastData.parsedPagesEU.length : UndercastData.parsedPagesUS.length); c++) {
                        int page = UndercastData.isEU ? UndercastData.parsedPagesEU[c] : UndercastData.parsedPagesUS[c];
                        if(page < 0) {
                            page = pages + (page + 1); // + 1 is for not removing anything from pages (keep in mind that I'm adding a negative value)
                        }
                        Minecraft.getMinecraft().thePlayer.sendChatMessage("/servers " + page);
                    }
                }
                isListening = true;
                removeChatMessage();
                returnValue = true;
            }
        }
        return returnValue;
    }

    public static void castByMod() {
        if (!isListening) {
            castedByMod = true;
        } else {
            nextCastedByMod = true;
        }
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
                    if(StringUtils.stripControlCodes(line.getChatLineString()).contains(PAGE_TITLE) || StringUtils.stripControlCodes(line.getChatLineString()).contains(MESSAGE_CHARACTERISTIC)) {
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
