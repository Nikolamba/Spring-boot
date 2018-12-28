package carsales.repository;

import carsales.domain.Brand;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Nikolay Meleshkin (sol.of.f@mail.ru)
 * @version 0.1
 */
@Repository
public interface DAOBrandImp extends CrudRepository<Brand, Integer> {
    boolean existsByName(String name);
}
