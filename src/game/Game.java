package game;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.transition.*;
import org.newdawn.slick.state.GameState;
import org.newdawn.slick.Input;
import java.io.*;
import game.states.*;
import game.connection.*;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryo.Kryo;


public class Game extends StateBasedGame{
    
    private MainGameState mainGameState;
    private GameClient client;

    private final int TIMEOUT = 5000;
    
    public Game(String name, String addr) throws SlickException,IOException{
        super(name);
        client = new GameClient();
        mainGameState = new MainGameState(client,addr);

        try{

            Listener listener = new Listener(){
                
                public void received (Connection connection, Object object){
                    if(object instanceof UnitIDData){
                        UnitIDData data = (UnitIDData)object; 
                        mainGameState.setPlayer(data.id);      
                    }

                }
            };

            client.addListener(listener);
            client.connect(TIMEOUT,addr);


        }catch(IOException e){ 
            e.printStackTrace();
        }


    }


    public void initStatesList(GameContainer gc) throws SlickException{
        addState((GameState)mainGameState);
        
    }
    

}
