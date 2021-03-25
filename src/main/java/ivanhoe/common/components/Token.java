package ivanhoe.common.components;

import ivanhoe.utils.Properties;

import java.io.Serializable;

/**
 * Created by Yan on 2/6/2016.
 */
public class Token implements Serializable {

    public static final long serialVersionUID = 2L;

    private Properties.COLOR tokenColor;

    public Token(Properties.COLOR tokenColor) {
        this.tokenColor = tokenColor;
    }

    public Token(Token obj) {
        this.tokenColor = obj.getTokenColor();
    }

    public Properties.COLOR getTokenColor() {
        return tokenColor;
    }
}
