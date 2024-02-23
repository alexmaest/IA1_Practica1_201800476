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
import com.ia.practica1.model.Student;
import com.ia.practica1.service.StudentService;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Base64;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@RestController
@RequestMapping("/data")
@CrossOrigin
public class StudentController {

    @Autowired
    private StudentService studentService;

    private static byte[] readImageData(String imagePath) throws IOException {
        File file = new File(imagePath);
        FileInputStream fis = new FileInputStream(file);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = fis.read(buffer)) != -1) {
            bos.write(buffer, 0, bytesRead);
        }
        fis.close();
        return bos.toByteArray();
    }

    @PostMapping("/")
    public List<LabelData> getAllStudents(@RequestBody String jsonBody) throws IOException {
        String imageDataString = jsonBody;
        byte[] imageData = Base64.getDecoder().decode(imageDataString);
        ByteString byteString = ByteString.copyFrom(imageData);

        //String filePath = "C:\\Users\\alexm\\OneDrive\\Documentos\\NetBeansProjects\\project1\\src\\main\\java\\com\\mycompany\\project1\\image_06.jpg";
        //String filePath2 = "C:\\Users\\alexm\\Downloads\\ALEXIS";
        com.google.cloud.storage.Storage storage = StorageOptions.newBuilder()
                .setCredentials(ServiceAccountCredentials.fromStream(new FileInputStream("C:\\Users\\alexm\\OneDrive\\Documentos\\NetBeansProjects\\project1\\src\\main\\java\\com\\mycompany\\project1\\noble-return-414922-694655a1d0f6.json")))
                .build()
                .getService();
        //generateImageWithBoundingBoxes(filePath, filePath2);
        return detectLabels(byteString);
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

    // Clase auxiliar para almacenar las etiquetas
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

    // Clase auxiliar para representar la lista de etiquetas
    static class LabelsList {

        private List<LabelData> labels;

        public LabelsList(List<LabelData> labels) {
            this.labels = labels;
        }

        public List<LabelData> getLabels() {
            return labels;
        }
    }

    public static int detectFaces(ByteString filePath) throws IOException {
        List<AnnotateImageRequest> requests = new ArrayList<>();

        Image img = Image.newBuilder().setContent(filePath).build();
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

    public static void generateImageWithBoundingBoxes(String inputImagePath, String outputImagePath) throws IOException {
        List<AnnotateImageRequest> requests = new ArrayList<>();
        ByteString imgBytes = ByteString.readFrom(new FileInputStream(inputImagePath));
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

            // Load original image
            BufferedImage originalImage = javax.imageio.ImageIO.read(new FileInputStream(inputImagePath));
            Graphics2D g2d = originalImage.createGraphics();
            g2d.setStroke(new BasicStroke(3)); // Width of the bounding box lines

            for (AnnotateImageResponse res : responses) {
                if (res.hasError()) {
                    System.err.println("Error: " + res.getError().getMessage());
                    return;
                }

                for (FaceAnnotation annotation : res.getFaceAnnotationsList()) {
                    // Get bounding box vertices
                    List<Vertex> vertices = annotation.getBoundingPoly().getVerticesList();
                    int[] xPoints = new int[vertices.size()];
                    int[] yPoints = new int[vertices.size()];
                    for (int i = 0; i < vertices.size(); i++) {
                        xPoints[i] = vertices.get(i).getX();
                        yPoints[i] = vertices.get(i).getY();
                    }

                    // Draw bounding box
                    g2d.setColor(Color.GREEN); // Set bounding box color
                    g2d.drawPolygon(xPoints, yPoints, vertices.size());
                }
            }

            // Save the modified image with bounding boxes
            javax.imageio.ImageIO.write(originalImage, "jpg", new java.io.File(outputImagePath + "\\image_alpha.jpg"));
            g2d.dispose();
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }

    public static String detectSafeSearch(String filePath) throws IOException {
        List<AnnotateImageRequest> requests = new ArrayList<>();
        ByteString imgBytes = ByteString.readFrom(new FileInputStream(filePath));
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
                System.out.println(res.getSafeSearchAnnotation());
                return "";
            }
        }
        return null;
    }
}
