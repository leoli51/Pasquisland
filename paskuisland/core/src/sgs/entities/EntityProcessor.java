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
		synchronized(da_aggiornare) {
			for (Entity entity : da_aggiornare) {
				entity.update(Gdx.graphics.getRawDeltaTime());
				if (entity.life <= 0)
					crepate.add(entity);
			}
			
		
			da_aggiornare.removeAll(crepate, true);
		}
		
		synchronized(crepate) {
			for (Entity entity : crepate) {
				synchronized(Mappone.getInstance().chiCeStaQua(entity.gridposition)) {
					Mappone.getInstance().chiCeStaQua(entity.gridposition).removeValue(entity, true);
				}
				if (entity instanceof Omino) {
					/*synchronized(Mappone.getInstance().getPopulationCountDictionary()) {
						Mappone.getInstance().getPopulationCountDictionary().put(((Omino) entity).tribu, 
								Mappone.getInstance().getPopulationCountDictionary().getOrDefault((((Omino) entity).tribu),0) - 1);
					}*/
					synchronized (Mappone.getInstance().getTribePopulationCounter()) {
						//Gdx.app.log("ec", Thread.currentThread().getName() + " " +crepate.size+" "+((Omino)entity).tribu);
						//if (Mappone.getInstance().getTribePopulationCounter()[Omino.getTribuIndex(((Omino) entity).tribu)] > 0)
						Mappone.getInstance().getTribePopulationCounter()[Omino.getTribuIndex(((Omino) entity).tribu)] --;
					}
				}
			}
			
			crepate.clear();
			//Gdx.app.log("ec", Thread.currentThread().getName() + " " +crepate.size);
		}
		
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