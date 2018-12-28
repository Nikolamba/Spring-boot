package carsales.repository;

import carsales.domain.BodyType;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * @author Nikolay Meleshkin (sol.of.f@mail.ru)
 * @version 0.1
 */
@Component
public interface DAOBodyTypeImp extends CrudRepository<BodyType, Integer>{
    boolean existsByName(String name);
    Optional<BodyType> findBodyTypeByName(String name);
}
