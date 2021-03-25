package ivanhoe.common.components;

import ivanhoe.utils.Properties;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by Yan on 2/6/2016.
 */
public class Card implements Serializable {

    public static final long serialVersionUID = 1L;

    public static HashMap<Integer, Card> cardIDMap = new HashMap<>();

    // The position in the array is the ImageID
    public final static String[] cardNames = {
            "cardBack",
            "blue2",      // blue
            "blue3",
            "blue4",
            "blue5",
            "green1",     // green
            "purple3",  // purple
            "purple4",
            "purple5",
            "purple7",
            "red3",     // red
            "red4",
            "red5",
            "white2",     // white ('supporters')
            "white3",
            "white6",
            "yellow2",    // yellow
            "yellow3",
            "yellow4",

            // alphabetical action cards
            "adapt",
            "break_lance",
            "change_weapon",
            "charge",
            "countercharge",
            "disgrace",
            "dodge",
            "drop_weapon",
            "ivanhoe",
            "knockdown",
            "outmaneuver",
            "outwit",
            "retreat",
            "riposte",
            "shield",
            "stunned",
            "unhorse"
    };

    int id, value, imageID;
    Properties.ACTION action;
    Properties.COLOR color;

    public Card(int id, int value, Properties.ACTION action, Properties.COLOR color) {
        this.action = action;
        this.color = color;
        this.id = id;
        this.value = value;

        this.imageID = getImageIDFromName(determineCardName(action, color));

        cardIDMap.put(this.id, this);
    }

    public Card(Card obj) {
        this.action = obj.action;
        this.color = obj.color;
        this.id = obj.id;
        this.value = obj.value;

        this.imageID = getImageIDFromName(determineCardName(action, color));
    }

    /**
     * Returns the id of the card or -1 if invalid card name.
     * <p>
     * Warning: Runs in O(n)
     *
     * @param name the name of the card
     * @return integer id (-1 if not found)
     */
    public static int getImageIDFromName(String name) {
        for (int i = 0; i < cardNames.length; i++)
            if (name.equals(cardNames[i]))
                return i;
        return -1;
    }

    /**
     * Returns the string value name of the card based on a given id
     *
     * @param i the id of the card
     * @return the name if valid id, null otherwise
     */
    public static String getNameFromImageID(int i) {
        if (i < 0 || i > cardNames.length - 1)
            return null;
        return cardNames[i];
    }

    private String determineCardName(Properties.ACTION action, Properties.COLOR color) {
        return (color.equals(Properties.COLOR.NONE) ? action.toString().toLowerCase() : color.toString().toLowerCase()) +
                (value == 0 ? "" : value);
    }

    public int getId() {
        return id;
    }

    public Properties.ACTION getAction() {
        return action;
    }

    public Properties.COLOR getColor() {
        return color;
    }

    public int getValue() {
        return value;
    }

    public int getImageID() {
        return imageID;
    }

    public String toString(){
        return action == Properties.ACTION.NONE ? String.format("[%s %d]", color.toString(), value) : "[" + action.toString() + "]";
    }
}
