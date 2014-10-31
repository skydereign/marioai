package cmps244Lib;
import java.util.List;
import java.math.*;
public class WeightPair<X> {
	public int count;
	public X obj;
	public WeightPair(int cc, X o){
		count = cc;
		obj = o;
	}
	public static <X> WeightPair<X> GetWeightPair(List<WeightPair<X> > list,int val){
		int runningSum = 0;
		for (WeightPair<X> pair : list){
			runningSum += pair.count;
			if (val <= runningSum){
				return pair;
			}
		}
		return null;
	}
	public static <X> int GetTotalWeight(List<WeightPair<X> > list){
		int runningTotal = 0;
		for (WeightPair<X> pair :list){
			runningTotal += pair.count;
		}
		return runningTotal;
	}
	public static <X> WeightPair<X> GetWeightPair(List<WeightPair<X> > list,float val){
		int stopWeight = (int) Math.round((WeightPair.GetTotalWeight(list)*val));
		return WeightPair.GetWeightPair(list, stopWeight);
	}
}
