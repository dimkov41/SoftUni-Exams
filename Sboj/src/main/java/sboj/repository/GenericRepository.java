package sboj.repository;

import java.util.List;
import java.util.Optional;

public interface GenericRepository<E,K> {
    Optional<E> save(E entity);

    List<E> getAll();
}
