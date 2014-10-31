package cmps244;

import java.io.IOException;

import competition.cig.robinbaumgarten.AStarAgent;

import ch.idsia.agents.Agent;
import ch.idsia.agents.controllers.ForwardAgent;
import ch.idsia.benchmark.mario.environments.Environment;
import ch.idsia.benchmark.tasks.BasicTask;
import ch.idsia.tools.MarioAIOptions;

public class PlayCustomizedLevelAI {
	
	public static void Play(dk.itu.mario.level.Level level)
	{
		// need to convert level into a format that the ai agent can play, ch.idsia.*.level
		final String argsString = "-vis on";
		final MarioAIOptions marioAIOptions = new MarioAIOptions(argsString);
		final Agent agent = new ForwardAgent();
		final BasicTask basicTask = new BasicTask(marioAIOptions);
		int count = 0;
		
		do
		{
			marioAIOptions.customLevel = level;
			marioAIOptions.setAgent(agent);
			basicTask.setOptionsAndReset(marioAIOptions);
			basicTask.runSingleEpisode(1);
			
			// evaluation of the run
			System.out.println(basicTask.getEnvironment().getEvaluationInfoAsString());
			
			count++;
		} while (basicTask.getEnvironment().getEvaluationInfo().marioStatus != Environment.MARIO_STATUS_WIN && count < 1);

		Runtime rt = Runtime.getRuntime();
		/*try
		{
			Process proc = rt.exec("python hello.py");
		} catch (IOException e)
		{
			e.printStackTrace();
		}*/
		System.exit(0);

	}
}


