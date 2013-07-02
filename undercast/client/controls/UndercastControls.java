package undercast.client.controls;

import org.lwjgl.input.Keyboard;

import net.minecraft.src.Minecraft;
import net.minecraft.src.*;

public class UndercastControls {
    protected static Class forgeCommonHandlerClass;
    protected boolean forgeDetected = false;

    public UndercastControls() {
        // Look for Forge
        try {
            this.forgeCommonHandlerClass = Class.forName("cpw.mods.fml.common.FMLCommonHandler");

            if(this.forgeCommonHandlerClass != null) {
                this.forgeDetected = true;
            }
        } catch(Exception ex) {
        }
    }

    public void onTickInGUI(float tick, Minecraft mc, GuiScreen screen) {
        if(!this.forgeDetected) { // If Forge detected no need for better controls, if not then open better controls when controls is open.
            if(mc.currentScreen instanceof GuiControls && !(mc.currentScreen instanceof GuiUndercastControls)) {
                mc.displayGuiScreen(new GuiUndercastControls(new GuiOptions((GuiScreen)null, mc.gameSettings), mc.gameSettings));
            }
        }
    }
}
