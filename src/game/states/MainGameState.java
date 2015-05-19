package game.states;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import org.newdawn.slick.Animation;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Input;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Image;
import org.newdawn.slick.tiled.TiledMap;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.GameState;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.transition.*;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape; 
import org.newdawn.slick.geom.ShapeRenderer; 
import org.newdawn.slick.particles.ParticleSystem; 
import org.newdawn.slick.particles.ParticleIO; 
import org.newdawn.slick.particles.ConfigurableEmitter; 
import org.newdawn.slick.Color;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryo.Kryo;

import game.Unit;
import game.Projectile;
import game.point.*;
import game.connection.*;

public class MainGameState extends BasicGameState{

    private final int MAIN_GAME_STATE_ID = 0;
    private final int NUM_PLAYERS = 2;

    private TiledMap map;
    private GameClient client;
    private int player;
    private Unit units[];

    private boolean isFirstLoad;

    public MainGameState(GameClient c, String addr) throws SlickException {
        client = c;
        units = new Unit[NUM_PLAYERS];
        player = -1;
        isFirstLoad = true;
    }

    public void init(GameContainer gc, StateBasedGame sbg) throws SlickException {

        units[0] = new Unit("Gray", "../res/img/units/", 40, 100, 100, 0.5f, 0.5f);

        units[1] = new Unit("Crystal", "../res/img/units/", 40, 100, 100, 0.5f, 0.5f);


        map = new TiledMap("../res/map/map.tmx");

        Listener listener = new Listener(){

            public void received (Connection connection, Object object){

                if(object instanceof UnitMovementData){
                    UnitMovementData uData = (UnitMovementData)object;

                    Point p = new Point(uData.x, uData.y, uData.direction); 
                    if(isFirstLoad){
                        isFirstLoad = false;
                        units[uData.unitID].setLocation(p);
                    }
                    else
                        units[uData.unitID].update(p,uData.delta);

                }else if(object instanceof ProjectileMovementData){

                }else if(object instanceof LogData){
                    System.out.println(((LogData)object).msg);
                }

            }
        };

        client.addListener(listener);

    }

    public void render(GameContainer gc, StateBasedGame sbg, Graphics g) throws SlickException{

        MessageData requestLocations = new MessageData();
        requestLocations.msg = "GET LOCATIONS";
        client.send(requestLocations);

        map.render(0,0);
        for(int i = 0; i < NUM_PLAYERS; i++)
            units[i].render(g);

    }

    public void update(GameContainer gc, StateBasedGame sbg, int delta) throws SlickException{

        Input in = gc.getInput();

        if(units[0].getHitBox().intersects(units[1].getHitBox()))
            System.out.println("BUNGUAN");

        if(player != -1){
            UnitMovementData uData = new UnitMovementData();
            Point p = units[player].getLocation();
            uData.x = p.x;
            uData.y = p.y;
            uData.unitID = player;
            uData.delta = delta;

            if(in.isKeyPressed(Input.KEY_SPACE)){
                System.out.println("SPAAAAAACE");
                LogData ld = new LogData();
                System.out.println("Cur Location (" + (int)p.x + "," + (int)p.y + ")");
                ld.msg = "SPACE";
                client.send(ld);
            }else if(in.isKeyDown(Input.KEY_DOWN)){
                uData.direction = Direction.DOWN;
                client.send(uData);
            }else if(in.isKeyDown(Input.KEY_UP)){
                uData.direction = Direction.UP;
                client.send(uData);
            }else if(in.isKeyDown(Input.KEY_LEFT)){
                uData.direction = Direction.LEFT;
                client.send(uData);
            }else if(in.isKeyDown(Input.KEY_RIGHT)){
                uData.direction = Direction.RIGHT;
                client.send(uData);
            }      
        }

    }

    public int getID(){
        return MAIN_GAME_STATE_ID;
    }

    public void setPlayer(int unitID){

        player = unitID;

    }
}
