package undercast.client.achievements;

import java.awt.image.BufferedImage;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import net.minecraft.client.Minecraft;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.Item;
import net.minecraft.src.Achievement;
import undercast.client.UndercastConfig;
import undercast.client.UndercastData;
import net.minecraft.src.mod_Undercast;

/**
 * @author Flv92
 */
public class UndercastKillsHandler {

    private String killer;
    private boolean killOrKilled;

    public UndercastKillsHandler() {
    }

    public void handleMessage(String message, String username, EntityPlayer player) {
        //When you die from someone
        if (UndercastConfig.showDeathAchievements && message.startsWith(username) && !message.toLowerCase().endsWith(" team") && (message.contains(" by ") || message.contains(" took ") || message.contains("fury of"))) {
            if (!message.contains("fury of") && !message.contains("took ")) {
                killer = message.substring(message.indexOf("by") + 3, message.lastIndexOf("'s") == -1 ? message.length() : message.lastIndexOf("'s"));
            } else if (message.contains("fury of")) {
                killer = message.substring(message.indexOf("fury of ") + 8).split("'s")[0];
            } else {
                killer = message.substring(message.indexOf("took ") + 5).split("'s")[0];
            }
            killOrKilled = false;
            this.printAchievement();
        } //if you kill a person
        else if (UndercastConfig.showKillAchievements && (message.contains("by " + username) || message.contains("took " + username) || message.contains("fury of " + username)) && !message.toLowerCase().contains(" destroyed by ")) {
            killer = message.substring(0, message.indexOf(" "));
            killOrKilled = true;
            this.printAchievement();
            UndercastData.isLastKillFromPlayer = true;
            if (UndercastData.isNextKillFirstBlood) {
                if (UndercastConfig.showFirstBloodAchievement) {
                    printFirstBloodAchievement();
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
                printLastKillAchievement();
            }
        } //When someone die
        else if ((message.contains("by ") || message.contains("took ") || message.contains("fury of ")) && !message.toLowerCase().endsWith(" team")) {
            UndercastData.isLastKillFromPlayer = false;
            UndercastData.isNextKillFirstBlood = false;
        }
    }

    private void printAchievement() {
        Runnable r1 = new Runnable() {
            public void run() {
                try {
                    Thread.sleep(1000L);
                    Achievement custom = (new Achievement(27, "custom", 1, 4, Item.ingotIron, (Achievement) null));
                   UndercastGuiAchievement gui = new UndercastGuiAchievement(Minecraft.getMinecraft());
                   Minecraft.getMinecraft().guiAchievement = gui;
                   gui.addFakeAchievementToMyList(custom, killOrKilled, killer);
                } catch (InterruptedException ex) {
                    Logger.getLogger(UndercastKillsHandler.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };
        Thread t1 = new Thread(r1);
        t1.start();
    }

    public void printFirstBloodAchievement() {
        final long waitingTime;
        if (UndercastConfig.showAchievements && UndercastConfig.showKillAchievements) {
            waitingTime = 4000L;
        } else {
            waitingTime = 0L;
        }
        Runnable r1 = new Runnable() {
            public void run() {
                try {
                    Thread.sleep(waitingTime);
                    Achievement custom = (new Achievement(27, "custom", 1, 4, Item.ingotIron, (Achievement) null));
                    Minecraft client = Minecraft.getMinecraft();
                    UndercastGuiAchievement gui = new UndercastGuiAchievement(Minecraft.getMinecraft());
                    client.guiAchievement = gui;
                    gui.addFakeAchievementToMyList(custom, true, client.thePlayer.username, client.thePlayer.username, "got the first Blood!");
                } catch (InterruptedException ex) {
                    Logger.getLogger(UndercastKillsHandler.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };
        Thread t1 = new Thread(r1);
        t1.start();
    }

    public void printLastKillAchievement() {
        Runnable r1 = new Runnable() {
            public void run() {
                Achievement custom = (new Achievement(27, "custom", 1, 4, Item.ingotIron, (Achievement) null));
                Minecraft client = Minecraft.getMinecraft();
                UndercastGuiAchievement gui = new UndercastGuiAchievement(Minecraft.getMinecraft());
                client.guiAchievement = gui;
                gui.addFakeAchievementToMyList(custom, true, client.thePlayer.username, client.thePlayer.username, "got the last Kill!");

            }
        };
        Thread t1 = new Thread(r1);
        t1.start();
    }
}