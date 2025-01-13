//package edu.example.testingsystem.messaging.rabbit;
//
//import edu.example.testingsystem.entities.TestCase;
//import edu.example.testingsystem.messaging.TestCaseMessagingService;
//import org.springframework.amqp.core.Message;
//import org.springframework.amqp.core.MessageProperties;
//import org.springframework.amqp.rabbit.core.RabbitTemplate;
//import org.springframework.amqp.support.converter.MessageConverter;
//import org.springframework.boot.autoconfigure.template.TemplateAvailabilityProvider;
//import org.springframework.stereotype.Service;
//
//@Service
//public class RabbitTestCaseMessagingService implements TestCaseMessagingService {
//
//    private RabbitTemplate rabbitTemplate;
//
//    RabbitTestCaseMessagingService(RabbitTemplate rabbitTemplate) {
//        this.rabbitTemplate = rabbitTemplate;
//    }
//
//    @Override
//    public void sendTestCase(TestCase testCase) {
//        rabbitTemplate.convertAndSend(testCase, message -> {
//            MessageProperties messageProperties = new MessageProperties();
//            messageProperties.setHeader("X_TESTCASE_SOURCE", "WEB");
//            return message;
//        });
//    }
//}
