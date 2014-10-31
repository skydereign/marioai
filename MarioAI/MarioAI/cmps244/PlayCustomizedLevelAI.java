package cmps244;

import java.io.IOException;

import competition.cig.robinbaumgarten.AStarAgent;
import ch.idsia.agents.Agent;
import ch.idsia.agents.controllers.ForwardAgent;
import ch.idsia.benchmark.mario.environments.Environment;
import ch.idsia.benchmark.tasks.BasicTask;
import ch.idsia.tools.MarioAIOptions;
import ch2009.idsia.tools.CmdLineOptions;
import ch2009.idsia.tools.Evaluator;

public class PlayCustomizedLevelAI {
	public static boolean vis = false;

	public static void Play(dk.itu.mario.level.Level level, boolean use2010)
	{
		if(use2010) {
			// need to convert level into a format that the ai agent can play, ch.idsia.*.level
			final String argsString = "-vis " + (vis ? "on" : "off");
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
		} else { // use 2009
			System.out.println("Using 2009 A*");
			String[] args = new String[10];
			//ch2009.idsia.mario.engine.level.Level lv2009 = new ch2009.idsia.mario.engine.level.Level(0, 0);
			//level.convert(lv2009);
			ch2009.idsia.ai.agents.Agent agent = new competition2009.cig.robinbaumgarten.AStarAgent(); // default agent to be evalutated. It is registered automatically in system.
			CmdLineOptions options = new CmdLineOptions(args);
			options.setAgent(agent);
			//options.setVisualization(vis);
			ch2009.idsia.mario.engine.GlobalOptions.level = level;
			Evaluator evaluator = new Evaluator(options);
			evaluator.evaluate();      
		}

	}
	
	
}


