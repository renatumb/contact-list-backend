package org.example.contact_list_backend.constant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Constant {
    public static String PHOTO_DIRECTORY;

    @Value("${AppConfig.pathPhotos}")
    public void setStaticPhotoDirectory(String name){
        Constant.PHOTO_DIRECTORY = name;
    }

}
