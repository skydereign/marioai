package ch2009.idsia.scenarios.test;

import ch2009.idsia.ai.agents.Agent;
import ch2009.idsia.ai.agents.AgentsPool;
import ch2009.idsia.ai.tasks.ProgressTask;
import ch2009.idsia.ai.tasks.Task;
import ch2009.idsia.tools.CmdLineOptions;
import ch2009.idsia.tools.EvaluationOptions;

/**
 * Created by IntelliJ IDEA.
 * User: julian
 * Date: May 9, 2009
 * Time: 4:23:04 PM
 */
public class StochasticityTest {

    final static int repetitions = 10;

    public static void main(String[] args) {
        Agent controller = AgentsPool.load (args[0]);
//        AgentsPool.registerAgent (controller);
        EvaluationOptions options = new CmdLineOptions(new String[0]);
        options.setAgent(controller);
        options.setPauseWorld (false);
        Task task = new ProgressTask(options);
        options.setMaxFPS(true);
        options.setVisualization(false);      
        options.setNumberOfTrials(1);
        options.setMatlabFileName("");
        task.setOptions(options);
        for (int i = 0; i < repetitions; i++) {
            System.out.println ("Score: " + task.evaluate (controller)[0]);
        }
    }

}
