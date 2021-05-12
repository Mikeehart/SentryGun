package com.mutated.sentryGun.controller;

import com.pi4j.io.gpio.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TriggerController {

    private static GpioPinDigitalOutput pin;

    @RequestMapping("/")
    public String bootMessage(){
        return "Sentry gun online";
    }

    @RequestMapping("/primerTrigger")
    public String primerTrigger(){

        if(pin == null) {

            GpioController gpio = GpioFactory.getInstance();
            pin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_04, "primer", PinState.LOW);
        }

        pin.toggle();

        return "Sentry arming...";
    }

}
