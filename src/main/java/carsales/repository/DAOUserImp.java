package carsales.repository;

import carsales.domain.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

/**
 * @author Nikolay Meleshkin (sol.of.f@mail.ru)
 * @version 0.1
 */
@Component
public interface DAOUserImp extends CrudRepository<User, Integer> {

    User findByName(String name);
    boolean existsByNameAndPass(String name, String pass);
}
