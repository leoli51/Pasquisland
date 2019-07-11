package sgs.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldFilter;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import sgs.entities.Omino;
import sgs.map.Mappone;
import sgs.pasquisland.Pasquisland;


public class SimulationUI extends Table {
	
	private Skin skin;
	private Label n_omini;
	private Label n_palme;
	
	private Label[] pop_counts;
	private Label[] pop_names;
	private Image[] pop_images;
	
	private TextButton stop;
	private TextButton play;
	private TextButton skip;
	private TextField frames_to_skip;
	
	private float update_interval = .5f;
	private float current_time = 0;
	
	public SimulationUI(Skin skin) 
	
	{
		super(skin);
		this.skin = skin;
		
		n_omini = new Label("0", skin);
		n_palme = new Label("0", skin);
		
		add("N Omini");
		add(n_omini).pad(10).row();
		
		add("N Palme");
		add(n_palme).pad(10).row();
		
		pop_counts = new Label[27];
		pop_names = new Label[27];
		pop_images = new Image[27];
		
		for (int i = 0; i < 27; i++) {
			pop_names[i] = new Label(Omino.NOMI_CASATE[i], skin);
			pop_counts[i] = new Label(Mappone.getInstance().getTribePopulationCounter()[i]+ "", skin);
			pop_images[i] = new Image();
			pop_images[i].setDrawable(new TextureRegionDrawable(Omino.moai));
			pop_images[i].setColor(Omino.getTribuColor(Omino.NOMI_TRIBU[i]));
			
			add(pop_names[i]).pad(2);
			add(pop_counts[i]).pad(2);
			add(pop_images[i]).pad(2).maxWidth(20).row();
		}
		
		stop = new TextButton("stop", skin);
		play = new TextButton("play", skin);
		skip = new TextButton("skip", skin);
		frames_to_skip = new TextField("10", skin);
		
		stop.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				((Pasquisland) Gdx.app.getApplicationListener()).pauseSimulation();
			}
		});
		
		play.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				((Pasquisland) Gdx.app.getApplicationListener()).resumeSimulation();
			}
		});
		
		skip.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				int n;
				try {
					n = Integer.parseInt(frames_to_skip.getText());
				}
				catch (NumberFormatException e) {
					n = 1;
				}
				
				((Pasquisland) Gdx.app.getApplicationListener()).skipNFrames(n);;
			}
		});
		
		frames_to_skip.setTextFieldFilter(new TextFieldFilter.DigitsOnlyFilter());
		frames_to_skip.setMaxLength(3);
		
		add(frames_to_skip).pad(3);
		add(skip).pad(3);
		add(stop).pad(3);
		add(play).pad(3);
	}
	
	@Override
	public void act(float delta) {
		super.act(delta);
		
		current_time += delta;
		
		if (current_time > update_interval) {
			current_time = 0;
			updatePopulationCounts();
		}
	}
	
	private void updatePopulationCounts() {
		int total = 0;
		for (int i = 0; i < 27; i++) {
			//pop_counts[i].setText(Mappone.getInstance().getPopulationCountDictionary().getOrDefault(Omino.NOMI_TRIBU[i], 0));
			int curr = Mappone.getInstance().getTribePopulationCounter()[i];
			pop_counts[i].setText(curr);
			total += curr;
		}
		
		n_omini.setText(total);
		n_palme.setText(Math.max(0, Mappone.getInstance().getTotalEntities() - total));
	}
	

}
