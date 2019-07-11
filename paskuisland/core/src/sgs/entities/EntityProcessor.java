package sgs.entities;

import java.util.concurrent.Callable;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;

import sgs.map.Mappone;

public class EntityProcessor implements Callable<Boolean> {
	
	private static final int MAX_ENTITIES = 256;
	
	private Array<Entity> da_aggiornare;
	private Array<Entity> crepate;
	
	public EntityProcessor() {
		da_aggiornare = new Array<Entity>(MAX_ENTITIES);
		crepate = new Array<Entity>();
	}

	@Override
	public Boolean call() {
		for (Entity entity : da_aggiornare) {
			entity.update(Gdx.graphics.getRawDeltaTime());
			if (entity.life <= 0)
				crepate.add(entity);
		}
		
		synchronized(da_aggiornare) {
			da_aggiornare.removeAll(crepate, true);
		}
		for (Entity entity : crepate) {
			synchronized(Mappone.getInstance().chiCeStaQua(entity.gridposition)) {
				Mappone.getInstance().chiCeStaQua(entity.gridposition).removeValue(entity, true);
			}
			if (entity instanceof Omino) {
				synchronized(Mappone.getInstance().getPopulationCountDictionary()) {
					Mappone.getInstance().getPopulationCountDictionary().put(((Omino) entity).tribu, 
							Mappone.getInstance().getPopulationCountDictionary().get(((Omino) entity).tribu) - 1);
				}
			}
		}
		
		crepate.clear();
		
		return true;
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
