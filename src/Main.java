import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.newdawn.slick.SlickException;

import game.Game;
import game.GameContainer;

import javax.swing.*;

//Main Class of the Game
public class Main{
    

    public static void main(String args[]){
        
        try{

            String add = JOptionPane.showInputDialog(null,"Input Server Address");

            Game game = new Game("Magus Arena",add);
            GameContainer container = new GameContainer(game);

            container.start();
        }catch(Exception e){
        
            e.printStackTrace();
        }

    }

}
