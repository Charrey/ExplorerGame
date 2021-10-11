package com.charrey.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Iterator;

public class GameScreen implements Screen {
	final Drop game;
	Texture dropImage;
	Texture bucketImage;
	Sound dropSound;
	Music rainMusic;
	OrthographicCamera camera;
	Rectangle bucket;
	Array<Rectangle> raindrops;
	long lastDropTime;
	int dropsGathered;



	public GameScreen(Drop game) {
		this.game = game;
		dropImage = new Texture(Gdx.files.internal("droplet.png"));
		bucketImage = new Texture(Gdx.files.internal("bucket.png"));
		dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.wav"));
		rainMusic = Gdx.audio.newMusic(Gdx.files.internal("rain.mp3"));
		rainMusic.setLooping(true);
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 480);

		bucket = new Rectangle();
		bucket.x = (800 / 2) - (64 / 2);
		bucket.y = 20;
		bucket.width = 64;
		bucket.height = 64;

		raindrops = new Array<>();
		spawnRainDrop();
	}

	private void spawnRainDrop() {
		Rectangle rainDrop = new Rectangle();
		rainDrop.x = MathUtils.random(0, 800-64);
		rainDrop.y = 480;
		rainDrop.width = 64;
		rainDrop.height = 64;
		raindrops.add(rainDrop);
		lastDropTime = TimeUtils.nanoTime();
	}

	@Override
	public void render(float delta) {
		ScreenUtils.clear(0, 0, 0.2f, 1);
		camera.update();
		game.batch.setProjectionMatrix(camera.combined);
		game.batch.begin();
		game.font.draw(game.batch, "Drops Collected: " + dropsGathered, 0, 480);
		game.batch.draw(bucketImage, bucket.x, bucket.y, bucket.width, bucket.height);
		raindrops.forEach(raindrop -> game.batch.draw(dropImage, raindrop.x, raindrop.y));
		game.batch.end();
		interpretInput();
		keepBucketInBounds();
		if(TimeUtils.nanoTime() - lastDropTime > 1_000_000_000) {
			spawnRainDrop();
		}
		moveRaindrops();
	}

	private void keepBucketInBounds() {
		bucket.x = Math.max(0, bucket.x);
		bucket.x = Math.min(800 - 64, bucket.x);
	}

	private void interpretInput() {
		if (Gdx.input.isTouched()) {
			Vector3 touchPos = new Vector3();
			touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
			camera.unproject(touchPos);
			bucket.x = touchPos.x - 64/2;
		}
		boolean rightPressed = Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D);
		boolean leftPressed = Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A);
		if (rightPressed && !leftPressed) {
			bucket.x += 200 * Gdx.graphics.getDeltaTime();
		} else if (!rightPressed && leftPressed) {
			bucket.x -= 200 * Gdx.graphics.getDeltaTime();
		}
	}

	private void moveRaindrops() {
		Rectangle currentRaindrop;
		for (Iterator<Rectangle> iterator = raindrops.iterator(); iterator.hasNext();) {
			currentRaindrop = iterator.next();
			currentRaindrop.y -= 1000 * Gdx.graphics.getDeltaTime();
			if (currentRaindrop.y + 64 < 0) {
				iterator.remove();
			} else if (currentRaindrop.overlaps(bucket)) {
				dropSound.play();
				iterator.remove();
				dropsGathered++;
			}
		}
	}

	@Override
	public void show() {
		rainMusic.play();
	}

	@Override
	public void resize(int width, int height) {

	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void hide() {

	}

	@Override
	public void dispose () {
		dropImage.dispose();
		bucketImage.dispose();
		dropSound.dispose();
		rainMusic.dispose();
	}


}
