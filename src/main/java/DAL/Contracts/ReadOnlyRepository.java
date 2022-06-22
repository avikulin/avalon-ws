package DAL.Contracts;

import javax.ejb.Local;
import javax.persistence.EntityManager;
import java.util.List;

@Local
public interface ReadOnlyRepository<T> {
    void registerDataSource(EntityManager entityManager);
    List<T> getAll();
    Class<?> getEntityClass();
}
