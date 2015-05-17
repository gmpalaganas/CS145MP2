package game;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Animation;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.SlickException;

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

        sheetUp = new SpriteSheet(dir + name + "-up.png", size, size);
        sheetDown = new SpriteSheet(dir + name + "-down.png", size, size);
        sheetLeft = new SpriteSheet(dir + name + "-left.png", size, size);
        sheetRight = new SpriteSheet(dir + name + "-right.png", size, size);
        
        float x = curLocation.x;
        float y = curLocation.y;

        x += size/2;
        y += size/2;

        hitBox = new Rectangle(x,y,size,size);

    }

}

