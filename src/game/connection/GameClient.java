package game.connection;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryo.Kryo;
import game.point.*;
import game.Projectile;
import game.Unit;
import java.io.*;

import java.util.ArrayList;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Animation;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Color;

public class GameClient{
    
    private final int UDP_PORT = 8888;
    private final int TCP_PORT = 8889;

    private Client client;
    private Kryo kryo;
    private GameClientListener clientListener;
    
    public GameClient() throws IOException{
    
        client = new Client();
        kryo = client.getKryo();
        clientListener = new GameClientListener();

        client.addListener((Listener)clientListener);

        registerClasses();

    }

    public void connect(int timeout, String addr) throws IOException{
        
        client.start();
        client.connect(timeout,addr,TCP_PORT, UDP_PORT);

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
        kryo.register(ProjectileRemovalData.class);
        kryo.register(HitData.class);
        kryo.register(ManaUseData.class);
        kryo.register(WinData.class);

     }

    public void send(Object object){
        client.sendUDP(object);
    }


    public void addListener(Listener l){
        client.addListener(l);
    }

    private class GameClientListener extends Listener{
        
        public GameClientListener(){

        }

        public void received(Connection connection, Object object){

           if(object instanceof UnitMovementData){

           }else if(object instanceof ProjectileMovementData){

           }else if(object instanceof LogData){
                System.out.println(((LogData)object).msg);
           }
        }

    }


}
