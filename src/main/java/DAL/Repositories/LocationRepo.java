package DAL.Repositories;

import DAL.Contracts.CrudRepository;
import DAL.DataEntities.Registers.Location;

import javax.ejb.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

@Singleton
public class LocationRepo implements CrudRepository<Location, Long> {
    @PersistenceContext
    EntityManager entityManager;

    @Override
    public void registerDataSource(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<Location> getAll() {
        TypedQuery<Location> q = entityManager.createQuery("select l from Location l", Location.class);
        return q.getResultList();
    }

    @Override
    public void deleteItem(Long key) {
        Location item = getItem(key);
        entityManager.remove(item);
    }

    @Override
    public Location getItem(Long key) {
        return entityManager.find(Location.class, key);
    }

    @Override
    public Class<?> getEntityClass() {
        return Location.class;
    }
}
