package com.capgemini.opapolicies.api;

import jakarta.annotation.security.RolesAllowed;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/food")
public class FoodController {

    @GetMapping("/onion")
    @ResponseStatus(HttpStatus.OK)
    @RolesAllowed({"vegan", "vegetarian", "omnivorous"})
    public String getOnion() {
        return "Onion";
    }

    @GetMapping("/milk")
    @ResponseStatus(HttpStatus.OK)
    @RolesAllowed({"vegetarian", "omnivorous"})
    public String getMilk() {
        return "Milk";
    }

    @GetMapping("/beef")
    @ResponseStatus(HttpStatus.OK)
    @RolesAllowed({"omnivorous"})
    public String getBeef() {
        return "Beef";
    }

    @GetMapping("/fish")
    @ResponseStatus(HttpStatus.OK)
    @RolesAllowed({"pescatarian", "omnivorous"})
    public String getFish() {
        return "Fish";
    }

    @GetMapping("/apple")
    @ResponseStatus(HttpStatus.OK)
    @RolesAllowed({"fruitarian", "vegan", "vegetarian", "omnivorous"})
    public String getApple() {
        return "Apple";
    }

    @GetMapping("/cheese")
    @ResponseStatus(HttpStatus.OK)
    @RolesAllowed({"vegetarian", "omnivorous"})
    public String getCheese() {
        return "Cheese";
    }

    @GetMapping("/egg")
    @ResponseStatus(HttpStatus.OK)
    @RolesAllowed({"vegetarian", "omnivorous"})
    public String getEgg() {
        return "Egg";
    }

    @GetMapping("/bread")
    @ResponseStatus(HttpStatus.OK)
    @RolesAllowed({"vegan", "vegetarian", "omnivorous"})
    public String getBread() {
        return "Bread";
    }

    @GetMapping("/salmon")
    @ResponseStatus(HttpStatus.OK)
    @RolesAllowed({"pescatarian", "omnivorous"})
    public String getSalmon() {
        return "Salmon";
    }

    @GetMapping("/carrot")
    @ResponseStatus(HttpStatus.OK)
    @RolesAllowed({"vegan", "vegetarian", "omnivorous"})
    public String getCarrot() {
        return "Carrot";
    }
}