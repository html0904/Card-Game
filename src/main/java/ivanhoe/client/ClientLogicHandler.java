package ivanhoe.client;

import ivanhoe.common.GameState;
import ivanhoe.common.components.Card;
import ivanhoe.common.components.Token;
import ivanhoe.common.player.Player;
import ivanhoe.common.player.PlayerAction;
import ivanhoe.utils.Logic;
import ivanhoe.utils.Properties;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * Created by Yannick on 2016-02-29.
 */
public class ClientLogicHandler {

    /** Reference to the client application */
    protected Client client;
    /** ID of the client player */
    protected int playerID;
    /** Reference to the current game state */
    protected GameState currentGameState;

    protected boolean stunned;

    public ClientLogicHandler(Client client){
        this.client = client;
        this.playerID = client.playerID;
    }

    public void setPlayerID(int id){
        this.playerID = id;
    }

    /**
     * Called when a card in the player's hand is clicked.
     * @param i the array position in the list of cards of the player's hand
     */
    protected void clickedPlayerHandCard(int i){
        // update the game state
        currentGameState = client.currentGameState;
        Player currentPlayer = currentGameState.getPlayer(playerID);
        Card clickedCard = currentPlayer.getHand().getCard(i);

        if(isPlayable(clickedCard) && currentGameState.getTargetPlayerID() == playerID){
            client.sendPlayerActionToServer(new PlayerAction(Properties.GAME_ACTION.SELECT_HAND_CARD, playerID, clickedCard.getId(), -1, -1, clickedCard.getColor()));
        }
    }

    /**
     * Called when a card in the player's special display is clicked
     * @param i the array position in the list of cards of the player's special display
     */
    public void clickedPlayerSpecialDisplayCard(int i){
        // update the game state
        currentGameState = client.currentGameState;

        if(currentGameState.getTargetPlayerID() == playerID){
            Player currentPlayer = currentGameState.getPlayer(playerID);
            Card clickedCard = currentPlayer.getDisplay().getSpecialDisplay().get(i);

            if(currentGameState.getRequestedAction() == Properties.GAME_ACTION.SELECT_DISPLAY_CARD){
                client.sendPlayerActionToServer(new PlayerAction(Properties.GAME_ACTION.SELECT_DISPLAY_CARD, playerID, -1, clickedCard.getId(), -1, currentGameState.getTournamentColor()));
            }
        }
    }

    /**
     * Called when a card in the player's display is clicked.
     * @param i the array position in the list of cards of the player's display
     */
    public void clickedPlayerDisplayCard(int i) {
        // update the game state
        currentGameState = client.currentGameState;

        if(currentGameState.getTargetPlayerID() == playerID){
            Player currentPlayer = currentGameState.getPlayer(playerID);
            Card clickedCard = currentPlayer.getDisplay().getDisplay().get(i);

            if(currentGameState.getRequestedAction() == Properties.GAME_ACTION.SELECT_DISPLAY_CARD){
                client.sendPlayerActionToServer(new PlayerAction(Properties.GAME_ACTION.SELECT_DISPLAY_CARD, playerID, -1, clickedCard.getId(), -1, currentGameState.getTournamentColor()));
            }
            else if(currentGameState.getRequestedAction() == Properties.GAME_ACTION.SELECT_DISPLAY_CARD_TO_KEEP_FOR_ADAPT){
                // check that it's NOT the only card of that value
                if(!isOnlyCardOfThatValue(currentPlayer.getDisplay().getDisplay(), clickedCard))
                    client.sendPlayerActionToServer(new PlayerAction(Properties.GAME_ACTION.SELECT_DISPLAY_CARD_TO_KEEP_FOR_ADAPT, playerID, -1, clickedCard.getId(), -1, currentGameState.getTournamentColor()));
            }
        }
    }

    /**
     * Called when the player clicks on one of the tokens in the middle of the board.
     * @param tokenColor color of the token that was clicked
     */
    public void clickedBoardToken(Properties.COLOR tokenColor) {
        // update the game state
        currentGameState = client.currentGameState;
        System.out.println("Clicked board token color: " + tokenColor.toString().toLowerCase());

        if(currentGameState.getTargetPlayerID() != playerID)
            return;
        switch (currentGameState.getRequestedAction()){
            case SELECT_COLOR:
                break;
            case SELECT_COLOR_OTHER_THAN_PURPLE:
                if(tokenColor == Properties.COLOR.PURPLE)
                    return;
                break;
            case SELECT_COLOR_OTHER_THAN_PURPLE_OR_GREEN:
                if(tokenColor == Properties.COLOR.PURPLE || tokenColor == Properties.COLOR.GREEN)
                    return;
                break;
            case WIN:
                client.sendPlayerActionToServer(new PlayerAction(Properties.GAME_ACTION.SELECT_COLOR, playerID, -1, -1, -1, tokenColor));
                return;
            default:
                return;
        }
        client.sendPlayerActionToServer(new PlayerAction(currentGameState.getRequestedAction(), playerID, -1, -1, -1, tokenColor));
    }

    /**
     * Called when the player clicks on one of his tokens
     * @param tokenColor color of the token that was clicked
     */
    public void clickedPlayerToken(Properties.COLOR tokenColor) {
        // update the game state
        currentGameState = client.currentGameState;
        System.out.println("Clicked player token color: " + tokenColor.toString().toLowerCase());

        // if it was a token return action AND this player AND they have that colored token
        if(currentGameState.getRequestedAction() == Properties.GAME_ACTION.SELECT_TOKEN_TO_RETURN && currentGameState.getTargetPlayerID() == playerID &&
                currentGameState.getPlayer(playerID).getPlayerTokens().stream().map(Token::getTokenColor).collect(Collectors.toCollection(LinkedList::new)).contains(tokenColor))
            client.sendPlayerActionToServer(new PlayerAction(Properties.GAME_ACTION.SELECT_TOKEN_TO_RETURN, playerID, -1, -1, -1, tokenColor));

    }

    /**
     * Called when the player clicks on the withdraw button
     */
    public void clickedWithdraw() {
        // update the game state
        currentGameState = client.currentGameState;
        System.out.println("Clicked on withdraw");
        
        // check that it was the player's turn
        if(canClickWithdraw(currentGameState, currentGameState.getPlayer(playerID))){
            client.sendPlayerActionToServer(new PlayerAction(Properties.GAME_ACTION.WITHDRAW, playerID, -1, -1, -1, currentGameState.getTournamentColor()));
        }
    }

    /**
     * Called when the player clicks on the continue button
     */
    public void clickedContinue() {
        // update the game state
        currentGameState = client.currentGameState;
        System.out.println("Clicked on continue");
        
        if(canClickContinue(currentGameState, currentGameState.getPlayer(playerID))){
            client.sendPlayerActionToServer(new PlayerAction(Properties.GAME_ACTION.CONTINUE, playerID, -1, -1, -1, currentGameState.getTournamentColor()));
        }
    }

    /**
     * Called when a card in an opponent's display is clicked
     * @param opponent the opposing player
     * @param cardPosition the card position in their display
     */
    public void clickedOpponentDisplay(Player opponent, int cardPosition) {
        // update the game state
        currentGameState = client.currentGameState;
        System.out.println(String.format("Clicked on opponent with player ID #%d's display card in position %d", opponent.getID(), cardPosition));

        if(currentGameState.getTargetPlayerID() == playerID){
            for(int i=0; i<opponent.getDisplay().getSpecialDisplay().size(); i++)
                if(opponent.getDisplay().getSpecialDisplay().get(i).getAction() == Properties.ACTION.SHIELD)
                    return;
            if(currentGameState.getRequestedAction() == Properties.GAME_ACTION.SELECT_OPPONENT_DISPLAY){
                client.sendPlayerActionToServer(new PlayerAction(Properties.GAME_ACTION.SELECT_OPPONENT_DISPLAY, playerID, -1, -1, opponent.getID(), currentGameState.getTournamentColor()));
            } else if(currentGameState.getRequestedAction() == Properties.GAME_ACTION.SELECT_OPPONENT_DISPLAY_CARD){
                client.sendPlayerActionToServer(new PlayerAction(Properties.GAME_ACTION.SELECT_OPPONENT_DISPLAY_CARD, playerID, -1, opponent.getDisplay().getDisplay().get(cardPosition).getId(), opponent.getID(), currentGameState.getTournamentColor()));
            } else if(currentGameState.getRequestedAction() == Properties.GAME_ACTION.SELECT_OPPONENT_DISPLAY_CARD_FOR_OUTWIT){
                client.sendPlayerActionToServer(new PlayerAction(Properties.GAME_ACTION.SELECT_OPPONENT_DISPLAY_CARD_FOR_OUTWIT, playerID, -1, opponent.getDisplay().getDisplay().get(cardPosition).getId(), opponent.getID(), currentGameState.getTournamentColor()));
            }
        }
    }

    /**
     * Called when a card in an opponent's special display is clicked
     * @param opponent the opposing player
     * @param cardPosition the card position in their special display
     */
    public void clickedOpponentSpecialDisplay(Player opponent, int cardPosition) {
        // update the game state
        currentGameState = client.currentGameState;
        System.out.println(String.format("Clicked on opponent with player ID #%d's special display card in position %d", opponent.getID(), cardPosition));

        if(currentGameState.getTargetPlayerID() == playerID){
            for(int i=0; i<opponent.getDisplay().getSpecialDisplay().size(); i++)
                if(opponent.getDisplay().getSpecialDisplay().get(i).getAction() == Properties.ACTION.SHIELD && currentGameState.getRequestedAction() != Properties.GAME_ACTION.SELECT_OPPONENT_DISPLAY_CARD_FOR_OUTWIT)
                    return;
            if(currentGameState.getRequestedAction() == Properties.GAME_ACTION.SELECT_OPPONENT_DISPLAY){
                client.sendPlayerActionToServer(new PlayerAction(Properties.GAME_ACTION.SELECT_OPPONENT_DISPLAY, playerID, -1, -1, opponent.getID(), currentGameState.getTournamentColor()));
            } else if(currentGameState.getRequestedAction() == Properties.GAME_ACTION.SELECT_OPPONENT_DISPLAY_CARD){
                client.sendPlayerActionToServer(new PlayerAction(Properties.GAME_ACTION.SELECT_OPPONENT_DISPLAY_CARD, playerID, -1, opponent.getDisplay().getSpecialDisplay().get(cardPosition).getId(), opponent.getID(), currentGameState.getTournamentColor()));
            } else if(currentGameState.getRequestedAction() == Properties.GAME_ACTION.SELECT_OPPONENT_DISPLAY_CARD_FOR_OUTWIT){
                client.sendPlayerActionToServer(new PlayerAction(Properties.GAME_ACTION.SELECT_OPPONENT_DISPLAY_CARD_FOR_OUTWIT, playerID, -1, opponent.getDisplay().getSpecialDisplay().get(cardPosition).getId(), opponent.getID(), currentGameState.getTournamentColor()));
            }
        }
    }

    public void clickedOpponentHand(Player opponent, int cardPosition){
        // update the game state
        currentGameState = client.currentGameState;
        System.out.println(String.format("Clicked on opponent with player ID #%d's hand card in position %d", opponent.getID(), cardPosition));
        if(currentGameState.getTargetPlayerID() == playerID && currentGameState.getRequestedAction() == Properties.GAME_ACTION.SELECT_OPPONENT_HAND){
            client.sendPlayerActionToServer(new PlayerAction(Properties.GAME_ACTION.SELECT_OPPONENT_HAND, playerID, -1, -1, opponent.getID(), currentGameState.getTournamentColor()));
        }
    }

    /**
     * Called when the player has to make a choice about whether to play Ivanhoe or not
     */
    public void clickedIvanhoe(List<Card> playerHand, boolean clickedIvanhoe) {
        if(clickedIvanhoe){
            for(Card card : playerHand){
                if(card.getAction() == Properties.ACTION.IVANHOE) {
                    client.sendPlayerActionToServer(new PlayerAction(Properties.GAME_ACTION.PLAYED_IVANHOE, playerID, card.getId(), -1, -1, Properties.COLOR.NONE));
                    return;
                }
            }
            System.out.println("ERROR: Ivanhoe was not in the player's hand but we're expecting them have it");
        } else{
            client.sendPlayerActionToServer(new PlayerAction(Properties.GAME_ACTION.CONTINUE, playerID, -1, -1, -1, Properties.COLOR.NONE));
        }
    }

    /**
     * Checks through all the cards passed to see if target card is the only card of that value
     * @param cards list of cards to check
     * @param targetCard targetCard to check value of
     * @return true if only card of that value, false otherwise
     */
    protected boolean isOnlyCardOfThatValue(List<Card> cards, Card targetCard){
        for(Card card : cards){
            if(card.getId() != targetCard.getId() && card.getValue() == targetCard.getValue())
                return false;
        }
        return true;
    }


    /**
     * Checks to see if a card can be played in the current game state.
     * It will first check if the card is an action card or if it's a color card and call the appropriate
     * method. This method is used to simplify checking cards by combining both.
     *
     * @param card the card the user wants to play
     * @return true if it can be player, false otherwise
     */
    protected boolean isPlayable(Card card) {
        //will stop processing card clcks if thats not what it requested atm.
        if (currentGameState.getRequestedAction() != Properties.GAME_ACTION.SELECT_HAND_CARD) return false;
        System.out.println("Player clicked card "+card.getId());
        return card.getAction() == Properties.ACTION.NONE ? isPlayableColorCard(card) : Logic.isPlayableActionCard(card, currentGameState);
    }

    /**
     * Checks to see if the color card can be played.
     *
     * @param card the color card to be played
     * @return true if it can, false otherwise
     */
    private boolean isPlayableColorCard(Card card) {
        // if we're not stunned yet this is the only card we can play
        boolean hasStun = false;
        for(Card specialCard : currentGameState.getPlayer(playerID).getDisplay().getSpecialDisplay()){
            if(specialCard.getAction() == Properties.ACTION.STUNNED)
                hasStun = true;
        }

        // if it matches the current tournament color
        if (card.getColor() == currentGameState.getTournamentColor()){
            if(hasStun && stunned)
                return false;
            else if(hasStun)
                stunned = true;
            return true;
        }
        // if it's a white card and there's not already a maiden in the display
        else if(card.getColor() == Properties.COLOR.WHITE){
            if(hasStun && stunned)
                return false;
            else if(hasStun)
                stunned = true;
            return card.getValue() != 6 || !currentGameState.getPlayer(playerID).getDisplay().hasMaiden();
        }

        return false;
    }




    public boolean canClickContinue(GameState gameState, Player player){
        // check that it was the player's turn
        // AND they currently have the highest display value
        // AND that they were asked to play a card
        return gameState.getTargetPlayerID() == player.getID()
                && gameState.getPlayerIdOfHighestDisplay() == player.getID()
                && gameState.getRequestedAction() == Properties.GAME_ACTION.SELECT_HAND_CARD;
    }

    public boolean canClickWithdraw(GameState gameState, Player player){
        // check that it was the player's turn
        // AND that they were asked to play a card
         return gameState.getTargetPlayerID() == player.getID()
                 && gameState.getRequestedAction() == Properties.GAME_ACTION.SELECT_HAND_CARD;
    }

    public void resetStun(){
        stunned = false;
    }
}
