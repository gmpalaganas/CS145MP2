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

public class Game extends StateBasedGame{
    
    private MainGameState mainGameState;
    private GameClient client;
    
    public Game(String name, String addr) throws SlickException,IOException{
        super(name);
        client = new GameClient();
        mainGameState = new MainGameState(client,addr);
    }


    public void initStatesList(GameContainer gc) throws SlickException{
        addState((GameState)mainGameState);
        
    }

}
