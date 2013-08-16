package undercast.client;

import java.util.ArrayList;

import net.minecraft.src.mod_Undercast;

import org.lwjgl.input.Keyboard;

public class UndercastKeybinding {
    public int keyCode;
    //name used in the config
    public String keyName;
    //name used for the display
    public String keyDescription;
    public boolean isDown;
    
    public static mod_Undercast modInstance;
    public static ArrayList<UndercastKeybinding> keybinds;
    
    public UndercastKeybinding(int keyCode, String keyName, String keyDescription) {
        this.keyCode = keyCode;
        this.keyName = keyName;
        this.keyDescription = keyDescription;
        // let it update
        keybinds.add(this);
    }
    
    public void update() {
        if(this.isDown && !Keyboard.isKeyDown(keyCode)) {
            this.isDown = false;
        } else if(!this.isDown && Keyboard.isKeyDown(keyCode)){
            this.isDown = true;
            modInstance.keyboardEvent(this);
        }
    }
    
    static {
        keybinds = new ArrayList<UndercastKeybinding>();
    }
    
    public static void onTick() {
        for(int c = 0; c < keybinds.size(); c++) {
            keybinds.get(c).update();
        }
    }
}
