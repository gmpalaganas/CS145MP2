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

    private final int MAX_X = 1135;
    private final int MIN_X = 20;
    private final int MAX_Y = 576;
    private final int MIN_Y = 11;

    private final int UDP_PORT = 8888;
    private final int TCP_PORT = 8889;

    private Server server;
    private Kryo kryo;
    private GameServerListener serverListener;

    private int playerCount;
    
    private UnitMovementData uMData1;
    private UnitMovementData uMData2;

    public GameServer() throws IOException{

        server = new Server();
        kryo = server.getKryo();
        serverListener = new GameServerListener();
        uMData1 = new UnitMovementData();
        uMData2 = new UnitMovementData();

        uMData1.unitID = 0;
        uMData1.x = MIN_X;
        uMData1.y = (int)(MAX_Y / 2);
        uMData1.direction = Direction.RIGHT;

        uMData2.unitID = 1;
        uMData2.x = MAX_X;
        uMData2.y = (int)(MAX_Y / 2);
        uMData2.direction = Direction.LEFT;
        
        server.addListener((Listener)serverListener);

        registerClasses();

    }

    public void registerClasses(){
        
        kryo.register(UnitMovementData.class);
        kryo.register(ProjectileMovementData.class);
        kryo.register(LogData.class);
        kryo.register(Point.class);
        kryo.register(Direction.class);
        kryo.register(MessageData.class);
        kryo.register(UnitIDData.class); 
    }
    
   
    public void start() throws IOException{
        server.start();
        server.bind(TCP_PORT,UDP_PORT);
    }

    public void close() throws IOException{
         server.close();
    }


    private class GameServerListener extends Listener{

       public GameServerListener(){
            
       }

       public void connected(Connection connection){
           if(playerCount < 2){
               UnitIDData data = new UnitIDData();
               data.id = playerCount;
               connection.sendTCP(data);
               playerCount++;
           }
       }

       public void disconnected(Connection connection){
            playerCount--;
       }

       public void received (Connection connection, Object object){
               if(object instanceof UnitMovementData){

                   UnitMovementData uData = (UnitMovementData)object;
                   
                   if(uData.direction == Direction.DOWN && uData.y <= MAX_Y){
                        uData.y += uData.delta * 0.1f;
                   }else if(uData.direction == Direction.UP && uData.y >= MIN_Y){
                        uData.y -= uData.delta * 0.1f;
                   }else if(uData.direction == Direction.LEFT && uData.x >= MIN_X){
                        uData.x -= uData.delta * 0.1f;
                   }else if(uData.direction == Direction.RIGHT && uData.x <= MAX_X){
                        uData.x += uData.delta * 0.1f;
                   }

                   if(uData.unitID == 0)
                       uMData1 = uData;
                   else
                       uMData2 = uData;

                   server.sendToAllUDP(uData);

               }else if(object instanceof ProjectileMovementData){

               }else if(object instanceof LogData){
                    System.out.println(((LogData)object).msg);
                    LogData ld = new LogData();
                    ld.msg = "SPACE RECEIVED";
                    connection.sendTCP(ld);
               }else if(object instanceof MessageData){
                    MessageData data = (MessageData)object;
                    if(data.msg.equals("GET LOCATIONS")){
                        connection.sendTCP(uMData1);
                        connection.sendTCP(uMData2);
                    }
               }

       }

    }
}
