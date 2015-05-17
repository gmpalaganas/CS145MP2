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

import game.Unit;
import game.point.*;

public class MainGameState extends BasicGameState{

    private final int MAIN_GAME_STATE_ID = 0;
    TiledMap map;

    private Unit player1;

    public void init(GameContainer gc, StateBasedGame sbg) throws SlickException {
        map = new TiledMap("../res/map/map.tmx");
        player1 = new Unit("Gray", "../res/img/units/", 40, 100, 100, 0.5f, 0.5f);
        player1.setLocation(315,315,Direction.DOWN);
    }

    public void render(GameContainer gc, StateBasedGame sbg, Graphics g) throws SlickException{

        map.render(0,0);
        player1.render(g);

    }

    public void update(GameContainer gc, StateBasedGame sbg, int delta) throws SlickException{

    }

    public int getID(){
        return MAIN_GAME_STATE_ID;
    }

}
