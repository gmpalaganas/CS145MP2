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

public class WaitingState extends BasicGameState{

    private Image background;
    private String bgdir;
    private GameClient client;
    private String address;
   

    public WaitingState(GameClient gameC, String addr) throws SlickException {
            
        bgdir = "../res/img/screen/wait.png";
        client = gameC;
        address = addr;

    }

    public void init(GameContainer gc, StateBasedGame sbg) throws SlickException {
        background = new Image(bgdir);

        Listener listener = new Listener(){

            public void received(Connection connection, Object object){
                
                if(object instanceof MessageData){
                    MessageData data = (MessageData)object;
                    if(data.msg.equals("START")){
                        System.out.println("GAME STARTS");
                        sbg.enterState(0, new FadeOutTransition(), new FadeInTransition());
                    }

                }
                

            }

        };

        client.addListener(listener);
    }

    public void render(GameContainer gc, StateBasedGame sbg, Graphics g) throws SlickException{
        g.drawImage(background,0,25);
    }


    public void update(GameContainer gc, StateBasedGame sbg, int delta) throws SlickException{

    }

    public int getID(){
        return 2;
    }

}
