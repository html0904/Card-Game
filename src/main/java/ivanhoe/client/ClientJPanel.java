package ivanhoe.client;

import ivanhoe.common.GameState;
import ivanhoe.common.components.Card;
import ivanhoe.common.components.Token;
import ivanhoe.common.player.Player;
import ivanhoe.utils.Properties;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * Created by Yan on 2/6/2016.
 */
public class ClientJPanel extends JPanel {

    private Client client;
    private ClientActionHandler clientActionHandler;

    private HashMap<Integer, Image> cardImages;
    private HashMap<Properties.COLOR, Image> tokenImages;
    protected HashMap<Properties.COLOR, Integer> tokenOrder;
    private Image boardImage;
    private Image withdrawImage;
    private Image withdrawImageGreyedOut;
    private Image continueImage;
    private Image continueImageGreyedOut;
    private Image targetImage;
    private Image bannerIvanhoe;
    private Image bannerWin;
    private Image bannerLose;

    /** Number of pixels in the height of the screen */
    int screenHeight;
    /** Number of pixels in the width of the screen */
    int screenWidth;
    /** Center pixel of the width of the screen */
    int screenWidthCenter;
    /** Center pixel of the height of the screen */
    int screenHeightCenter;
    /** Default card image width in pixels */
    int defaultCardWidth = 100;
    /** Default card image height in pixels */
    int defaultCardHeight = 120;
    /** Default card overlap for stacking cards */
    int defaultCardOverlap = 30;
    /** Card shadow width in pixels */
    int cardShadowWidth = 15;
    /** Card shadow height in pixels */
    int cardShadowHeight = 10;

    /** Percent of screen height at bottom of player's hand */
    int playerHandBuffer = 6;
    /** Percent of screen height to show between player's display and hand */
    int playerDisplayBuffer = 2;
    /** Percent of maximum screen width the player's cards can take up */
    int playerMaxWidthPercent = 80;
    /** Percent of screen height at top of opponents' hands */
    int opponentHandBuffer = 6;
    /** Percent of screen height to show between opponent's hand and display */
    int opponentDisplayBuffer = 2;
    /** Percent of maximum screen width the opponent's cards can take up */
    int opponentMaxWidthPercent = 90;

    /** Y value to draw the player's hand */
    int playerHandHeight;
    /** Y value to draw the player's display */
    int playerDisplayHeight;
    /** Maximum width in pixels the player's cards can take up */
    int playerMaxWidth;
    /** Y value to draw the opponents' hands */
    int opponentHandHeight;
    /** Y value to draw the opponents' displays */
    int opponentDisplayHeight;
    /** Maximum width in pixels the opponents' cards can take up */
    int opponentMaxWidth;

    /** Size in pixels of the token sprite. Note it is a square */
    int tokenImageSize = 18; //it's square so 18x18
    /** Space in pixels between each token on the board */
    int tokenSpaceBoard = 25;
    /** Space in pixels between each token in the player displays */
    int tokenSpaceDisplay = 10;

    int buttonOffset = 100;
    /** Pixel width of the continue and withdraw buttons */
    int buttonWidth = 300;
    /** Pixel height of the continue and withdraw buttons */
    int buttonHeight = 100;
    /** Pixel value between the edge of the screen and a button */
    int buttonBuffer = 100;
    /** Pixel value of the whitespace for the shadow on the width of the button */
    int buttonShadowX = 25;
    /** Pixel value of the whitespace for the shadow on the height of the button */
    int buttonShadowY = 15;
    /** Size in pixels of the target sprite. Note it is a square */
    int targetImageSize = 16;

    int bannerHeight = 250;
    int bannerWidth = 700;


    public ClientJPanel(Client client) {
        this.client = client;
        this.clientActionHandler = new ClientActionHandler(this, client);
        client.currentGameState = null;

        initializeImages();
        initializeMouseListener();
    }

    /**
     * Loads all the images into memory.
     */
    private void initializeImages() {
        // Load the board image
        URL url = getClass().getResource("/images/board.jpg");
        boardImage = new ImageIcon(url).getImage();

        // Load the continue and withdraw
        url = getClass().getResource("/images/continue.png");
        continueImage = new ImageIcon(url).getImage();
        url = getClass().getResource("/images/withdraw.png");
        withdrawImage = new ImageIcon(url).getImage();
        url = getClass().getResource("/images/continue_grey.png");
        continueImageGreyedOut = new ImageIcon(url).getImage();
        url = getClass().getResource("/images/withdraw_grey.png");
        withdrawImageGreyedOut = new ImageIcon(url).getImage();

        // Load banners
        url = getClass().getResource("/images/banner_ivanhoe.png");
        bannerIvanhoe = new ImageIcon(url).getImage();
        url = getClass().getResource("/images/banner_win.png");
        bannerWin = new ImageIcon(url).getImage();
        url = getClass().getResource("/images/banner_lose.png");
        bannerLose = new ImageIcon(url).getImage();

        // Load the target
        url = getClass().getResource("/images/target.png");
        targetImage = new ImageIcon(url).getImage();

        // Load the card images
        cardImages = new HashMap<>();
        for(int i=0; i<Card.cardNames.length; i++){
            url = getClass().getResource("/images/cards/" + Card.cardNames[i] + ".png");
            cardImages.put(i, new ImageIcon(url).getImage());
        }
        // Get the greyed out card back
        url = getClass().getResource("/images/cards/cardBackGrey.png");
        cardImages.put(-1, new ImageIcon(url).getImage());

        // Load the token images
        tokenImages = new HashMap<>();
        for( Properties.COLOR color : Properties.COLOR.values()){
            url = getClass().getResource("/images/tokens/" + color.toString().toLowerCase() + "Token.png");
            tokenImages.put(color, new ImageIcon(url).getImage());
        }

        // Assign the order in which they'll be drawn
        int order = 0;
        tokenOrder = new HashMap<>();
        tokenOrder.put(Properties.COLOR.BLUE, order++);
        tokenOrder.put(Properties.COLOR.GREEN, order++);
        tokenOrder.put(Properties.COLOR.PURPLE, order++);
        tokenOrder.put(Properties.COLOR.RED, order++);
        tokenOrder.put(Properties.COLOR.YELLOW, order);
    }

    /**
     * Creates a mouse listener to check for mouse clicks. The listener delegates the handling
     * of the event to the ClientActionHandler.
     */
    private void initializeMouseListener() {
        MouseListener mouseListener = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                clientActionHandler.handleClick(e.getPoint());
            }
        };
        addMouseListener(mouseListener);
    }

    @Override
    /**
     * This is the paint method that is called with 'repaint'.
     */
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawBoard(g);
    }

    /**
     * Draws the game board.
     * @param g Java Graphics object
     */
    private void drawBoard(Graphics g) {
        g.drawImage(boardImage, 0, 0, null);
        if(client.currentGameState == null)
            return;

        List<Player> players = client.currentGameState.getPlayers();

        calculateRatios();

        if(client.currentGameState.getRequestedAction() == Properties.GAME_ACTION.WIN_GAME){
            if(client.currentGameState.getTargetPlayerID() == client.playerID || client.playerID == -1)
                g.drawImage(bannerWin, screenWidthCenter - bannerWidth / 2, screenHeightCenter - bannerHeight/4, null);
            else
                g.drawImage(bannerLose, screenWidthCenter - bannerWidth / 2, screenHeightCenter - bannerHeight/4, null);

            return;
        }

        if(client.playerID == -1){
            drawPlayer(g, players.get(0));
            List<Player> spectatorOpponents = new LinkedList<>();
            for(int i=1; i<players.size(); i++){
                spectatorOpponents.add(players.get(i));
            }
            drawOpponents(g, spectatorOpponents);
        } else {
            drawPlayer(g, players.get(client.playerID));
            drawOpponents(g, players);
        }
        drawBoardTokens(g, client.currentGameState);
        // drawDiscardPile(g, client.currentGameState.getLastCardPlayed());
        drawRoundButtons(g, client.currentGameState);

        // check if ivanhoe
        if (client.currentGameState.getRequestedAction() == Properties.GAME_ACTION.SELECT_IVANHOE) {
            g.drawImage(bannerIvanhoe, screenWidthCenter - bannerWidth / 2, screenHeightCenter - bannerHeight/4, null);
            g.drawImage(cardImages.get(client.currentGameState.getLastCardPlayed().getImageID()), screenWidthCenter - defaultCardWidth/2, screenHeightCenter + bannerHeight/6, null);
        }
    }



    /**
     * Calculates the ratios of all integer values based on the screen width and height.
     * Makes the placement of the image drawing entirely dynamic for resizing the screen.
     */
    private void calculateRatios(){
        screenWidth = this.getSize().width;
        screenHeight = this.getSize().height;
        screenWidthCenter = screenWidth/2;
        screenHeightCenter = screenHeight/2;

        playerHandHeight = screenHeight - (defaultCardHeight + (screenHeight*playerHandBuffer)/100);
        playerDisplayHeight = playerHandHeight - (defaultCardHeight + (screenHeight*playerDisplayBuffer)/100);
        playerMaxWidth = (screenWidth*playerMaxWidthPercent)/100;

        opponentHandHeight = (screenHeight*opponentHandBuffer)/100;
        opponentDisplayHeight = opponentHandHeight + defaultCardHeight + (screenHeight* opponentDisplayBuffer)/100;
        opponentMaxWidth = ((screenWidth/client.currentGameState.getPlayers().size()-1)*opponentMaxWidthPercent)/100;
    }

    /**
     * Draws the current player's hand and display at the bottom of the screen.
     * @param g the graphics object
     * @param player the current player object
     */
    private void drawPlayer(Graphics g, Player player) {
        // draw player hand
        drawCardArray(g, player.getHand().getCards(), screenWidthCenter, playerHandHeight, playerMaxWidth);
        // draw player tokens
        drawDisplayTokens(g, player.getPlayerTokens(), screenWidthCenter, playerHandHeight - (tokenImageSize / 2));
        // draw special display
        drawCardArray(g, player.getDisplay().getSpecialDisplay(), screenWidthCenter, playerDisplayHeight - defaultCardHeight/2, playerMaxWidth);
        // draw player display
        drawCardArray(g, player.getDisplay().getDisplay(), screenWidthCenter, playerDisplayHeight, playerMaxWidth);
        // draw target
        if(client.currentGameState.getTargetPlayerID() == player.getID())
            drawTarget(g, screenWidthCenter, playerHandHeight + defaultCardHeight + targetImageSize/2);
    }

    /**
     * Draws the player's opponents' hand and displays at the top of the screen.
     * @param g the graphics object
     * @param players the list of all player objects in the game
     */
    private void drawOpponents(Graphics g, List<Player> players) {
        int currentOpponent = 1;
        for(int i=0; i<players.size(); i++){
            if(players.get(i).getID() != client.playerID) {
                int center = (screenWidth / (client.playerID == -1 ? players.size()+1 : players.size()) ) * currentOpponent++;
                // draw opponent hand
                if(client.playerID == -1)
                    drawCardArray(g, players.get(i).getHand().getCards(), center, opponentHandHeight, opponentMaxWidth);
                else
                    drawHiddenCardArray(g, players.get(i).getHand().getCards(), center, opponentHandHeight, opponentMaxWidth, players.get(i).isInTournament());
                // draw opponent tokens
                drawDisplayTokens(g, players.get(i).getPlayerTokens(), center, opponentDisplayHeight - (tokenImageSize/2));
                // draw opponent special display
                drawCardArray(g, players.get(i).getDisplay().getSpecialDisplay(), center, opponentDisplayHeight + defaultCardHeight/2, opponentMaxWidth);
                // draw opponent display
                drawCardArray(g, players.get(i).getDisplay().getDisplay(), center, opponentDisplayHeight, opponentMaxWidth);
                // draw opponent target
                drawOpponentTarget(g, players.get(i), center, opponentHandHeight - targetImageSize/2);
            }
        }
    }

    /**
     * Determines whether or not to draw the target at an opponents location based on whether or not it's their turn
     * and if it's a select ivanhoe game action
     * @param g the graphics object
     * @param player the player to check
     * @param centerX the center X of the target
     * @param centerY the center Y of the target
     */
    private void drawOpponentTarget(Graphics g, Player player, int centerX, int centerY){
        // Only draw if it's the target player and we're not checking to see if they will play ivanhoe
        if(player.getID() == client.currentGameState.getTargetPlayerID() && client.currentGameState.getRequestedAction() != Properties.GAME_ACTION.SELECT_IVANHOE)
            drawTarget(g, centerX, centerY);
    }

    /**
     * Draws a target at the specified location
     * @param g the graphics object
     * @param centerX the center X of the target
     * @param centerY the center Y of the target
     */
    private void drawTarget(Graphics g, int centerX, int centerY){
        g.drawImage(targetImage, centerX - tokenImageSize/2, centerY - tokenImageSize/2, null);
    }


    /**
     * Draws the display tokens at the specified location. Will draw any missing tokens greyed out
     *
     * @param g the graphics object
     * @param tokens the list of tokens to be colored in
     * @param center the center X of where to draw the tokens
     * @param y the Y value of where to draw the tokens
     */
    private void drawDisplayTokens(Graphics g, List<Token> tokens, int center, int y){
        // Draw all tokens
        List<Properties.COLOR> colors = tokens.stream().map(Token::getTokenColor).collect(Collectors.toCollection(LinkedList::new));
        for(Map.Entry<Properties.COLOR, Integer> entry : tokenOrder.entrySet()){
            drawToken(g, entry.getKey(), center, y, tokenSpaceDisplay, colors.contains(entry.getKey()));
        }
    }

    /**
     * Draws the tokens in the middle of the board based on the game state
     * @param g the graphics object
     */
    private void drawBoardTokens(Graphics g, GameState gameState) {
        switch (gameState.getRequestedAction()){
            // draw all tokens colored
            case SELECT_COLOR:
                for(Map.Entry<Properties.COLOR, Integer> entry : tokenOrder.entrySet())
                    drawToken(g, entry.getKey(), screenWidthCenter, screenHeightCenter, tokenSpaceBoard, true);
                break;

            // draw all but purple colored
            case SELECT_COLOR_OTHER_THAN_PURPLE:
                for(Map.Entry<Properties.COLOR, Integer> entry : tokenOrder.entrySet())
                    drawToken(g, entry.getKey(), screenWidthCenter, screenHeightCenter, tokenSpaceBoard, entry.getKey() != Properties.COLOR.PURPLE);
                break;

            // draw all but purple and green colored
            case SELECT_COLOR_OTHER_THAN_PURPLE_OR_GREEN:
                for(Map.Entry<Properties.COLOR, Integer> entry : tokenOrder.entrySet())
                    drawToken(g, entry.getKey(), screenWidthCenter, screenHeightCenter, tokenSpaceBoard, entry.getKey() != Properties.COLOR.PURPLE && entry.getKey() != Properties.COLOR.GREEN);
                break;

            // draw all but the last tournament color if it was purple
            case WIN:
                for(Map.Entry<Properties.COLOR, Integer> entry : tokenOrder.entrySet())
                    drawToken(g, entry.getKey(), screenWidthCenter, screenHeightCenter, tokenSpaceBoard, true);
                break;

            // otherwise only draw the tournament color
            default:
                for(Map.Entry<Properties.COLOR, Integer> entry : tokenOrder.entrySet())
                    drawToken(g, entry.getKey(), screenWidthCenter, screenHeightCenter, tokenSpaceBoard, entry.getKey() == gameState.getTournamentColor());
                break;
        }
    }

    private void drawDiscardPile(Graphics g, Card lastPlayedCard){
        if(lastPlayedCard == null)
            return;
        int x = screenWidthCenter + 3*tokenImageSize + 2*tokenSpaceBoard;
        int y = screenHeightCenter - cardShadowHeight/2;

        for(int i=10; i>0; i-=2){
            g.drawImage(cardImages.get(0), x-i, y-i, null);
        }
        g.drawImage(cardImages.get(lastPlayedCard.getImageID()), x, y, null);
    }

    /**
     * Draws the appropriate token on the board based on the color in color or greyscale.
     *
     * @param g the graphics object
     * @param color the color of the token to draw
     * @param centerX the center X of where to draw the token
     * @param centerY the center Y of where to draw the token
     * @param spacing the spacing that is between tokens
     * @param inColor whether or not to draw it in color or in grey
     */
    private void drawToken(Graphics g, Properties.COLOR color, int centerX, int centerY, int spacing, boolean inColor){
        Image image = inColor ? tokenImages.get(color) : tokenImages.get(Properties.COLOR.WHITE);
        int offset = tokenImageSize*2 + spacing*2 + tokenImageSize/2;
        g.drawImage(image, (centerX - offset) + (tokenOrder.get(color) * (tokenImageSize + spacing)), centerY - tokenImageSize / 2, null);
    }

    /**
     * Draws the continue and withdraw buttons
     * @param g the graphics object
     * @param gameState the current state of the game
     */
    private void drawRoundButtons(Graphics g, GameState gameState){
        if(client.playerID == -1)
            return;
        // check continue
        if(clientActionHandler.clientLogicHandler.canClickContinue(gameState, gameState.getPlayer(client.playerID)))
            g.drawImage(continueImage, screenWidth - (buttonWidth + buttonBuffer) , screenHeightCenter + buttonOffset - (buttonHeight/2), null);
        else
            g.drawImage(continueImageGreyedOut, screenWidth - (buttonWidth + buttonBuffer) , screenHeightCenter + buttonOffset - (buttonHeight/2), null);

        // check withdraw
        if(clientActionHandler.clientLogicHandler.canClickWithdraw(gameState, gameState.getPlayer(client.playerID)))
            g.drawImage(withdrawImage, buttonBuffer , screenHeightCenter + buttonOffset - (buttonHeight/2), null);
        else
            g.drawImage(withdrawImageGreyedOut, buttonBuffer , screenHeightCenter + buttonOffset - (buttonHeight/2), null);
    }

    /**
     * Draws an array of card backs at the specified location on the screen.
     * @param g the graphics object
     * @param cards the list of cards to draw
     * @param center the x value at the center of the cards
     * @param y the y value of the cards
     * @param maxWidth the maximum width the cards can take up
     */
    private void drawHiddenCardArray(Graphics g, List<Card> cards, int center, int y, int maxWidth, boolean inColor){
        int cardOverlap = getActualCardArrayOverlap(cards.size(), maxWidth);
        int width = getActualCardArrayWidth(cards.size(), maxWidth);
        int farLeft = center - width/2;

        for(int i=0; i<cards.size(); i++){
            int x = farLeft + (i * cardOverlap);
            g.drawImage(inColor ? cardImages.get(0) : cardImages.get(-1), x, y, null);
        }
    }

    /**
     * Draws an array of cards at the specified location on the screen.
     * @param g the graphics object
     * @param cards the list of cards to draw
     * @param center the x value at the center of the cards
     * @param y the y value of the cards
     * @param maxWidth the maximum width the cards can take up
     */
    private void drawCardArray(Graphics g, List<Card> cards, int center, int y, int maxWidth){
        int cardOverlap = getActualCardArrayOverlap(cards.size(), maxWidth);
        int width = getActualCardArrayWidth(cards.size(), maxWidth);
        int farLeft = center - width/2;

        for(int i=0; i<cards.size(); i++){
            int x = farLeft + (i * cardOverlap);
            g.drawImage(cardImages.get(cards.get(i).getImageID()), x, y, null);
        }
    }

    /**
     * Calculates and returns the actual card overlap for stacking the cards. Only changes from the
     * default card overlap value if there isn't enough room for it according to the maxWidth.
     * @param size the number of cards in the list
     * @param maxWidth the maximum width they can take up
     * @return the true overlap if greater than maxWidth, the default overlap otherwise
     */
    protected int getActualCardArrayOverlap(int size, int maxWidth){
        return getDefaultCardArrayWidth(size) < maxWidth ? defaultCardOverlap : (maxWidth - defaultCardWidth) / (size-1);
    }

    /**
     * Calculates and returns the default card width based on the number of cards.
     * @param size the number of cards to calculate the width for
     * @return  the default width this many cards would take up in pixels
     */
    protected int getDefaultCardArrayWidth(int size){
        return defaultCardWidth + (size-1) * defaultCardOverlap;
    }

    /**
     * Calculates the actual width of a number of cards. Takes into account the maximum width allowed
     * and determines if the default overlap of the cards (for stacking) is too much.
     * @param size the number of cards to calculate the width for
     * @param maxWidth the maximum width available
     * @return the actual width the cards will take up
     */
    protected int getActualCardArrayWidth(int size, int maxWidth){
        int cardOverlap = getActualCardArrayOverlap(size, maxWidth);
        return cardOverlap < defaultCardOverlap ? (defaultCardWidth + (size-1) * cardOverlap) : getDefaultCardArrayWidth(size);
    }

    protected void setClientID(int id){
        clientActionHandler.setClientID(id);
    }

    public void resetStunned() {
        clientActionHandler.resetStunned();
    }
}
