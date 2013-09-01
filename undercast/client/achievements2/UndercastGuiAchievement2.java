package undercast.client.achievements2;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import org.lwjgl.opengl.GL11;

import undercast.client.achievements2.animation.UndercastAchievementAccessor;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;
import aurelienribon.tweenengine.equations.Bounce;
import net.minecraft.src.EntityRendererProxy;
import net.minecraft.src.Minecraft;
import net.minecraft.src.AbstractClientPlayer;
import net.minecraft.src.GuiScreen;
import net.minecraft.src.ScaledResolution;
import net.minecraft.src.TextureManager;
import net.minecraft.src.ResourceLocation;
import net.minecraft.src.mod_Undercast;

public class UndercastGuiAchievement2 extends GuiScreen {

    private Minecraft client;
    public ArrayList<UndercastAchievement> achievements = new ArrayList();
    private static final ResourceLocation achievementBackground = new ResourceLocation("textures/gui/achievement/achievement_background.png");
    ScaledResolution scr = new ScaledResolution(Minecraft.getMinecraft().gameSettings, Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
    private ArrayList<Integer> usedRank = new ArrayList();

    public UndercastGuiAchievement2(Minecraft mc) {
        client = mc;
        Tween.registerAccessor(UndercastAchievement.class, new UndercastAchievementAccessor());
    }

    public void queueTakenAchievement(UndercastAchievement achievement) {
        int rank = getFirstAvailableRank();
        achievement.setRank(rank);
        achievements.add(achievement);
        usedRank.add(rank);
    }

    @Override
    public void updateScreen() {
        if(mod_Undercast.enableML && Minecraft.getMinecraft().entityRenderer != null) {
            mod_Undercast.enableML = false;
            Minecraft.getMinecraft().entityRenderer = new EntityRendererProxy(Minecraft.getMinecraft());
        }
        for (int i = 0; i < achievements.size(); i++) {
            achievements.get(i).draw();
        }
    }
    public void removeAchievement(UndercastAchievement achievement){
        usedRank.remove(new Integer(achievement.getRank()));
        achievements.remove(achievement);
    }
    public int getFirstAvailableRank() {
        for (int i = 0; i < 20; i++) {
            if(!usedRank.contains(i)){
                return i;
            }
        }
        return -1;
    }

}
