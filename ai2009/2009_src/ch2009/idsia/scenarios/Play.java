package ch2009.idsia.scenarios;

import ch2009.idsia.ai.agents.Agent;
import ch2009.idsia.ai.agents.AgentsPool;
import ch2009.idsia.ai.agents.human.HumanKeyboardAgent;
import ch2009.idsia.ai.tasks.ProgressTask;
import ch2009.idsia.ai.tasks.Task;
import ch2009.idsia.tools.CmdLineOptions;
import ch2009.idsia.tools.EvaluationOptions;

/**
 * Created by IntelliJ IDEA.
 * User: julian
 * Date: May 5, 2009
 * Time: 12:46:43 PM
 */
public class Play {

    public static void main(String[] args) {
        Agent controller = new HumanKeyboardAgent();
        if (args.length > 0) {
            controller = AgentsPool.load (args[0]);
            AgentsPool.addAgent(controller);
        }
        EvaluationOptions options = new CmdLineOptions(new String[0]);
        options.setAgent(controller);
        Task task = new ProgressTask(options);
        options.setMaxFPS(false);
        options.setVisualization(true);
        options.setNumberOfTrials(1);
        options.setMatlabFileName("");
        options.setLevelRandSeed((int) (Math.random () * Integer.MAX_VALUE));
        options.setLevelDifficulty(3);
        task.setOptions(options);

        System.out.println ("Score: " + task.evaluate (controller)[0]);
    }
}
