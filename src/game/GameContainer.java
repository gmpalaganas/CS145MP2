package game;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.SlickException;

public class GameContainer extends AppGameContainer{

    private final int WINDOW_HEIGHT = 640;
    private final int WINDOW_WIDTH = 1200;
    private final boolean FULLSCREEN = false;

    public GameContainer(Game game) throws SlickException{
        super(game);
        setDisplayMode(WINDOW_WIDTH,WINDOW_HEIGHT, FULLSCREEN);
        setShowFPS(false);
    }

}

