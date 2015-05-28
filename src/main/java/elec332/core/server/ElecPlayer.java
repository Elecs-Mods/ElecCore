package elec332.core.server;

import elec332.core.util.NBTHelper;

/**
 * Created by Elec332 on 28-5-2015.
 */
public class ElecPlayer {
    public ElecPlayer(NBTHelper data){
        this.data = data;
    }

    private boolean online;
    private NBTHelper data;

    public boolean isOnline(){
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public NBTHelper getData() {
        return data;
    }
}
