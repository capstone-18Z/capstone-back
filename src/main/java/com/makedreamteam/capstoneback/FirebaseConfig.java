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

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Configuration
public class FirebaseConfig {


    @Bean
    public FirebaseApp firebaseApp() throws IOException {
        FirebaseApp firebaseApp = null;
        List<FirebaseApp>  firebaseApps= FirebaseApp.getApps();
        if(firebaseApps != null && !firebaseApps.isEmpty()) {
            for(FirebaseApp app : firebaseApps){
                if(app.getName().equals(FirebaseApp.DEFAULT_APP_NAME)) {
                    firebaseApp = app;
                }
            }
        }else{
            InputStream serviceAccount = getClass().getResourceAsStream("/serviceAccountKey.json");

            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setStorageBucket("caps-1edf8.appspot.com")
                    .build();

            firebaseApp=FirebaseApp.initializeApp(options);
        }
        return firebaseApp;
    }

    @Bean
    public Storage firebaseStorage() {
        return StorageOptions.getDefaultInstance().getService();
    }

}