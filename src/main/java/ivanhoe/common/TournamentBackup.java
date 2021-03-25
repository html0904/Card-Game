package ivanhoe.common;

import ivanhoe.common.components.Deck;
import ivanhoe.common.player.Player;
import ivanhoe.utils.Properties;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by lee on 2/29/2016.
 */
public class TournamentBackup {

    private Deck deck;
    private List<Player> players;
    private int currentPlayerID;
    private Properties.COLOR color;
    private int ivanhoeOwnerID;
    private boolean ivanhoeInPlay;

    public TournamentBackup(Deck deck, List<Player> players, int currentPlayerID, Properties.COLOR color, int ivanhoeOwnerID, boolean ivanhoeInPlay) {
        this.deck = new Deck(deck);
        this.players = new ArrayList<>();
        this.players.addAll(players.stream().map(Player::new).collect(Collectors.toList()));
        this.color = color;
        this.currentPlayerID = currentPlayerID;
        this.ivanhoeInPlay = ivanhoeInPlay;
        this.ivanhoeOwnerID = ivanhoeOwnerID;
    }

    public Deck getDeck() {
        return deck;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public int getCurrentPlayerID() {
        return currentPlayerID;
    }

    public Properties.COLOR getColor() {
        return color;
    }

    public int getIvanhoeOwnerID() {
        return ivanhoeOwnerID;
    }

    public boolean isIvanhoeInPlay() {
        return ivanhoeInPlay;
    }
}
