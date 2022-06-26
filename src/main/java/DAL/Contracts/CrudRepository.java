package DAL.Contracts;

import javax.ejb.Local;
import javax.persistence.EntityManager;
import java.util.List;

@Local
public interface CrudRepository<T,K> {
    void registerDataSource(EntityManager entityManager);
    List<T> getAll();
    T getItem(K key);
    void deleteItem(K key);
    Class<?> getEntityClass();
}
