package com.makedreamteam.capstoneback;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {
    @Value("${app.firebase-configuration-file}")
    private String keyPath;
    @Value("${app.firebase-bucket}")
    private String bucketPath;

    @Bean
    public FirebaseApp firebaseApp() throws IOException {
        InputStream serviceAccount = getClass().getResourceAsStream("/serviceAccountKey.json");

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setStorageBucket("caps-1edf8.appspot.com")
                .build();

        return FirebaseApp.initializeApp(options);
    }

    @Bean
    public Storage firebaseStorage() {
        return StorageOptions.getDefaultInstance().getService();
    }

}