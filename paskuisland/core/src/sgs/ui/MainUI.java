package sgs.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.Viewport;

import sgs.pasquisland.Pasquisland;

public class MainUI extends Stage {
	
	//******* MAIN UI
	private Window ui_window;
	private Table main_table;
	private SettingsUI settings;
	private SimulationUI simulation;
	
	private TextButton start;
	private TextButton stop;
	
	//private Label population;
	
	private Label fps;
		
	private Skin skin;

	//private Graph pop_graph;
	

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
		
		settings = new SettingsUI(skin);
		ui_window.add(settings).row();
		
		start = new TextButton(" Start Simulation ", skin);
		ui_window.add(start).pad(10).colspan(3).row();
		
		simulation = new SimulationUI(skin);
		stop = new TextButton(" Stop Simulation ", this.skin);
		stop.addListener(new ChangeListener() {public void changed (ChangeEvent event, Actor actor) {stopSimulation();}});
		start.addListener(new ChangeListener() {public void changed (ChangeEvent event, Actor actor) {startSimulation();}});
		
		main_table = new Table(skin);
		fps = new Label("FPS : ",skin);
		
		main_table.add(fps).left().top().expand();
		main_table.setFillParent(true);
		main_table.validate();
		main_table.layout();
		addActor(main_table);
		addActor(ui_window);
		setDebugAll(false);
	}
	
	private void startSimulation() {
		ui_window.getTitleLabel().setText("SIMULATION");
		
		ui_window.clearChildren();
		
		ui_window.add(simulation).top().expand().row();
		ui_window.add(stop).pad(10).row();
	
		((Pasquisland) Gdx.app.getApplicationListener()).startSimulation();
		
		
	};
	
	//F
	
	private void stopSimulation() {
		ui_window.getTitleLabel().setText("SETTINGS");
		ui_window.clearChildren();
		ui_window.add(settings).row();
		ui_window.add(start).row();
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
