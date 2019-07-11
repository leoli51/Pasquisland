package sgs.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;

import sgs.map.Mappone;


public class SimulationUI extends Table {
	
	private Skin skin;
	private Label n_omini;
	private Label n_palme;

	
	public SimulationUI(Skin skin) 
	
	{
		/*
		super(skin);
		this.skin = skin;
		n_omini = new Label("Numero Omini : ",skin);
		n_omini.setText("Numero Omini : " + Mappone.pop_count);
		n_palme = new Label("Numero Palme : ",skin);
		n_palme.setText("Numero Palme : " + Mappone.pop_count);
		this.add(n_omini).pad(10).row();
		this.add().row();
		this.add(n_palme).pad(10).row();
		*/
	}
	

}
