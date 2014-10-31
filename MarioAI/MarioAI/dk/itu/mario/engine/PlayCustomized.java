package dk.itu.mario.engine;

import java.awt.*;

import javax.swing.*;

import cmps244.CustomizedGenerator;
import cmps244.LevelNode;
import cmps244.PlayCustomizedLevelAI;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Random;

import org.json.*;
public class PlayCustomized {

	public static void main(String[] args)
	{
		JFrame frame = new JFrame("Mario Experience Showcase");
		MarioComponent mario = new MarioComponent(640, 480,true);

		frame.setContentPane(mario);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		frame.setLocation((screenSize.width-frame.getWidth())/2, (screenSize.height-frame.getHeight())/2);

		frame.setVisible(true);

		mario.start();   
		//PlayCustomizedLevelAI.Play(null);
	}	

}
