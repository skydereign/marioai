package cmps244;

import dk.itu.mario.MarioInterface.GamePlay;
import dk.itu.mario.MarioInterface.LevelGenerator;
import dk.itu.mario.MarioInterface.LevelInterface;

import java.nio.charset.Charset;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;

import cmps244.LevelEntity;
public class CustomizedGenerator implements LevelGenerator {
	
	public static String readFile(String path, Charset encoding) 
	  throws IOException {
	  byte[] encoded = Files.readAllBytes(Paths.get(path));
	  return new String(encoded, encoding);
	}

	public LevelInterface generateLevel(GamePlay playerMetrics) {
		return new CustomizedLevel(320,15,new Random().nextLong(),1,1,playerMetrics);
	}

	
	public LevelInterface generateLevel(String detailedInfo) {
		// TODO Auto-generated method stub
		return null;
	}
	

	public static LevelEntity[][] stringToLevelChunk(String str){
		
		String[] rows = str.split(";");
		String[] columns = rows[0].split(",");
		LevelEntity[][] levelChunk = new LevelEntity[columns.length][rows.length];
		int jj = 0;
		for (String row : rows){
			String[] entries = row.split(",");
			int ii = 0;
			for (String entry : entries){
				switch(entry){
				case "0.0":
					levelChunk[ii][jj] = LevelEntity.Empty;
					break;
				case "1.0":
					levelChunk[ii][jj] = LevelEntity.Solid;
					break;
				case "2.0":
					levelChunk[ii][jj] = LevelEntity.Destructible;
					break;
				case "3.0":
					levelChunk[ii][jj] = LevelEntity.PowerUp;
					break;
				case "4.0":
					levelChunk[ii][jj] = LevelEntity.CoinBlock;
					break;
				case "5.0":
					levelChunk[ii][jj] = LevelEntity.Enemy;
					break;
				case "6.0":
					levelChunk[ii][jj] = LevelEntity.Pipe;
					break;
				case "7.0":
					levelChunk[ii][jj] = LevelEntity.Coin;
					break;
				
				}
				ii++;
			}
			jj++;
		}
		return levelChunk;
	}
	
	public static String levelChunkToString(LevelEntity[][] levelChunk){
		int maxX = levelChunk.length;
		int maxY = levelChunk[0].length;
		String outStr = "";
		for (int jj = 0; jj < maxY; jj++){ 
			for (int ii = 0; ii < maxX; ii++){
				switch(levelChunk[ii][jj]){
					case Empty:
						outStr += " ";
						break;
					case Solid:
						outStr += "X";
						break;
					case Destructible:
						outStr += ".";
						break;
					case PowerUp:
						outStr += "P";
						break;
					case CoinBlock:
						outStr += "C";
						break;
					case Enemy:
						outStr += "E";
						break;
					case Pipe:
						outStr += "|";
						break;
					case Coin:
						outStr += "o";
						break;
				}
			}
			outStr += "\n";
		}
		return outStr;
	}

}
