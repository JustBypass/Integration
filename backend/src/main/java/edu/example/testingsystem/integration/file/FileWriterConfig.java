package edu.example.testingsystem.integration.file;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.annotation.Transformer;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.GenericTransformer;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlowBuilder;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.file.FileWritingMessageHandler;
import org.springframework.integration.file.dsl.Files;
import org.springframework.integration.file.support.FileExistsMode;
import org.springframework.messaging.MessageChannel;

import java.io.File;

@Configuration
public class FileWriterConfig {

    @Bean
    @Transformer(inputChannel = "textInChannel", outputChannel = "fileWriterChannel")
    public GenericTransformer<String, String> upperCaseTransformer() {
        return String::toUpperCase;
    }

    @Bean
    @ServiceActivator(inputChannel = "fileWriterChannel")
    public FileWritingMessageHandler fileWriter(){
        FileWritingMessageHandler handler =
                new FileWritingMessageHandler(new File("backend/src/main/stat"));
        handler.setExpectReply(false);
        handler.setFileExistsMode(FileExistsMode.REPLACE_IF_MODIFIED);
        handler.setAppendNewLine(true);
        return handler;
    }

//    @Bean
//    public IntegrationFlow fileWriterFlow() {
//        return IntegrationFlow
//                .from(MessageChannels.direct("textInChannel"))
//                .<String, String>transform(String::toUpperCase)
//                .handle(Files
//                        .outboundAdapter(new File("/tmp/integration/files"))
//                        .fileExistsMode(FileExistsMode.APPEND)
//                        .appendNewLine(true))
//                .get();
//    }
}
