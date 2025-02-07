package com.capgemini.opapolicies.api;

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
    public String getOnion() {
        return "Onion";
    }

    @GetMapping("/milk")
    @ResponseStatus(HttpStatus.OK)
    public String getMilk() {
        return "Milk";
    }

    @GetMapping("/beef")
    @ResponseStatus(HttpStatus.OK)
    public String getBeef() {
        return "Beef";
    }

    @GetMapping("/fish")
    @ResponseStatus(HttpStatus.OK)
    public String getFish() {
        return "Fish";
    }

    @GetMapping("/apple")
    @ResponseStatus(HttpStatus.OK)
    public String getApple() {
        return "Apple";
    }

    @GetMapping("/cheese")
    @ResponseStatus(HttpStatus.OK)
    public String getCheese() {
        return "Cheese";
    }

    @GetMapping("/egg")
    @ResponseStatus(HttpStatus.OK)
    public String getEgg() {
        return "Egg";
    }

    @GetMapping("/bread")
    @ResponseStatus(HttpStatus.OK)
    public String getBread() {
        return "Bread";
    }

    @GetMapping("/salmon")
    @ResponseStatus(HttpStatus.OK)
    public String getSalmon() {
        return "Salmon";
    }

    @GetMapping("/carrot")
    @ResponseStatus(HttpStatus.OK)
    public String getCarrot() {
        return "Carrot";
    }
}