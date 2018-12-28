package carsales.repository;

import carsales.domain.Car;
import carsales.domain.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Nikolay Meleshkin (sol.of.f@mail.ru)
 * @version 0.1
 */
@Repository
public interface DAOCarImp extends CrudRepository<Car, Integer> {
    List<Car> findBySeller(User user);
}
