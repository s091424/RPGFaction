package cm.bukkit.rpgfaction.handle;

import cm.bukkit.rpgfaction.Faction;
import cm.bukkit.rpgfaction.RPGFaction;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class InviteHandler extends AbstractHandler{

    public static InviteHandler INSTANCE;

    private static HashMap<Player, Set<Faction>> invitations;

    public static boolean sendInvitation(String senderName, String playerName){

        Player sender = Bukkit.getPlayer(senderName);
        Player player = Bukkit.getPlayer(playerName);

        if(!FactionHandler.INSTANCE.haveFaction(sender)){
            sender.sendMessage(
                    MessageHandler.INSTANCE.getMessage(
                            MessageHandler.FactionMessage.INVITE_FAIL_NO_FACTION));
            return false;
        }

        Faction faction = FactionHandler.INSTANCE.findFaction(sender);

        if(player == null || !player.isOnline()){
            sender.sendMessage(
                    MessageHandler.INSTANCE.getMessage(
                            MessageHandler.FactionMessage.INVITE_FAIL_NOT_ONLINE)
            .replace("!player", player.getName()));
            return false;
        }

        if(!checkInviteDuplication(player, faction)){
            invitations.get(player).add(faction);
            player.sendMessage(
                    MessageHandler.INSTANCE.getMessage(
                            MessageHandler.FactionMessage.INVITE_MESSAGE)
                    .replace("!sender", sender.getName())
                    .replace("!faction", faction.getName())
            );
            sender.sendMessage(
                    MessageHandler.INSTANCE.getMessage(
                            MessageHandler.FactionMessage.INVITED_MEMBER
                    )
            );
            return true;
        }else{
            sender.sendMessage(
                    MessageHandler.INSTANCE.getMessage(
                            MessageHandler.FactionMessage.ALREADY_INVITED));
            return false;
        }
    }
    public static boolean responseInvitation(Player player, String factionId, String response){

        if(!FactionHandler.INSTANCE.haveFaction(factionId)){
            player.sendMessage(
                    MessageHandler.INSTANCE.getMessage(
                            MessageHandler.FactionMessage.FACTION_NOT_FOUND
                    )
            );
            return false;
        }

        return false;
    }
    private static boolean checkInviteDuplication(Player player, Faction faction){
        initPlayer(player);
        return invitations.get(player).contains(faction);
    }
    private static void initPlayer(Player player){
        if(!invitations.containsKey(player)) invitations.put(player, new HashSet<>());
    }

    @Override
    public void onLoad() {
        invitations = new HashMap<>();
        INSTANCE = RPGFaction.getHandler(this.getClass());
    }

    @Override
    public void onUnload() {

    }
}
