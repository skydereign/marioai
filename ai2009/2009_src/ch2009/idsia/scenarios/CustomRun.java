package ch2009.idsia.scenarios;

import competition2009.cig.robinbaumgarten.AStarAgent;
import ch2009.idsia.ai.agents.Agent;
import ch2009.idsia.ai.agents.ai.ForwardAgent;
import ch2009.idsia.tools.CmdLineOptions;
import ch2009.idsia.tools.Evaluator;

/**
 * Created by IntelliJ IDEA.
 * User: Sergey Karakovskiy, firstName_at_idsia_dot_ch
 * Date: May 7, 2009
 * Time: 4:38:23 PM
 * Package: ch.idsia
 */

public class CustomRun
{
    public static void main(String[] args) {
        Agent agent = new AStarAgent(); // default agent to be evalutated. It is registered automatically in system.
        CmdLineOptions options = new CmdLineOptions(args);
        options.setAgent(agent);
        Evaluator evaluator = new Evaluator(options);
        evaluator.evaluate();                
    }
}
