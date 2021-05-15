package com.mutated.sentryGun.controller;

import com.pi4j.io.gpio.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TriggerController {

    private static GpioPinDigitalOutput relayPin1;
    private static GpioPinDigitalOutput relayPin2;
    private boolean isFiring = false;

    @RequestMapping("/")
    public String bootMessage(){
        return "Sentry gun online";
    }

    private String primerTrigger(){

        if(relayPin1 == null) {

            GpioController gpio = GpioFactory.getInstance();
            relayPin1 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_04, "primer relay", PinState.LOW);
        }

        relayPin1.toggle();

        return "primerTrigger() called";
    }

    private String fireTrigger(){

        if(relayPin2 == null) {

            GpioController gpio = GpioFactory.getInstance();
            relayPin2 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_01, "fire relay", PinState.LOW);
        }

        relayPin2.toggle();

            return "fireTrigger() called";
    }

    private String fire() throws InterruptedException {

        primerTrigger();
        Thread.sleep(2000);
        fireTrigger();
        isFiring = true;

        return "Engaging target";

    }

    private String ceaseFire() throws InterruptedException {

        fireTrigger();
        Thread.sleep(1000);
        primerTrigger();
        isFiring = false;

        return "Target neutralized";
    }

    @RequestMapping("/toggleTrigger")
    public String toggleTrigger(){

        String message = "exception caught";
        try {
                if(!isFiring)
                    message = fire();
                else
                    message = ceaseFire();

        } catch (InterruptedException e) {
            e.printStackTrace();

        }

        return message;
    }
}
