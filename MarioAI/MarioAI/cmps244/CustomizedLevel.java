package cmps244;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Random;

import org.json.JSONObject;

import dk.itu.mario.level.Level;
import dk.itu.mario.MarioInterface.Constraints;
import dk.itu.mario.MarioInterface.GamePlay;
import dk.itu.mario.MarioInterface.LevelInterface;
import dk.itu.mario.engine.sprites.SpriteTemplate;
import dk.itu.mario.engine.sprites.Enemy;
import dk.itu.mario.engine.LevelFactory;

public class CustomizedLevel extends Level implements LevelInterface {
	public static final byte  CANNON = (byte) (14 + 0 * 16);
	public static final byte  CANNON_CONNECTOR = (byte) (14 + 1 * 16);
	public static final byte  CANNON_POLE = (byte) (14 + 2 * 16);
    public CustomizedLevel(int width, int height, long seed, int difficulty,
                           int type, GamePlay playerMetrics) {
        super(width, height);
        create();
    }
    public String[] getTiles(){
    	try {
			String tileFile = (CustomizedGenerator.readFile("../Mario Levels/tiles.json",Charset.defaultCharset()));
			JSONObject json = new JSONObject(tileFile);
			String[] tiles = JSONObject.getNames(json);
			ArrayList<LevelNode> list = new ArrayList<LevelNode>();
			for (String tile : tiles){
				
				list.add(new LevelNode(tile,json));
			}
			return tiles;
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

    }
    public void create(){
    	long startTime = System.nanoTime();
    	String startString = "";
    	startString = "0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,;0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,;0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,;0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,;0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,;0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,;0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,;0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,;0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,;0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,;0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,;0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,;0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,;1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,;";
    	LevelNode node = new LevelNode(startString,getTiles());
		//LevelNode node = new LevelNode(getTiles());
		while ( node.getTotalSize() < this.width-16){
			node = node.selectAction();
		}
		System.out.println(node.getTotalSize());
		System.out.println((System.nanoTime()-startTime)/1000000000.0);
    	startTime = System.nanoTime();
		while (node != null){
			int pos = node.getTotalSize()-node.getSize();
			fillLevel(pos,0,node.levelChunk,1);
			node = node.parent;
		}
		postProcessing();
		System.out.println((System.nanoTime()-startTime)/1000000000.0);
    }
    public void postProcessing(){
    	
    	/*
    	addExit();
    	for (int ii = 0; ii < width; ii++){
    		for (int jj = 0; jj < height; jj++){
    			if (getBlock(ii,jj) == GROUND){
    				setBlock(ii,jj,ROCK);
    			}
    		}
    	}*/
    	addExit();
    	fixCannons();
    	
    	//For some reason the bottom is cut off
    	for (int xx = 0; xx < width; xx++){
    		if (getBlock(xx,13) == GROUND){
    			setBlock(xx,14,GROUND);
    		}
    	}
    	fixPipes();
    	
    	
    	fixWalls();
    	fixHills();
    	
    }
    private void addExit(){

    	int edgeX = 0, edgeY = 0;
		boolean foundGround = false;
    	for (int xx = 1; xx <= 16; xx++){
    		
    		for (int yy = height-1; yy >= 0; yy--){
    			byte c = getBlock(width-xx,yy);
    			if (c == 0 && foundGround){
    				edgeY = yy-1;
    				break;
    			}
    			foundGround = foundGround || (c == GROUND);
    			
    		}
    		if (foundGround){
    			edgeX = xx;
    			break;
    		}
    	}
    	if (foundGround){
	    	for (int xx = 1; xx <= edgeX; xx++){
	    		for (int yy = edgeY; yy < height; yy++){
	    			setBlock(width-xx,yy,GROUND);
	    		}
	    	}
    	}
    	else {

	    	for (int xx = 1; xx <= 16; xx++){
	    		for (int yy = height-3; yy < height; yy++){
	    			setBlock(width-xx,yy,GROUND);
	    		}
	    	}
    	}//place the exit
    	boolean foundExit = false;
    	for (int offset = 0; offset <= 10 && !foundExit; offset++){
    		int xx = width-8+ ((offset % 2)*2-1)*(offset/2);
    		for (int yy = 0; yy < height && !foundExit; yy++){
    			if (getBlock(xx,yy) == GROUND){
    				if (getBlock(xx-1,yy) == GROUND && getBlock(xx+1,yy) == GROUND &&
    						getBlock(xx-1,yy-1) == 0 && getBlock(xx+1,yy-1) == 0){
    					foundExit = true;
    					this.xExit = xx;
    					this.yExit = yy;
    				}
    				else {
    					break;
    				}
    			}
    		}
    	}
    }
    private void fixCannons(){

    	for (int xx = 0 ; xx < width ; xx++){
    		for (int yy = 0; yy < height; yy++){
    			if (getBlock(xx,yy) == 0){
	    			if (getBlock(xx,yy-1) == CANNON){
	    				setBlock(xx,yy,CustomizedLevel.CANNON_CONNECTOR);
	    			}
	    			if (getBlock(xx,yy-1) == CANNON_CONNECTOR || getBlock(xx,yy-1) ==  CANNON_POLE){
	    				setBlock(xx,yy,CustomizedLevel.CANNON_POLE);
	    			}
    			}
    		}
    	}
    }
    public void fixPipes(){
    	for (int xx = 0; xx < width; xx++){
    		for (int yy = 0; yy < height; yy++){
    			if (getBlock(xx,yy) == TUBE_TOP_LEFT){
    				fillPipeDown(xx,yy);
    			}
    		}	
    	}
    }
    public void fillPipeDown(int xx, int yy){
    	setBlock(xx,yy,TUBE_TOP_LEFT);
    	setBlock(xx+1,yy,TUBE_TOP_RIGHT);
    	for (int ny = yy+1; ny < height; ny++){
    		if (getBlock(xx,ny) == GROUND){
    			return;
    		}
    		else {
    			setBlock(xx,ny,TUBE_SIDE_LEFT);
    			setBlock(xx+1,ny,TUBE_SIDE_RIGHT);
    		}
    	}
    }
    public void fillLevel(int left, int top, String str, int difficulty ){
		String[] rows = str.split(";");

		for (int yy = top; yy < rows.length; yy++){
			String[] columns = rows[yy-top].split(",");
			for (int xx = left; xx < left+columns.length; xx++){
				switch(columns[xx-left]){
				case "1.0":
					setBlock(xx, yy, GROUND);
					break;
				case "2.0":
					setBlock(xx, yy, BLOCK_EMPTY); 
					break;
				case "3.0":
					setBlock(xx, yy, BLOCK_POWERUP); 
					break;
				case "4.0":
					setBlock(xx, yy, BLOCK_COIN);
					break;
				case "5.0":
					setSpriteTemplate(xx, yy,
                            new SpriteTemplate(Enemy.ENEMY_GOOMBA, false));
					//levelChunk[ii][jj] = LevelEntity.Enemy;
					break;
				case "6.0":
					setBlock(xx, yy, TUBE_TOP_LEFT);//levelChunk[ii][jj] = LevelEntity.Pipe;
					break;
				case "7.0":
					setBlock(xx, yy,COIN);
					break;
				case "8.0":
					setBlock(xx, yy,CANNON);
					break;
				default:
					break;
				}
				
			}
		}
	}
    private void fixWalls()
    {
        boolean[][] blockMap = new boolean[width + 1][height + 1];

        for (int x = 0; x < width + 1; x++)
        {
            for (int y = 0; y < height + 1; y++)
            {
                int blocks = 0;
                for (int xx = x - 1; xx < x + 1; xx++)
                {
                    for (int yy = y - 1; yy < y + 1; yy++)
                    {
                        if (getBlockCapped(xx, yy) == GROUND){
                        	blocks++;
                        }
                    }
                }
                blockMap[x][y] = blocks == 4;
            }
        }
        blockify(this, blockMap, width + 1, height + 1);
        for (int x = 0; x < width + 1; x++)
        {
            for (int y = 0; y < height + 1; y++)
            {
		        if (!blockMap[x][y] && getBlockCapped(x,y) == GROUND){
		        	//TODO figure this out
		        	//setBlock(x,y,HILL_TOP_LEFT);
		        	setBlock(x,y,ROCK);
		        }
            }
        }
    }
    private void fixHills(){
    	for (int xx = 0; xx < width; xx++){
    		for (int yy= 0; yy < height; yy++){
    			if (getBlock(xx,yy) == HILL_TOP_LEFT){
    				int nx = xx+1;
    				fillHillDown(xx,yy,1);
    				for (; getBlock(nx,yy) == HILL_TOP_LEFT; nx++){
    					setBlock(nx,yy,HILL_TOP);
        				fillHillDown(nx,yy,0);
    				}
    				setBlock(nx,yy,HILL_TOP_RIGHT);
    				fillHillDown(nx,yy,-1);
    			}
    		}
    	}
    }
    private void fillHillDown(int xx, int yy,int side){
    	for (int ny = yy+1; ny < height; ny++){
    		if (getBlock(xx,ny) == 0 || getBlock(xx,ny) == HILL_TOP_LEFT){
    			if (side == 0){
    				setBlock(xx,ny,Level.HILL_FILL);
    			}
    			else if (side == 1){
    				setBlock(xx,ny,Level.HILL_LEFT);    				
    			}
    			else if (side == -1){
    				setBlock(xx,ny,Level.HILL_RIGHT);    				
    			}
    		}
    	}
    }
    private void blockify(Level level, boolean[][] blocks, int width, int height){
        int to = 0;
        /*
        if (type == LevelInterface.TYPE_CASTLE)
        {
            to = 4 * 2;
        }
        else if (type == LevelInterface.TYPE_UNDERGROUND)
        {
            to = 4 * 3;
        }
*/
        boolean[][] b = new boolean[2][2];

        for (int x = 0; x < width; x++)
        {
            for (int y = 0; y < height; y++)
            {
                for (int xx = x; xx <= x + 1; xx++)
                {
                    for (int yy = y; yy <= y + 1; yy++)
                    {
                        int _xx = xx;
                        int _yy = yy;
                        if (_xx < 0) _xx = 0;
                        if (_yy < 0) _yy = 0;
                        if (_xx > width - 1) _xx = width - 1;
                        if (_yy > height - 1) _yy = height - 1;
                        b[xx - x][yy - y] = blocks[_xx][_yy];
                    }
                }

                if (b[0][0] == b[1][0] && b[0][1] == b[1][1])
                {
                    if (b[0][0] == b[0][1])
                    {
                        if (b[0][0])
                        {
                            level.setBlock(x, y, (byte) (1 + 9 * 16 + to));
                        }
                        else
                        {
                        	
                            // KEEP OLD BLOCK!
                        }
                    }
                    else
                    {
                        if (b[0][0])
                        {
                        	//down grass top?
                            level.setBlock(x, y, (byte) (1 + 10 * 16 + to));
                        }
                        else
                        {
                        	//up grass top
                            level.setBlock(x, y, (byte) (1 + 8 * 16 + to));
                        }
                    }
                }
                else if (b[0][0] == b[0][1] && b[1][0] == b[1][1])
                {
                    if (b[0][0])
                    {
                    	//right grass top
                        level.setBlock(x, y, (byte) (2 + 9 * 16 + to));
                    }
                    else
                    {
                    	//left grass top
                        level.setBlock(x, y, (byte) (0 + 9 * 16 + to));
                    }
                }
                else if (b[0][0] == b[1][1] && b[0][1] == b[1][0])
                {
                    level.setBlock(x, y, (byte) (1 + 9 * 16 + to));
                }
                else if (b[0][0] == b[1][0])
                {
                    if (b[0][0])
                    {
                        if (b[0][1])
                        {
                            level.setBlock(x, y, (byte) (3 + 10 * 16 + to));
                        }
                        else
                        {
                            level.setBlock(x, y, (byte) (3 + 11 * 16 + to));
                        }
                    }
                    else
                    {
                        if (b[0][1])
                        {
                        	//right up grass top
                            level.setBlock(x, y, (byte) (2 + 8 * 16 + to));
                        }
                        else
                        {
                        	//left up grass top
                            level.setBlock(x, y, (byte) (0 + 8 * 16 + to));
                        }
                    }
                }
                else if (b[0][1] == b[1][1])
                {
                    if (b[0][1])
                    {
                        if (b[0][0])
                        {
                        	//left pocket grass
                            level.setBlock(x, y, (byte) (3 + 9 * 16 + to));
                        }
                        else
                        {
                        	//right pocket grass
                            level.setBlock(x, y, (byte) (3 + 8 * 16 + to));
                        }
                    }
                    else
                    {
                        if (b[0][0])
                        {
                            level.setBlock(x, y, (byte) (2 + 10 * 16 + to));
                        }
                        else
                        {
                            level.setBlock(x, y, (byte) (0 + 10 * 16 + to));
                        }
                    }
                }
                else
                {
                    level.setBlock(x, y, (byte) (0 + 1 * 16 + to));
                }
            }
        }
    }
    
}