package sgs.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;

import sgs.map.Mappone;

public class EntityProcessor implements Runnable {
	
	private static final int MAX_ENTITIES = 256;
	
	private Array<Entity> da_aggiornare;
	private Array<Entity> crepate;
	
	public EntityProcessor() {
		da_aggiornare = new Array<Entity>(MAX_ENTITIES);
		crepate = new Array<Entity>();
	}

	@Override
	public void run() {
		for (Entity entity : da_aggiornare) {
			entity.update(Gdx.graphics.getRawDeltaTime());
			if (entity.life <= 0)
				crepate.add(entity);
		}
		
		synchronized(da_aggiornare) {
			da_aggiornare.removeAll(crepate, true);
		}
		for (Entity entity : crepate)
			synchronized(Mappone.getInstance().chiCeStaQua(entity.gridposition)) {
				Mappone.getInstance().chiCeStaQua(entity.gridposition).removeValue(entity, true);
			}
		
		crepate.clear();
	}
	
	public boolean isFull() {
		synchronized(da_aggiornare) {
			return da_aggiornare.size > MAX_ENTITIES;
		}
	}
	
	public void addEntity(Entity entity) {
		synchronized(da_aggiornare) {
			da_aggiornare.add(entity);
		}
	}
	
	public Array<Entity> getEntities(){
		synchronized(da_aggiornare) {
			return da_aggiornare;
		}
	}
}
