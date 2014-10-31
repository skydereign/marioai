package cmps244;

import java.lang.Math;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;

import org.json.JSONObject;

import cmps244Lib.WeightPair;
import dk.itu.mario.engine.sprites.Enemy;
import dk.itu.mario.engine.sprites.SpriteTemplate;
import dk.itu.mario.level.Level;


public class LevelNode {
	static Random rand = new Random();
	public ArrayList<WeightPair<String> >  potentialRight;
	public static Hashtable<String,LevelNode> levelPieces = new Hashtable<String,LevelNode>();
	public static Hashtable<String,LevelEntity[][]> levelInstantiations = new Hashtable<String,LevelEntity[][]>();
	public String levelChunk;
	public int position;
	public static double K = Math.sqrt(2);
	public static int levelSize = 320;
	public static int numIterations = 1000;
	static double epsilon = 1e-6;
	public LevelNode parent;
	ArrayList<LevelNode> children;
	public double nVisits, totValue;
	protected int size = -1;
	public LevelNode(String lchunk,String[] allChunks){
		levelChunk = lchunk;
		potentialRight = new ArrayList<WeightPair<String> >();
		for (String chunk : allChunks){
			potentialRight.add(new WeightPair<String>(1,chunk));
		}
		children = new ArrayList<LevelNode>();

	}
	public LevelNode(String chunk,JSONObject json){
		levelChunk = chunk;
		JSONObject directions = json.getJSONObject(chunk);
		JSONObject rightJSON = directions.getJSONObject("right");
		//	JSONObject leftJSON = directions.getJSONObject("left");

		String[] rightNodes = JSONObject.getNames(rightJSON);

		potentialRight = new ArrayList<WeightPair<String> >();
		if (rightNodes != null){
			for (String node : rightNodes){
				potentialRight.add(new WeightPair<String>(rightJSON.getInt(node),node));
			}
		}
		children = new ArrayList<LevelNode>();
		getSize();
		/*
		String[] leftNodes = JSONObject.getNames(leftJSON);
		potentialLeft = new ArrayList<WeightPair<String> >();
		for (String node : leftNodes){
			potentialLeft.add(new WeightPair<String>(leftJSON.getInt(node),node));
		}
		 */
		levelInstantiations.put(chunk, getLevel(chunk));
		levelPieces.put(chunk, this);
	}

	public LevelNode(LevelNode other){

		potentialRight = new ArrayList<WeightPair<String> >(other.potentialRight);
		levelChunk = other.levelChunk;
		children = new ArrayList<LevelNode>();
		size = other.size;
	}

    public void backUp(LevelNode node, double result)   {
    	LevelNode n = node;
        while(n != null)
        {
            n.nVisits++;
            n.totValue += result;
            n = n.parent;
        }
    }
	public LevelNode selectAction() {
		for (int ii =0; ii < numIterations; ii++){
			LevelNode selected = select();
			double delta = selected.rollOut();
			backUp(selected,delta);
		}
		return bestAction();
		/*
        List<LevelNode> visited = new LinkedList<LevelNode>();
        LevelNode cur = this;
        visited.add(this);
        for (int ii = 0; ii < numIterations; ii++){
        	cur = this;
	        while (!cur.isLeaf()) {
	            cur = cur.select();
	            visited.add(cur);
	        }
	        cur.expand();
	        LevelNode newNode = cur.select();
	        visited.add(newNode);
	        double value = rollOut(newNode);
	        for (LevelNode node : visited) {
	            node.updateStats(value);
	        }
        }
        double maxValue = Double.NEGATIVE_INFINITY;
        LevelNode best = null;
        for (LevelNode child : children){
        	if (child.totValue > maxValue){
        		maxValue = child.totValue;
        		best = child;
        	}
        }
        return best;
		 */
	}
    public LevelNode bestAction()
    {
        int selected = -1;
        double bestValue = Double.NEGATIVE_INFINITY;

        for (int ii=0; ii<children.size(); ii++) {

            double tieBreaker = rand.nextDouble() * epsilon;
            if(children.get(ii) != null && children.get(ii).totValue + tieBreaker > bestValue) {
                bestValue = children.get(ii).totValue + tieBreaker;
                selected = ii;
            }
        }

        if (selected == -1)
        {
            System.out.println("Unexpected selection!");
            selected = 0;
        }

        return children.get(selected);
    }
	private LevelNode select() {
		LevelNode current = this;
		while (current.position < levelSize){
			if (current.notFullyExpanded()){
				return current.expand();	    			
			}
			else {
				current = current.uct();
			}
		}
		return current;
		/*
	        LevelNode selected = null;
	        double bestValue = Double.NEGATIVE_INFINITY;
	        for (LevelNode c : children) {
	            double uctValue = c.totValue / (c.nVisits + epsilon) +
	                       Math.sqrt(Math.log(nVisits+1) / (c.nVisits + epsilon)) +
	                           rand.nextDouble() * epsilon;
	            // small random number to break ties randomly in unexpanded nodes
	            if (uctValue > bestValue) {
	                selected = c;
	                bestValue = uctValue;
	            }
	        }
	        return selected;
		 */

	}
	private boolean notFullyExpanded() {
		return potentialRight.size() > 0;
	}
	public LevelNode uct(){

		LevelNode selected = null;
		double bestValue = Double.NEGATIVE_INFINITY;
		for (LevelNode child : children)
		{
			double hvVal = child.totValue;
			double childValue =  hvVal / (child.nVisits + LevelNode.epsilon);

			double uctValue = childValue +
					LevelNode.K * Math.sqrt(Math.log(this.nVisits + 1) / (child.nVisits + LevelNode.epsilon)) +
					LevelNode.rand.nextDouble() * LevelNode.epsilon;

			// small sampleRandom numbers: break ties in unexpanded nodes
			if (uctValue > bestValue) {
				selected = child;
				bestValue = uctValue;
			}
		}

		return selected;
	}
	public LevelNode expand(){
		WeightPair<String> pair = WeightPair.GetWeightPair(this.potentialRight,rand.nextFloat());
		String nextChunk = pair.obj;
		this.potentialRight.remove(pair);
		LevelNode nextPiece = new LevelNode(levelPieces.get(nextChunk));
		nextPiece.parent = this;
		children.add(nextPiece);
		return nextPiece;
	}
	public boolean isLeaf() {
		return children.size() == 0;
	}
	public int getSize(){
		if (levelChunk == ""){
			return 0;
		}
		if (size < 0){
			size = levelChunk.split(";")[0].split(",").length;
		}
		return size;
	}
	public int getTotalSize(){
		if (parent == null){
			return  getSize();
		}
		else {
			return getSize()+parent.getTotalSize();
		}
	}
	public double rollOut() {
		ArrayList<LevelNode> restOfLevel = new ArrayList<LevelNode>();
		randomLevelCreation(restOfLevel,levelSize-getTotalSize());

		return EvaluateLevel(restOfLevel);

	}
	public double EvaluateLevel(ArrayList<LevelNode> restOfLevel){
		LevelEntity[][] level = constructLevel(restOfLevel);
		int[] enemyDanger = getEnemyDanger(level);
		double[] enemyStatistics = new double[5];
		getStatistics(enemyDanger,enemyStatistics);


		int[] gapDanger = getGapDanger(level);
		double[] gapStatistics = new double[5];
		getStatistics(gapDanger,gapStatistics);
		
		int[] danger = new int[gapDanger.length];
		for (int ii= 0; ii < danger.length; ii++){
			danger[ii] = Math.min(gapDanger[ii],enemyDanger[ii]);
		}
		double[] dangerStatistics = new double[5];
		getStatistics(danger,dangerStatistics);
		
		
		return danger[1];
		//Decent formula for jumpiness -Math.pow(20.0-gapStatistics[1],2.0)+ 0.01*gapStatistics[2];
		//return -enemyStatistics[1]+0.01*-enemyStatistics[2]-enemyStatistics[4];
	}
	public void getStatistics(int[] array, double[] statistics){
		Double total = 0.0;
		Arrays.sort(array);
		double min = Double.POSITIVE_INFINITY;
		double max = Double.NEGATIVE_INFINITY;
		for (int xx : array){
			total += xx;
			if (xx < min){
				min = xx;
			}
			if (xx > max){
				max = xx;
			}
		}
		statistics[0]= (double) array[array.length/2];
		statistics[1] = total/array.length;
		statistics[2] = 0.0;
		statistics[3] = max;
		statistics[4] = min;
		for (int xx :array){
			double residual = (statistics[1]-xx);
			
			statistics[2] += residual*residual;
		}
		statistics[2] /= (double) array.length;
		

	}
	public void randomLevelCreation(ArrayList<LevelNode> levelSoFar,int totalSize){
		if (totalSize > 0){
			if (this.potentialRight.size() != 0){
				String nextChunk = WeightPair.GetWeightPair(this.potentialRight,rand.nextFloat()).obj;
				LevelNode nextPiece = new LevelNode(levelPieces.get(nextChunk));
				nextPiece.parent = this;
				levelSoFar.add(nextPiece);
				nextPiece.randomLevelCreation(levelSoFar, totalSize-nextPiece.getSize());
			}
		}

	}

    public LevelEntity[][] getLevel( String str ){
		String[] rows = str.split(";");
		LevelEntity[][] output = new LevelEntity[rows[0].split(",").length][rows.length];
		for (int yy = 0; yy < rows.length; yy++){
			String[] columns = rows[yy].split(",");
			for (int xx = 0; xx < columns.length; xx++){
				switch(columns[xx]){
				case "1.0":
					output[xx][yy] =  LevelEntity.Solid;
					break;
				case "2.0":
					output[xx][yy] =  LevelEntity.Destructible; 
					break;
				case "3.0":
					output[xx][yy] =  LevelEntity.PowerUp; 
					break;
				case "4.0":
					output[xx][yy] =  LevelEntity.CoinBlock;
					break;
				case "5.0":
					output[xx][yy] = LevelEntity.Enemy;
					break;
				case "6.0":
					output[xx][yy] =  LevelEntity.Pipe;
					break;
				case "7.0":
					output[xx][yy] = LevelEntity.Coin;
					break;
				case "8.0":
					output[xx][yy] = LevelEntity.Cannon;
					break;
				default:
					output[xx][yy] = LevelEntity.Empty;
					break;
				}
				
			}
		}
		return output;
	}
    public static void fillLevel(LevelEntity[][] level, int xx,String chunk){
    	LevelEntity[][] levelChunk = levelInstantiations.get(chunk);
    	for (int ii = 0; ii < levelChunk.length && ii + xx < level.length; ii++){
    		for (int jj = 0; jj < levelChunk[ii].length; jj++){
    			level[ii+xx][jj] = levelChunk[ii][jj];
    		}
    	}
    }
    public LevelEntity[][] constructLevel(ArrayList<LevelNode> level){
    	LevelNode node = this;
    	LevelEntity[][] constructedLevel = new LevelEntity[320][15];
    	while (node != null){
			int pos = node.getTotalSize()-node.getSize();
			fillLevel(constructedLevel,pos,node.levelChunk);
			node = node.parent;
		}
    	int pos = getTotalSize();
    	for (LevelNode lnode : level){
    		fillLevel(constructedLevel,pos,lnode.levelChunk);
    		pos += lnode.size;
    	}
    	return constructedLevel;
    }
	public int[] getEnemyDanger(LevelEntity[][] level){
		int[] dangerArray = new int[level.length];
		Arrays.fill(dangerArray, Integer.MAX_VALUE);
		int distanceFromEnemy = level.length;
		for (int xx = 0; xx < level.length; xx++){
			for (int yy = 0; yy < level[0].length; yy++){
				if (level[xx][yy] == LevelEntity.Enemy || level[xx][yy] == LevelEntity.Cannon){
					distanceFromEnemy = 0;
					int reverseDistanceFromEnemy = 1;
					for (int nx = xx-1; nx >= 0; nx--){
						if (dangerArray[xx] > reverseDistanceFromEnemy){
							dangerArray[xx] = reverseDistanceFromEnemy;
						}
						else {
							break;
						}
						reverseDistanceFromEnemy++;
					}
					break;
				}
			}
			dangerArray[xx] = distanceFromEnemy;
			distanceFromEnemy++;
		}
		return dangerArray;
	}
	public int[] getGapDanger(LevelEntity[][] level){
		int[] dangerArray = new int[level.length];
		Arrays.fill(dangerArray, Integer.MAX_VALUE);
		int distanceFromGround = level.length;

		for (int xx = 0; xx < level.length; xx++){
			if (level[xx][13] == LevelEntity.Empty){
				distanceFromGround = 0;
				int reverseDistanceFromGround = 1;
				for (int nx = xx-1; nx >= 0; nx--){
					if (dangerArray[xx] > reverseDistanceFromGround){
						dangerArray[xx] = reverseDistanceFromGround;
					}
					else {
						break;
					}
					reverseDistanceFromGround++;
				}
			}
			
			dangerArray[xx] = distanceFromGround;
			distanceFromGround++;
		}
		return dangerArray;
	}
	public void updateStats(double value) {
		nVisits++;
		totValue += value;
	}

}
