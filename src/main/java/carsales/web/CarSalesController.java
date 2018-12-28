package carsales.web;

import carsales.domain.Brand;
import carsales.domain.Car;
import carsales.service.LogicLayer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletContext;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Nikolay Meleshkin (sol.of.f@mail.ru)
 * @version 0.1
 */
@Controller
public class CarSalesController {
    private LogicLayer logic;
    private static String UPLOADED_FOLDER = "C://temp//";

    @Autowired
    public CarSalesController(LogicLayer logic) {
        this.logic = logic;
    }



    @GetMapping ("/cars")
    public String showCarsView() {
        return "cars";
    }

    @GetMapping ("/resource/allcars")
    @ResponseBody
    public Iterable<Car> getAllCars() {
        return logic.findAllCars();
    }

    @GetMapping("/resource/allbrand")
    @ResponseBody
    public Iterable<Brand> getAllBrands() {
        return logic.findAllBrands();
    }

    @GetMapping("/getmodels")
    @ResponseBody
    public List<carsales.domain.Model> getModelsByBrand(@RequestParam Integer brandId) {
        Brand brand = new Brand();
        brand.setId(brandId);
        return logic.findModelsByBrand(brand);
    }

    @GetMapping ("/registration")
    public String showRegistrationPage() {
        return "registration";
    }

    @PostMapping ("/registration")
    public String registrationUser(@RequestParam String name, @RequestParam String pass, Model model) {
        if (logic.registrationUser(name, pass)) model.addAttribute("message", "registration successfully completed");
        else model.addAttribute("error", "user with this name is already registered");
        return "registration";
    }

    @GetMapping("/addcar")
    public String showAddCarPage(Model model) {
        model.addAttribute("brands", logic.findAllBrands());
        model.addAttribute("types", logic.findAllBodyType());
        model.addAttribute(new Car());
        return "addcar";
    }

    @PostMapping("/addcar")
    public ModelAndView performAdditionCar(@ModelAttribute @Valid Car car,
                                           BindingResult bindingResult,
                                           @RequestParam(value = "image", required = false) MultipartFile image,
                                           ModelAndView model, Principal principal) {
        if (bindingResult.hasErrors()) {

            model.setViewName("addcar");
            model.addObject("brands", logic.findAllBrands());
            model.addObject("types", logic.findAllBodyType());
            return model;
        }
        if (image.isEmpty()) {
            car.setPicturePath("");
        } else {
            File file = new File(UPLOADED_FOLDER);
            if (!file.exists()) {
                file.mkdir();
            }
            Path fileDir = Paths.get(UPLOADED_FOLDER + image.getOriginalFilename());
            car.setPicturePath(image.getOriginalFilename());
            try {
                Files.write(fileDir, image.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
                model.addObject("error", "failed to save image");
                model.setViewName("addcar");
                return model;
            }
        }
        car.setSeller(logic.findUserByName(principal.getName()));
        logic.addCar(car);
        model.addObject("message", "application successfully added");
        model.setViewName("cars");
        return model;
    }

    @GetMapping("/editcar")
    public String showEditPage(Principal principal, Model model) {
        String userName = principal.getName();
        model.addAttribute("cars", logic.findCarsByUser(logic.findUserByName(userName)));
        model.addAttribute("user", principal.getName());
        return "editcar";
    }

    @PostMapping("/editcar")
    public String changeItemStatus(@RequestParam Integer carId, Model model) {
        Car car = logic.findById(carId);
        car.setStatus(!car.isStatus());
        logic.editCar(car);
        return "redirect:editcar";
    }

    @GetMapping("/login")
    public String showSigninPage(@RequestParam(value = "error", required = false) String error,
                                 Model model) {
        if (error != null){
            model.addAttribute("error", "Invalid username and password!");
        }
        return "login";
    }

    @GetMapping("/resource/filters")
    @ResponseBody
    public Iterable<Car> getAllCarsByFilters(@RequestParam Integer brandId,
                                             @RequestParam Boolean onlyFoto, @RequestParam Boolean currentData) {
        Map<String, Integer> filterMap = new HashMap<>();
        if (brandId != -1) { filterMap.put("brandFilter", brandId);
        }
        if (onlyFoto) {
            filterMap.put("onlyFotoFilter", 1);
        }
        if (currentData) {
            filterMap.put("currentDataFilter", 1);
        }
        return  (filterMap.isEmpty()) ? logic.findAllCars() : logic.useFilters(filterMap);
    }
}
