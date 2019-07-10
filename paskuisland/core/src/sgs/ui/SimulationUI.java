package sgs.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;


public class SimulationUI extends Table {
	
	private Skin skin;
	Graph pop_graph;

	 /*private void buildSimulationUI() {
	TextButton stop = new TextButton(" Stop Simulation ", skin);
		pop_graph = new Graph(skin, "population", "time");
		
		stop.addListener(new ChangeListener() {public void changed (ChangeEvent event, Actor actor) {stopSimulation();}});

		ui_table.add(pop_graph).row();
		ui_table.add(stop);
	}
	 */
	
	public SimulationUI(Skin skin) 
	
	{
		super(skin);
		this.skin = skin;
		pop_graph = new Graph(this.skin, "population", "time");
		this.add(pop_graph).row();
		for (float x = 0; x < 100; x+= 1) {
			pop_graph.addPoint(x,x+x);
		}
		
		pop_graph.setGraphBackgroundColor(Color.RED);
		add("hello");
	}
}
