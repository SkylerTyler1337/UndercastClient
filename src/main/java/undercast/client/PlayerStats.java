package undercast.client;

public class PlayerStats {

    public String name;
    public int kills;
    public int deaths;
    public int friendCount;
    public double kd;
    public double kk;
    public int serverJoins;
    public int wools;
    public int cores;
    public int monuments;

    public PlayerStats() {
        name = "";
        kills = 0;
        deaths = 0;
        friendCount = 0;
        kd = 0.0D;
        kk = 0.0D;
        serverJoins = 0;
        wools = 0;
        cores = 0;
        monuments = 0;
    }

    public int getKilled() {
        return (int) Math.round((float) kills / kk);
    }

}
