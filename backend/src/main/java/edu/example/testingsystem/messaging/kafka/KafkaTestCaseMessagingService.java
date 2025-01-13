package edu.example.testingsystem.messaging.kafka;

import edu.example.testingsystem.entities.TestCase;
import edu.example.testingsystem.messaging.TestCaseMessagingService;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaTestCaseMessagingService implements TestCaseMessagingService {

    private KafkaTemplate<String, TestCase> kafkaTemplate;

    public KafkaTestCaseMessagingService(KafkaTemplate<String, TestCase> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void sendTestCase(TestCase testCase) {
        kafkaTemplate.sendDefault(testCase);
    }
}
