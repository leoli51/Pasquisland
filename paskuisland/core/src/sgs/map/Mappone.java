package sgs.map;

import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import sgs.entities.Entity;
import sgs.entities.EntityProcessor;
import sgs.entities.Omino;
import sgs.entities.Palma;
import sgs.entities.posRandom;
import sgs.pasquisland.Pasquisland;


public class Mappone {

	private static Mappone singleton;
	
	
	private WorldMap map; // mappa con le grafiche e ti dice se � acqua o terra il terreno
	private HashMap<GridPoint2, Array<Entity>> mappa_entita; // mappa 2d delle entita
	private HashMap<String, Integer> population_count;
	
	private Array<EntityProcessor> processors;
	private ScheduledExecutorService executor;
	
	private Array<Entity> selected_entities;
	
	private int selected = 0;

	private Mappone(int map_width, int map_height) {
		float[] terrain_values = new float[3];
		terrain_values[0] = .2f;
		terrain_values[1] = .3f;
		terrain_values[2] = .9f;
		map = new WorldMap(map_width, map_height, ((Pasquisland) Gdx.app.getApplicationListener()).getRandom().nextInt(1000000),
				200f, 4, .3f, 2f, Vector2.Zero, terrain_values, true, .1f);
		mappa_entita = new HashMap<GridPoint2, Array<Entity>>();
		selected_entities= new Array<Entity>();
		
		for (int x = 0; x < map_width; x++)
			for (int y = 0; y < map_height; y++)
				chiCeStaQua(x, y);
		
		processors = new Array<EntityProcessor>();
		executor = Executors.newScheduledThreadPool(32);
	}
	
	public void aggiorna(float delta) {
		int pop_count = 0;
		for (int i = 0; i < processors.size; i++) {
			pop_count += processors.get(i).getEntities().size;
			executor.execute(processors.get(i));
		}
		
		Gdx.app.log("MIM", pop_count + " OMINI, "+processors.size+" Entity processors");
	}

	public void disegnaTutto(SpriteBatch batch, ShapeRenderer sr, int[] che_se_vede) {
		disegnaMappetta(sr, che_se_vede);
		sr.begin(ShapeType.Filled);
		for (Entity entita : selected_entities) {
			sr.setColor(Color.RED);
			for (Entity e :vedi((Omino) entita, Omino.RAGGIO_VISIVO)) {
				sr.rect(e.position.x, e.position.y, 30, 30); //perch� ogni quadrato � 32x32 => per non avere rettangoli in caso di entit� vicine considero un'area minore :)	
			}
		}
		for (Entity entita : selected_entities) {
			sr.setColor(Color.CHARTREUSE);
			sr.rect(entita.position.x, entita.position.y, 30, 30); //perch� ogni quadrato � 32x32 => per non avere rettangoli in caso di entit� vicine considero un'area minore :)
		}
		sr.end();
		batch.begin();
		batch.enableBlending();
		disegnaEntita(batch);
		batch.disableBlending();
		batch.end();
		for( Entity e: selected_entities) {
			if(e.life<=0) {
				selected_entities.removeValue(e, true);
			}
		}
	}
	
	public void disegnaMappetta(ShapeRenderer sr, int[] che_se_vede) {
		map.render(sr, che_se_vede[0], che_se_vede[1], che_se_vede[2], che_se_vede[3]);
	}
	
	public void disegnaEntita(SpriteBatch batch) {
		//DAngerous code ... ep.getEntities è synchonized su da_aggiornare
		//Vedere in EntityProcessor
		for (int i = 0; i < processors.size; i++)
			for (int ei = 0; ei < processors.get(i).getEntities().size; ei++)
				processors.get(i).getEntities().get(ei).disegnami(batch);
		
	}

	/**
	 * @exception ATTENZIONE se leggete / modificate le liste ritornate da 
	 * questo metodo senza prima sincronizzarvi su di esse succedono brutte cose
	 * @param qua
	 * @return
	 */
	public Array<Entity> chiCeStaQua(GridPoint2 qua) {
		Array<Entity> ecco_la_lista = mappa_entita.get(qua);
		if (ecco_la_lista == null) {
			ecco_la_lista = new Array<Entity>();
			mappa_entita.put(qua, ecco_la_lista);
		}
		return ecco_la_lista;
	}
	
	/**
	 * @see chiCeStaQua(GridPoint2 qua)
	 * @param x
	 * @param y
	 * @return
	 */
	public Array<Entity> chiCeStaQua(int x, int y) {
		GridPoint2 p = new GridPoint2(x,y);
		return chiCeStaQua(p);
	}
	
	public WorldMap getMap() {
		return map;
	}	

	public void spammaOmini(float densita) {
		Random r = ((Pasquisland) Gdx.app.getApplicationListener()).getRandom();
		for(int y=0; y<map.getHeight(); y++) {
			for(int x=0; x< map.getWidth(); x++) {
				if(map.getTerrainTypeAt(x, y)!=map.water_id) {
					if(r.nextFloat()<densita) {
						Omino primoUomo= new Omino(x*map.tile_size,y*map.tile_size);
						registerEntity(primoUomo);
					}	
				}
			}
		}
	}

	public void spammaPalme(float densita){
		Random r = ((Pasquisland) Gdx.app.getApplicationListener()).getRandom();
		for(int y=0; y<map.getHeight(); y++) {
			for(int x=0; x< map.getWidth(); x++) {
				if(map.getTerrainTypeAt(x, y)==map.land_id) {
					if(r.nextFloat()<densita) {
						Palma cespuglio= new Palma(x*map.tile_size,y*map.tile_size);
						registerEntity(cespuglio);
					}
				}
			}
		}
	}

	public void ammazzaOmini() {
		for(int y=0; y<map.getHeight(); y++) {
			for(int x=0; x< map.getWidth(); x++) {
				synchronized(chiCeStaQua(x,y)) {
					chiCeStaQua(x, y).clear();
				}
			}
		}
		
		for (EntityProcessor ep : processors) {
			ep.getEntities().clear();
		}
	}
	

	//public int getPopulationCount() {return da_aggiornare.size;}
	
	public posRandom posizioneIntorno(GridPoint2 posizione) {
		posRandom newpos = new posRandom(posizione.x*map.tile_size,posizione.y*map.tile_size);
		newpos.gridposition= posizione.cpy(); 
		Random r = ((Pasquisland) Gdx.app.getApplicationListener()).getRandom();
		int r1= r.nextInt(4)-1;
		int r2= r.nextInt(4)-1;
		if (posizione.x+r1< map.getWidth() && posizione.x+r1>0 && posizione.y+r2<map.getHeight() && posizione.y+r2>0) {
			if(map.getTerrainTypeAt(posizione.x+r1, posizione.y+r2)!= WorldMap.water_id) {
					newpos.gridposition.x= posizione.x+r1;
					newpos.gridposition.y= posizione.y+r2;
					newpos.position.x= (posizione.x+r1)*map.tile_size;
					newpos.position.y= (posizione.y+r2)*map.tile_size;
			}
		}
		return newpos;
		
		
	}
	public Array<Entity> vedi(Entity omino,int raggio, boolean add_pos){
		Array<Entity> RaggioVisivo= new Array<Entity>();
		for( int y=omino.gridposition.y-raggio; y<= omino.gridposition.y+raggio; y++) {
			if(y< map.getHeight() && y>=0) {
				for( int x= omino.gridposition.x-raggio; x<=omino.gridposition.x+raggio; x++) {
					if (x< map.getWidth() && x>=0) {
						//DANGER
						synchronized(chiCeStaQua(x,y)) {
							Array<Entity> ccsq = chiCeStaQua(x,y);
							if( y==omino.gridposition.y && x== omino.gridposition.x) {
								for(int ei = 0; ei < ccsq.size; ei++) {
									if(ccsq.get(ei) != omino) {
										RaggioVisivo.add(ccsq.get(ei));
									}
								}
							}
							else {
									RaggioVisivo.addAll(ccsq);
							}
						}
					}
				}
			}
		}
		
		if(add_pos==true) {
		RaggioVisivo.add(posizioneIntorno(omino.gridposition));
		}
		return RaggioVisivo;
	}

	public Array<Entity> vedi(Entity omino, int raggio){
		return vedi(omino,raggio, true);
		
	}
	public static Mappone getInstance() {
		return  singleton;
	}
	
	public static Mappone createInstance(int width, int height) {
		singleton = new Mappone(width, height);
		return singleton;
	}
	
	public void spawnaBimbo(Omino genitore1, Omino genitore2) {
		Random r = ((Pasquisland) Gdx.app.getApplicationListener()).getRandom();
		int r1= r.nextInt(2);
		if(r1==0) {
			posRandom newpos= posizioneIntorno(genitore1.gridposition);
			Omino bimbo= new Omino(newpos.gridposition.x*WorldMap.tile_size,newpos.gridposition.y*WorldMap.tile_size);
			registerEntity(bimbo);
			assegnaNuoviValoriAlBimbo(genitore1, genitore2, bimbo);
		}
		else if(r1==1){
			posRandom newpos= posizioneIntorno(genitore2.gridposition);
			Omino bimbo= new Omino(newpos.gridposition.x*WorldMap.tile_size,newpos.gridposition.y*WorldMap.tile_size);
			registerEntity(bimbo);
			assegnaNuoviValoriAlBimbo(genitore1, genitore2, bimbo);
		}
	}
	
	private void assegnaNuoviValoriAlBimbo(Omino genitore1, Omino genitore2, Omino bimbo) {
		Random r = ((Pasquisland) Gdx.app.getApplicationListener()).getRandom();
		bimbo.setValues(
				MathUtils.clamp(genitore1.strength + genitore2.strength * r.nextFloat() / 10 * (r.nextInt(3) - 1), Omino.MIN_STRENGTH, Omino.MAX_STRENGTH),
				MathUtils.clamp(genitore1.sociality + genitore2.sociality * r.nextFloat() / 10 * (r.nextInt(3) - 1), Omino.MIN_SOCIALITY, Omino.MAX_SOCIALITY),
				MathUtils.clamp(genitore1.speed + genitore2.speed * r.nextFloat() / 10 * (r.nextInt(3) - 1), Omino.MIN_SPEED, Omino.MAX_SPEED));
	}

	public void vediRect(Rectangle rettangolo) {
		selected_entities.clear();
		int xgs= (int) (rettangolo.x/map.tile_size);//vertice basso sx griglia
		int ygs= (int) (rettangolo.y/map.tile_size);//vertice basso sx griglia
		float yd= rettangolo.y+ rettangolo.height;// vertice alto dx pixel
		float xd= rettangolo.x+rettangolo.width;//vertice alto dx pixel
		int ygd= (int) (yd/map.tile_size);//vertice alto dx griglia
		int xgd= (int) (xd/map.tile_size);//vertice alto dx griglia
		for(int y=Math.min(ygs, ygd); y<= Math.max(ygs,ygd); y++) {
			if(rettangolo.y<=map.getHeight()*map.tile_size && y>=0)
				for(int x= Math.min(xgs, xgd); x<=Math.max(xgs, xgd); x++) {
					if(rettangolo.x<=map.getWidth()*map.tile_size && x>=0) 
					for( Entity entita: chiCeStaQua(x,y)) {
						if(entita instanceof Omino) {
							selected_entities.add(entita);
							}
						}
					}
				}
			}	

	public void spawnaPalmaQuaVicino(GridPoint2 posizione) {
	posRandom newpos= posizioneIntorno(posizione);
	if(map.getTerrainTypeAt(newpos.gridposition.x, newpos.gridposition.y)== map.land_id) {
		if(!presente(newpos.gridposition.x,newpos.gridposition.y,Palma.class)) {
			Palma palmetta= new Palma(newpos.gridposition.x*map.tile_size,newpos.gridposition.y*map.tile_size);
			registerEntity(palmetta);
			return;
			}
		}
	}
	
	/**
	 * Metodo che Evita di riscrivere 20 volte la stessa parte di codice <3
	 * 
	 * @param e
	 */
	private void registerEntity(Entity entity) {
		getFreeProcessor().addEntity(entity);
		synchronized(chiCeStaQua(entity.gridposition)) {
			chiCeStaQua(entity.gridposition).add(entity);
		}
	}
	
	private <T extends Entity> boolean presente(int x,int y, Class<T> cls){
		for(Entity e: chiCeStaQua(x,y)) {
			if(cls.isInstance(e)) 
				return true;
		}
		return false;
	}
	
	private EntityProcessor getFreeProcessor() {
		for (EntityProcessor check : processors)
			if (!check.isFull())
				return check;
		
		EntityProcessor processor = new EntityProcessor();
		processors.add(processor);
		
		return processor;
	}

}
