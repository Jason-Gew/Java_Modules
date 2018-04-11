package gew.ftp;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;


/**
 * Implementation of the Abstract Interface...
 * @author Jason/Ge Wu
 */
public class Client implements IClient
{
    private String address;
    private int port;
    private String username;
    private String password;

    private FTPClient ftpClient;
    private boolean passiveMode = true;
    private boolean activeMode = false;
    private boolean removeFailureFile = true;
    private int connectionTimeout = 60000;
    private int dataTimeout = 30000;


    private static final Logger logger = LoggerFactory.getLogger(Client.class);

    public Client(String address, int port) {
        this.address = address;
        this.port = port;
        ftpClient = new FTPClient();
    }

    public Client(String address, int port, String username, String password) {
        this.address = address;
        this.port = port;
        this.username = username;
        this.password = password;
        ftpClient = new FTPClient();
    }

    public String getAddress() { return address; }

    public int getPort() { return port; }

    public void setUsername(String username) { this.username = username; }

    public void setPassword(String password) { this.password = password; }

    public boolean isRemoveFailureFile() { return removeFailureFile; }

    public void setRemoveFailureFile(boolean removeFailureFile) { this.removeFailureFile = removeFailureFile; }

    public int getConnectionTimeout() { return connectionTimeout; }

    public void setConnectionTimeout(int connectionTimeout) { this.connectionTimeout = connectionTimeout; }

    public int getDataTimeout() { return dataTimeout; }

    public void setDataTimeout(int dataTimeout) { this.dataTimeout = dataTimeout; }

    public void useCompressedTransfer() {
        try {
            ftpClient.setFileTransferMode(FTP.COMPRESSED_TRANSFER_MODE);
        } catch (Exception e) {
            logger.error("Set Compressed Transfer Mode Failed: {}", e.getMessage());
        }
    }

    @Override
    public void enablePassiveMode() {
        this.passiveMode = true;
        this.activeMode = false;
    }

    @Override
    public void enableActiveMode() {
        this.passiveMode = false;
        this.activeMode = true;
    }

    @Override
    public boolean connect() throws IOException {
        boolean status = false;
        if (ftpClient != null) {
            ftpClient.setConnectTimeout(connectionTimeout);
            ftpClient.connect(address, port);
            ftpClient.setDataTimeout(dataTimeout);
            int replyCode = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(replyCode)) {
                logger.error("FTP Connect Failed!");
            } else {
                status = true;
                logger.info("FTP Connected!");
                if (passiveMode && !activeMode) {
                    ftpClient.enterLocalPassiveMode();
                } else if (!passiveMode && activeMode) {
                    ftpClient.enterLocalActiveMode();
                }
            }
        }
        return status;
    }

    @Override
    public boolean login() {
        boolean status = false;
        try {
            status = ftpClient.login(username, password);
            if (status) {
                logger.info("FTP Login Success!");
            }
        } catch (IOException e) {
            logger.error("FTP Login Exception: " + e.getMessage());
        }
        if (!status) {
            logger.error("FTP Login Failed!");
        }
        return status;
    }

    @Override
    public boolean connectAndLogin() {
        boolean status = false;
        if (!ftpClient.isConnected()) {
            try {
                status = connect();
                if (status) {
                    status = login();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return status;
    }

    public void reconnect() {
        if (ftpClient == null) {
            logger.error("FTP Client Has Not Been Initialized!");
        } else if (!ftpClient.isConnected()) {
            connectAndLogin();
        }
    }

    @Override
    public void disconnect() {
        if (ftpClient != null) {
            try {
                ftpClient.logout();
                ftpClient.disconnect();
                logger.info("FTP Client Logout & Disconnect.");
            } catch (IOException e) {
                logger.warn("FTP Client Disconnect Failed: " + e.getMessage());
            }
        }
    }

    public List<String> listDirectory() throws IOException {
        return listDirectory("");
    }

    @Override
    public List<String> listDirectory(String path) throws IOException {
        List<String> directories = null;
        if (ftpClient != null && ftpClient.isConnected()) {
            FTPFile[] ftpFiles;
            if (path == null || path.isEmpty()) {
                ftpFiles = ftpClient.listFiles();
                logger.info("Checking Directories in Main Directory");
            } else {
                ftpFiles = ftpClient.listFiles(path);
                logger.info("Checking Directories in {}", path);
            }
            directories =  new ArrayList<>();
            for (FTPFile ftpFile : ftpFiles) {
                if (ftpFile.isDirectory()) {
                    directories.add(ftpFile.getName());
                }
            }
        } else {
            logger.warn("FTP Client Is Not Ready...");
        }
        return directories;
    }

    @Override
    public List<String> listFile() throws IOException {
        return listFile("");
    }

    @Override
    public List<String> listFile(String path) throws IOException {
        List<String> files = null;
        if (ftpClient != null && ftpClient.isConnected()) {
            FTPFile[] ftpFiles;
            if (path == null || path.isEmpty()) {
                ftpFiles = ftpClient.listFiles();
                logger.info("Checking Files in Main Directory");
            } else {
                ftpFiles = ftpClient.listFiles(path);
                logger.info("Checking Files in {}", path);
            }
            files =  new ArrayList<>();
            for (FTPFile ftpFile : ftpFiles) {
                if (ftpFile.isFile()) {
                    files.add(ftpFile.getName());
                }
            }
        } else {
            logger.warn("FTP Client Is Not Ready...");
        }
        return files;
    }

    @Override
    public boolean mkdir(String dir) {
        try {
            return ftpClient.makeDirectory(dir);
        } catch (IOException e) {
            logger.error("FTP Client Make Directory Failed: " + e.getMessage());
            return false;
        } catch (Exception err) {
            logger.error("FTP Client Make Directory Exception: " + err.getMessage());
            return false;
        }
    }

    @Override
    public boolean delete(String remoteFile) {
        boolean status = false;

        try {
            status = ftpClient.deleteFile(remoteFile);
        } catch (IOException err) {
            logger.error("FTP Client Delete Remote File Exception: " + err.getMessage());
        }
        return status;
    }

    @Override
    public boolean upload(String localFile, String remoteFile) throws IOException {
        boolean status = false;
        File local = new File(localFile);
        File remote = new File(remoteFile);
        InputStream source = null;
        reconnect();
        try {
            source = new FileInputStream(local);
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            String remoteParent = remote.getParent();
            if (remoteParent != null && !remoteParent.isEmpty()) {
                mkdir(remoteParent);
            }
            status = ftpClient.storeFile(remoteFile, source);
            logger.info("FTP File [{}] Upload Status: {}", local, status);
        } catch (IOException e) {
            logger.error("FTP File [{}] Upload Failed: {}", local, e.getMessage());
            throw e;
        } catch (Exception err) {
            logger.error("FTP File [{}] Upload Exception: {}", local, err.getMessage());
        } finally {
            if (source != null) {
                try {
                    source.close();
                } catch (IOException e) {
                    logger.warn("FTP Local File InputStream Close Failed: " + e.getMessage());
                }
            }
        }
        return status;
    }

    @Override
    public boolean upload(InputStream source, String remoteFile) throws IOException {
        boolean status = false;
        if (source != null) {
            File remote = new File(remoteFile);
            reconnect();
            String remoteParent = remote.getParent();
            if (remoteParent != null && !remoteParent.isEmpty()) {
                mkdir(remoteParent);
            }
            try {
                status = ftpClient.storeFile(remoteFile, source);
            } catch (IOException e) {
                logger.error("FTP Stream File Upload Failed: {}", e.getMessage());
                throw e;
            } catch (Exception err) {
                logger.error("FTP Stream File Upload Failed: {}", err.getMessage());
            } finally {
                try {
                    source.close();
                } catch (IOException e) {
                    logger.warn("FTP Upload Stream Close Failed: " + e.getMessage());
                }
            }
        }
        return status;
    }

    @Override
    public boolean download(String localFile, String remoteFile) throws IOException {
        boolean status = false;
        File local = new File(localFile);
        OutputStream outputStream = null;
        reconnect();
        try {
            outputStream = new BufferedOutputStream(new FileOutputStream(local));
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            status = ftpClient.retrieveFile(remoteFile, outputStream);
            logger.info("FTP File [{}] Download Status: {}", remoteFile, status);

        } catch (IOException e) {
            logger.error("FTP File [{}] Download Failed: {}", remoteFile, e.getMessage());
            throw e;
        } catch (Exception err) {
            logger.error("FTP File [{}] Download Exception: {}", remoteFile, err.getMessage());
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    logger.warn("FTP Local File OutputStream Close Failed: " + e.getMessage());
                }
            }
            if(removeFailureFile && !status) {
                local.deleteOnExit();
            }
        }
        return status;
    }

    private void showServerReply(FTPClient ftpClient) {
        String[] replies = ftpClient.getReplyStrings();
        if (replies != null && replies.length > 0) {
            for (String reply : replies) {
                System.out.println("SERVER: " + reply);
            }
        }
    }
}
