package ch2009.idsia.ai.tasks;

import ch2009.idsia.ai.agents.Agent;
import ch2009.idsia.tools.EvaluationOptions;

/**
 * Created by IntelliJ IDEA.
 * User: Sergey Karakovskiy
 * Date: Apr 8, 2009
 * Time: 11:20:41 AM
 * Package: ch.idsia.ai.tasks
 */
public interface Task {
    public double[] evaluate (Agent controller);

    public void setOptions (EvaluationOptions options);

    public EvaluationOptions getOptions ();

}
