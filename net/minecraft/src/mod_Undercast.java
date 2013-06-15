package net.minecraft.src;
//You may not release this source under any condition, it must be linked to this page
//You may recompile and publish as long as skipperguy12 and Guru_Fraser are given credit
//You may not claim this to be your own
//You may not remove these comments

import java.awt.image.BufferedImage;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import net.minecraft.client.Minecraft;
import undercast.client.PlayTimeCounterThread;
import undercast.client.UndercastChatHandler;
import undercast.client.UndercastConfig;
import undercast.client.UndercastCustomMethods;
import undercast.client.UndercastData;
import undercast.client.UndercastData.ServerType;
import undercast.client.UndercastData.Teams;
import undercast.client.UndercastMenuButton;
import undercast.client.achievements.UndercastGuiAchievement;
import undercast.client.achievements.UndercastKillsHandler;
import undercast.client.controls.UndercastControls;
import undercast.client.internetTools.ServersCommandParser;
import undercast.client.internetTools.FriendHandler;
import undercast.client.server.UndercastServerGUI;
import undercast.client.settings.SettingsGUI;
import undercast.client.settings.UndercastGuiConfigButton;
import undercast.client.update.UndercastUpdaterThread;

public class mod_Undercast extends BaseMod {
    public final static String MOD_VERSION = "1.5.9";
    public final static String MOD_NAME = "UndercastMod";
    protected String username = "Not_Found";
    protected Minecraft mc = Minecraft.getMinecraft();
    public static UndercastConfig CONFIG;
    public static boolean brightActive;
    public float brightLevel = (float) 20.0D;
    public float defaultLevel = mc.gameSettings.gammaSetting;
    private UndercastControls undercastControls;
    private PlayTimeCounterThread playTimeCounter;
    private UndercastKillsHandler achievementHandler;
    public static FriendHandler friendHandler;
    private int buttonListSize;
    private Integer buttonListSizeOfGuiOptions;

    @Override
    public String getVersion() {
        return MOD_VERSION;
    }

    @Override
    public String getName() {
        return MOD_NAME;
    }

    @Override
    public void load() {
        // Custom Config
        CONFIG = new UndercastConfig();

        //main hooks
        ModLoader.setInGUIHook(this, true, false);
        ModLoader.setInGameHook(this, true, false);

        ModLoader.addLocalization("undercast.gui", "Toggle Overcast Network mod gui");
        ModLoader.addLocalization("undercast.inGameGui", "Switch to an Overcast Network server");
        ModLoader.addLocalization("undercast.fullBright", "Toggle fullbright");
        ModLoader.addLocalization("undercast.settings", "Show Undercast mod settings");

        //load variables defaults
        new UndercastData();

        achievementHandler = new UndercastKillsHandler();
        UndercastGuiAchievement gui = new UndercastGuiAchievement(Minecraft.getMinecraft());
        Minecraft.getMinecraft().guiAchievement = gui;
        friendHandler = new FriendHandler();

        //check for update
        new UndercastUpdaterThread();

        //load the new controls menu
        undercastControls = new UndercastControls();

        //hook keybinds
        ModLoader.registerKey(this, UndercastData.keybind, false);
        ModLoader.registerKey(this, UndercastData.keybind2, false);
        ModLoader.registerKey(this, UndercastData.keybind3, false);
        ModLoader.registerKey(this, UndercastData.keybind4, false);
    }

    /**
     * On client chat event this is called.
     * Send all the info to the AresChatHandler
     * NOTE: only sends none global ares messages
     */
    public void clientChat(String var1) {
        try {
            Minecraft mc = ModLoader.getMinecraftInstance();
            EntityPlayer player = mc.thePlayer;
            username = mc.thePlayer.username;
            String message = StringUtils.stripControlCodes(var1);
            // stop global msg and team chat and whispered messages to go through
            if(!message.startsWith("<") && !message.startsWith("[Team]") && !message.startsWith("(From ") && !message.startsWith("(To ")&& UndercastData.isOC) {
                new UndercastChatHandler(message, username, player, var1);
                if(CONFIG.parseMatchState) {
                    ServersCommandParser.handleChatMessage(message, var1);
                }
                if(CONFIG.showAchievements) {
                    achievementHandler.handleMessage(message, username, player, var1);
                }
            }
            if(UndercastConfig.showFriends){
                friendHandler.handleMessage(message);
            }
        } catch(Exception e) {
        }
    }

    /**
     * On game tick this is called.
     * Draws the gui ingame based on the config file
     */
    public boolean onTickInGame(float time, Minecraft mc) {
        //if the game over screen is active then you have died
        //if it is the first time it is active count a death
        //if it is not don't do anything
        if (mc.currentScreen instanceof GuiGameOver) {
            //get the title screen button
            GuiButton titleScreen = (GuiButton) mc.currentScreen.buttonList.get(1);
            //if the button is enabled and the user wants to disable it
            if (titleScreen.enabled && CONFIG.toggleTitleScreenButton) {
                titleScreen.enabled = false;
                mc.currentScreen.buttonList.set(1, titleScreen);
                mc.currentScreen.updateScreen();
            }
            GuiGameOver gameOverScreen = (GuiGameOver) mc.currentScreen;
            gameOverScreen.drawCenteredString(gameOverScreen.fontRenderer, "Killstreak" + ": " + EnumChatFormatting.YELLOW + (int)UndercastData.getPreviousKillstreak(), gameOverScreen.width / 2, 110, 16777215);
        }

        //get debug info for the fps
        String fps = mc.debug.split(",")[0];
        int height = CONFIG.x;
        int width = CONFIG.y;
        boolean isInGameGuiEmpty = !this.mc.gameSettings.showDebugInfo && !this.mc.gameSettings.keyBindPlayerList.pressed;
        //if the gui is enabled display
        //if chat is open and config says yes then show gui
        if (UndercastData.guiShowing && (mc.inGameHasFocus || CONFIG.showGuiChat && mc.currentScreen instanceof GuiChat) && isInGameGuiEmpty) {
            //show fps
            if (CONFIG.showFPS) {
                mc.fontRenderer.drawStringWithShadow(fps, width, height, 0xffff);
                height += 8;
            }
        }
        //if on Ares server then display this info.
        //if chat is open and config says yes then show gui
        if (UndercastData.isPlayingOvercast() && UndercastData.guiShowing && (mc.inGameHasFocus || CONFIG.showGuiChat && mc.currentScreen instanceof GuiChat) && isInGameGuiEmpty) {
            // Server display
            if (CONFIG.showServer) {
                mc.fontRenderer.drawStringWithShadow("Server: \u00A76" + UndercastData.getServer(), width, height, 16777215);
                height += 8;
            }

            // Team display (based on color)
            if (CONFIG.showTeam && !UndercastData.isLobby) {
                mc.fontRenderer.drawStringWithShadow("Team: " + UndercastData.getTeam(), width, height, getTeamColors());
                height += 8;
            }
         // Class display (Ghost Squadron only)
            if (CONFIG.showGSClass && UndercastData.currentServerType == ServerType.ghostsquadron && !UndercastData.isLobby) {
                mc.fontRenderer.drawStringWithShadow("Class: " + UndercastData.currentGSClass, width, height, 2446535);
                height += 8;
            }
            // Friend display:
            if (CONFIG.showFriends) {
                mc.fontRenderer.drawStringWithShadow("Friends Online: \u00A73" + UndercastCustomMethods.getOnlineFriends(), width, height, 16777215);
                height += 8;
            }
            // Playing Time display:
            if (CONFIG.showPlayingTime) {
                mc.fontRenderer.drawStringWithShadow(UndercastCustomMethods.getPlayingTimeString(), width, height, 16777215);
                height += 8;
            }
            // Match Time display:
            if (CONFIG.showMatchTime && !UndercastData.isLobby) {
                mc.fontRenderer.drawStringWithShadow(UndercastCustomMethods.getMatchTimeString(), width, height, 16777215);
                height += 8;
            }
            // Map fetcher:
            if (CONFIG.showMap && !UndercastData.isLobby) {
                if (UndercastData.getMap() != null) {
                    mc.fontRenderer.drawStringWithShadow("Current Map: \u00A7d" + UndercastData.getMap(), width, height, 16777215);
                    height += 8;
                } else {
                    UndercastData.setMap("Fetching...");
                    mc.fontRenderer.drawStringWithShadow("Current Map: \u00A78" + UndercastData.getMap(), width, height, 16777215);
                    height += 8;
                }
            }
            // Show next map
            if (CONFIG.showNextMap && !UndercastData.isLobby) {
                if (UndercastData.getNextMap() != null) {
                    mc.fontRenderer.drawStringWithShadow("Next Map: \u00A7d" + UndercastData.getNextMap(), width, height, 16777215);
                    height += 8;
                } else {
                    mc.fontRenderer.drawStringWithShadow("Next Map: \u00A78Loading...", width, height, 16777215);
                    height += 8;
                }
            }
            //Show KD Ratio
            if (CONFIG.showKD && !UndercastData.isLobby) {
                mc.fontRenderer.drawStringWithShadow("K/D: \u00A73" + UndercastCustomMethods.getKD(), width, height, 16777215);
                height += 8;
            }
            //show KK Ratio
            if (CONFIG.showKK && !UndercastData.isLobby) {
                mc.fontRenderer.drawStringWithShadow("K/K: \u00A73" + UndercastCustomMethods.getKK(), width, height, 16777215);
                height += 8;
            }
            //show amount of kills
            if (CONFIG.showKills && !UndercastData.isLobby) {
                mc.fontRenderer.drawStringWithShadow(UndercastCustomMethods.getKillDisplayString(), width, height, 16777215);
                height += 8;
            }
            //show amount of deaths
            if (CONFIG.showDeaths && !UndercastData.isLobby) {
                mc.fontRenderer.drawStringWithShadow(UndercastCustomMethods.getDeathDisplayString(), width, height, 16777215);
                height += 8;
            }
            // Kill Streak display
            if (CONFIG.showStreak && !UndercastData.isLobby) {
                mc.fontRenderer.drawStringWithShadow("Current Killstreak: \u00A75" + (int)UndercastData.getKillstreak() + "\u00A7f/\u00A75" + (int)UndercastData.getLargestKillstreak(), width, height, 16777215);
                height += 8;
            }
            // Score display
            if (UndercastConfig.showScore && !UndercastData.isLobby && UndercastData.score != 0) {
                mc.fontRenderer.drawStringWithShadow("Score: \u00A79" + UndercastData.score, width, height, 16777215);
                height += 8;
            }
        }
        
        //if you not on obs turn it off
        if((UndercastData.team != Teams.Observers && !UndercastData.isGameOver) || !UndercastData.isOC){
            brightActive=false;
            //if full bright is on turn it off
            if(mc.gameSettings.gammaSetting>=brightLevel){
                mc.gameSettings.gammaSetting=defaultLevel;
                if(defaultLevel>=brightLevel){
                    mc.gameSettings.gammaSetting=(float) 0.0D;
                    defaultLevel=(float) 0.0D;
                }
            }
        }
        
        //gui display for obs if you have brightness
        if(UndercastData.isPlayingOvercast() && UndercastData.guiShowing && (mc.inGameHasFocus || CONFIG.showGuiChat && mc.currentScreen instanceof GuiChat) && isInGameGuiEmpty){
            if(brightActive && CONFIG.fullBright && (UndercastData.team == Teams.Observers || UndercastData.isGameOver)){
                mc.fontRenderer.drawStringWithShadow("Full Bright: \u00A72ON", width, height, 16777215);
                 height += 8;
            }else if(!brightActive && CONFIG.fullBright && (UndercastData.team == Teams.Observers || UndercastData.isGameOver)){
                mc.fontRenderer.drawStringWithShadow("Full Bright: \u00A7cOFF", width, height, 16777215);
                 height += 8;
            }
        }
        return true;
    }
    
    public boolean onTickInGUI(float tick, Minecraft mc, GuiScreen screen){
        undercastControls.onTickInGUI(tick, mc, screen);
        this.addOvercastButton();
        // Listen for disconnect, as it isn't properly called
        if(UndercastData.isOC && screen instanceof GuiMainMenu) {
            clientDisconnect(null);
        }
        if(screen instanceof GuiOptions) {
            List customButtonList = new ArrayList();
            customButtonList = screen.buttonList;
            if (this.buttonListSizeOfGuiOptions == null) {
                this.buttonListSizeOfGuiOptions = customButtonList.size();
            }
            if (customButtonList.size() == this.buttonListSizeOfGuiOptions) {
                customButtonList.add(new UndercastGuiConfigButton(301, screen.width / 2 + 5, screen.height / 6 + 60, 150, 20, "Undercast config", screen));
            }
            screen.buttonList = customButtonList;
        }
        return true;
    }

    /**
     * Called on client connect to the server
     * Sets variables if the server is a Ares server
     */
    public void clientConnect(NetClientHandler var1) {
        UndercastData.setTeam(UndercastData.Teams.Observers); 
        //if logging onto a overcast network server, then enable the main mod
        if (var1.getNetManager().getSocketAddress().toString().contains(".oc.tc")) {
            // What happens if logs into project ares
            UndercastData.isOC = true;
            UndercastData.isLobby = true;
            UndercastData.guiShowing = true;
            System.out.println("[UndercastMod] Joined Overcast Network - Mod activated!");
            UndercastData.setTeam(UndercastData.Teams.Observers);
            UndercastData.setServer("Lobby");
            playTimeCounter = new PlayTimeCounterThread();
        } else{
            UndercastData.isOC=false;
        }
        //update notifier
        if(!UndercastData.isUpdate()){
            Thread thread = new Thread() {
                public void run() {
                    try {
                        Thread.sleep(3000);
                        for(int c = 0; c < 10; c++) { // don't wait longer than 10 sec
                            Thread.sleep(1000);
                            if(UndercastUpdaterThread.finished) {
                                break;
                            }
                        }
                    } catch (InterruptedException e) {
                    }
                    mc.thePlayer.addChatMessage("\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-");
                    mc.thePlayer.addChatMessage("[UndercastMod]: A New Version of the Undercast Mod is avaliable");
                    mc.thePlayer.addChatMessage("[UndercastMod]: Link: \u00A74"+UndercastData.updateLink);
                    mc.thePlayer.addChatMessage("\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-");
                }
            };
            thread.start();
        }
    }

    /**
     * Called when client disconnects.
     * Resets all the values
     */
    public void clientDisconnect(NetClientHandler handler) {
        UndercastData.isOC = false;
        UndercastData.guiShowing = false;
        UndercastData.setTeam(UndercastData.Teams.Observers);
        UndercastData.resetKills();
        UndercastData.resetKilled();
        UndercastData.resetDeaths();
        UndercastData.resetKillstreak();
        UndercastData.resetLargestKillstreak();
        UndercastData.setMap("Attempting to fetch map...");
        if(mc.gameSettings.gammaSetting>=brightLevel){
            brightActive=false;
            mc.gameSettings.gammaSetting=defaultLevel;
        }
        // for the next connect
        UndercastData.welcomeMessageExpected = false;
    }

    

    /**
     * Called when a key is pressed.
     * Used to activate the gui ect.
     */
    public void keyboardEvent(KeyBinding keybinding) {
        if (mc.inGameHasFocus) {
            if (keybinding == UndercastData.keybind) {
                UndercastData.guiShowing = !UndercastData.guiShowing;
            } else if (keybinding == UndercastData.keybind2) {
                ModLoader.openGUI(mc.thePlayer, new UndercastServerGUI(true));
            }
            //if you are an obs;have the config to true; toggle fullbright and play sound
            else if(UndercastData.isPlayingOvercast() && keybinding == UndercastData.keybind3 && (UndercastData.team == Teams.Observers || UndercastData.isGameOver) && CONFIG.fullBright){
                if(mc.inGameHasFocus){
                    brightActive = !brightActive;
                    if(brightActive)
                        mc.gameSettings.gammaSetting = brightLevel;
                    else
                        mc.gameSettings.gammaSetting = defaultLevel;
                    mc.sndManager.playSoundFX("random.click", 0.5F, 1.0F);
                }
            } else if (keybinding == UndercastData.keybind4) {
                ModLoader.openGUI(mc.thePlayer, new SettingsGUI(null));
            }
        } else if (keybinding == UndercastData.keybind2 && this.mc.currentScreen instanceof UndercastServerGUI) {
            ((UndercastServerGUI)this.mc.currentScreen).closeGui();
        }
    }

    /**
     * Returns the team color hex based on the team you are on
     *
     * @return hex value of team color
     */
    public int getTeamColors() {
        switch(UndercastData.getTeam()) {
        case Red:
        case Cot:
            return 0x990000;
        case Blue:
        case Bot:
            return 0x0033FF;
        case Purple:
            return 0x9933CC;
        case Cyan:
            return 0x00AAAA;
        case Yellow:
            return 0xFFFF00;
        case Lime:
        case Green:
            return 0x55FF55;
        case Orange:
            return 0xFF9900;
        case Observers:
            return 0x00FFFF;
        default:
            return 0x606060;
        }
    }

    /**
     * Adds the Overcast Network button
     */
    private void addOvercastButton() {
        // if the main menu is active then add a button
        if (mc.currentScreen instanceof GuiMainMenu && mc.currentScreen.buttonList.size() > 0 && CONFIG.showGuiMulti) {
            //buttonListSize == 0 means, it hasn't been added once and the second condition checks if the main menu has refreshed
            if(buttonListSize == 0 || buttonListSize > mc.currentScreen.buttonList.size()) {
                //edit the current multiplayer button
                GuiButton multi = ((GuiButton) mc.currentScreen.buttonList.get(1));
                multi.width = (multi.width / 2) - 1;
                mc.currentScreen.buttonList.set(1, multi);
                //get values
                int y = multi.yPosition;
                int x = multi.xPosition + multi.width + 2;
                int height = multi.height;
                int width = multi.width;
                //add the custom ares button
                UndercastMenuButton menuButton = new UndercastMenuButton(-1, x, y, width, height, "Overcast Network", "Serverlist with Overcast Network Servers");
                mc.currentScreen.buttonList.add(menuButton);
                mc.currentScreen.updateScreen();
                buttonListSize = mc.currentScreen.buttonList.size();
            }
        }
    }
}
