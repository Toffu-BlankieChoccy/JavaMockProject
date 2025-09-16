//package com.tofftran.mockproject.data.service;
//
//import com.cloudinary.Cloudinary;
//import com.cloudinary.Transformation;
//import com.cloudinary.utils.ObjectUtils;
//import org.springframework.stereotype.Service;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.IOException;
//import java.util.HashMap;
//import java.util.Map;
//
//@Service
//public class CloudinaryService {
//    private final Cloudinary cloudinary;
//
//    public CloudinaryService(Cloudinary cloudinary) {
//        this.cloudinary = cloudinary;
//    }
//
//    public Map uploadFile(MultipartFile file, String folderName) throws IOException {
//        return cloudinary.uploader().upload(file.getBytes(),
//                ObjectUtils.asMap(
//                        "folder", folderName
//                ));
//    }
//
//    public void deleteImage(String publicId) throws IOException{
//        if (publicId != null && !publicId.isEmpty()){
//            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
//        }
//    }
//
//    public String generateThumbnailUrl(String publicId) {
//        return cloudinary.url()
//                .transformation(new Transformation()
//                        .width(200).height(250)
//                        .crop("fill") // Smart crop to maintain aspect ratio
//                        .quality("auto")
//                        .fetchFormat("auto"))
//                .generate(publicId);
//    }
//
//    public Map<String, String> generateResponsiveUrls(String publicId) {
//        Map<String, String> urls = new HashMap<>();
//
//        urls.put("thumbnail", cloudinary.url()
//                        .transformation(new Transformation().width(200).height(250).crop("fill"))
//                        .generate(publicId));
//
//        urls.put("medium", cloudinary.url()
//                .transformation(new Transformation().width(400).height(500).crop("fill"))
//                .generate(publicId));
//
//        urls.put("large", cloudinary.url()
//                .transformation(new Transformation().width(800).height(1000).crop("fill"))
//                .generate(publicId));
//
//        urls.put("original", cloudinary.url()
//                .transformation(new Transformation().quality("auto").fetchFormat("auto"))
//                .generate(publicId));
//
//        return urls;
//    }
//
//
//}
