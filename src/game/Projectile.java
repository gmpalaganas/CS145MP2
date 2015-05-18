package game;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Animation;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Color;

import game.point.*;

public class Projectile{

    private String name;
    private String sourceName;
    private String resourceDir;

    private SpriteSheet sheet;
    private Animation anim;

    private Rectangle hitBox;

    private Point curLocation;

    private int size;
    private float damage;

    private final int ANIMATION_SPEED = 1400;

    public Projectile(String name,String source, String dir, int spriteSize, float dmg, Point p) throws SlickException{
        
       resourceDir = dir;
       sourceName = source;
       this.name = name;

       size = spriteSize;
       damage = dmg;

       curLocation = p;

       sheet = new SpriteSheet(dir + "/projectiles/" + name + ".png", size,size);

       anim = new Animation(sheet,ANIMATION_SPEED);

        hitBox = new Rectangle(curLocation.x, curLocation.y, size, size); 



    }

    public void render(Graphics g){
       
        anim.draw(curLocation.x,curLocation.y);

    }


    public setLocation(float x, float y, Direction dir){
        
        curLocation.x = x;
        curLocation.y = y;
        curLocation.direction - dir;
    
    }

    public void setLocation(Point p){
        
        curLocation = p;
        hitBox.setLocation(p.x,p.y);

    }
    
}
