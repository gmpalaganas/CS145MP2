package game;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.transition.*;
import org.newdawn.slick.state.GameState;
import org.newdawn.slick.Input;

import game.states.*;

public class Game extends StateBasedGame{
    
    GameState mainGameState;
    
    public Game(String name){
        super(name);
        
        mainGameState = new MainGameState();
        
    }


    public void initStatesList(GameContainer gc) throws SlickException{
        addState(mainGameState);
        
    }

}
