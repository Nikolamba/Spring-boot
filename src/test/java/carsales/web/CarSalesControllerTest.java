package carsales.web;

import carsales.domain.*;
import carsales.service.LogicLayer;
import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.ModelAndView;

import javax.sql.DataSource;

import java.util.ArrayList;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.fileUpload;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(CarSalesController.class)
public class CarSalesControllerTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private LogicLayer logic;

    @MockBean
    DataSource dataSource;

    private User user_1 = new User("Nikolay", "123");
    private User user_2 = new User("Masha", "321");
    private BodyType bodyType_1 = new BodyType("BodyType_1");
    private BodyType bodyType_2 = new BodyType("BodyType_2");
    private Brand brand_1 = new Brand("brand_1");
    private Brand brand_2 = new Brand("brand_2");
    private Model model_1 = new Model("model_1", brand_1);
    private Model model_2 = new Model("model_2", brand_2);
    private Car car_1 = new Car(2010, "color_1", model_1, bodyType_1, user_1);
    private Car car_2 = new Car(2011, "color_2", model_2, bodyType_2, user_2);

    @Test
    public void testingShowAllCarsPage() throws Exception {
        this.mvc.perform(get("/cars"))
                .andExpect(status().isOk())
                .andExpect(view().name("cars"))
                .andExpect(content().contentType("text/html;charset=UTF-8"));
    }

    @Test
    public void testingShowAllCars() throws Exception {
        given(this.logic.findAllCars()).willReturn(new ArrayList<Car>(Lists.newArrayList(car_1, car_2)));
        this.mvc.perform(get("/resource/allcars").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].color", is(car_1.getColor())))
                .andExpect(jsonPath("$[1].year", is(car_2.getYear())));
    }

    @Test
    public void testingAllBrand() throws Exception {
        given(this.logic.findAllBrands()).willReturn(new ArrayList<>(Lists.newArrayList(brand_1, brand_2)));
        this.mvc.perform(get("/resource/allbrand").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is(brand_1.getName())))
                .andExpect(jsonPath("$[1].name", is(brand_2.getName())));
    }

    @Test
    @WithMockUser(username = "Nikolay", password = "123", roles = {"USER"})
    public void testingGetModelsByBrandWithAutorization() throws Exception {
        brand_1.setId(1);
        given(this.logic.findModelsByBrand(any(Brand.class))).willReturn(new ArrayList<>(Lists.newArrayList(model_1)));
        this.mvc.perform(get("/getmodels?brandId=1").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is(model_1.getName())));
    }

    @Test
    public void testingGetModelsByBrandWithoutAutorization() throws Exception {
        this.mvc.perform(get("/getmodels?brandId=1").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("http://**/login"));
    }

    @Test
    public void testingShowRegistrationPageGet() throws Exception {
        this.mvc.perform(get("/registration"))
                .andExpect(status().isOk())
                .andExpect(view().name("registration"))
                .andExpect(content().contentType("text/html;charset=UTF-8"));
    }

    @Test
    public void testingShowRegistrationPagePost() throws Exception {
        given(this.logic.registrationUser("user_1", "pass")).willReturn(true);
        this.mvc.perform(post("/registration")
                .param("name", "user_1")
                .param("pass", "pass"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("message"))
                .andExpect(model().attributeDoesNotExist("error"));
        verify(this.logic, times(1)).registrationUser("user_1", "pass");
    }

    @Test
    @WithMockUser(username = "user", password = "123", roles = {"USER"})
    public void testingShowAddCarPageGet() throws Exception {
        given(this.logic.findAllBrands()).willReturn(new ArrayList<>(Lists.newArrayList(brand_1)));
        given(this.logic.findAllBodyType()).willReturn(new ArrayList<>(Lists.newArrayList(bodyType_1)));
        this.mvc.perform(get("/addcar"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("brands", "types", "car"))
                .andExpect(view().name("addcar"));
        verify(this.logic, times(1)).findAllBrands();
        verify(this.logic, times(1)).findAllBodyType();
    }

    @Test
    @WithMockUser(username = "user", password = "123", roles = {"USER"})
    public void testingShowAddCarPagePost() throws Exception {
        given(logic.findUserByName(anyString())).willReturn(user_1);
        MockMultipartFile image = new MockMultipartFile("image", "".getBytes());
        this.mvc.perform(fileUpload("/addcar").file(image)
                .flashAttr("car", car_1)
                .accept(MediaType.MULTIPART_FORM_DATA))
                .andExpect(model().attributeExists("message"))
                .andExpect(status().isOk());
        verify(this.logic, times(1)).addCar(any(Car.class));
        verify(this.logic, times(1)).findUserByName(anyString());
        verifyNoMoreInteractions(this.logic);
    }

    @Test
    @WithMockUser(username = "user", password = "123", roles = {"USER"})
    public void testingShowEditPageGet() throws Exception {
        given(logic.findUserByName(anyString())).willReturn(user_1);
        given(logic.findCarsByUser(any(User.class))).willReturn(new ArrayList<>(Lists.newArrayList(car_1)));
        this.mvc.perform(get("/editcar"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(model().attributeExists("cars", "user"))
                .andExpect(view().name("editcar"));
        verify(this.logic, times(1)).findUserByName(anyString());
        verify(this.logic, times(1)).findCarsByUser(any(User.class));
        verifyNoMoreInteractions(this.logic);
    }

    @Test
    @WithMockUser(username = "user", password = "123", roles = {"USER"})
    public void testingShowEditCarPagePost() throws Exception {
        given(logic.findById(anyInt())).willReturn(car_1);
        this.mvc.perform(post("/editcar").param("carId", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("editcar"));
        verify(logic, times(1)).findById(anyInt());
        verify(logic, times(1)).editCar(any(Car.class));
    }

    @Test
    public void testingShowSigninPageGet() throws Exception {
        this.mvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(content().contentType("text/html;charset=UTF-8"));
    }

    @Test
    public void testingGetAllCarsByFilters() throws Exception {
        given(logic.findAllCars()).willReturn(new ArrayList<>(Lists.newArrayList(car_1)));
        this.mvc.perform(get("/resource/filters")
                .param("brandId", "-1")
                .param("onlyFoto", "false")
                .param("currentData", "false"))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$[0].color", is("color_1")))
                .andExpect(status().isOk());
        verify(this.logic, times(1)).findAllCars();

    }
}