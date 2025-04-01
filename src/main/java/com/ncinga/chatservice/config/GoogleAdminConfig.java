package com.ncinga.chatservice.config;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.services.admin.directory.Directory;
import com.google.api.services.admin.directory.DirectoryScopes;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;

@Configuration
public class GoogleAdminConfig {

    @Value("${google.service.account.file}")
    private String serviceAccountKeyFile;

    @Value("${google.admin.user}")
    private String adminEmail;

    @Bean
    public Directory googleDirectoryService() throws IOException {
        GoogleCredential credential = GoogleCredential.fromStream(new FileInputStream(serviceAccountKeyFile))
                .createScoped(Collections.singleton(DirectoryScopes.ADMIN_DIRECTORY_USER))
                .createDelegated(adminEmail); // Impersonate admin

        return new Directory.Builder(credential.getTransport(), credential.getJsonFactory(), credential)
                .setApplicationName("Google Account Management")
                .build();
    }
}
