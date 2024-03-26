package org.example.userservice3.Clients;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaProducerClient {
    private KafkaTemplate<String,String> kafkaTemplate;

    //Message is a Json String of whatever data you want to send
    public KafkaProducerClient(KafkaTemplate<String,String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    //Are you sending message to kafka or sending message to Topic within Kafka ?
    public void sendMessage(String topic, String message) {
        kafkaTemplate.send(topic,message);
    }
}

//message will appear like this - {
//         "id" : 1,
//        "name" : "anurag"
//}
