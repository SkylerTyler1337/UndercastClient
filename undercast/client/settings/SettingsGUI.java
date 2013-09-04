package undercast.client.settings;

import net.minecraft.src.GuiButton;
import net.minecraft.src.GuiScreen;
import net.minecraft.src.ModLoader;
import net.minecraft.src.mod_Undercast;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
public class SettingsGUI extends GuiScreen {
    public GuiScreen parentScreen;
    
    public SettingsGUI(GuiScreen gs) {
        super();
        parentScreen = gs;
    }
    @Override
    public void initGui() {
        // Add buttons		
        int x = width / 2 - 75;
        int y = height / 2;
        this.buttonList.add(new GuiButton(1, x, y-75, 150, 20, "Overlay Settings"));
        this.buttonList.add(new GuiButton(2, x, y-50, 150, 20, "General Settings"));
        this.buttonList.add(new GuiButton(3, x, y-25, 150, 20, "Achievement Settings"));
        this.buttonList.add(new GuiButton(4, x, y, 150, 20, "Control Settings"));
        this.buttonList.add(new GuiButton(5, x, y+40, 150, 20, "Done"));
    }

    @Override
    public void drawScreen(int par1, int par2, float par3) {
        drawDefaultBackground();

        int y = height / 2;
        // Draw label at top of screen
        drawCenteredString(fontRenderer, "Undercast mod settings", width / 2, y-100, 0x4444bb);

        // Draw buttons
        super.drawScreen(par1, par2, par3);
    }
    @Override
    protected void keyTyped(char par1, int par2) {
        if (par2 == Keyboard.KEY_ESCAPE) {
            mc.displayGuiScreen(null);
            return;
        }
    }

    @Override
    protected void actionPerformed(GuiButton guibutton) {
        if (guibutton.id == 1) {
            ModLoader.openGUI(mc.thePlayer, new OverlaySettings(this));
        }
        if (guibutton.id == 2) {
            ModLoader.openGUI(mc.thePlayer, new GeneralSettings(this));
        }
        if (guibutton.id == 3) {
            ModLoader.openGUI(mc.thePlayer, new AchievementSettings(this));
        }
        if (guibutton.id == 4) {
           ModLoader.openGUI(mc.thePlayer, new GuiUndercastControls(this)); 
        }
        if (guibutton.id == 5) {
            mc.displayGuiScreen(parentScreen);
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return true;
    }
}