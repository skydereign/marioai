package ch2009.idsia.ai.tasks;

import ch2009.idsia.ai.agents.Agent;
import ch2009.idsia.tools.EvaluationInfo;
import ch2009.idsia.tools.EvaluationOptions;
import ch2009.idsia.tools.Evaluator;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: julian
 * Date: May 24, 2009
 * Time: 11:21:12 PM
 */
public class StochasticProgressTask implements Task {

    private EvaluationOptions options;
    private int startingSeed = 0;
    private int numberOfSeeds = 3;

    public StochasticProgressTask(EvaluationOptions evaluationOptions) {
        setOptions(evaluationOptions);
    }

    public double[] evaluate(Agent controller) {
        double distanceTravelled = 0;
        controller.reset();
        options.setAgent(controller);
        for (int i = 0; i < numberOfSeeds; i++) {
            options.setLevelRandSeed(startingSeed + i);
            Evaluator evaluator = new Evaluator(options);
            List<EvaluationInfo> results = evaluator.evaluate();
            EvaluationInfo result = results.get(0);
            distanceTravelled += result.computeDistancePassed();
        }
        distanceTravelled = distanceTravelled / numberOfSeeds;
        return new double[]{distanceTravelled};
    }

    public void setStartingSeed (int seed) {
        startingSeed = seed;
    }

    public void setNumberOfSeeds (int number) {
        numberOfSeeds = number;
    }

    public void setOptions(EvaluationOptions options) {
        this.options = options;
    }

    public EvaluationOptions getOptions() {
        return options;
    }

}
