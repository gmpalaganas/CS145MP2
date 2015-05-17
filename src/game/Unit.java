package game;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Animation;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Color;

import game.point.*;

public class Unit{

    private SpriteSheet sheetUp, sheetDown, sheetLeft, sheetRight;
    
    private Animation animUp, animDown,  animLeft, animRight;
    private Animation curAnimation;
    
    private Rectangle hitBox;

    private float health, mana;
    private float healthRegen, manaRegen;

    private Point curLocation;

    private String resourceDir;
    private String name;

    private int size; 

    private final int ANIMATION_SPEED = 700;

    public Unit(String unitName, String dir, int spriteSize, float health, float mana, float hRegen, float mRegen)
        throws SlickException{
        
        resourceDir = dir;
        name = unitName;
        size = spriteSize;
        
        healthRegen = hRegen;
        manaRegen = manaRegen;

        this.health = health;
        this.mana = mana;

        curLocation = new Point(0,0,Direction.DOWN);

        sheetUp = new SpriteSheet(dir + name + "/up.png", size, size);
        sheetDown = new SpriteSheet(dir + name + "/down.png", size, size);
        sheetLeft = new SpriteSheet(dir + name + "/left.png", size, size);
        sheetRight = new SpriteSheet(dir + name + "/right.png", size, size);

        animUp = new Animation(sheetUp,ANIMATION_SPEED);
        animDown = new Animation(sheetDown,ANIMATION_SPEED);
        animLeft = new Animation(sheetLeft,ANIMATION_SPEED);
        animRight = new Animation(sheetRight,ANIMATION_SPEED);

        curAnimation = animDown;
        
        float x = curLocation.x;
        float y = curLocation.y;


        hitBox = new Rectangle(x,y,size,size);

    }

    public void render(Graphics g){
        
       g.setColor(Color.red);
       g.draw(hitBox);
       curAnimation.draw(curLocation.x,curLocation.y); 

    }

    public void setLocation(float x, float y, Direction dir){
        curLocation.x = x;
        curLocation.y = y;
        curLocation.direction = dir;
        hitBox.setLocation(x,y);
    }

    public void setLocation(Point p){
        curLocation.x = p.x;
        curLocation.y = p.y;
        curLocation.direction = p.direction;
        hitBox.setLocation(p.x,p.y);
    }
        

}

