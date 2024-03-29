package sgs.entities;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;

import sgs.map.Mappone;
import sgs.pasquisland.Pasquisland;

public class Palma extends Entity{
	
	public static int MIN_PALME_PER_RIPRODURSI = 0;
	public static int MAX_PALME_PER_RIPRODURSI = 10;
	
	public static float MIN_REPRODUCTION_TIME = .5f;
	public static float MAX_REPRODUCTION_TIME = 1f;

	public static Texture texture = new Texture(Gdx.files.internal("bossPalmetta.png"));
	
	private float reproduction_interval;
	private float my_time;
	
	public Palma(float x, float y) {
		super(x, y);
		Random r = ((Pasquisland)Gdx.app.getApplicationListener()).getRandom();
		reproduction_interval = r.nextFloat()*(MAX_REPRODUCTION_TIME - MIN_REPRODUCTION_TIME)
				+ MIN_REPRODUCTION_TIME;
		my_time = 0;
	}
	public void disegnami(SpriteBatch batch) {
		batch.draw(texture, position.x, position.y, 50, 50);
	}
	
	public void update(float delta) {
		if (my_time > reproduction_interval) {
			Array<Entity> palms = Mappone.getInstance().vedi(this, 1,false);
			int num_palms = 0;
			for (Entity e : palms) {
				if (e instanceof Palma)
					num_palms++;
			}
			
			if (num_palms < MAX_PALME_PER_RIPRODURSI && num_palms > MIN_PALME_PER_RIPRODURSI)
				Mappone.getInstance().spawnaPalmaQuaVicino(gridposition);
			my_time = 0;
		}
		else {
			my_time += delta;
		}
	}
}
