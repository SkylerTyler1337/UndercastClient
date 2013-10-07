package undercast.client;

import net.minecraft.src.ModLoader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;


public class UndercastConfig {
    private static Properties defaults = new Properties();
    private static String CONFIG_PATH;
    private Properties config;
    private static final String FILE_NAME = "UndercastMod.cfg";

    // update this value to change the config version.
    private static int version = 13;

    // main variables
    public static boolean showFPS;
    public static boolean showKills;
    public static boolean showDeaths;
    public static boolean showKilled;
    public static boolean showServer;
    public static boolean showTeam;
    public static boolean showKD;
    public static boolean showKK;
    public static boolean showFriends;
    public static boolean showMap;
    public static boolean showNextMap;
    public static boolean showStreak;
    public static boolean showGuiChat;
    public static boolean showGuiMulti;
    public static boolean showPlayingTime;
    public static boolean showMatchTime;
    public static boolean showMatchTimeSeconds;
    public static boolean showGSClass;
    public static boolean showScore;
    public static boolean showTotalKills;
    public static int x;
    public static int y;
    public static boolean toggleTitleScreenButton;
    public static boolean filterTips;
    public static boolean fullBright;
    public static boolean matchOnServerJoin;
    public static boolean enableButtonTooltips;
    public static boolean showAchievements;
    public static boolean showKillAchievements;
    public static boolean showDeathAchievements;
    public static boolean showFirstBloodAchievement;
    public static boolean showLastKillAchievement;
    public static boolean displaySpecialKillMessages;
    public static boolean displaySpecialObjectives;
    public static boolean showRevengeAchievement;
    public static boolean parseMatchState;
    public static boolean realtimeStats;
    public static int lastUsedFilter;
    public static int lastUsedLocation;
    public static String keyGui;
    public static String keyServerList;
    public static String keyFullBright;
    public static String keySettings;
    public static boolean lessObstructive;
    public static String ignoreVersionUpdateMessage;
    public static boolean achievementAnimation;
    public static double achievementAnimationDuration;
    public static boolean displaySkinBorder;
    public static int configVersion;

    /**
     * Default values created when class is first referenced
     */
    static {
        try {
            CONFIG_PATH = ModLoader.getMinecraftInstance().mcDataDir.getCanonicalPath() + File.separatorChar + "config" + File.separatorChar + "UndercastClient" + File.separatorChar;
        } catch(Exception e) {
            System.out.println("[UndercastMod]: Failed to get config path.");
        }
        defaults.setProperty("showFPS", "true");
        defaults.setProperty("showKills", "true");
        defaults.setProperty("showDeaths", "true");
        defaults.setProperty("showKilled", "true");
        defaults.setProperty("showServer", "true");
        defaults.setProperty("showTeam", "true");
        defaults.setProperty("showKD", "true");
        defaults.setProperty("showKK", "true");
        defaults.setProperty("showFriends", "false");
        defaults.setProperty("showMap", "true");
        defaults.setProperty("showNextMap", "true");
        defaults.setProperty("showStreak", "true");
        defaults.setProperty("showGuiChat", "true");
        defaults.setProperty("showGuiMulti", "true");
        defaults.setProperty("showPlayingTime", "false");
        defaults.setProperty("showMatchTime", "true");
        defaults.setProperty("showMatchTimeSeconds", "true");
        defaults.setProperty("showGSClass", "true");
        defaults.setProperty("showScore", "true");
        defaults.setProperty("showTotalKills", "true");
        defaults.setProperty("X", "2");
        defaults.setProperty("Y", "2");
        defaults.setProperty("toggleTitleScreenButton", "true");
        defaults.setProperty("filterTips", "true");
        defaults.setProperty("fullBright", "true");
        defaults.setProperty("matchOnServerJoin", "false");
        defaults.setProperty("enableButtonTooltips", "true");
        defaults.setProperty("showAchievements", "false");
        defaults.setProperty("showKillAchievements", "true");
        defaults.setProperty("showDeathAchievements", "true");
        defaults.setProperty("showFirstBloodAchievement", "false");
        defaults.setProperty("showLastKillAchievement", "false");
        defaults.setProperty("displaySpecialKillMessages", "true");
        defaults.setProperty("displaySpecialObjectives", "true");
        defaults.setProperty("showRevengeAchievement", "true");
        defaults.setProperty("parseMatchState", "true");
        defaults.setProperty("lastUsedFilter", "0");
        defaults.setProperty("lastUsedLocation", "0");
        defaults.setProperty("realtimeStats", "true");
        defaults.setProperty("keyGui", "F6");
        defaults.setProperty("keyServerList", "L");
        defaults.setProperty("keyFullBright", "G");
        defaults.setProperty("keySettings", "P");
        defaults.setProperty("lessObstructive", "false");
        defaults.setProperty("ignoreVersionUpdateMessage", "0.0.0");
        defaults.setProperty("achievementAnimation", "true");
        defaults.setProperty("achievementAnimationDuration", "1.0");
        defaults.setProperty("displaySkinBorder", "true");
        // if the value is missing, it should force an update. Don't change it.
        defaults.setProperty("configVersion", "0");
    }

    public UndercastConfig() {
        System.out.println("[UndercastMod]: Attempting to load/create the configuration.");
        loadConfig();
        loadConfigData();
    }

    /**
     * Attempts to find a config
     * If there is one load it
     * If there is not one create one
     */
    private void loadConfig() {
        config = new Properties(defaults);

        try {
            File cfg = new File(CONFIG_PATH + FILE_NAME);

            if(cfg.exists()) {
                System.out.println("[UndercastMod]: Config file found, loading...");
                config.load(new FileInputStream(CONFIG_PATH + FILE_NAME));
            } else {
                System.out.println("[UndercastMod]: No config file found, creating...");
                createConfig(cfg);
            }
        } catch (Exception e) {
            displayErrorMessage(e.toString());
        }
    }

    /**
     * Creates a config properties of default values
     * Then saves the config to the config location
     *
     * @param cfg config file
     */
    private void createConfig(File cfg) {
        File folder = new File(CONFIG_PATH);
        if(!folder.exists()) {
            System.out.println("[UndercastMod]: No folder found, creating...");
            folder.mkdir();
        }
        try {
            cfg.createNewFile();

            config.setProperty("showFPS", "true");
            config.setProperty("showKills", "true");
            config.setProperty("showDeaths", "true");
            config.setProperty("showKilled", "true");
            config.setProperty("showServer", "true");
            config.setProperty("showTeam", "true");
            config.setProperty("showKD", "true");
            config.setProperty("showKK", "true");
            config.setProperty("showFriends", "false");
            config.setProperty("showMap", "true");
            config.setProperty("showNextMap", "true");
            config.setProperty("showStreak", "true");
            config.setProperty("showGuiChat", "true");
            config.setProperty("showGuiMulti", "true");
            config.setProperty("showPlayingTime", "false");
            config.setProperty("showMatchTime", "true");
            config.setProperty("showMatchTimeSeconds", "true");
            config.setProperty("showGSClass", "true");
            config.setProperty("showScore", "true");
            config.setProperty("showTotalKills", "true");
            config.setProperty("X", "2");
            config.setProperty("Y", "2");
            config.setProperty("toggleTitleScreenButton", "true");
            config.setProperty("filterTips", "true");
            config.setProperty("fullBright", "true");
            config.setProperty("matchOnServerJoin", "false");
            config.setProperty("enableButtonTooltips", "true");
            config.setProperty("showAchievements", "false");
            config.setProperty("showKillAchievements", "true");
            config.setProperty("showDeathAchievements", "true");
            config.setProperty("showFirstBloodAchievement", "false");
            config.setProperty("showLastKillAchievement", "false");
            config.setProperty("displaySpecialKillMessages", "true");
            config.setProperty("displaySpecialObjectives", "true");
            config.setProperty("showRevengeAchievement", "true");
            config.setProperty("parseMatchState", "true");
            config.setProperty("lastUsedFilter", "0");
            config.setProperty("lastUsedLocation", "0");
            config.setProperty("realtimeStats", "true");
            config.setProperty("keyGui", "F6");
            config.setProperty("keyServerList", "L");
            config.setProperty("keyFullBright", "G");
            config.setProperty("keySettings", "P");
            config.setProperty("lessObstructive", "false");
            config.setProperty("ignoreVersionUpdateMessage", "0.0.0");
            config.setProperty("achievementAnimation", "true");
            config.setProperty("achievementAnimationDuration", "1.0");
            config.setProperty("displaySkinBorder", "true");
            config.setProperty("configVersion", ""+version);

            config.store(new FileOutputStream(CONFIG_PATH + FILE_NAME),"This is the Unoffical Undercast Mod Config" + "\nCustomize it to your taste" + "\nkeyGui = Ingame Stats" +"\nkeyGui2 = Ingame Server Menu" + "\nkeyGui3 = Full Bright\n");
        } catch (Exception e) {
            displayErrorMessage(e.toString());
        }
    }

    /**
     * Loads the property data into the local data
     */
    public void loadConfigData() {
        System.out.println("[UndercastMod]: Loading Config to Local Data");
        showFPS = this.getBoolProperty("showFPS");
        showKills = this.getBoolProperty("showKills");
        showDeaths = this.getBoolProperty("showDeaths");
        showKilled = this.getBoolProperty("showKilled");
        showServer = this.getBoolProperty("showServer");
        showTeam = this.getBoolProperty("showTeam");
        showKD = this.getBoolProperty("showKD");
        showKK = this.getBoolProperty("showKK");
        showFriends = this.getBoolProperty("showFriends");
        showNextMap = this.getBoolProperty("showNextMap");
        showMap = this.getBoolProperty("showMap");
        showStreak = this.getBoolProperty("showStreak");
        showGuiChat = this.getBoolProperty("showGuiChat");
        showGuiMulti = this.getBoolProperty("showGuiMulti");
        showPlayingTime = this.getBoolProperty("showPlayingTime");
        showMatchTime = this.getBoolProperty("showMatchTime");
        showMatchTimeSeconds = this.getBoolProperty("showMatchTimeSeconds");
        showGSClass = this.getBoolProperty("showGSClass");
        showScore = this.getBoolProperty("showScore");
        showTotalKills = this.getBoolProperty("showTotalKills");
        x = this.getIntProperty("X");
        y = this.getIntProperty("Y");
        toggleTitleScreenButton = this.getBoolProperty("toggleTitleScreenButton");
        filterTips = this.getBoolProperty("filterTips");
        fullBright = this.getBoolProperty("fullBright");
        matchOnServerJoin = this.getBoolProperty("matchOnServerJoin");
        enableButtonTooltips = this.getBoolProperty("enableButtonTooltips");
        showAchievements = this.getBoolProperty("showAchievements");
        showKillAchievements = this.getBoolProperty("showKillAchievements");
        showDeathAchievements = this.getBoolProperty("showDeathAchievements");
        showFirstBloodAchievement = this.getBoolProperty("showFirstBloodAchievement");
        showLastKillAchievement = this.getBoolProperty("showLastKillAchievement");
        displaySpecialKillMessages = this.getBoolProperty("displaySpecialKillMessages");
        displaySpecialObjectives = this.getBoolProperty("displaySpecialObjectives");
        showRevengeAchievement = this.getBoolProperty("showRevengeAchievement");
        parseMatchState = this.getBoolProperty("parseMatchState");
        lastUsedFilter = this.getIntProperty("lastUsedFilter");
        lastUsedLocation = this.getIntProperty("lastUsedLocation");
        realtimeStats = this.getBoolProperty("realtimeStats");
        keyGui = this.getStringProperty("keyGui");
        keyServerList = this.getStringProperty("keyServerList");
        keyFullBright = this.getStringProperty("keyFullBright");
        keySettings = this.getStringProperty("keySettings");
        lessObstructive = this.getBoolProperty("lessObstructive");
        ignoreVersionUpdateMessage = this.getStringProperty("ignoreVersionUpdateMessage");
        achievementAnimation = this.getBoolProperty("achievementAnimation");
        achievementAnimationDuration = this.getDoubleProperty("achievementAnimationDuration");
        displaySkinBorder = this.getBoolProperty("displaySkinBorder");
        configVersion = this.getIntProperty("configVersion");

        checkForConfigUpdate();
    }

    public void setProperty(String prop, String value) {
        config.setProperty(prop, value);
        saveConfig();
    }

    public void setProperty(String prop, float value) {
        String s = String.valueOf(value);
        config.setProperty(prop, s);
        saveConfig();
    }

    public void setProperty(String prop, int value) {
        String s = String.valueOf(value);
        config.setProperty(prop, s);
        saveConfig();
    }

    public void setProperty(String prop, boolean value) {
        String s = String.valueOf(value);
        config.setProperty(prop, s);
        saveConfig();
    }

    public void setProperty(String prop, double value) {
        String s = String.valueOf(value);
        config.setProperty(prop, s);
        saveConfig();
    }

    public String getStringProperty(String prop) {
        return config.getProperty(prop);
    }

    public float getFloatProperty(String prop) {
        String s = config.getProperty(prop);
        return Float.parseFloat(s);
    }

    public int getIntProperty(String prop) {
        String s = config.getProperty(prop);
        return Integer.parseInt(s);
    }

    public boolean getBoolProperty(String prop) {
        String s = config.getProperty(prop);
        return Boolean.parseBoolean(s);
    }

    public double getDoubleProperty(String prop) {
        String s = config.getProperty(prop);
        return Double.parseDouble(s);
    }

    public static String getDefaultPropertyValue(String prop) {
        return defaults.getProperty(prop);
    }

    public static float getDefaultFloatProperty(String prop) {
        String s = defaults.getProperty(prop);
        return Float.parseFloat(s);
    }

    public static int getDefaultIntProperty(String prop) {
        String s = defaults.getProperty(prop);
        return Integer.parseInt(s);
    }

    public static boolean getDefaultBoolProperty(String prop) {
        String s = defaults.getProperty(prop);
        return Boolean.parseBoolean(s);
    }

    public static double getDefaultDoubleProperty(String prop) {
        String s = defaults.getProperty(prop);
        return Double.parseDouble(s);
    }

    public void saveConfig() {
        try {
            config.store(new FileOutputStream(CONFIG_PATH + FILE_NAME), null);
            config.load(new FileInputStream(CONFIG_PATH + FILE_NAME));
        } catch (Exception e) {
            displayErrorMessage(e.toString());
        }
    }

    private void displayErrorMessage(String error) {
        System.out.println("[UndercastMod]: ERROR: " + error);
    }

    /***
     * Checks if the config version has changed and adds the options which are new.
     */
    private void checkForConfigUpdate(){
        if(version != configVersion){
            System.out.println("[UndercastMod]: Updating the config...");
            switch(configVersion){
            case 0:
                // add you additional options.
                if(fullBright == true){ // do not overwrite the setting, if it isn't the default value
                    config.setProperty("fullBright", "true");
                }
                if(matchOnServerJoin == false){ // do not overwrite the setting, if it isn't the default value
                    config.setProperty("matchOnServerJoin", "false");
                }
            case 1:
                if(showNextMap == true){
                    config.setProperty("showNextMap", "true");
                }
            case 2:
                if(enableButtonTooltips == true) {
                    config.setProperty("enableButtonTooltips", "true");
                }
            case 3:
                if(showPlayingTime == false) {
                    config.setProperty("showPlayingTime", "false");
                }
            case 4:
                if(showAchievements == false) {
                    config.setProperty("showAchievements", "false");
                }
                if(showKillAchievements == true) {
                    config.setProperty("showKillAchievements", "true");
                }
                if(showDeathAchievements == true) {
                    config.setProperty("showDeathAchievements", "true");
                }
            case 5:
                if(showFirstBloodAchievement == false) {
                    config.setProperty("showFirstBloodAchievement", "false");
                }
                if(showLastKillAchievement == false) {
                    config.setProperty("showLastKillAchievement", "false");
                }
                if(showMatchTime == true) {
                    config.setProperty("showMatchTime", "true");
                }
                if(showMatchTimeSeconds == true) {
                    config.setProperty("showMatchTimeSeconds", "true");
                }
            case 6:
                if(showGSClass == true) {
                    config.setProperty("showGSClass", "true");
                }
            case 7:
                if(parseMatchState == true) {
                    config.setProperty("parseMatchState", "true");
                }
            case 8:
                if(showScore == true) {
                    config.setProperty("showScore", "true");
                }
                if(lastUsedFilter == 0) {
                    config.setProperty("lastUsedFilter", "0");
                }
            case 9:
                if(realtimeStats == true) {
                    config.setProperty("realtimeStats", "true");
                }
                if(showAchievements == true) {
                    config.setProperty("showTotalKills", "true");
                }
                if(displaySpecialKillMessages == true) {
                    config.setProperty("displaySpecialKillMessages", "true");
                }
            case 10:
                if(displaySpecialObjectives == true) {
                    config.setProperty("displaySpecialObjectives", "true");
                }
            case 11:
                if(keyGui.equalsIgnoreCase("F6")) {
                    config.setProperty("keyGui", "F6");
                }
                if(keyServerList.equalsIgnoreCase("L")) {
                    config.setProperty("keyServerList", "L");
                }
                if(keyFullBright.equalsIgnoreCase("G")) {
                    config.setProperty("keyFullBright", "G");
                }
                if(keySettings.equalsIgnoreCase("P")) {
                    config.setProperty("keySettings", "P");
                }
                if(lessObstructive == false) {
                    config.setProperty("lessObstructive", "false");
                }
            case 12:
                if(lastUsedLocation == 0) {
                    config.setProperty("lastUsedLocation", "0");
                }
                if(showRevengeAchievement == true) {
                    config.setProperty("showRevengeAchievement", "true");
                }
                if(ignoreVersionUpdateMessage.equals("0.0.0")) {
                    config.setProperty("ignoreVersionUpdateMessage", "0.0.0");
                }
                if(achievementAnimation == true) {
                    config.setProperty("achievementAnimation", "true");
                }
                if(achievementAnimationDuration == 1.0F) {
                    config.setProperty("achievementAnimationDuration", "1.0");
                }
                if(displaySkinBorder == true) {
                    config.setProperty("displaySkinBorder", "true");
                }
            case 13:
                //Next version
            }
            config.setProperty("configVersion", ""+version);
            saveConfig();
        }
    }
}
