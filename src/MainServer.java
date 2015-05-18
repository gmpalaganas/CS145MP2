import game.connection.*;
import game.*;
import game.point.*;
import java.io.*;

public class MainServer{

    public static void main(String args[]){
        
        try{

            GameServer server = new GameServer();
            server.start();

        }catch(IOException e){}
    }


}
