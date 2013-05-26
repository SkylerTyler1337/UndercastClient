package undercast.client;
//You may not release this source under any condition, it must be linked to this page
//You may recompile and publish as long as skipperguy12 and Guru_Fraser are given credit
//You may not claim this to be your own
//You may not remove these comments

import undercast.client.achievements.UndercastKillsHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.mod_Undercast;

public class UndercastChatHandler {
    public UndercastChatHandler(String message, String username, EntityPlayer player) {
        //Friend tracking Joining.
        if (message.contains(" joined the game")) {
            String name;
            message = message.replace(" joined the game", "");
            if (message.contains("[")) {
                name = message.split(" ")[1];
            } else {
                name = message;
            }

            UndercastData.addFriend(name);
        }
        //friend traking. Leaving
        else if (message.contains("left the game")) {
            String name;
            message = message.replace(" left the game", "");
            if (message.contains("[")) {
                name = message.split(" ")[1];
            } else {
                name = message;
            }
            if (UndercastData.isFriend(name)) {
                UndercastData.removeFriend(name);
            }
        }
        //update what map you are playing on
        else if (message.contains("Now playing")) {
            message = message.replace("Now playing ", "");
            UndercastData.setMap((message.split(" by ")[0]));
            if(UndercastData.getKills() == 0 && UndercastData.getDeaths() == 0) { // new match or observer or noob
                UndercastData.reload(false);
            }
        }
        //if you die
        else if (message.startsWith(username) && !message.toLowerCase().endsWith(" team")) {
            // if you die form someone
            if((message.contains(" by ") || message.contains(" took ") || message.contains(" fury of"))) {
                UndercastData.addKilled(1);
            }
                UndercastData.addDeaths(1);
                UndercastData.resetKillstreak();
        }
        //if you kill a person
        else if ((message.contains("by " + username) && !message.toLowerCase().contains(" destroyed by ")) || message.contains("took " + username) || message.contains("fury of " + username)) {
            UndercastData.addKills(1);
            UndercastData.addKillstreak(1);
        }
        //when you join a match
        else if (message.contains("You joined the")) {
            
            try {
                UndercastData.setTeam(UndercastData.Teams.valueOf(message.replace("You joined the ", "").replace(" Team", "").replace(" team", "")));
            } catch(Exception e) {
                // if the team set fails because of an alias, set the team to Unknown
                UndercastData.setTeam(UndercastData.Teams.Unknown);
            }
        }
        //when a map is done. Display all the stats
        else if (!message.startsWith("<") && message.toLowerCase().contains("cycling to") && message.contains("1 second")) {
            player.addChatMessage("\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-");
            player.addChatMessage("Final Stats:");
            player.addChatMessage("\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-");
            player.addChatMessage("Kills: " + UndercastData.getKills());
            player.addChatMessage("Deaths: " + UndercastData.getDeaths());
            player.addChatMessage("K/D: " + UndercastCustomMethods.getKD());
            player.addChatMessage("Kill Streak: " + UndercastData.getLargestKillstreak());
            UndercastData.resetKills();
            UndercastData.resetKilled();
            UndercastData.resetDeaths();
            UndercastData.resetKillstreak();
            UndercastData.resetLargestKillstreak();
            UndercastData.setTeam(UndercastData.Teams.Observers);
        }
        //filters [Tip] messages
        else if (message.startsWith("[Tip]") && mod_Undercast.CONFIG.filterTips) {
            Minecraft.getMinecraft().ingameGUI.getChatGUI().deleteChatLine(0);
        }
        //sends /match when you join a server.
        else if(message.contains("Welcome to the Overcast Network")){
            if(UndercastData.redirect && UndercastData.server.equalsIgnoreCase("lobby")) {
                UndercastData.redirect = false;
                Minecraft.getMinecraft().thePlayer.sendChatMessage("/server " + UndercastData.directionServer);
            }
        }
        //server detection
        else if(message.contains("Teleporting you to ")) {
            UndercastData.setServer(message.replace("Teleporting you to ", ""));
            if(!message.toLowerCase().contains("lobby")) {
                UndercastData.welcomeMessageExpected = true;
            }
            UndercastCustomMethods.handleServerSwap();
        } else if(message.contains("You are currently on ")) {
            if(UndercastData.serverDetectionCommandExecuted) {
                UndercastData.serverDetectionCommandExecuted = false;
                UndercastData.setServer(message.replace("You are currently on ", ""));
                UndercastCustomMethods.handleServerSwap();
            }
        } else if(message.toLowerCase().contains("game over")) {
            UndercastData.isGameOver = true;
            UndercastData.isNextKillFirstBlood = false;
            try {
                // stop the timer
                UndercastData.matchTimer.stop();
            } catch (Exception ignored) {
            }
        } else if(message.toLowerCase().contains("the match has started")) {
            UndercastData.isGameOver = false;
            UndercastData.isNextKillFirstBlood = true;
            
            // stop the timer
            try {
                UndercastData.matchTimer.stop();
            } catch (Exception ignored) {
            }
            //and start one which starts from 0
            UndercastData.incrementMatchTime = true;
            UndercastData.matchTimeHours = 0;
            UndercastData.matchTimeMin = 0;
            UndercastData.matchTimeSec = 0;
            UndercastData.matchTimer = new MatchTimer();
            
        } else if(message.equals("                    ")) {
            if(!UndercastData.welcomeMessageExpected) {
                UndercastData.serverDetectionCommandExecuted = true;
                Minecraft.getMinecraft().thePlayer.sendChatMessage("/server");
            } else {
                UndercastData.welcomeMessageExpected = false;
            }
            if(mod_Undercast.CONFIG.matchOnServerJoin || mod_Undercast.CONFIG.showMatchTime) {
                Minecraft.getMinecraft().thePlayer.sendChatMessage("/match");
            }
        // start and sync the match timer
        } else if(message.toLowerCase().contains("time:") || message.toLowerCase().contains("score:") || message.toLowerCase().contains("time remaining: ")) {
             System.out.println(message);
            String time = "-2:-2";
            String messageToReplace;
            // stop the timer
            try {
                UndercastData.matchTimer.stop();
            } catch (Exception ignored) {
            }
            // extract the time
            messageToReplace = message.split("[0-9]{1,2}[:]{1}[0-5]?[0-9]{1}[:]?[0-5]?[0-9]?")[0];
            time = message.replace(messageToReplace, "");
            
            // detect if it should increment or decrement
            if(messageToReplace.toLowerCase().contains("time:")) {
                UndercastData.incrementMatchTime = true;
            } else {
                UndercastData.incrementMatchTime = false;
            }
            
            // read the time
            String[] numbers = time.split("[:]{1}");
            if(numbers.length == 3) {
                UndercastData.matchTimeHours = Integer.parseInt(numbers[0]);
                UndercastData.matchTimeMin = Integer.parseInt(numbers[1]);
                UndercastData.matchTimeSec = Integer.parseInt(numbers[2]);
            } else {
                UndercastData.matchTimeHours = 0;
                UndercastData.matchTimeMin = Integer.parseInt(numbers[0]);
                UndercastData.matchTimeSec = Integer.parseInt(numbers[1]);
            }
            // start the timer
            UndercastData.matchTimer = new MatchTimer();
        } else if(message.startsWith("Current class:")) {
            UndercastData.currentGSClass = message.replace("Current class: ", "");
            System.out.println(UndercastData.currentGSClass);
        } else if(message.startsWith("You have selected")){
            UndercastData.currentGSClass = message.replace("You have selected ", "");
            System.out.println(UndercastData.currentGSClass);
        }
    }
}
