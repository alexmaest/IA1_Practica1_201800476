/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ia.practica1.controller;

import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.storage.StorageOptions;
import com.google.cloud.vision.v1.AnnotateImageRequest;
import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.BatchAnnotateImagesResponse;
import com.google.cloud.vision.v1.EntityAnnotation;
import com.google.cloud.vision.v1.FaceAnnotation;
import com.google.cloud.vision.v1.Feature;
import com.google.cloud.vision.v1.Image;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.cloud.vision.v1.SafeSearchAnnotation;
import com.google.cloud.vision.v1.Vertex;
import com.google.protobuf.ByteString;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import org.springframework.web.bind.annotation.*;
import java.util.Base64;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import javax.imageio.ImageIO;

@RestController
@RequestMapping("/data")
@CrossOrigin
public class DataController {

    @PostMapping("/")
    public DataList getAllData(@RequestBody String jsonBody) throws IOException {
        String imageDataString = jsonBody;
        byte[] imageData = Base64.getDecoder().decode(imageDataString);
        ByteString byteString = ByteString.copyFrom(imageData);

        com.google.cloud.storage.Storage storage = StorageOptions.newBuilder()
                .setCredentials(ServiceAccountCredentials.fromStream(new FileInputStream("C:\\Users\\alexm\\OneDrive\\Documentos\\NetBeansProjects\\project1\\src\\main\\java\\com\\mycompany\\project1\\noble-return-414922-694655a1d0f6.json")))
                .build()
                .getService();
        
        List<LabelData> labels = detectLabels(byteString);
        int faces_number = detectFaces(byteString);
        String imageDetectedFaces = generateImageWithBoundingBoxes(byteString);
        Sensitive sensitive = detectSafeSearch(byteString);
        DataList data = new DataList(faces_number, imageDetectedFaces, labels, sensitive);

        return data;
    }

    public static List<LabelData> detectLabels(ByteString imgBytes) throws IOException {
        List<AnnotateImageRequest> requests = new ArrayList<>();
        
        Image img = Image.newBuilder().setContent(imgBytes).build();
        Feature feat = Feature.newBuilder().setType(Feature.Type.LABEL_DETECTION).build();
        AnnotateImageRequest request = AnnotateImageRequest.newBuilder()
                .addFeatures(feat)
                .setImage(img)
                .build();
        requests.add(request);
        List<LabelData> labels = new ArrayList<>();
        try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {
            BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
            List<AnnotateImageResponse> responses = response.getResponsesList();

            for (AnnotateImageResponse res : responses) {
                if (res.hasError()) {
                    System.err.println("Error: " + res.getError().getMessage());
                }
                for (EntityAnnotation annotation : res.getLabelAnnotationsList()) {
                    String description = annotation.getDescription();
                    float score = annotation.getScore();
                    labels.add(new LabelData(description, score * 100));
                }
            }
        }catch(Exception e){
            System.out.println(e);
        }
        return labels;
    }

    public static int detectFaces(ByteString imgBytes) throws IOException {
        List<AnnotateImageRequest> requests = new ArrayList<>();

        Image img = Image.newBuilder().setContent(imgBytes).build();
        Feature feat = Feature.newBuilder().setType(Feature.Type.FACE_DETECTION).build();
        AnnotateImageRequest request = AnnotateImageRequest.newBuilder()
                .addFeatures(feat)
                .setImage(img)
                .build();
        requests.add(request);

        int faceCount = 0;
        try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {
            BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
            List<AnnotateImageResponse> responses = response.getResponsesList();

            for (AnnotateImageResponse res : responses) {
                if (res.hasError()) {
                    System.err.println("Error: " + res.getError().getMessage());
                    return -1; // Return -1 to indicate an error
                }

                // Counting the number of faces detected
                for (FaceAnnotation annotation : res.getFaceAnnotationsList()) {
                    faceCount++;
                }
            }
        }
        return faceCount;
    }

    public static String generateImageWithBoundingBoxes(ByteString imgBytes) {
        List<AnnotateImageRequest> requests = new ArrayList<>();
        Image img = Image.newBuilder().setContent(imgBytes).build();
        Feature feat = Feature.newBuilder().setType(Feature.Type.FACE_DETECTION).build();
        AnnotateImageRequest request = AnnotateImageRequest.newBuilder()
                .addFeatures(feat)
                .setImage(img)
                .build();
        requests.add(request);

        try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {
            BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
            List<AnnotateImageResponse> responses = response.getResponsesList();

            BufferedImage originalImage = ImageIO.read(new ByteArrayInputStream(imgBytes.toByteArray()));
            Graphics2D g2d = originalImage.createGraphics();
            g2d.setStroke(new BasicStroke(3));

            for (AnnotateImageResponse res : responses) {
                if (res.hasError()) {
                    System.err.println("Error: " + res.getError().getMessage());
                    return null;
                }

                for (FaceAnnotation annotation : res.getFaceAnnotationsList()) {
                    List<Vertex> vertices = annotation.getBoundingPoly().getVerticesList();
                    int[] xPoints = new int[vertices.size()];
                    int[] yPoints = new int[vertices.size()];
                    for (int i = 0; i < vertices.size(); i++) {
                        xPoints[i] = vertices.get(i).getX();
                        yPoints[i] = vertices.get(i).getY();
                    }

                    g2d.setColor(Color.GREEN);
                    g2d.drawPolygon(xPoints, yPoints, vertices.size());
                }
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(originalImage, "jpg", baos);
            baos.flush();
            byte[] imageInByte = baos.toByteArray();
            baos.close();

            return java.util.Base64.getEncoder().encodeToString(imageInByte);
        } catch (Exception ex) {
            System.out.println(ex);
            return null;
        }
    }

    public static Sensitive detectSafeSearch(ByteString imgBytes) throws IOException {
        List<AnnotateImageRequest> requests = new ArrayList<>();

        Image img = Image.newBuilder().setContent(imgBytes).build();
        Feature feat = Feature.newBuilder().setType(Feature.Type.SAFE_SEARCH_DETECTION).build();
        AnnotateImageRequest request = AnnotateImageRequest.newBuilder()
                .addFeatures(feat)
                .setImage(img)
                .build();
        requests.add(request);

        try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {
            BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
            List<AnnotateImageResponse> responses = response.getResponsesList();
    
            for (AnnotateImageResponse res : responses) {
                if (res.hasError()) {
                    System.err.println("Error: " + res.getError().getMessage());
                    return null;
                }
                SafeSearchAnnotation safeSearchAnnotation = res.getSafeSearchAnnotation();
                Sensitive sensitive = new Sensitive(
                    safeSearchAnnotation.getAdult().name(),
                    safeSearchAnnotation.getSpoof().name(),
                    safeSearchAnnotation.getMedical().name(),
                    safeSearchAnnotation.getViolence().name(),
                    safeSearchAnnotation.getRacy().name()
                );
                return sensitive;
            }
        }
        return null;
    }
    
    static class LabelData {

        private String description;
        private float score;

        public LabelData(String description, float score) {
            this.description = description;
            this.score = score;
        }

        public String getDescription() {
            return description;
        }

        public float getScore() {
            return score;
        }
    }
    
    static class Sensitive {

        private String adult;
        private String spoof;
        private String medical;
        private String violence;
        private String racy;

        public Sensitive(String adult, String spoof, String medical, String violence, String racy) {
            this.adult = adult;
            this.spoof = spoof;
            this.medical = medical;
            this.violence = violence;
            this.racy = racy;
        }

        public String getAdult() {
            return adult;
        }

        public String getSpoof() {
            return spoof;
        }
        
        public String getMedical() {
            return medical;
        }
        
        public String getViolence() {
            return violence;
        }
        
        public String getRacy() {
            return racy;
        }
    }

    static class DataList {

        private int faces = 0;
        private String imageDetectedFaces;
        private List<LabelData> labels;
        private Sensitive sensitive;

        public DataList(int faces, String imageDetectedFaces, List<LabelData> labels, Sensitive sensitive) {
            this.faces = faces;
            this.imageDetectedFaces = imageDetectedFaces;
            this.labels = labels;
            this.sensitive = sensitive;
        }

        public List<LabelData> getLabels() {
            return labels;
        }
        
        public String getImageDetectedFaces() {
            return imageDetectedFaces;
        }
        
        public int getFacesNumber() {
            return faces;
        }
        
        public Sensitive getSensitive() {
            return sensitive;
        }
    }
    
}
