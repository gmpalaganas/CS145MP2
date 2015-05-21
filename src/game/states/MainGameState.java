package game.states;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.ConcurrentModificationException;

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
import org.newdawn.slick.Image;

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
    private final int MANA_COST = 10;

    private final float MAX_HEALTH = 100f;
    private final float MAX_MANA = 100f;
    private final float MANA_REGEN = 6.5f;
    private final float HEALTH_REGEN = 2f;

    private int counter;

    private final String RESOURCE_DIR = "../res/img/";

    private TiledMap map;
    private GameClient client;
    private int player;
    private Unit units[];
    private String projectileNames[];
    private SpriteSheet projectileSheets[];
    private Animation projectileAnimation[];

    private boolean isFirstLoad;

    private HashMap<Integer,Projectile> projectiles;

    public MainGameState(GameClient c, String addr) throws SlickException {
        client = c;
        units = new Unit[NUM_PLAYERS];
        projectileNames = new String[NUM_PLAYERS + 5];
        projectileSheets = new SpriteSheet[NUM_PLAYERS + 5];
        projectileAnimation = new Animation[NUM_PLAYERS + 5];
        projectileNames[0] = "white";
        projectileNames[1] = "snow";
        projectileNames[2] = "fire";
        projectileNames[3] = "green";
        projectileNames[4] = "blue";
        projectileNames[5] = "rock";
        projectileNames[6] = "yellow";
        player = -1;
        isFirstLoad = true;
       
        if(projectiles == null)
            projectiles = new HashMap<Integer,Projectile>();
    }

    public void init(GameContainer gc, StateBasedGame sbg) throws SlickException {

        units[0] = new Unit("Gray", RESOURCE_DIR + "units/", 40, MAX_HEALTH, MAX_MANA, HEALTH_REGEN, MANA_REGEN);
        units[1] = new Unit("Crystal", RESOURCE_DIR + "units/", 40, MAX_HEALTH, MAX_MANA, HEALTH_REGEN, MANA_REGEN);

        for(int i = 0; i < NUM_PLAYERS + 5; i++){
            projectileSheets[i] = new SpriteSheet(RESOURCE_DIR + "projectiles/" + projectileNames[i] + ".png",16,16); 
            projectileAnimation[i] = new Animation(projectileSheets[i],600);
        }

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

                }else if(object instanceof UnitResourceData){
                   UnitResourceData rdata = (UnitResourceData)object;
                   units[rdata.unitID].setResources(rdata);
                }else if(object instanceof ProjectileMovementData){
                    synchronized(projectiles){
                        try{
                            ProjectileMovementData pmdata = (ProjectileMovementData)object;
                            projectiles.get(pmdata.projectileID).setLocation(pmdata.point);
                        }catch(NullPointerException e){
                        }
                        
                    }
                }else if(object instanceof ProjectileRemovalData){
                    synchronized(projectiles){
                        ProjectileRemovalData rpdata = (ProjectileRemovalData)object;
                        projectiles.remove(rpdata.projectileID);
                    }
                }else if(object instanceof NewProjectileData){
                    NewProjectileData npdata = (NewProjectileData)object;
                    fire(npdata);
                }else if(object instanceof LogData){
                    System.out.println(((LogData)object).msg);
                }else if(object instanceof WinData){
                    
                    int state = 4;
                    WinData wData = (WinData)object;

                    if(wData.playerID == player)
                        state = 3;

                    sbg.enterState(state, new FadeOutTransition(), new FadeInTransition());

                }
            }
        };

        client.addListener(listener);
        MessageData projRequest = new MessageData();
        projRequest.msg = "GET PROJECTILES";
        client.send(projRequest);
 
    }

    public void render(GameContainer gc, StateBasedGame sbg, Graphics g) throws SlickException{

        updateUnitLocations();
        map.render(0,0);
        renderBars(g);

        for(int i = 0; i < NUM_PLAYERS; i++)
            units[i].render(g);

        renderProjectiles(g);


    }

    public void update(GameContainer gc, StateBasedGame sbg, int delta) throws SlickException{

        Input in = gc.getInput();
        checkProjectileHit();
        updateProjectiles(gc,delta);

        if(units[player].getHealth() < 1){
            
            System.out.println("HERE");
            WinData data = new WinData();
            data.playerID = (player == 0)?1:0;
            client.send(data);

        }

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

            if(in.isKeyPressed(Input.KEY_Z)){

                Unit unit = units[player];
                
                if(unit.getMana() >= MANA_COST){

                    ManaUseData mana = new ManaUseData();
                    mana.manaCost = MANA_COST;
                    mana.unitID = player;

                    NewProjectileData nProjData = new NewProjectileData();
                    nProjData.unitID = player;
                    nProjData.source = units[player].getLocation();
                    client.send(nProjData);
                    client.send(mana);
                }
                
            
            }
        }

    }

    public int getID(){
        return MAIN_GAME_STATE_ID;
    }

    public void setPlayer(int unitID){

        player = unitID;

    }

    public synchronized void fire(NewProjectileData data){
        
        int incX = 14;
        int incY = 16;

        data.source.x += incX;
        data.source.y += incY;
        
        try{

            if(data.unitID < NUM_PLAYERS){

                SpriteSheet sheet = projectileSheets[data.unitID];
                Animation anim = projectileAnimation[data.unitID];
                String name = projectileNames[data.unitID];
                Projectile newPew = new Projectile(name,sheet,anim,16,10,data.source,data.unitID);

                projectiles.put(data.projectileID,newPew);
                
            }else{
                Random rng = new Random();
                int i = rng.nextInt(5);
                int id = data.unitID + i;

                SpriteSheet sheet = projectileSheets[id];
                Animation anim = projectileAnimation[id];
                String name = projectileNames[id];
                Projectile newPew = new Projectile(name,sheet,anim,16,5,data.source,data.unitID);

                projectiles.put(data.projectileID,newPew);
            }


        }catch(SlickException e){

        }

    
    }


    public void updateProjectiles(GameContainer gc, int delta){
        

        synchronized(projectiles){
            try{
                for(Integer i : projectiles.keySet()){
                    int id = i.intValue();
                    ProjectileMovementData data = new ProjectileMovementData();
                    ProjectileRemovalData rData = new ProjectileRemovalData();
                    data.projectileID = id;
                    rData.projectileID = id;
                    data.point = projectiles.get(i).getLocation();
                    data.unitID = projectiles.get(i).getSourceID();
                    data.delta = delta;
                    if(isInMap(gc,data.point))
                        client.send(data);
                    else
                        client.send(rData);

                }
            }catch(ConcurrentModificationException e){ }
        }
        

    }

    public void updateUnitLocations(){
        
        MessageData data = new MessageData();
        data.msg = "GET LOCATIONS";
        client.send(data);

    }

   public boolean isInMap(GameContainer gc, Point p){
       
       boolean ret = (p.x <= gc.getWidth()) && (p.x >= 0);
       ret &= (p.y <= gc.getHeight()) && (p.y >= 0); 

       return ret;

   }

    public synchronized void renderProjectiles(Graphics g){
        
        synchronized(projectiles){
            for(Projectile p : projectiles.values()){
                
                p.render(g);

            }
        }

    }

    public synchronized void checkProjectileHit(){
        
        synchronized(projectiles){
            for(Integer id : projectiles.keySet()){
                Projectile p = projectiles.get(id);
                int x = 0;
                for(int i = 0; i < NUM_PLAYERS; i++){

                    if(p.getHitBox().intersects(units[i].getHitBox()) && i != p.getSourceID()){
                        HitData data = new HitData();
                        data.dmg = p.getDamage();
                        data.unitID = i;
                        data.projectileID = id;
                        client.send(data);              
                    }else if((x = checkProjectileCollision(id.intValue())) != -1){
                        ProjectileRemovalData rData = new ProjectileRemovalData();
                        ProjectileRemovalData rData2 = new ProjectileRemovalData();
                        rData.projectileID = id;
                        rData2.projectileID = x;
                        client.send(rData);
                        client.send(rData2);
                    }

                }
            }
        }

    }

    public int checkProjectileCollision(int id){
        
        Projectile p = projectiles.get(id);
        int ret = -1;

        synchronized(projectiles){
            for(int i : projectiles.keySet()){
                try{
                    Projectile current = projectiles.get(i);
                    boolean isSameSource = (p.getSourceID() == current.getSourceID());
                    if(id != i && p.getHitBox().intersects(current.getHitBox()) && !isSameSource){

                        ret = i;
                        break;

                    }
                }catch(NullPointerException e) { 
                }catch(ConcurrentModificationException e){

                }
            }

        }

        return ret;

    }

    public void renderBars(Graphics g){
        for(int i = 0; i < NUM_PLAYERS; i++){
            
            int x = (i == 0)?0:1200;
            int textX = (i==0)?300:900;
            int portraitX = (i==0)?0:1115;
    
            Image portrait = units[i].getPortrait();

            Rectangle healthBar = new Rectangle(x,640,600,25);
            float fillWidth = (units[i].getHealth() / MAX_HEALTH) * healthBar.getWidth();

            if(i == 1)
                fillWidth = -fillWidth;

            Rectangle fill = new Rectangle(healthBar.getX() , healthBar.getY(), fillWidth, healthBar.getHeight() );
            String hText = (int)units[i].getHealth() + "/" + (int)MAX_HEALTH;

            Color prevColor = g.getColor();
            g.setColor(Color.red);
            g.fill(fill);
            g.setColor(Color.white);
            g.drawString(hText,textX,640);

            Rectangle manaBar = new Rectangle(x,665,600,25);
            fillWidth = (units[i].getMana() / MAX_MANA) * manaBar.getWidth();

            if(i == 1)
                fillWidth = -fillWidth;

            fill = new Rectangle(manaBar.getX(), manaBar.getY(), fillWidth, manaBar.getHeight());
            hText = (int)units[i].getMana() + "/" + (int)MAX_MANA;

            g.setColor(Color.blue);
            g.fill(fill);
            g.setColor(Color.white);
            g.drawString(hText,textX,665);
            g.drawLine(600,640,600,690);
            g.setColor(prevColor);

            Color filter = (i == player)?Color.green:Color.red;
            
            g.drawImage(portrait,portraitX,620,filter);


        }
    }
}
