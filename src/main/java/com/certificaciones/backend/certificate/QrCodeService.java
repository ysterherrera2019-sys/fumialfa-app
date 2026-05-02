package com.certificaciones.backend.certificate;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;

@Service
public class QrCodeService {

    public String generateBase64Qr(String text) {
        try {
            int size = 200;

            QRCodeWriter writer = new QRCodeWriter();
            var matrix = writer.encode(text, BarcodeFormat.QR_CODE, size, size);

            BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);

            for (int x = 0; x < size; x++) {
                for (int y = 0; y < size; y++) {
                    image.setRGB(x, y, matrix.get(x, y) ? 0x000000 : 0xFFFFFF);
                }
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(image, "png", outputStream);

            return "data:image/png;base64," +
                    Base64.getEncoder().encodeToString(outputStream.toByteArray());

        } catch (Exception e) {
            throw new RuntimeException("Error generando código QR", e);
        }
    }
}