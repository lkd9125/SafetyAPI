package com.safety.law.global.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;

@Configuration
public class FcmConfig {


    @Bean
    public FirebaseMessaging firebaseMessaging () throws IOException{
        ClassPathResource resource = new ClassPathResource("firebase/safe-a112d-firebase-adminsdk-y9uvn-0b8ccb9928.json");

        InputStream refreshToken = resource.getInputStream();

        FirebaseApp firebaseApp = null;
        List<FirebaseApp> firebaseAppList = FirebaseApp.getApps();

        if(firebaseAppList != null && !firebaseAppList.isEmpty()){
            for(FirebaseApp app : firebaseAppList){
                if(app.getName().equals(FirebaseApp.DEFAULT_APP_NAME)){
                    firebaseApp = app;
                }
            }
        } else {
            FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(refreshToken))
                .build();
            
            firebaseApp = FirebaseApp.initializeApp(options);
        }

        return FirebaseMessaging.getInstance(firebaseApp);
    }
}
