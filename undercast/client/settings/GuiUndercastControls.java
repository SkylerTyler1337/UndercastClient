package undercast.client.settings;

import net.minecraft.src.GameSettings;
import net.minecraft.src.GuiButton;
import net.minecraft.src.GuiScreen;
import net.minecraft.src.StringTranslate;
import net.minecraft.src.mod_Undercast;

public class GuiUndercastControls extends GuiScreen {
    /**
     * A reference to the screen object that created this. Used for navigating between screens.
     */
    private GuiScreen parentScreen;

    private GuiControlsScrollPanel scrollPane;

    public GuiUndercastControls(GuiScreen par1GuiScreen) {
        this.parentScreen = par1GuiScreen;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void initGui() {
        this.scrollPane = new GuiControlsScrollPanel(this, this.mc);
        StringTranslate stringtranslate = mod_Undercast.getStringTranslate();
        this.buttonList.add(new GuiButton(200, this.width / 2 - 100, this.height - 24, stringtranslate.translateKey("gui.done")));
        scrollPane.registerScrollButtons(null,7, 8);
    }

    /**
     * Fired when a control is clicked. This is the equivalent of ActionListener.actionPerformed(ActionEvent e).
     */
    protected void actionPerformed(GuiButton par1GuiButton) {
        if(par1GuiButton.id == 200) {
            this.mc.displayGuiScreen(this.parentScreen);
        }
    }

    /**
     * Called when the mouse is clicked.
     */
    protected void mouseClicked(int par1, int par2, int par3) {
        super.mouseClicked(par1, par2, par3);
    }

    /**
     * Fired when a key is typed. This is the equivalent of KeyListener.keyTyped(KeyEvent e).
     */
    protected void keyTyped(char par1, int par2) {
        if(this.scrollPane.keyTyped(par1, par2)) {
            super.keyTyped(par1, par2);
        }
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int par1, int par2, float par3) {
        this.drawDefaultBackground();
        // Draw label at top of screen
        drawCenteredString(fontRenderer, "Undercast mod settings", width / 2, (height/2)-100, 0x4444bb);
        this.scrollPane.drawScreen(par1, par2, par3);
        super.drawScreen(par1, par2, par3);
    }
}
