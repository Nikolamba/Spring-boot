package carsales.domain;

import javax.persistence.*;
import java.util.Objects;

/**
 * @author Nikolay Meleshkin (sol.of.f@mail.ru)
 * @version 0.1
 */
@Entity
@Table(name = "model")
public class Model {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "name")
    private String name;

    @ManyToOne
    @JoinColumn(name = "brand_id")
    private Brand brand;

    public Model() {
    }

    public Model(String name, Brand brand) {
        this.name = name;
        this.brand = brand;
    }

    public Model(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name.trim();
    }

    public Brand getBrand() {
        return brand;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBrand(Brand brand) {
        this.brand = brand;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Model model = (Model) o;
        return id == model.id
                && Objects.equals(name, model.name)
                && Objects.equals(brand, model.brand);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, brand);
    }
}
