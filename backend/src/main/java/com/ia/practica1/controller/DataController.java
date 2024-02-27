/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ia.practica1.controller;

import org.springframework.web.bind.annotation.*;
import com.google.cloud.vision.v1.BatchAnnotateImagesResponse;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.SafeSearchAnnotation;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.cloud.vision.v1.AnnotateImageRequest;
import com.google.cloud.vision.v1.EntityAnnotation;
import com.google.cloud.vision.v1.FaceAnnotation;
import com.google.cloud.storage.StorageOptions;
import com.google.cloud.vision.v1.Feature;
import com.google.cloud.vision.v1.Vertex;
import com.google.cloud.vision.v1.Image;
import com.google.protobuf.ByteString;
import com.ia.practica1.model.Sensitive;
import com.ia.practica1.model.LabelData;
import com.ia.practica1.model.DataList;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.File;
import java.awt.image.BufferedImage;
import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
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

        String path = new File("src/main/java/com/ia/practica1/noble.json").getAbsolutePath();
        com.google.cloud.storage.Storage storage = StorageOptions.newBuilder()
                .setCredentials(ServiceAccountCredentials.fromStream(new FileInputStream(path)))
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
            ImageIO.write(originalImage, "png", baos);
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
}
