package DAL.Repositories;

import DAL.Contracts.CrudRepository;
import DAL.DataEntities.Registers.Equipment;

import javax.ejb.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

@Singleton
public class EquipmentRepo implements CrudRepository<Equipment, Long> {
    @PersistenceContext
    EntityManager entityManager;

    @Override
    public void registerDataSource(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<Equipment> getAll() {
        TypedQuery<Equipment> q = entityManager.createQuery("select p from Equipment p", Equipment.class);
        return q.getResultList();
    }

    @Override
    public void deleteItem(Long key) {
        Equipment item = getItem(key);
        entityManager.remove(item);
    }

    @Override
    public Equipment getItem(Long key) {
        return entityManager.find(Equipment.class, key);
    }

    @Override
    public Class<?> getEntityClass() {
        return Equipment.class;
    }
}
