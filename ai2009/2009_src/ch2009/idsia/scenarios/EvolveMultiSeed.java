package ch2009.idsia.scenarios;

import ch2009.idsia.ai.Evolvable;
import ch2009.idsia.ai.agents.Agent;
import ch2009.idsia.ai.agents.AgentsPool;
import ch2009.idsia.ai.agents.ai.SimpleMLPAgent;
import ch2009.idsia.ai.ea.ES;
import ch2009.idsia.ai.tasks.MultiSeedProgressTask;
import ch2009.idsia.tools.CmdLineOptions;
import ch2009.idsia.tools.EvaluationOptions;
import wox2009.serial.Easy;

/**
 * Created by IntelliJ IDEA.
 * User: julian
 * Date: May 24, 2009
 * Time: 1:18:44 AM
 */
public class EvolveMultiSeed {

    final static int generations = 100;
    final static int populationSize = 100;

    public static void main(String[] args) {
        EvaluationOptions options = new CmdLineOptions(new String[0]);
        options.setNumberOfTrials(1);
        options.setPauseWorld(true);
        Evolvable initial = new SimpleMLPAgent();
        if (args.length > 0) {
            initial = (Evolvable) AgentsPool.load (args[0]);
        }
//        AgentsPool.registerAgent ((Agent) initial);
//        AgentsPool.setCurrentAgent((Agent) initial);
        options.setMaxFPS(true);
            options.setVisualization(false);
            //Task task = new ProgressTask(options);
            MultiSeedProgressTask task = new MultiSeedProgressTask(options);
            task.setNumberOfSeeds(3);
            task.setStartingSeed(0);
            ES es = new ES (task, initial, populationSize);
            System.out.println("Evolving " + initial + " with task " + task);
            for (int gen = 0; gen < generations; gen++) {
                //task.setStartingSeed((int)(Math.random () * Integer.MAX_VALUE));
                es.nextGeneration();
                double bestResult = es.getBestFitnesses()[0];
                System.out.println("Generation " + gen + " best " + bestResult);
                options.setVisualization(gen % 5 == 0 || bestResult > 4000);
                options.setMaxFPS(true);
                Agent a = (Agent) es.getBests()[0];
                a.setName(((Agent)initial).getName() + gen);
//                RegisterableAgent.registerAgent(a);
//                AgentsPool.setCurrentAgent(a);
                double result = task.evaluate(a)[0];
                options.setVisualization(false);
                options.setMaxFPS(true);
                Easy.save (es.getBests()[0], "evolved.xml");
                if (result > 4000) {
                    break; //finished
                }
            }
    }
}
