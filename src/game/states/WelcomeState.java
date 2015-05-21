package game.states;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.GameState;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.transition.*;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape; 
import org.newdawn.slick.Color;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryo.Kryo;

import game.connection.*;

public class WelcomeState extends BasicGameState{

    private Image background;
    private String bgdir;
    private GameClient client;
    private String address;
   

    public WelcomeState(GameClient gameC, String addr) throws SlickException {
            
        bgdir = "../res/img/screen/start.png";
        client = gameC;
        address = addr;

    }

    public void init(GameContainer gc, StateBasedGame sbg) throws SlickException {
        background = new Image(bgdir);
    }

    public void render(GameContainer gc, StateBasedGame sbg, Graphics g) throws SlickException{
        g.drawImage(background,0,25);
        g.drawString("Press Enter to Continue...",480,345);
    }


    public void update(GameContainer gc, StateBasedGame sbg, int delta) throws SlickException{

        Input in = gc.getInput();

        if(in.isKeyPressed(Input.KEY_ENTER)){

            MessageData data = new MessageData();
            data.msg = "READY";
            client.send(data);
            sbg.enterState(2,new FadeOutTransition(), new FadeInTransition());

        }

    }

    public int getID(){
        return 1;
    }

}
