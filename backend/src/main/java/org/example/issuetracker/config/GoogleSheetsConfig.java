package org.example.issuetracker.config;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.gax.core.CredentialsProvider;
import com.google.api.services.sheets.v4.Sheets;
import com.google.auth.Credentials;
import com.google.auth.http.HttpCredentialsAdapter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.security.GeneralSecurityException;

@Configuration
public class GoogleSheetsConfig {

    @Value("${spring.cloud.gcp.project-id}")
    private String applicationName;

    @Bean
    public Sheets sheetsService(CredentialsProvider credentialsProvider) throws IOException, GeneralSecurityException {
        Credentials credentials = credentialsProvider.getCredentials();

        return new Sheets.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                GsonFactory.getDefaultInstance(),
                new HttpCredentialsAdapter(credentials))
                .setApplicationName(applicationName)
                .build();
    }

}
