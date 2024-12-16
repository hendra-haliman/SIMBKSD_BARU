package id.go.jakarta.bksd.dokumen.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class FileEncryptionService {

    private static final String ALGORITHM = "AES";
    private static final int KEY_SIZE = 128; // Can be 128, 192, or 256 bits

    @Value("${encryption.password}")
    private String password;

    @Value("${encryption.salt}")

    public SecretKey generateKey() throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);
        keyGenerator.init(KEY_SIZE, new SecureRandom());
        return keyGenerator.generateKey();
    }

    // public SecretKey generateKeyFromPassword() throws Exception {
    //     //IvParameterSpec ivParameterSpec = AESUtil.generateIv();

    // }

    public void encryptFile(File inputFile, File outputFile, SecretKey secretKey) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        FileInputStream inputStream = new FileInputStream(inputFile);
        FileOutputStream outputStream = new FileOutputStream(outputFile);
        byte[] buffer = new byte[1024];
        int bytesRead;

        while ((bytesRead = inputStream.read(buffer)) != -1) {
            byte[] output = cipher.update(buffer, 0, bytesRead);
            if (output != null) {
                outputStream.write(output);
            }
        }

        byte[] outputBytes = cipher.doFinal();
        if (outputBytes != null) {
            outputStream.write(outputBytes);
        }

        inputStream.close();
        outputStream.close();
    }

    public void decryptFile(File encryptedFile, File decryptedFile, SecretKey secretKey) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);

        FileInputStream inputStream = new FileInputStream(encryptedFile);
        FileOutputStream outputStream = new FileOutputStream(decryptedFile);
        byte[] buffer = new byte[1024];
        int bytesRead;

        while ((bytesRead = inputStream.read(buffer)) != -1) {
            byte[] output = cipher.update(buffer, 0, bytesRead);
            if (output != null) {
                outputStream.write(output);
            }
        }

        byte[] outputBytes = cipher.doFinal();
        if (outputBytes != null) {
            outputStream.write(outputBytes);
        }

        inputStream.close();
        outputStream.close();
    }

    public void encryptStream(InputStream inputStream, OutputStream outputStream, SecretKey secretKey) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
    
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            byte[] output = cipher.update(buffer, 0, bytesRead);
            if (output != null) {
                outputStream.write(output);
            }
        }
    
        byte[] outputBytes = cipher.doFinal();
        if (outputBytes != null) {
            outputStream.write(outputBytes);
        }
    
        inputStream.close();
        outputStream.close();
    }

    
}
