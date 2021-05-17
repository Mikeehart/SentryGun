package com.mutated.sentryGun.controller;

import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PirController {

    private static GpioPinDigitalInput pir;
    private TriggerController trigger = new TriggerController();

    private void provisionPirInput(){

        if(pir == null)
        {
            GpioController gpio = GpioFactory.getInstance();
            pir = gpio.provisionDigitalInputPin(RaspiPin.GPIO_02 ,"PIR pin", PinPullResistance.PULL_UP);
        }
    }

    @RequestMapping("/SentryMode")
    public String inputListener(){

        provisionPirInput();
        pir.addListener(new GpioPinListenerDigital() {
            @Override
            public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {

                if(event.getState().isHigh() && !trigger.getIsFiring()){
                    try {
                        trigger.fire();
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                else if(event.getState().isLow() && trigger.getIsFiring()) {
                    try {
                        trigger.ceaseFire();
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
        return "Sentry Mode activated";
    }


}
