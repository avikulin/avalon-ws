package DAL.Repositories;

import DAL.Contracts.ReadOnlyRepository;
import DAL.DataEntities.Registers.Equipment;

import javax.ejb.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

@Singleton
public class EquipmentRepo implements ReadOnlyRepository<Equipment> {
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
    public Class<?> getEntityClass() {
        return Equipment.class;
    }
}
