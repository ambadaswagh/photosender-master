package com.cwidanage.photoftp.services;

import org.apache.commons.io.FileUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * @author Chathura Widanage
 */
@Service
@ConfigurationProperties(prefix = "photoftp.thumb")
public class ThumbnailService {
    private String source;
    private String destination;

    private File sourceFolder;
    private File thumbsFolder;

    @PostConstruct
    public void init() throws IOException {
        this.sourceFolder = new File(source);
        this.thumbsFolder = new File(destination);
        FileUtils.forceMkdir(this.sourceFolder);
        FileUtils.forceMkdir(this.thumbsFolder);
    }

    public byte[] getThumbnail(String fileName) throws IOException {
        File thumbFile = new File(this.thumbsFolder, fileName);
        if (!thumbFile.exists()) {
            File sourceFile = new File(this.sourceFolder, fileName);
            BufferedImage bufferedImage = ImageIO.read(sourceFile);
            int width = 200;
            int height = bufferedImage.getHeight() * width / bufferedImage.getWidth();
            Image scaledInstance = bufferedImage.getScaledInstance(width, height, BufferedImage.SCALE_FAST);
            BufferedImage scaledImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            scaledImage.createGraphics().drawImage(scaledInstance, 0, 0, null);
            ImageIO.write(scaledImage, "JPG", thumbFile);
            return FileUtils.readFileToByteArray(sourceFile);
        }
        return FileUtils.readFileToByteArray(thumbFile);
    }


    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }
}
