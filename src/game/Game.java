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
    private WelcomeState welcomeState;
    private WaitingState waitState;
    private WinState winState;
    private LoseState loseState;
    private GameClient client;

    private final int TIMEOUT = 5000;

    private final float MAX_HEALTH = 100f;
    private final float MAX_MANA = 100f;
    private final float MANA_REGEN = 6.5f;
    private final float HEALTH_REGEN = 2f;

    private final int NUM_PLAYERS = 2;

    private int playerID = -1;


    public Game(String name, String addr) throws SlickException,IOException{
        super(name);
        client = new GameClient();
        mainGameState = new MainGameState(client,addr);
        welcomeState = new WelcomeState(client,addr);
        waitState = new WaitingState(client,addr);
        loseState = new LoseState(client,addr);
        winState = new WinState(client,addr);

        try{

            Listener listener = new Listener(){
                
                public void received (Connection connection, Object object){
                    if(object instanceof UnitIDData){
                        UnitIDData data = (UnitIDData)object; 
                        mainGameState.setPlayer(data.id);      
                    }else if(object instanceof MessageData){
                        MessageData data = (MessageData)object;
                        if(data.msg.equals("UNIT STATS")){
                            for(int i = 0; i < NUM_PLAYERS; i++){
                                UnitStatData udata = new UnitStatData();
                                udata.unitID = i;
                                udata.maxHealth = MAX_HEALTH;
                                udata.maxMana = MAX_MANA;
                                udata.healthRegen = HEALTH_REGEN;
                                udata.manaRegen = MANA_REGEN;
                                client.send(udata);
                            }

                        }
                    }

                }
            };

            client.addListener(listener);
            client.connect(TIMEOUT,addr);


        }catch(IOException e){ 
            System.out.println("Unable to connect to server");
            System.exit(1);
        }


    }


    public void initStatesList(GameContainer gc) throws SlickException{
        addState((GameState)welcomeState);
        addState((GameState)mainGameState);
        addState((GameState)waitState);
        addState((GameState)winState);
        addState((GameState)loseState);
        
    }

    public void setPlayerID(int i){
        playerID = i;
    }
    

}
