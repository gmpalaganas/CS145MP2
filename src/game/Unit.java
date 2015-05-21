package game;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Animation;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Color;
import org.newdawn.slick.Image;

import game.point.*;
import game.connection.*;

public class Unit{

    private SpriteSheet sheetUp, sheetDown, sheetLeft, sheetRight;
    private Image portrait;
    
    private Animation animUp, animDown,  animLeft, animRight;
    private Animation curAnimation;
    
    private Rectangle hitBox;
    private Rectangle healthBar, manaBar;

    private float health, mana;
    private float maxHealth, maxMana;
    private float healthRegen, manaRegen;

    private Point curLocation;

    private String resourceDir;
    private String name;

    private int size; 

    private final int ANIMATION_SPEED = 700;
    private final int REGEN_INTERVAL = 100;

    public Unit(String unitName, String dir, int spriteSize, float health, float mana, float hRegen, float mRegen)
        throws SlickException{
        
        resourceDir = dir;
        name = unitName;
        size = spriteSize;
        
        healthRegen = hRegen;
        manaRegen = manaRegen;

        maxHealth = health;
        maxMana = mana;

        this.mana = 0;
        this.health = maxHealth;

        curLocation = new Point(0,0,Direction.DOWN);

        sheetUp = new SpriteSheet(dir + name + "/up.png", size, size);
        sheetDown = new SpriteSheet(dir + name + "/down.png", size, size);
        sheetLeft = new SpriteSheet(dir + name + "/left.png", size, size);
        sheetRight = new SpriteSheet(dir + name + "/right.png", size, size);
        portrait = new Image(dir + name + "/portrait.png");

        animUp = new Animation(sheetUp,ANIMATION_SPEED);
        animDown = new Animation(sheetDown,ANIMATION_SPEED);
        animLeft = new Animation(sheetLeft,ANIMATION_SPEED);
        animRight = new Animation(sheetRight,ANIMATION_SPEED);

        curAnimation = animDown;
        
        float x = curLocation.x;
        float y = curLocation.y;


        hitBox = new Rectangle(x,y,size,size);
        healthBar = new Rectangle(x, y + (size/2), size, 5);
        manaBar = new Rectangle(x, y + (size/2) + 5, size, 5);

    }

    public void render(Graphics g){
        
       //g.setColor(Color.red);
       //g.draw(hitBox);
       drawHealthBar(g);
       drawManaBar(g);
       curAnimation.draw(curLocation.x,curLocation.y); 

    }

    public void drawManaBar(Graphics g){
        
        float fillWidth = (mana / maxMana) * manaBar.getWidth();
        Rectangle fill = new Rectangle(manaBar.getX(), manaBar.getY(), fillWidth, manaBar.getHeight());
        
        Color prevColor = g.getColor();
        g.setColor(Color.black);
        g.draw(manaBar);
        g.setColor(Color.blue);
        g.fill(fill);
        g.setColor(prevColor);

    }


    public void drawHealthBar(Graphics g){
        
        float fillWidth = (health / maxHealth) * healthBar.getWidth();
        Rectangle fill = new Rectangle(healthBar.getX() , healthBar.getY(), fillWidth, healthBar.getHeight() );
        
        Color prevColor = g.getColor();
        g.setColor(Color.black);
        g.draw(healthBar);
        g.setColor(Color.red);
        g.fill(fill);
        g.setColor(prevColor);

    }

    public void update(Point p, int delta){
        
        if(p.direction == Direction.UP){
            curAnimation = animUp;
        }else if(p.direction == Direction.DOWN){
            curAnimation = animDown;
        }else if(p.direction == Direction.LEFT){
            curAnimation = animLeft;
        }else if(p.direction == Direction.RIGHT){
            curAnimation = animRight;
        }


        curLocation = p;
        setLocation(p);
        curAnimation.update(delta);

    }


    public void setLocation(Point p){
        
        curLocation = p;
        hitBox.setLocation(p.x,p.y);
        manaBar.setLocation(p.x, p.y  + size + 5);
        healthBar.setLocation(p.x , p.y + size);
    }

    public Rectangle getHitBox(){
        return hitBox;
    }

    public Point getLocation(){
        return curLocation;
    }

    public String getName(){
        return name;
    }

    public void setHealth(float h){
        health = h;
    }

    public void setMana(float m){
        mana = m;
    }

    public void setResources(UnitResourceData data){
        
        health = data.health;
        mana = data.mana;

    }

    public float getHealth(){ return health; }
    public float getMana(){ return mana; }
    public float getHealthRegen(){ return healthRegen; }
    public float getManaRegen(){ return manaRegen; }
    public float getMaxHealth(){ return maxHealth; }
    public float getMaxMana(){ return maxMana; }
    public Image getPortrait(){ return portrait; }

}

