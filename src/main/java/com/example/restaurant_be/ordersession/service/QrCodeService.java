package com.example.restaurant_be.ordersession.service;

import java.io.ByteArrayOutputStream;
import java.util.Base64;

import org.springframework.stereotype.Service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

@Service
public class QrCodeService {

    public String generateBase64QrCode(
            String text,
            int width,
            int height
    ) {

        try {

            QRCodeWriter qrCodeWriter =
                    new QRCodeWriter();

            BitMatrix bitMatrix =
                    qrCodeWriter.encode(
                            text,
                            BarcodeFormat.QR_CODE,
                            width,
                            height
                    );

            ByteArrayOutputStream outputStream =
                    new ByteArrayOutputStream();

            MatrixToImageWriter.writeToStream(
                    bitMatrix,
                    "PNG",
                    outputStream
            );

            byte[] pngData =
                    outputStream.toByteArray();

            return Base64.getEncoder()
                    .encodeToString(pngData);

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed generate QR Code",
                    e
            );
        }
    }
}