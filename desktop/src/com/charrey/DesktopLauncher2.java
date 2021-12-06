package com.charrey;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.charrey.game.Explore;

/**
 * Class containing the main method for the Explorer game
 */
public class DesktopLauncher2 {
	/**
	 * Runs the Explorer game in Desktop mode
	 * @param arg ignored
	 */
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Explore";
		config.width = 1920;
		config.height = 1080;
		new LwjglApplication(new Explore(), config);
	}
}
