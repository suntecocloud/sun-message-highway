/*
 *  Copyright (C) 2021 Sunteco, Inc.
 *
 *  Sunteco License Notice
 *
 *  The contents of this file are subject to the Sunteco License
 *  Version 1.0 (the "License"). You may not use this file except in
 *  compliance with the License. The Initial Developer of the Original
 *  Code is Sunteco, JSC. Portions Copyright 2021 Sunteco JSC
 *
 *  All Rights Reserved.
 */

package com.sunteco.example;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SimpleProducer {

    public static void main(String[] args) throws InterruptedException {
        String bootstrapServers = "sunteco-northvn-sun.sunteco.cloud:9094";
        String apiKey = "myApiKey";
        String apiSecret = "myApiSecret";

        String ClientID = "myClient";
        String topicName = "myTopic";


        String jaasTemplate = "org.apache.kafka.common.security.scram.ScramLoginModule required username=\"%s\" password=\"%s\";";
        String jaasCfg = String.format(jaasTemplate, apiKey, apiSecret);

        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.CLIENT_ID_CONFIG, String.valueOf(new Date().getTime()));
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "100");
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "15000");

        props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_SSL");
        props.put(SaslConfigs.SASL_MECHANISM, "SCRAM-SHA-512");
        props.put(SaslConfigs.SASL_JAAS_CONFIG, jaasCfg);

        ProducerFactory<String, String> producerFactory = new DefaultKafkaProducerFactory<>(props);
        KafkaTemplate kafkaTemplate = new KafkaTemplate<>(producerFactory);
        for (int i = 0; i < 100; i++) {
            kafkaTemplate.send(topicName, String.valueOf(new Date().getTime()), "Hello sunteco " + i);
            System.out.printf("Hello sunteco " + i + "\n");
            Thread.sleep(100);
        }
    }

}
