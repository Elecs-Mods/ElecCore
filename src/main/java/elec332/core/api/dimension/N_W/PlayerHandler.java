package elec332.core.api.dimension.N_W;

/**
 * Created by Elec332 on 28-1-2015.
 */
public class PlayerHandler {
/*
    static HashMap<String, Boolean> playerTPBool;
    static HashMap<String, Integer> playerTPInfo;

    public PlayerHandler(){
        playerTPBool = new HashMap<String, Boolean>();
        playerTPInfo = new HashMap<String, Integer>();
    }

    public static void setPlayerTPBool(EntityPlayerMP entityPlayerMP, boolean inPortal){
        String playername = entityPlayerMP.toString();
        if (entityPlayerMP.timeUntilPortal > 0) {
            entityPlayerMP.timeUntilPortal = entityPlayerMP.getPortalCooldown();
        } else {
            if (!playerTPBool.containsKey(playername)) {
                playerTPBool.put(playername, inPortal);
            } else {
                playerTPBool.remove(playername);
                playerTPBool.put(playername, inPortal);
            }
        }
    }

    public static void setPlayerTPInfo(EntityPlayerMP entityPlayerMP, int DimID){
        String playername = entityPlayerMP.toString();
        if (!playerTPInfo.containsKey(playername)) {
            playerTPInfo.put(playername, DimID);
        } else {
            playerTPInfo.remove(playername);
            playerTPInfo.put(playername, DimID);
        }
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        World world = event.player.getEntityWorld();
        if (world.isRemote) {
            //TODO: client stuff
        } else {

            //TODO: Make accesstransformes to make "portalcounter" accessible

            int i = event.player.getMaxInPortalTime();
            if (playerTPBool.get(event.player.toString())) {
                if (event.player.portalCounter++ >= i) {
                    this.portalCounter = i;
                    event.player.timeUntilPortal = event.player.getPortalCooldown();
                    this.travelToDimension(b0);
                }
            }
        }
    }*/
}
