package carsales.web;

import carsales.domain.*;
import carsales.service.LogicLayer;
import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseDataSource;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.ModelAndView;

import javax.sql.DataSource;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class CarSalesControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private LogicLayer logic;

    @Test
    public void testingShowAllCarsPage() throws Exception {
        this.mvc.perform(get("/cars"))
                .andExpect(status().isOk())
                .andExpect(view().name("cars"))
                .andExpect(content().contentType("text/html;charset=UTF-8"));
    }

    @Test
    public void testingShowAllCars() throws Exception {
        int size = convertToList(this.logic.findAllCars().iterator()).size();
        this.mvc.perform(get("/resource/allcars").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(size)))
                .andExpect(jsonPath("$[0].color", is("color_1")))
                .andExpect(jsonPath("$[1].year", is(2011)));
    }

    @Test
    public void testingAllBrand() throws Exception {
        this.mvc.perform(get("/resource/allbrand").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("brand_1")))
                .andExpect(jsonPath("$[1].name", is("brand_2")));
    }

    @Test
    @WithMockUser(username = "Nikolay", password = "123", roles = {"USER"})
    public void testingGetModelsByBrandWithAutorization() throws Exception {
        this.mvc.perform(get("/getmodels?brandId=1").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("model_1")));
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
        this.mvc.perform(post("/registration")
                .param("name", "user_1")
                .param("pass", "pass"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("message"))
                .andExpect(model().attributeDoesNotExist("error"));
        List<User> users = this.convertToList(this.logic.findAllUsers().iterator());
        assertEquals(3, users.size());
    }

    @Test
    @WithMockUser(username = "user", password = "123", roles = {"USER"})
    public void testingShowAddCarPageGet() throws Exception {
        this.mvc.perform(get("/addcar"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("brands", "types", "car"))
                .andExpect(view().name("addcar"))
                .andExpect(model().attribute("brands", this.logic.findAllBrands()))
                .andExpect(model().attribute("types", this.logic.findAllBodyType()));
    }

    @Test
    @WithMockUser(username = "Nikolay", password = "123", roles = {"USER"})
    public void testingShowAddCarPagePost() throws Exception {
        MockMultipartFile image = new MockMultipartFile("image", "".getBytes());
        Car car = this.logic.findAllCars().iterator().next();
        car.setId(3);
        this.mvc.perform(fileUpload("/addcar").file(image)
                .flashAttr("car", car)
                .accept(MediaType.MULTIPART_FORM_DATA))
                .andExpect(model().attributeExists("message"))
                .andExpect(view().name("cars"))
                .andExpect(status().isOk());
        assertEquals(3, convertToList(this.logic.findAllCars().iterator()).size());
    }

    @Test
    @WithMockUser(username = "Nikolay", password = "123", roles = {"USER"})
    public void testingShowEditPageGet() throws Exception {
        User currentUser = this.logic.findAllUsers().iterator().next();
        this.mvc.perform(get("/editcar"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(model().attributeExists("cars", "user"))
                .andExpect(model().attribute("cars", this.logic.findCarsByUser(currentUser)))
                .andExpect(view().name("editcar"));
    }

    @Test
    @WithMockUser(username = "user", password = "123", roles = {"USER"})
    public void testingShowEditCarPagePost() throws Exception {
        this.mvc.perform(post("/editcar").param("carId", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("editcar"));
        assertTrue(this.logic.findById(1).isStatus());
        this.logic.findById(1).setStatus(false);
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
        this.mvc.perform(get("/resource/filters")
                .param("brandId", "-1")
                .param("onlyFoto", "false")
                .param("currentData", "false"))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$[0].color", is("color_1")))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(status().isOk());
    }

    private <T> List<T> convertToList(Iterator<T> iterator) {
        List<T> source = new ArrayList<>();
        iterator.forEachRemaining(source::add);
        return source;
    }
}