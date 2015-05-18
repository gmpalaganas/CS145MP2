package game.connection;

import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryo.Kryo;
import game.point.*;
import game.Projectile;
import game.Unit;
import org.newdawn.slick.Input;


import java.util.ArrayList;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Animation;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Color;


import java.io.*;

public class GameServer{

    private final int UDP_PORT = 8888;
    private final int TCP_PORT = 8889;

    private Server server;
    private Kryo kryo;
    private GameServerListener serverListener;

    public GameServer() throws IOException{

        server = new Server();
        kryo = server.getKryo();
        serverListener = new GameServerListener();
        
        server.addListener((Listener)serverListener);

        registerClasses();

    }

    public void registerClasses(){
        
        kryo.register(UnitMovementData.class);
        kryo.register(ProjectileMovementData.class);
        kryo.register(LogData.class);
        kryo.register(Point.class);
        kryo.register(Direction.class);

    }
   
    public void start() throws IOException{
        server.start();
        server.bind(TCP_PORT,UDP_PORT);
    }


    private class GameServerListener extends Listener{

       public GameServerListener(){
            
       }

       public void received (Connection connection, Object object){
               if(object instanceof UnitMovementData){

                   UnitMovementData uData = (UnitMovementData)object;
                   
                   if(uData.direction == Direction.DOWN){
                        uData.y += uData.delta * 0.1f;
                   }else if(uData.direction == Direction.UP){
                        uData.y -= uData.delta * 0.1f;
                   }else if(uData.direction == Direction.LEFT){
                        uData.x -= uData.delta * 0.1f;
                   }else if(uData.direction == Direction.RIGHT){
                        uData.x += uData.delta * 0.1f;
                   }

                   connection.sendUDP(uData);

               }else if(object instanceof ProjectileMovementData){

               }else if(object instanceof LogData){
                    System.out.println(((LogData)object).msg);
                    LogData ld = new LogData();
                    ld.msg = "SPACE ACK";
                    connection.sendTCP(ld);
               }

       }

    }
}
