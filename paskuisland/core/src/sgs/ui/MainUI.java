package sgs.ui;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.utils.viewport.Viewport;

import sgs.pasquisland.Pasquisland;
import sgs.ui.MultiSlider.MultiSliderStyle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.Slider.SliderStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;

import sgs.entities.Omino;
import sgs.map.WorldMap;
import sgs.pasquisland.Pasquisland;
import sgs.ui.MultiSlider.MultiSliderStyle;

public class MainUI extends Stage {
	
	//******* MAIN UI
	private Window ui_window;
	private Table main_table;
	
	private Label fps;
		
	private Skin skin;
	
	// SETTINGS
	private TextField seed;
	private MultiSlider terrain;
	private Slider gaussian;
	private CheckBox apply_gaussian;
	
	private TextField max_fort;
	private TextField view_range;
	private TextField action_range;
	private TextField pregnancy;
	private MultiSlider strenght;
	private MultiSlider sociality;
	private MultiSlider speed;
	
	private Slider hunger;
	private Slider maturity;
	private Slider omini;
	private Slider palme;
	

	public MainUI(Skin skin, Viewport viewport) {
		super(viewport);
		init(skin);
	}

	public MainUI(Skin skin, Viewport viewport, Batch batch) {
		super(viewport, batch);
		init(skin);
	}
	
	
	
	
	private void init(Skin skin) {
		this.skin = skin;
		ui_window = new Window("SETTINGS", skin);
		ui_window.setResizable(true);
		ui_window.setPosition(getWidth(), getHeight());
		ui_window.setSize(getWidth()/3, getHeight());
		getRoot().addCaptureListener(new InputListener() {
		    public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
		        if (!(event.getTarget() instanceof TextField)) setKeyboardFocus(null);
		        return false;
		    }
		});
		
	
		
		//### MAP SETTINGS ###
		
		ui_window.add(" MAP SETTINGS ").top().pad(10).row();
		
		//MAP LISTENER
		
		ChangeListener updateMapListener = new ChangeListener() 
		{
			public void changed (ChangeEvent event, Actor actor) {updateMap();}
		}; 
		
		//SEED
		
		seed = new TextField(""+((Pasquisland) Gdx.app.getApplicationListener()).getMappone().getMap().getSeed(), skin);
		seed.setMaxLength(7);
		seed.setTextFieldFilter(new TextField.TextFieldFilter.DigitsOnlyFilter());
		ui_window.add(" Seed: ");
		ui_window.add(seed).pad(5);
		
		
		TextButton seed_button = new TextButton(" Random! ", skin);
		seed_button.addListener
		(
				new ChangeListener() 
			{
				public void changed (ChangeEvent event, Actor actor) 
				{
					seed.setText(((Pasquisland) Gdx.app.getApplicationListener()).getRandom().nextInt(9999999)+"");
				updateMap();
				}
			}
		);
		ui_window.add(seed_button).row();
		
		//GAUSSIAN
		
		gaussian = new Slider(0.001f, .2f, .01f, false, skin);
		gaussian.setValue(.1f);
		ui_window.add(" Gaussian value ");
		ui_window.add(gaussian).colspan(2).row();

		
		apply_gaussian = new CheckBox(" Apply Gaussian ", skin);
		ui_window.add(apply_gaussian).colspan(10).pad(10).fill().row();
		apply_gaussian.setChecked(true);
		
		//TERRAIN
		
		terrain = new MultiSlider(2, 0, .45f, .005f, false, new MultiSliderStyle(skin.get("default-horizontal", SliderStyle.class)));
		ui_window.add(" Terrain Specs: ");
		ui_window.add(terrain).colspan(2).row();
		
		//#########################################################################
		//MAP CONNECTIONS
		
		seed.addListener(updateMapListener);
		terrain.addListener(updateMapListener);
		gaussian.addListener(updateMapListener);
		apply_gaussian.addListener(updateMapListener);
		
		//#########################################################################
		
		
		//### ENTITY SETTINGS ###
		
		/* VALORI DI DEFAULT IMPORTATI DALLA CLASSE OMINO
		public static final int RAGGIO_VISIVO = 4;
		public static final float ACTION_DST = 20;
		public static final float BLOCCO_INT = 2f;
		//public static final float PREGNANCY = .1f;
		public static final float HUNGER_PER_SECOND = .2f; // la fame arriva a 1 in 5 sec
		public static final float MATURITY = 17;
		
		##BOUNDARIES FOR STATS 
		public static final float MIN_STRENGTH = 0, MAX_STRENGTH = 1;
		public static final float MIN_SOCIALITY = 0, MAX_SOCIALITY = 1;
		public static final float MIN_SPEED = 32, MAX_SPEED = 96;
		*/
		
		//ENTITIES LISTENER
		
		ChangeListener updateEntitiesListener = new ChangeListener() 
		{
			public void changed (ChangeEvent event, Actor actor) {updateEntities();}
		}; 
		
		//### SPAWN SETTINGS ###
		
		ui_window.add(" SPAWN SETTINGS ").top().pad(10);
		TextButton spawn_button = new TextButton(" Random! ", skin);
		ui_window.add(spawn_button).row();
		spawn_button.addListener
		(
				new ChangeListener() 
			{
				public void changed (ChangeEvent event, Actor actor) 
				{
					Random r = ((Pasquisland) Gdx.app.getApplicationListener()).getRandom();
					
					omini.setValue((r.nextFloat()*(omini.getMaxValue())+omini.getMinValue()));//updateEntities();
					palme.setValue((r.nextFloat()*(palme.getMaxValue())+palme.getMinValue()));//updateEntities();
					updateEntities();
				}
			}
		);
		
		
		
		//OMINI
		
		omini = new Slider(0.001f, .2f, .01f, false, skin);
		omini.setValue(.1f);
		ui_window.add(" Omini ");
		ui_window.add(omini).colspan(2).row();
		
		//PALME
		
		palme = new Slider(0.001f, .2f, .01f, false, skin);
		palme.setValue(.1f);
		ui_window.add(" Palme ");
		ui_window.add(palme).colspan(2).row();
		
		
		//SPAWN LISTENER ---> updateEntities
		
		/*
		cambia il valore di spamma omini e spamma palme nel metodo startSimulation()
		
		public void startSimulation() 
		{
			mappone.spammaOmini(.1f);
			mappone.spammaPalme(.3f);
		}
		*/
		
		//palme_omini.addListener(new ChangeListener(){public void changed(ChangeEvent event,Actor actor) {Pasquisland.oratio = palme_omini.getValue();Pasquisland.pratio = palme_omini.getMaxValue()-Pasquisland.oratio;}});
		
		//### OMINI SETTINGS ###
		
		
		ui_window.add(" OMINI SETTINGS ").top().pad(10);
		TextButton tribe_button = new TextButton(" Random! ", skin);
		ui_window.add(tribe_button).row();
		
		
		//MAX FOR TRIBE
		
		final TextField max_fort = new TextField("", skin);
		max_fort.setMaxLength(5);
		max_fort.setTextFieldFilter(new TextField.TextFieldFilter.DigitsOnlyFilter());
		ui_window.add(" Max tribesmen for tribe: ");
		ui_window.add(max_fort).pad(5).row();
		
		//VIEW RANGE
		
		final TextField view_range = new TextField("", skin);
		view_range.setMaxLength(2);
		view_range.setTextFieldFilter(new TextField.TextFieldFilter.DigitsOnlyFilter());
		ui_window.add(" View Range: ");
		ui_window.add(view_range).pad(5).row();
		
		//ACTION RANGE
		
		final TextField action_range = new TextField("", skin);
		action_range.setMaxLength(2);
		action_range.setTextFieldFilter(new TextField.TextFieldFilter.DigitsOnlyFilter());
		ui_window.add(" Action Range: ");
		ui_window.add(action_range).pad(5).row();
		
		//PREGNANCY
		
		final TextField pregnancy = new TextField("", skin);
		pregnancy.setMaxLength(2);
		pregnancy.setTextFieldFilter(new TextField.TextFieldFilter.DigitsOnlyFilter());
		ui_window.add(" Pregnancy: ");
		ui_window.add(pregnancy).pad(5).row();
		
		
		//HUNGER PER SECOND
		
		hunger = new Slider(0.001f, .2f, .01f, false, skin);
		hunger.setValue(.1f);
		ui_window.add(" Hunger ");
		ui_window.add(hunger).colspan(2).row();
		
		/*
		hunger.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				hunger.getValue();
			}
		});
		*/
		
		
		//MATURITY
		
		maturity = new Slider(0.001f, .2f, .01f, false, skin);
		maturity.setValue(.1f);
		ui_window.add(" Maturity ");
		ui_window.add(maturity).colspan(2).row();
		
		
		//STATS BOUNDARIES

		//STRENGHT (slider 0<x<1) OPPURE DIRETTAMENTE INSERIRE MAX STRENGHT E MIN STRENGHT
		
		strenght = new MultiSlider(2, 0, .45f, .005f, false, new MultiSliderStyle(skin.get("default-horizontal", SliderStyle.class)));
		ui_window.add(" Strenght: ");
		ui_window.add(strenght).colspan(2).row();
		
		//SOCIALITY (slider 0<x<1)

		sociality = new MultiSlider(2, 0, .45f, .005f, false, new MultiSliderStyle(skin.get("default-horizontal", SliderStyle.class)));
		ui_window.add(" Sociality: ");
		ui_window.add(sociality).colspan(2).row();
		
		//SPEED (slider 32<x<96)

		speed = new MultiSlider(2, 0, .45f, .005f, false, new MultiSliderStyle(skin.get("default-horizontal", SliderStyle.class)));
		ui_window.add(" Speed: ");
		ui_window.add(speed).colspan(2).row();
		
		
		//RANDOM BUTTON CONNECTIONS
		tribe_button.addListener(new ChangeListener() 
		{
			public void changed (ChangeEvent event, Actor actor) 
			{
				Random r = ((Pasquisland) Gdx.app.getApplicationListener()).getRandom();
				
				max_fort.setText(r.nextInt(999)+"");//updateEntities();
				view_range.setText(r.nextInt(99)+"");//updateEntities();
				action_range.setText(r.nextInt(99)+"");//updateEntities();
				pregnancy.setText(r.nextInt(99)+"");//updateEntities();
				hunger.setValue((r.nextFloat()*(hunger.getMaxValue())+hunger.getMinValue()));//updateEntities();
				maturity.setValue((r.nextFloat()*(maturity.getMaxValue())+maturity.getMinValue()));//updateEntities();
				strenght.setValue(0, (r.nextFloat()*(strenght.knobsPositions[1])+strenght.getMinValue()));
				strenght.setValue(1, (r.nextFloat()*(strenght.getMaxValue())+strenght.knobsPositions[0]));
				sociality.setValue(0, (r.nextFloat()*(sociality.knobsPositions[1])+sociality.getMinValue()));
				sociality.setValue(1, (r.nextFloat()*(sociality.getMaxValue())+sociality.knobsPositions[0]));
				speed.setValue(0, (r.nextFloat()*(speed.knobsPositions[1])+speed.getMinValue()));
				speed.setValue(1, (r.nextFloat()*(speed.getMaxValue())+speed.knobsPositions[0]));
				
			}
		});
		
		
		
		//OMINI LISTENER ---> updateEntities
		
		//### PALME SETTINGS ###

		ui_window.add(" PALME SETTINGS ").top().pad(10).row();
		
		/*
		private static int MIN_PALME_PER_RIPRODURSI = 0;
		private static int MAX_PALME_PER_RIPRODURSI = 10;
		
		private static float MIN_REPRODUCTION_TIME = .5f;
		private static float MAX_REPRODUCTION_TIME = 1f;
		*/
		
		//MIN-Max PALME PER RIPRODURSI
		/*
		TextField min_palme = new TextField("", skin);
		min_palme.setMaxLength(7);
		min_palme.setTextFieldFilter(new TextField.TextFieldFilter.DigitsOnlyFilter());
		TextField max_palme = new TextField("", skin);
		max_palme.setMaxLength(7);
		max_palme.setTextFieldFilter(new TextField.TextFieldFilter.DigitsOnlyFilter());
		ui_window.add(" Min-Max Palme: ");
		ui_window.add(min_palme).pad(5);
		ui_window.add(max_palme).pad(5).row();
		
		*/
		//MIN-MAX REPRODUCTION TIME
		/*
		TextField min_rept = new TextField("", skin);
		min_rept.setMaxLength(7);
		min_rept.setTextFieldFilter(new TextField.TextFieldFilter.DigitsOnlyFilter());
		TextField max_rept = new TextField("", skin);
		max_rept.setMaxLength(7);
		max_rept.setTextFieldFilter(new TextField.TextFieldFilter.DigitsOnlyFilter());
		ui_window.add(" Min-Max Reproduction Time: ");
		ui_window.add(min_rept).pad(5);
		ui_window.add(max_rept).pad(5).row();
		*/
		
		//PALME PER RIPRODURSI
		
		MultiSlider npalme_rep = new MultiSlider(2, 0, .45f, .005f, false, new MultiSliderStyle(skin.get("default-horizontal", SliderStyle.class)));
		ui_window.add(" Palme per riprodursi ");
		ui_window.add(npalme_rep).colspan(2).row();
		
		
		//REPRODUCTION TIME
		
		MultiSlider rept = new MultiSlider(2, 0, .45f, .005f, false, new MultiSliderStyle(skin.get("default-horizontal", SliderStyle.class)));
		ui_window.add(" Reproduction Time ");
		ui_window.add(rept).colspan(2).row();
		
		
		//#########################################################################
		//ENTITIES CONNECTIONS

		omini.addListener(updateEntitiesListener);
		palme.addListener(updateEntitiesListener);
		max_fort.addListener(updateEntitiesListener);
		view_range.addListener(updateEntitiesListener);
		action_range.addListener(updateEntitiesListener);
		pregnancy.addListener(updateEntitiesListener);
		hunger.addListener(updateEntitiesListener);
		maturity.addListener(updateEntitiesListener);
		strenght.addListener(updateEntitiesListener);
		sociality.addListener(updateEntitiesListener);
		speed.addListener(updateEntitiesListener);
		
		//#########################################################################

		
		
		
		
		// START

		TextButton start = new TextButton(" Start Simulation ", skin);
		ui_window.add(start).pad(10).colspan(3).row();
		
		start.addListener
		(
				new ChangeListener() 
				{
					public void changed (ChangeEvent event, Actor actor) 
					{
						startSimulation();
					}
				}
		);
		
		//STOP TEMPORANEO
		
		TextButton stop = new TextButton(" Stop Simulation ", skin);
		ui_window.add(stop).pad(10).colspan(3);
		
		stop.addListener
		(
				new ChangeListener() 
				{
					public void changed (ChangeEvent event, Actor actor) 
					{
						stopSimulation();
					}
				}
		);

		
		main_table = new Table(skin);
		fps = new Label("FPS : ",skin);
		
		main_table.add(fps).left().top().expand();
		main_table.setFillParent(true);
		main_table.validate();
		main_table.layout();
		addActor(main_table);
		addActor(ui_window);
		setDebugAll(true);
	}
	
	private void updateMap() 
	{
		WorldMap map = ((Pasquisland) Gdx.app.getApplicationListener()).getMappone().getMap();
		int seed_val = map.getSeed();
		try 
		{
			seed_val = Integer.parseInt(seed.getText());
		}
		catch(Exception ex) {}
		float[] terrain_vals = java.util.Arrays.copyOf(terrain.getKnobPositions(), 3);
		terrain_vals[2] = 1f;
		map.resetMapSettings(terrain_vals);
		map.resetMapGenSettings(
				map.getWidth(),map.getHeight(), seed_val,200,4,.3f, 2f, Vector2.Zero,apply_gaussian.isChecked(), gaussian.getValue());
		map.generateMap();
	}

	private void updateEntities() 
	{
		
		Pasquisland.oratio = omini.getValue();	
		Pasquisland.pratio = palme.getValue();
		//Omino.RAGGIO_VISIVO = Integer.parseInt(view_range.getText());

		/*
		Omino.RAGGIO_VISIVO =  Integer.parseInt(view_range.getText());
		Omino.ACTION_DST = Integer.parseInt(action_range.getText());
		Omino.PREGNANCY = Integer.parseInt(view_range.getText());
		*/
		
		//strenght sociality speed
		
		/*
		Omino omo = ((Omino) Gdx.app.getApplicationListener());
		
		float[] strenght_vals = java.util.Arrays.copyOf(strenght.getKnobPositions(), 3);
		strenght_vals[2] = 1f;
		float[] sociality_vals = java.util.Arrays.copyOf(sociality.getKnobPositions(), 3);
		sociality_vals[2] = 1f;
		float[] speed_vals = java.util.Arrays.copyOf(speed.getKnobPositions(), 3);
		speed_vals[2] = 1f;
		
		Random r = ((Pasquisland)Gdx.app.getApplicationListener()).getRandom();
		
		float strenght_v = (r.nextFloat()*(strenght_vals[1])-strenght_vals[0]);
		float sociality_v = (r.nextFloat()*(sociality_vals[1])-sociality_vals[0]);
		float speed_v = (r.nextFloat()*(speed_vals[1])-speed_vals[0]);
		
		
		omo.setValues(strenght_v, sociality_v, speed_v);
		*/
		
	}
	
	private void startSimulation() {
		ui_window.getTitleLabel().setText("SIMULATION");
		((Pasquisland) Gdx.app.getApplicationListener()).startSimulation();
	};
	
	private void stopSimulation() {
		ui_window.getTitleLabel().setText("SETTINGS");
		((Pasquisland) Gdx.app.getApplicationListener()).stopSimulation();
	}
	
	@Override
	public void act() {
		super.act();
		fps.setText("FPS: "+Gdx.graphics.getFramesPerSecond());
	}
	
	public void draw() {
		super.draw();
	}

}
