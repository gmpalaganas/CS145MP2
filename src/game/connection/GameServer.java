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
import java.util.HashMap;

public class GameServer{

    private final int MAX_X = 1135;
    private final int MIN_X = 20;
    private final int MAX_Y = 576;
    private final int MIN_Y = 11;

    private final int UDP_PORT = 8888;
    private final int TCP_PORT = 8889;

    private final int UNIT_RADIUS = 20;
    private final int NUM_PLAYERS = 2;

    private Server server;
    private Kryo kryo;
    private GameServerListener serverListener;

    private int playerCount;
    
    private UnitMovementData uMData[];
    private UnitStatData uSData[];
    private UnitResourceData uRData[];
    
    private RegenThread regenThread;
    private int curProjectileID;

    private HashMap<Integer, ProjectileMovementData> pMData; 

    public GameServer() throws IOException{

        server = new Server();
        kryo = server.getKryo();
        serverListener = new GameServerListener();

        uRData = new UnitResourceData[NUM_PLAYERS];
        uSData = new UnitStatData[NUM_PLAYERS];
        uMData = new UnitMovementData[NUM_PLAYERS];

        pMData = new HashMap<Integer, ProjectileMovementData>();

        for(int i = 0; i < NUM_PLAYERS; i++){
            uMData[i] = new UnitMovementData();
            uSData[i] = new UnitStatData();
            uRData[i] = new UnitResourceData();
            uRData[i].unitID = i;
            
        }


        uMData[0].unitID = 0;
        uMData[0].x = MIN_X;
        uMData[0].y = (int)(MAX_Y / 2);
        uMData[0].direction = Direction.RIGHT;

        uMData[1].unitID = 1;
        uMData[1].x = MAX_X;
        uMData[1].y = (int)(MAX_Y / 2);
        uMData[1].direction = Direction.LEFT;
        
        server.addListener((Listener)serverListener);

        registerClasses();

        regenThread = new RegenThread();

    }

    public void registerClasses(){
        
        kryo.register(UnitMovementData.class);
        kryo.register(ProjectileMovementData.class);
        kryo.register(LogData.class);
        kryo.register(Point.class);
        kryo.register(Direction.class);
        kryo.register(MessageData.class);
        kryo.register(UnitIDData.class); 
        kryo.register(UnitResourceData.class);
        kryo.register(UnitStatData.class);
        kryo.register(NewProjectileData.class);

    }
    
   
    public void start() throws IOException{
        server.start();
        server.bind(TCP_PORT,UDP_PORT);
    }

    public void close() throws IOException{
         server.close();
    }

    public boolean checkPlayerCollision(){
        
        boolean ret = inRange(uMData[0].x,uMData[1].x, UNIT_RADIUS);
        ret &= inRange(uMData[0].y, uMData[1].y, UNIT_RADIUS);

        return ret;
    }

    public boolean inRange(float x, float center, int radius){
        
        return (center - radius) <= x && (center + radius) >= x;

    }

    public void updateLocation(UnitMovementData data){
        
        uMData[data.unitID] = data;

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

           if(playerCount == 1){
                MessageData data = new MessageData();
                data.type = "REQUEST";
                data.msg = "UNIT STATS";
                connection.sendTCP(data);
                regenThread.start();
           }
       }

       public void disconnected(Connection connection){
            playerCount--;
            if(playerCount < 1){
                server.close();
                System.exit(0);
            }
       }

       public void received (Connection connection, Object object){
               if(object instanceof UnitMovementData){

                   UnitMovementData uData = (UnitMovementData)object;
                   float x = uData.x;
                   float y = uData.y;
                   
                   if(uData.direction == Direction.DOWN && uData.y <= MAX_Y){
                        uData.y += uData.delta * 0.1f;
                   }else if(uData.direction == Direction.UP && uData.y >= MIN_Y){
                        uData.y -= uData.delta * 0.1f;
                   }else if(uData.direction == Direction.LEFT && uData.x >= MIN_X){
                        uData.x -= uData.delta * 0.1f;
                   }else if(uData.direction == Direction.RIGHT && uData.x <= MAX_X){
                        uData.x += uData.delta * 0.1f;
                   }


                   updateLocation(uData);
                   
                   if(checkPlayerCollision()){
                        uData.x = x;
                        uData.y = y;
                        updateLocation(uData);
                   }
                   //server.sendToAllUDP(uData);

               }else if(object instanceof ProjectileMovementData){
                   ProjectileMovementData pmdata = (ProjectileMovementData)object;
                    
                   float move = pmdata.delta * 0.25f;

                   switch(pmdata.point.direction){
                       case UP: { pmdata.point.y -= move; }; break;
                       case DOWN: { pmdata.point.y += move; }; break;
                       case RIGHT: { pmdata.point.x += move; }; break;
                       case LEFT: { pmdata.point.x -= move; }; break;
                   }

                   pMData.put(pmdata.projectileID,pmdata);

                   server.sendToAllUDP(pmdata);

               }else if(object instanceof LogData){
                    System.out.println(((LogData)object).msg);
                    LogData ld = new LogData();
                    ld.msg = "SPACE RECEIVED";
                    connection.sendTCP(ld);
               }else if(object instanceof MessageData){
                    MessageData data = (MessageData)object;
                    if(data.msg.equals("GET LOCATIONS")){
                        connection.sendUDP(uMData[0]);
                        connection.sendUDP(uMData[1]);
                        for(ProjectileMovementData mdata : pMData.values())
                            connection.sendUDP(mdata);
                    }else if(data.msg.equals("GET PROJECTILES")){
                        for( ProjectileMovementData dat : pMData.values() ){
                            NewProjectileData npd = new NewProjectileData();
                            npd.unitID = dat.unitID;
                            npd.projectileID = dat.projectileID;
                            npd.source = dat.point;
                            connection.sendUDP(npd);
                        }
                    }
               }else if(object instanceof UnitStatData){
                    UnitStatData data = (UnitStatData)object;
                    uSData[data.unitID] = data;                    
                    uRData[data.unitID].health = data.maxHealth;
                    uRData[data.unitID].mana = 0;
                    
               }else if(object instanceof NewProjectileData){
                    NewProjectileData data = (NewProjectileData)object;
                    data.projectileID = curProjectileID;
                    server.sendToAllUDP(data);
                    curProjectileID++;

               }

       }

    }

    private class RegenThread extends Thread{

        private boolean isRunning = true;
            
        public void run(){
            
           try{

               while(isRunning){
                   for(int i = 0; i < NUM_PLAYERS; i++){

                       if(uRData[i].mana < uSData[i].maxMana)
                            uRData[i].mana += uSData[i].manaRegen/10f;
                        
                       if(uRData[i].health < uSData[i].maxHealth)
                           uRData[i].health += uSData[i].healthRegen/10f;


                        server.sendToAllUDP(uRData[i]);
                   }                                         
                   Thread.sleep(100);
               }

           }catch(InterruptedException e){}

        }

        public void terminate(){
            isRunning = false;
        }

    }

}
