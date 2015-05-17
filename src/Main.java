import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import game.Game;
import game.GameContainer;

//Main Class of the Game
public class Main{
    

    public static void main(){
        
        Game game = new Game("Game");
        GameContainer container = new GameContainer(game);

        container.start();


    }

}
