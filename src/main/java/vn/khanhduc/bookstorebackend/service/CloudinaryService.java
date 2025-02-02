package vn.khanhduc.bookstorebackend.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "CLOUDINARY-SERVICE")
public class CloudinaryService {

    private final Cloudinary cloudinary;

    @PreAuthorize("isAuthenticated()")
    public String uploadImage(MultipartFile file){
        try{
            var result = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                    "folder", "/upload",
                    "use_filename", true,
                    "unique_filename", true,
                    "resource_type","auto"
            ));
            return  result.get("secure_url").toString();
        } catch (IOException io){
            throw new RuntimeException("Image upload fail");
        }
    }

}
