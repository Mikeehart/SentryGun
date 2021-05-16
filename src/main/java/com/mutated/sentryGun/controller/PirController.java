package com.mutated.sentryGun.controller;

import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import org.springframework.context.event.EventListener;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PirController {

    private static GpioPinDigitalInput pir;
    TriggerController trigger = new TriggerController();

    private String pirInput(){

        if(pir == null)
        {
            GpioController gpio = GpioFactory.getInstance();
            pir = gpio.provisionDigitalInputPin(RaspiPin.GPIO_02 ,"PIR pin", PinPullResistance.PULL_UP);
        }

        return "PIR input pin allocated";

    }

    @RequestMapping("/SentryMode")
    @EventListener
    public String inputListener(){

        pir.addListener(new GpioPinListenerDigital() {
            @Override
            public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {

                if(event.getState().isHigh() && !trigger.getIsFiring()){
                    try {
                        trigger.fire();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                else if(event.getState().isLow() && trigger.getIsFiring()) {
                    try {
                        trigger.ceaseFire();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
        return "Motion detected!";
    }


}
