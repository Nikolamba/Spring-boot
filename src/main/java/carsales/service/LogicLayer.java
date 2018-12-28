package carsales.service;

import carsales.repository.*;
import carsales.domain.*;
import org.hibernate.Filter;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Nikolay Meleshkin (sol.of.f@mail.ru)
 * @version 0.1
 */
@Service
public class LogicLayer {
    @Autowired
    private DAOCarImp daoCar;
    @Autowired
    private DAOModelImp daoModel;
    @Autowired
    private DAOBodyTypeImp daoBodyType;
    @Autowired
    private DAOBrandImp daoBrand;
    @Autowired
    private DAOUserImp daoUser;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @PersistenceContext
    private EntityManager entityManager;

    public void addCar(Car car) {
        daoCar.save(car);
    }

    public BodyType findBodyTypeByName(String name) {
        return daoBodyType.findBodyTypeByName(name).orElse(null);
   }

    public Model findModelById(Integer id) {
        return daoModel.findById(id).orElse(null);
    }

    public User findUserByName(String name) {
        return daoUser.findByName(name);
    }

    public Iterable<Brand> findAllBrands() {
        return daoBrand.findAll();
    }

    public Iterable<BodyType> findAllBodyType() {
        return daoBodyType.findAll();
    }

    public Iterable<Car> findAllCars() {
        return daoCar.findAll();
    }

    public List<Model> findModelsByBrand(Brand brand) {
        return daoModel.findByBrand(brand);
    }

    public List<Car> findCarsByUser(User user) {
        return daoCar.findBySeller(user);
    }

    public boolean registrationUser(String name, String pass) {
        Iterator<User> source = daoUser.findAll().iterator();
        List<User> target = new ArrayList<>();
        source.forEachRemaining(target::add);
        if (target.stream().anyMatch(user -> user.getName().trim().equals(name))) {
            return false;
        } else {
            daoUser.save(new User(name, passwordEncoder.encode(pass)));
            return true;
        }
    }

    @Transactional
    public Iterable<Car> useFilters(Map<String, Integer> filters) {
        for (String filter : filters.keySet()) {
            if (filter.equals("brandFilter")) {
                Filter fil = entityManager.unwrap(Session.class).enableFilter(filter);
                fil.setParameter("brand_id", filters.get(filter));
            } else {
                entityManager.unwrap(Session.class).enableFilter(filter);
            }
        }
        return daoCar.findAll();
    }

    public Car findById(Integer carId) {
        return daoCar.findById(carId).orElse(null);
    }

    public void editCar(Car car) {
        daoCar.save(car);
    }
}
