import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.newdawn.slick.SlickException;

import game.Game;
import game.GameContainer;


//Main Class of the Game
public class Main{
    

    public static void main(String args[]){
        
        try{
            Game game = new Game("Magus Arena","localhost");
            GameContainer container = new GameContainer(game);

            container.start();
        }catch(Exception e){
        
            e.printStackTrace();
        }

    }

}
