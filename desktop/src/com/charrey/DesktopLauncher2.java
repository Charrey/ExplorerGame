package com.charrey;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.charrey.game.Explore;

public class DesktopLauncher2 {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Explore";
		config.width = 1920;
		config.height = 1080;
		new LwjglApplication(new Explore(), config);
	}
}
