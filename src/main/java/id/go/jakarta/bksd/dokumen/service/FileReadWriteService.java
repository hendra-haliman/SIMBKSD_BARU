package id.go.jakarta.bksd.dokumen.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.ChannelSftp;

import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class FileReadWriteService {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final ResourceLoader resourceLoader;

    @Value("${upload.folder}")
    private String folderName;

    public FileReadWriteService(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public String readTextFile(String filePath) throws IOException {
        // logger.info("*** readFile filePath=[{}]", filePath);

        Resource resource = resourceLoader.getResource("file:" + filePath);
        byte[] fileBytes = resource.getInputStream().readAllBytes();
        return new String(fileBytes, StandardCharsets.UTF_8);
    }

    public String readTextFileFromFolder(String fileName) {
        String filePath = folderName + "/" + fileName;
        String content = "";

        try {
            content = readTextFile(filePath);
            logger.info("==== Start of file content [{}] === ", fileName);
            logger.info(content);
            logger.info("=== End of file content ===");

        } catch (IOException ioe) {
            logger.error("**ERROR READING FILE** {} [{}]", fileName, ioe.getMessage());
            ioe.printStackTrace();
        }
        return content;
    }

    public Resource loadFileAsResource(String fileName) throws IOException {
        Path filePath = Paths.get(folderName + "/" + fileName);
        logger.debug("Filepath *** [{}]", filePath);
        Resource resource = new UrlResource(filePath.toUri());
        if (resource.exists()) {
            return resource;
        } else {
            throw new IOException("File not found [" + fileName + "]");
        }
    }

    /**
     * Save the given file to upload folder (see application.properties)
     * 
     * @param file
     * @return filenameHash: a hashed filename
     * @throws IOException
     */
    public String saveFile(MultipartFile file) throws IOException {
        String fileNameHash = "";
        try {
            // Path filePath = Paths.get(folderName + "/" + file.getOriginalFilename());
            fileNameHash = generateRandomHash(file.getOriginalFilename());
            Path filePath = Paths.get(folderName + "/" + fileNameHash);
            Files.copy(file.getInputStream(), filePath);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return fileNameHash;
    }

    public void saveFileNoHash(MultipartFile file) throws IOException {
        Path filePath = Paths.get(folderName + "/" + file.getOriginalFilename());
        try {
            Files.copy(file.getInputStream(), filePath);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

    }

    /**
     * Transfer file using SCP
     * 
     * @param username
     * @param password
     * @param host
     * @param port
     * @param localFile
     * @param remoteDir
     */
    public void scpTransferFile(String username, 
                                String password, 
                                String host, 
                                int port, 
                                String localFile,
                                String remoteDir) {
        Session session = null;
        Channel channel = null;

        try {
            JSch jsch = new JSch();
            session = jsch.getSession(username, host, port);
            session.setConfig("StrictHostKeyChecking", "no");
            session.setPassword(password);
            session.connect();

            // Command for SCP to remote server
            String command = "scp -t " + remoteDir;
            channel = session.openChannel("exec");
            ((ChannelExec) channel).setCommand(command);

            // Get I/O streams for remote scp
            try (OutputStream out = channel.getOutputStream();
                    InputStream in = channel.getInputStream()) {

                channel.connect();

                if (checkAck(in) != 0) {
                    throw new RuntimeException("SCP to remote server failed.");
                }

                File file = new File(localFile);
                long filesize = file.length();
                command = "C0644 " + filesize + " " + file.getName() + "\n";
                out.write(command.getBytes());
                out.flush();

                if (checkAck(in) != 0) {
                    throw new RuntimeException("SCP to remote server failed.");
                }

                byte[] buf = new byte[1024];
                // Send the content of the file
                try (FileInputStream fis = new FileInputStream(file)) {
                    // byte[] buf = new byte[1024];
                    while (true) {
                        int len = fis.read(buf, 0, buf.length);
                        if (len <= 0)
                            break;
                        out.write(buf, 0, len);
                    }
                    out.flush();
                }

                // Send '\0'
                buf[0] = 0;
                out.write(buf, 0, 1);
                out.flush();
                if (checkAck(in) != 0) {
                    throw new RuntimeException("SCP to remote server failed.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (channel != null)
                channel.disconnect();
            if (session != null)
                session.disconnect();
        }
    }

    /**
     * Copy file from remote to local
     * 
     * @param user
     * @param password
     * @param host
     * @param port
     * @param remoteFile
     * @param localFile
     */
    public void sftpRemoteToLocal(  String user, 
                                    String password, 
                                    String host, 
                                    int port, 
                                    String remoteFile,
                                    String localFile) {
        JSch jsch = new JSch();
        try {
            Session session = jsch.getSession(user, host, port);
            session.setConfig("StrictHostKeyChecking", "no");
            session.setPassword(password);
            session.connect();

            Channel channel = session.openChannel("sftp");
            channel.connect();

            ChannelSftp sftpChannel = (ChannelSftp) channel;
            sftpChannel.get(remoteFile, localFile);

            sftpChannel.exit();
            session.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static int checkAck(InputStream in) throws IOException {
        int b = in.read();
        if (b == 0)
            return b;
        if (b == -1 || b == 1 || b == 2) {
            StringBuilder sb = new StringBuilder();
            int c;
            do {
                c = in.read();
                sb.append((char) c);
            } while (c != '\n');
            if (b == 1 || b == 2) {
                throw new IOException(sb.toString());
            }
        }
        return b;
    }

    public void deleteFile(String filename) {
        try {
            Path filePath = Paths.get(folderName + "/" + filename);
            Files.delete(filePath);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

    }

    private String generateRandomHash(String input) {
        String hash = "";
        try {
            int seed = 1234592342; // The seed value

            Random random = new Random();
            // random.setSeed(seed);

            int randomNumber = random.nextInt();
            int randomNumber2 = random.nextInt();

            input = input + String.valueOf(randomNumber) + String.valueOf(randomNumber2);

            // Create an instance of the MessageDigest with the desired algorithm (e.g.,
            // "SHA-256")
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            // Convert the input string to bytes
            byte[] inputBytes = input.getBytes();

            // Update the digest with the input bytes
            digest.update(inputBytes);

            // Generate the hash as a byte array
            byte[] hashBytes = digest.digest();

            // Convert the hash bytes to a hexadecimal string
            StringBuilder hashBuilder = new StringBuilder();
            for (byte b : hashBytes) {
                hashBuilder.append(String.format("%02x", b));
            }
            hash = hashBuilder.toString();

            // Print the hash
            // System.out.println("Hash: " + hash);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return hash;
    }
}