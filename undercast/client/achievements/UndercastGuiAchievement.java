package undercast.client.achievements;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import net.minecraft.src.Minecraft;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import net.minecraft.src.AbstractClientPlayer;
import net.minecraft.src.EntityRendererProxy;
import net.minecraft.src.Gui;
import net.minecraft.src.ResourceLocation;
import net.minecraft.src.ScaledResolution;
import net.minecraft.src.GuiAchievement;
import net.minecraft.src.RenderHelper;
import net.minecraft.src.RenderItem;
import net.minecraft.src.Achievement;
import net.minecraft.src.StatCollector;
import net.minecraft.src.ImageBufferDownload;
import net.minecraft.src.TextureManager;
import net.minecraft.src.mod_Undercast;

public class UndercastGuiAchievement extends GuiAchievement {

    public UndercastGuiAchievement(Minecraft par1Minecraft) {
        super(par1Minecraft);
    }
    /**
     * Enables Modloader
     */
    public void updateAchievementWindow() {
        if(mod_Undercast.enableML && Minecraft.getMinecraft().entityRenderer != null) {
            mod_Undercast.enableML = false;
            Minecraft.getMinecraft().entityRenderer = new EntityRendererProxy(Minecraft.getMinecraft());
        }
        super.updateAchievementWindow();
    }
}
