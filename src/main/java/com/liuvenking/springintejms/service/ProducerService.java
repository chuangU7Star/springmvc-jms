package com.liuvenking.springintejms.service;

import javax.jms.Destination;

/**
 * Created by venking on 15/11/4.
 */
public interface ProducerService {

    public void sendMessage(Destination destination, final String message);
}
