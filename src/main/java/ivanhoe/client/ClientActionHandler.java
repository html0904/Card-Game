package ivanhoe.client;

import ivanhoe.common.components.Card;
import ivanhoe.common.player.Player;
import ivanhoe.utils.Properties;

import java.awt.*;
import java.util.List;
import java.util.Map;

/**
 *
 * Created by Yan on 2/20/2016.
 */
public class ClientActionHandler {

    /** Reference to the GUI panel*/
    private ClientJPanel p;
    /** Reference to the client application */
    private Client client;
    /** ID of the player of this client */
    private int playerID;
    /** Reference to the client side logic handler */
    protected ClientLogicHandler clientLogicHandler;

    public ClientActionHandler(ClientJPanel clientJPanel, Client client){
        this.p = clientJPanel;
        this.client = client;
        this.playerID = client.playerID;
        this.clientLogicHandler = new ClientLogicHandler(client);
    }

    /**
     * Handles a mouse click within the jpanel and notifies the client.
     *
     * @param point x,y of mouse click
     */
    public void handleClick(Point point) {

        if(client == null || client.currentGameState == null || client.playerID == -1)
            return;

        // ivanhoe
        if(client.currentGameState.getRequestedAction() == Properties.GAME_ACTION.SELECT_IVANHOE){
            if(point.y > 432 && point.y < 474)
                handleIvanhoe(point);
            return;
        }

        // best to sort it by Y to find things faster

        // check player hand
        if(point.y > p.playerHandHeight + p.cardShadowHeight && point.y < (p.playerHandHeight + p.defaultCardHeight)- p.cardShadowHeight){
            handlePlayerHand(point);
        }
        // check player token
        if(point.y > p.playerHandHeight - p.tokenImageSize && point.y < p.playerHandHeight){
            handlePlayerToken(point);
        }
        // check player display
        if(point.y > p.playerDisplayHeight + p.cardShadowHeight && point.y < (p.playerDisplayHeight + p.defaultCardHeight)- p.cardShadowHeight){
            handlePlayerDisplay(point);
        }
        // check player special display
        if(point.y > p.playerDisplayHeight - p.defaultCardHeight/2 && point.y < p.playerDisplayHeight + p.cardShadowHeight){
            handlePlayerSpecialDisplay(point);
        }
        // check middle tokens
        if(point.y < p.screenHeightCenter + p.tokenImageSize/2 && point.y > p.screenHeightCenter - p.tokenImageSize/2){
            handleBoardToken(point);
        }
        // check withdraw / continue   screenHeightCenter + buttonOffset - (buttonHeight/2)
        if(point.y < p.screenHeightCenter + p.buttonOffset + p.buttonHeight/2 - p.buttonShadowY && point.y > p.screenHeightCenter + p.buttonOffset - p.buttonHeight/2 + p.buttonShadowY){
            handleButton(point);
        }
        // check opponent display
        if(point.y < p.opponentDisplayHeight + p.defaultCardHeight - p.cardShadowHeight && point.y > p.opponentDisplayHeight + p.cardShadowHeight){
            handleOpponentDisplay(point);
        }
        // check opponent special display
        if(point.y < p.opponentDisplayHeight + 3*p.defaultCardHeight/2 - p.cardShadowHeight && point.y > p.opponentDisplayHeight + p.defaultCardHeight - p.cardShadowHeight){
            handleOpponentSpecialDisplay(point);
        }

        // check opponent hand
        if(point.y < p.opponentHandHeight + p.defaultCardHeight - p.cardShadowHeight && point.y > p.opponentHandHeight + p.cardShadowHeight){
            handleOpponentHand(point);
        }
    }

    /**
     * Determines which card in the special display was clicked
     * @param point x,y of mouse
     */
    private void handleOpponentSpecialDisplay(Point point) {
        List<Player> players = client.currentGameState.getPlayers();
        int currentOpponent = 1;

        for(int i=0; i<players.size(); i++){
            if(players.get(i).getID() != playerID){
                int center = (p.screenWidth / (players.size()) ) * currentOpponent++;
                int cardPosition = positionInCardArray(point, players.get(i).getDisplay().getSpecialDisplay().size(), p.opponentMaxWidth, center);

                if(cardPosition != -1){
                    clientLogicHandler.clickedOpponentSpecialDisplay(players.get(i), cardPosition);
                }
            }
        }
    }

    /**
     * Determines if YES or NO was clicked for playing ivanhoe
     *
     * @param point x,y of mouse click
     */
    private void handleIvanhoe(Point point){
        // TODO: Clean up these magic numbers
        if(point.x > 480 && point.x < 566){
            clientLogicHandler.clickedIvanhoe(client.currentGameState.getPlayer(playerID).getHand().getCards(), true);
        } else if(point.x > 714 && point.x < 800){
            clientLogicHandler.clickedIvanhoe(client.currentGameState.getPlayer(playerID).getHand().getCards(), false);
        }
    }

    /**
     * Determines which card in which opponents hand was clicked
     *
     * @param point x,y of mouse click
     */
    private void handleOpponentHand(Point point){
        List<Player> players = client.currentGameState.getPlayers();
        int currentOpponent = 1;

        for(int i=0; i<players.size(); i++){
            if(players.get(i).getID() != playerID){
                int center = (p.screenWidth / (players.size()) ) * currentOpponent++;
                int cardPosition = positionInCardArray(point, players.get(i).getHand().getCards().size(), p.opponentMaxWidth, center);

                if(cardPosition != -1){
                    clientLogicHandler.clickedOpponentHand(players.get(i), cardPosition);
                }
            }
        }
    }

    /**
     * Determines which card in which opponents display was clicked
     *
     * @param point x,y of mouse click
     */
    private void handleOpponentDisplay(Point point){
        List<Player> players = client.currentGameState.getPlayers();
        int currentOpponent = 1;

        for(int i=0; i<players.size(); i++){
            if(players.get(i).getID() != playerID){
                int center = (p.screenWidth / (players.size()) ) * currentOpponent++;
                int cardPosition = positionInCardArray(point, players.get(i).getDisplay().getDisplay().size(), p.opponentMaxWidth, center);

                if(cardPosition != -1){
                    clientLogicHandler.clickedOpponentDisplay(players.get(i), cardPosition);
                }
            }
        }
    }

    /**
     * Determines which button the player clicked (withdraw, continue, or neither)
     *
     * @param point x,y of mouse click
     */
    private void handleButton(Point point){
        if(point.x > p.buttonBuffer + p.buttonShadowX && point.x < p.buttonBuffer + p.buttonWidth - p.buttonShadowX)
            clientLogicHandler.clickedWithdraw();
        else if(point.x < p.screenWidth - (p.buttonBuffer + p.buttonShadowX) && point.x > p.screenWidth - (p.buttonBuffer + p.buttonWidth - p.buttonShadowX))
            clientLogicHandler.clickedContinue();
    }

    /**
     * Determines which player token was clicked
     *
     * @param point x,y of mouse click
     */
    private void handlePlayerToken(Point point){
        int leftX = p.screenWidthCenter - (p.tokenSpaceDisplay*2 + p.tokenImageSize*2 + p.tokenImageSize/2);
        int rightX = p.screenWidthCenter + (p.tokenSpaceDisplay*2 + p.tokenImageSize*2 + p.tokenImageSize/2);

        if(point.x > leftX && point.x < rightX){
            for(Map.Entry<Properties.COLOR, Integer> entry : p.tokenOrder.entrySet()){
                if(point.x > leftX + entry.getValue()*(p.tokenImageSize + p.tokenSpaceDisplay) && point.x < rightX - (4-entry.getValue())*(p.tokenImageSize + p.tokenSpaceDisplay)) {
                    clientLogicHandler.clickedPlayerToken(entry.getKey());
                    return;
                }
            }
        }
    }

    /**
     * Determines which token was clicked in the middle of board.
     *
     * @param point x,y of mouse click
     */
    private void handleBoardToken(Point point){
        int leftX = p.screenWidthCenter - (p.tokenSpaceBoard*2 + p.tokenImageSize*2 + p.tokenImageSize/2);
        int rightX = p.screenWidthCenter + (p.tokenSpaceBoard*2 + p.tokenImageSize*2 + p.tokenImageSize/2);

        if(point.x > leftX && point.x < rightX){
            for(Map.Entry<Properties.COLOR, Integer> entry : p.tokenOrder.entrySet()){
                if(point.x > leftX + entry.getValue()*(p.tokenImageSize + p.tokenSpaceBoard) && point.x < rightX - (4-entry.getValue())*(p.tokenImageSize + p.tokenSpaceBoard)) {
                    clientLogicHandler.clickedBoardToken(entry.getKey());
                    return;
                }
            }
        }
    }

    /**
     * Determines the position of the card clicked in an array of cards.
     * Note: returns -1 if not in array.
     *
     * @param point x,y of mouse click
     * @param numberOfCards number of cards in the array
     * @param maxWidth maximum width in pixels it can take up
     * @param centerX center pixels of the array
     * @return the position in the array or -1 if outside
     */
    private int positionInCardArray(Point point, int numberOfCards, int maxWidth, int centerX){
        int halfCardArrayWidth = (p.getActualCardArrayWidth(numberOfCards,maxWidth)/2) - p.cardShadowWidth;
        int cardOverlap = p.getActualCardArrayOverlap(numberOfCards, maxWidth);

        int leftX = centerX - halfCardArrayWidth;
        int rightX = centerX + halfCardArrayWidth;

        if(point.x > leftX && point.x < rightX){
            int offset = leftX + cardOverlap;

            for(int i=0; i<numberOfCards; i++){
                if(i == numberOfCards-1)
                    return numberOfCards - 1;
                else if(point.x - offset < 0){
                    return i;
                }
                offset += cardOverlap;
            }
        }
        return -1;
    }

    /**
     * Determines which card was clicked in the player's special display based on the X value of the mouse click
     * @param point x,y of mouse click
     */
    private void handlePlayerSpecialDisplay(Point point){
        List<Card> playerSpecialDisplay = client.currentGameState.getPlayer(playerID).getDisplay().getSpecialDisplay();
        int cardPosition = positionInCardArray(point, playerSpecialDisplay.size(), p.playerMaxWidth, p.screenWidthCenter);

        if(cardPosition == -1)
            return;

        System.out.println("Clicked player display card in position: " + cardPosition);
        clientLogicHandler.clickedPlayerSpecialDisplayCard(cardPosition);
    }

    /**
     * Determines which card was clicked in the player's display based on the X value of the mouse click.
     * @param point x,y of mouse click
     */
    private void handlePlayerDisplay(Point point){
        List<Card> playerDisplay = client.currentGameState.getPlayer(playerID).getDisplay().getDisplay();
        int cardPosition = positionInCardArray(point, playerDisplay.size(), p.playerMaxWidth, p.screenWidthCenter);

        if(cardPosition == -1)
            return;

        System.out.println("Clicked player display card in position: " + cardPosition);
        clientLogicHandler.clickedPlayerDisplayCard(cardPosition);
    }

    /**
     * Determines which card was clicked based on the X value of the mouse click.
     *
     * Notifies the client which card was clicked if it was a card (array position).
     * @param point x,y of mouse click
     */
    private void handlePlayerHand(Point point){
        List<Card> playerHand = client.currentGameState.getPlayer(playerID).getHand().getCards();
        int cardPosition = positionInCardArray(point, playerHand.size(), p.playerMaxWidth, p.screenWidthCenter);

        if(cardPosition == -1)
            return;

        System.out.println("Clicked player hand card in position: " + cardPosition);
        clientLogicHandler.clickedPlayerHandCard(cardPosition);
    }

    public void setClientID(int clientID) {
        playerID = clientID;
        clientLogicHandler.setPlayerID(clientID);
    }

    public void resetStunned() {
        clientLogicHandler.resetStun();
    }
}
