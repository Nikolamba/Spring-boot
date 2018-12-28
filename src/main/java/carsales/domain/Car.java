package carsales.domain;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.ParamDef;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.Objects;

/**
 * @author Nikolay Meleshkin (sol.of.f@mail.ru)
 * @version 0.1
 */
@Entity
@Table(name = "car")
@FilterDef(name = "onlyFotoFilter")
@FilterDef(name = "currentDataFilter")
@FilterDef(name = "brandFilter", parameters = {
        @ParamDef(name = "brand_id", type = "integer")
})

@Filters({
        @Filter(name = "onlyFotoFilter", condition = "picture_path != ''"),
        @Filter(name = "currentDataFilter",
                condition = "date_part('year', data) = date_part('year', current_date) AND "
                        + "date_part('month', data) = date_part('month', current_date) AND "
                        + "date_part('day', data) = date_part('day', current_date)"),
        @Filter(name = "brandFilter", condition = "model_id in (select car.model_id from car "
                + "inner join model on car.model_id = model.id "
                + "inner join brand on model.brand_id = brand.id "
                + "where brand.id = :brand_id)")
})
public class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Min(1950) @Max(2018)
    @Column(name = "year")
    private int year;

    @Column(name = "status")
    private boolean status;

    @Size(min = 2, max = 30)
    @Column(name = "color")
    private String color;

    @Column(name = "picture_path")
    private String picturePath;

    @Column(name = "data")
    private Date data;

    @ManyToOne
    @JoinColumn(name = "model_id")
    private Model model;

    @ManyToOne
    @JoinColumn(name = "body_type")
    private BodyType bodyType;

    @ManyToOne
    @JoinColumn(name = "seller")
    private User seller;

    public Car() {
        data = new Date();
    }

    public Car(int year, String color, Model model, BodyType bodyType, User seller) {
        this.year = year;
        this.color = color;
        this.model = model;
        this.bodyType = bodyType;
        this.seller = seller;
        this.status = false;
        this.picturePath = "";
        data = new Date();
    }

    public int getId() {
        return id;
    }

    public int getYear() {
        return year;
    }

    public boolean isStatus() {
        return status;
    }

    public String getColor() {
        return color;
    }

    public String getPicturePath() {
        return picturePath;
    }

    public Model getModel() {
        return model;
    }

    public BodyType getBodyType() {
        return bodyType;
    }

    public User getSeller() {
        return seller;
    }

    public Date getData() {
        return data;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setPicturePath(String picturePath) {
        this.picturePath = picturePath;
    }

    public void setModel(Model model) {
        this.model = model;
    }

    public void setBodyType(BodyType bodyType) {
        this.bodyType = bodyType;
    }

    public void setSeller(User seller) {
        this.seller = seller;
    }

    public void setData(Date data) {
        this.data = data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Car car = (Car) o;
        return id == car.id
                && year == car.year
                && status == car.status
                && Objects.equals(color, car.color)
                && Objects.equals(data, car.data)
                && Objects.equals(model, car.model)
                && Objects.equals(bodyType, car.bodyType)
                && Objects.equals(seller, car.seller);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, year, status, color, data, model, bodyType, seller);
    }
}
