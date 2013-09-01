package undercast.client.achievements;

import java.awt.image.BufferedImage;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import net.minecraft.src.Minecraft;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.Item;
import net.minecraft.src.Achievement;
import undercast.client.UndercastConfig;
import undercast.client.UndercastCustomMethods;
import undercast.client.UndercastData;
import undercast.client.achievements2.UndercastAchievement;
import net.minecraft.src.mod_Undercast;

/**
 * @author Flv92
 */
public class UndercastKillsHandler {

    private String killer;
    private boolean killOrKilled;

    public UndercastKillsHandler() {
    }

    public void handleMessage(String message, String username, EntityPlayer player, String unstripedMessage) {
        //When you die from someone
        if (UndercastConfig.showDeathAchievements && message.startsWith(username) && !message.toLowerCase().endsWith(" team") && (message.contains(" by ") || message.contains(" took ") || message.contains("fury of"))) {
            if (!message.contains("fury of") && !message.contains("took ")) {
                killer = message.substring(message.indexOf("by") + 3, message.lastIndexOf("'s") == -1 ? message.length() : message.lastIndexOf("'s"));
                // cut the distance message
                if(killer.contains(" ")) {
                    killer = killer.substring(0, killer.indexOf(' '));
                }
            } else if (message.contains("fury of")) {
                killer = message.substring(message.indexOf("fury of ") + 8).split("'s")[0];
            } else {
                killer = message.substring(message.indexOf("took ") + 5).split("'s")[0];
            }
            killOrKilled = false;
            UndercastData.isLastKillFromPlayer = false;
            UndercastData.isNextKillFirstBlood = false;
            if(UndercastCustomMethods.isTeamkill(unstripedMessage, killer, username)) {
                this.printTeamKillAchievement();
            } else {
                if (UndercastConfig.showRevengeAchievement) {
                    // add your killer to the list so it can be detected if you take revenge
                    UndercastData.killerList.add(killer);
                    for(int c = 0; c < UndercastData.victimList.size(); c++) {
                        // test if the killer took revenge
                        if(UndercastData.victimList.get(c).equals(killer)) {
                            this.printRevengeAchievemt();
                            UndercastData.victimList.remove(c);
                            break;
                        }
                    }
                }
                this.printAchievement();
            }
        } //if you kill a person
        else if (UndercastConfig.showKillAchievements && (message.contains("by " + username) || message.contains("took " + username) || message.contains("fury of " + username)) && !message.toLowerCase().contains(" destroyed by ")) {
            killer = message.substring(0, message.indexOf(" "));
            killOrKilled = true;
            if(UndercastCustomMethods.isTeamkill(unstripedMessage, username, killer)) {
                this.printTeamKillAchievement();
            }  else {
                // check if there is a special kill coming
                int kills = (int)UndercastData.getKills() + UndercastData.stats.kills;
                if(mod_Undercast.CONFIG.displaySpecialKillMessages) {
                    if (isSpecialKill(kills + 10)) {
                        Minecraft.getMinecraft().thePlayer.addChatMessage("[UndercastMod] Your are \u00A7c10\u00A7f kills away from a \u00A7ospecial kill\u00A7r (" + (kills + 10) + ")");
                    } else if (isSpecialKill(kills + 5)) {
                        Minecraft.getMinecraft().thePlayer.addChatMessage("[UndercastMod] Your are \u00A7c5\u00A7f kills away from a \u00A7ospecial kill\u00A7r (" + (kills + 5) + ")");
                    } else if (isSpecialKill(kills + 2)) {
                        Minecraft.getMinecraft().thePlayer.addChatMessage("[UndercastMod] Your are \u00A7c2\u00A7f kills away from a \u00A7ospecial kill\u00A7r (" + (kills + 2) + ")");
                    } else if (isSpecialKill(kills + 1)) {
                        Minecraft.getMinecraft().thePlayer.addChatMessage("[UndercastMod] Your are \u00A7c1\u00A7f kill away from a \u00A7ospecial kill\u00A7r (" + (kills + 1) + ")");
                    }
                }
                if (isSpecialKill(kills)) {
                    if(mod_Undercast.CONFIG.displaySpecialKillMessages) {
                        Minecraft.getMinecraft().thePlayer.addChatMessage("[UndercastMod] \u00A7lSPECIAL KILL(" + kills + "): \u00A7c" + killer);
                    }
                    SpecialKillLogger.logSpecialKill(kills, killer, UndercastData.server, UndercastData.map);
                }
                if(UndercastConfig.showRevengeAchievement) {
                    // add the victim to the revenge list in case it takes revenge
                    UndercastData.victimList.add(killer);
                    for(int c = 0; c < UndercastData.killerList.size(); c++) {
                        // test if the player took revenge
                        if(UndercastData.killerList.get(c).equals(killer)) {
                            this.printRevengeAchievemt();
                            UndercastData.killerList.remove(c);
                            break;
                        }
                    }
                }
                this.printAchievement();
            }

            UndercastData.isLastKillFromPlayer = true;
            if (UndercastData.isNextKillFirstBlood) {
                if (UndercastConfig.showFirstBloodAchievement) {
                    printFirstBloodAchievement(username);
                }
                UndercastData.isNextKillFirstBlood = false;
            }
        } //when you die, but nobody killed you.
        else if (UndercastConfig.showDeathAchievements && message.startsWith(username) && !message.toLowerCase().endsWith(" team") && !message.toLowerCase().contains(" the game")) {
            killer = username;
            killOrKilled = false;
            this.printAchievement();
        } else if (message.toLowerCase().contains("game over")) {
            if (UndercastData.isLastKillFromPlayer && mod_Undercast.CONFIG.showLastKillAchievement) {
                printLastKillAchievement(username);
            }
        } //When someone die
        else if ((message.contains("by ") || message.contains("took ") || message.contains("fury of ")) && !message.toLowerCase().endsWith(" team")) {
            UndercastData.isLastKillFromPlayer = false;
            UndercastData.isNextKillFirstBlood = false;
        }
    }

    private void printAchievement() {
        UndercastAchievement ac = new UndercastAchievement(killer,killOrKilled);
        mod_Undercast.guiAchievement.queueTakenAchievement(ac);
    }

    public void printFirstBloodAchievement(String username) {
        UndercastAchievement ac = new UndercastAchievement(username, "\u00A7a" + username,"\u00A7agot the first Blood!");
        mod_Undercast.guiAchievement.queueTakenAchievement(ac);
    }

    public void printLastKillAchievement(String username) {
        UndercastAchievement ac = new UndercastAchievement(username, "\u00A7a" + username,"\u00A7agot the last Kill!");
        mod_Undercast.guiAchievement.queueTakenAchievement(ac);
    }

    private void printTeamKillAchievement() {
        UndercastAchievement ac = new UndercastAchievement(killer,killOrKilled ? "\u00A7a" + killer : "\u00A74" + killer,killOrKilled ? "\u00A7aTeam Kill" : "\u00A74Team Kill");
        mod_Undercast.guiAchievement.queueTakenAchievement(ac);
        
    }

    private void printRevengeAchievemt() {
        UndercastAchievement ac = new UndercastAchievement(killer, killer, killOrKilled ? "\u00A7aRevengekill!" : "\u00A74 took Revenge!");
        mod_Undercast.guiAchievement.queueTakenAchievement(ac);
    }

    public static boolean isSpecialKill(int kill) {
        if(kill > 99) {
            if(kill < 1000) {
                // detect special kills like 100, 200, 300, 500, 900
                int i = kill / 100;
                if (i * 100 == kill) {
                    return true;
                }
            } else {
                // detect special kills like 1000m 5000, 7500, 10500
                int i1 = kill / 1000;
                int i2 = (kill + 500) / 1000;
                if((i1 * 1000 == kill) || (i2 * 1000 == kill + 500)) {
                    return true;
                }
            }

            // detect special kills like 3333, 5555, 16666
            String s = String.valueOf(kill);
            char c1, c2, c3, c4;
            c1 = s.charAt(s.length() - 1);
            c2 = s.charAt(s.length() - 2);
            c3 = s.charAt(s.length() - 3);
            if(s.length() > 3) {
                c4 = s.charAt(s.length() -4);
            } else {
                c4 = c3;
            }

            if(c1 == c2 && c1 == c3 && c1 == c4) {
                return true;
            }
        }
        return false;
    }
}