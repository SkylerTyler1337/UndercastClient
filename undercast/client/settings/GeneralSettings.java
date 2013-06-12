package undercast.client.settings;

import net.minecraft.src.GuiButton;
import net.minecraft.src.GuiScreen;
import net.minecraft.src.GuiTextField;
import net.minecraft.src.ModLoader;
import net.minecraft.src.mod_Undercast;

import org.lwjgl.input.Keyboard;
public class GeneralSettings extends GuiScreen {

    public GuiScreen parentScreen;
    // Toggle settings
    public String[] toggleSettings = new String[]{ "showGuiChat", "showGuiMulti", "toggleTitleScreenButton", "filterTips", "matchOnServerJoin", "enableButtonTooltips", "parseMatchState", "realtimeStats"};
    public String[] enabledStrings = new String[]{ "Chat gui shown", "Overcast Button shown", "Death screen cleared", "Tips filtered", "/match on server join", "Button tooltips shown", "Match state shown", "Realtime stats"};
    public String[] disabledStrings = new String[]{ "Chat gui hidden", "Overcast Button hidden", " Default death screen", "No tips filtered", "No /match on server join", "Button tooltips hidden", "Match state hidden", "Game stats"};

    // X Offset vars
    public GuiButton xPlusButton;
    public GuiButton xMinusButton;
    public GuiTextField xTextField;

    // X Offset vars
    public GuiButton yPlusButton;
    public GuiButton yMinusButton;
    public GuiTextField yTextField;

    // Allowed chars
    public String allowedChars = "0123456789-\b";

    public GeneralSettings(GuiScreen gs) {
        super();
        parentScreen = gs;
    }

    
    @Override
    public void initGui() {
        // Positioning
        int x1 = width / 2 - 150;
        int x2 = width / 2 + 10;
        int y = height / 2 - 80;

        // Add buttons
        for (int i = 0; i < toggleSettings.length / 2; i++) {
            this.buttonList.add(new SettingsToggleButton(0, x1, y+(i*25), 150, 20, "", enabledStrings[i], disabledStrings[i], toggleSettings[i]));
        }
        y = height / 2 - 80;
        for (int i = toggleSettings.length / 2; i < toggleSettings.length; i++) {
            this.buttonList.add(new SettingsToggleButton(0, x2, y+((i - toggleSettings.length / 2)*25), 150, 20, "", enabledStrings[i], disabledStrings[i], toggleSettings[i]));
        }

        // Positioning
        y = (y - 30) + toggleSettings.length * 25;
        int x = width / 2 - 77;
        x2 = width / 2 + 75;

        //X and Y Offset
        xPlusButton = new GuiButton(3, x1, y-35, 15, 20, "+");
        yPlusButton = new GuiButton(4, x2, y-35, 15, 20, "+");
        xMinusButton = new GuiButton(5, x1+60, y-35, 15, 20, "-");
        yMinusButton = new GuiButton(6, x2+60, y-35, 15, 20, "-");
        this.buttonList.add(xPlusButton);
        this.buttonList.add(yPlusButton);
        this.buttonList.add(xMinusButton);
        this.buttonList.add(yMinusButton);
        xTextField = new GuiTextField(mc.fontRenderer, x1+20, y-35, 35, 20);
        xTextField.setText(Integer.toString(mod_Undercast.CONFIG.x));
        yTextField = new GuiTextField(mc.fontRenderer, x2+20, y-35, 35, 20);
        yTextField.setText(Integer.toString(mod_Undercast.CONFIG.y));
        
        // Back button
        this.buttonList.add(new GuiButton(1, x, y, 150, 20, "Back"));
    }

    @Override
    public void drawScreen(int par1, int par2, float par3) {
        drawDefaultBackground();
        // Draw label at top of screen
        drawCenteredString(fontRenderer, "General settings", width / 2, height / 2 - 80 - 20, 0x4444bb);
        xTextField.drawTextBox();
        yTextField.drawTextBox();

        int x = width / 2 - 77,
                x2 = width / 2 + 75,
                x1 = width / 2 - 150;
        int y = (height / 2 - 120) + toggleSettings.length * 25;
        drawCenteredString(fontRenderer, "X Offset", x1 + 40, y - 38, 0xffffff);
        drawCenteredString(fontRenderer, "Y Offset", x2 + 40, y - 38, 0xffffff);
        // Draw buttons
        super.drawScreen(par1, par2, par3);
    }

    @Override
    protected void keyTyped(char par1, int par2) {
        if (par2 == Keyboard.KEY_ESCAPE) {
                //secure reading of the int values
                int x, y = 0;
                try {
                    x = Integer.parseInt(xTextField.getText());
                } catch (Exception e) {
                    x = 0;
                }
                try {
                    y = Integer.parseInt(yTextField.getText());
                } catch (Exception e) {
                    y = 0;
                }
                mod_Undercast.CONFIG.setProperty("X", x);
                mod_Undercast.CONFIG.x = x;
                mod_Undercast.CONFIG.setProperty("Y", y);
                mod_Undercast.CONFIG.y = y;
            mc.displayGuiScreen(null);
            return;
        }
        if (!allowedChars.contains(String.valueOf(par1)))
            return;
        if (xTextField.isFocused()) {
            xTextField.textboxKeyTyped(par1, par2);
        }
        if (yTextField.isFocused()) {
            yTextField.textboxKeyTyped(par1, par2);
        }
    }

    @Override
    protected void actionPerformed(GuiButton guibutton) {
        // If the button is clicked, toggle and save the setting
        if (guibutton instanceof SettingsToggleButton) {
            //Toggle button
            SettingsToggleButton button = (SettingsToggleButton) guibutton;
            button.buttonPressed();
        } else if (guibutton.id == 1) {
            //secure reading of the int values
            int x, y = 0;
            try {
                x = Integer.parseInt(xTextField.getText());
            } catch (Exception e) {
                x = 0;
            }
            try {
                y = Integer.parseInt(yTextField.getText());
            } catch (Exception e) {
                y = 0;
            }
            
            //Close screen
            mod_Undercast.CONFIG.setProperty("X", x);
            mod_Undercast.CONFIG.x = x;
            mod_Undercast.CONFIG.setProperty("Y", y);
            mod_Undercast.CONFIG.y = y;
            ModLoader.openGUI(mc.thePlayer, parentScreen);
        }

        //Handle +/-
        if (guibutton.id == 3) 
            xTextField.setText(Integer.toString(Integer.parseInt(xTextField.getText()) + 1));
        if (guibutton.id == 4) 
            yTextField.setText(Integer.toString(Integer.parseInt(yTextField.getText()) + 1));
        if (guibutton.id == 5) 
            xTextField.setText(Integer.toString(Integer.parseInt(xTextField.getText()) - 1));
        if (guibutton.id == 6) 
            yTextField.setText(Integer.toString(Integer.parseInt(yTextField.getText()) - 1));
    }

    @Override
    public boolean doesGuiPauseGame() {
        return true;
    }

    @Override
    protected void mouseClicked(int par1, int par2, int par3) {
        super.mouseClicked(par1, par2, par3);
        xTextField.mouseClicked(par1, par2, par3);
        yTextField.mouseClicked(par1, par2, par3);
    }
}
