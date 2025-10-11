package com.pcf.recognition;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

import java.net.InetAddress;
import java.net.UnknownHostException;

@SpringBootApplication
@Slf4j
public class RecognitionApplication {

    public static void main(String[] args) throws UnknownHostException {
        SpringApplication app = new SpringApplication(RecognitionApplication.class);
        Environment env = app.run(args).getEnvironment();
        
        String protocol = "http";
        if (env.getProperty("server.ssl.key-store") != null) {
            protocol = "https";
        }
        
        String serverPort = env.getProperty("server.port", "8080");
        String contextPath = env.getProperty("server.servlet.context-path", "");
        String hostAddress = InetAddress.getLocalHost().getHostAddress();
        
        log.info("\n----------------------------------------------------------\n" +
                "🚀 图像识别系统启动成功！\n" +
                "----------------------------------------------------------\n" +
                "📍 应用访问地址:\n" +
                "   本地访问: \t{}://localhost:{}{}\n" +
                "   网络访问: \t{}://{}:{}{}\n" +
                "📋 配置信息:\n" +
                "   环境配置: \t{}\n" +
                "   应用名称: \t{}\n" +
                "----------------------------------------------------------",
                protocol, serverPort, contextPath,
                protocol, hostAddress, serverPort, contextPath,
                env.getActiveProfiles().length == 0 ? "default" : String.join(", ", env.getActiveProfiles()),
                env.getProperty("spring.application.name", "image-recognition-system"));
    }
}
