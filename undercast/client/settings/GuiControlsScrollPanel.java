package undercast.client.settings;

import net.minecraft.src.*;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import undercast.client.UndercastKeybinding;
import undercast.client.server.UndercastServerSlotGui;

public class GuiControlsScrollPanel extends UndercastServerSlotGui {
    private GuiUndercastControls controls;
    private Minecraft mc;
    private int _mouseX;
    private int _mouseY;
    private int selected = -1;

    public GuiControlsScrollPanel(GuiUndercastControls controls, Minecraft mc) {
        super(true, controls.width, controls.height, 32, (controls.height - 64) + 4, 25);
        this.controls = controls;
        this.mc = mc;
    }

    @Override
    protected int getSize() {
        return UndercastKeybinding.keybinds.size();
    }

    @Override
    protected void elementClicked(int i, boolean flag) {
        if(!flag) {
            if(this.selected == -1) {
                this.selected = i;
            }
        }
    }

    @Override
    protected boolean isSelected(int i) {
        return false;
    }

    @Override
    protected void drawBackground() {
    }

    @Override
    public void drawScreen(int mX, int mY, float f) {
        this._mouseX = mX;
        this._mouseY = mY;
        
       super.drawScreen(mX, mY, f);
    }

    @Override
    protected void drawSlot(int index, int xPosition, int yPosition, int l, Tessellator tessellator) {
        int width = 70;
        int height = 20;
        xPosition -= 20;
        boolean flag = this._mouseX >= xPosition && this._mouseY >= yPosition && this._mouseX < xPosition + width && this._mouseY < yPosition + height;
        int k = (flag ? 2 : 1);
        Minecraft.getMinecraft().func_110434_K().func_110577_a(new ResourceLocation("textures/gui/widgets.png"));;
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.controls.drawTexturedModalRect(xPosition, yPosition, 0, 46 + k * 20, width / 2, height);
        this.controls.drawTexturedModalRect(xPosition + width / 2, yPosition, 200 - width / 2, 46 + k * 20, width / 2, height);
        this.controls.drawString(this.mc.fontRenderer, UndercastKeybinding.keybinds.get(index).keyDescription, xPosition + width + 4, yPosition + 6, 0xFFFFFFFF);
        boolean conflict = false;

        for(int x = 0; x < UndercastKeybinding.keybinds.size(); x++) {
            if(x != index && UndercastKeybinding.keybinds.get(x).keyCode == UndercastKeybinding.keybinds.get(index).keyCode) {
                conflict = true;
                break;
            }
        }

        String str = (conflict ? EnumChatFormatting.RED : "") + GameSettings.getKeyDisplayString(UndercastKeybinding.keybinds.get(index).keyCode);
        str = (index == this.selected ? EnumChatFormatting.WHITE + "> " + EnumChatFormatting.YELLOW + "??? " + EnumChatFormatting.WHITE + "<" : str);
        this.controls.drawCenteredString(this.mc.fontRenderer, str, xPosition + (width / 2), yPosition + (height - 8) / 2, 0xFFFFFFFF);
    }

    public boolean keyTyped(char c, int i) {
        if(this.selected != -1) {
            UndercastKeybinding.keybinds.get(this.selected).setKeyCode(i);
            this.selected = -1;
            return false;
        }

        return true;
    }
}
