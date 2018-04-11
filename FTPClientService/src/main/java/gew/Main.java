package gew;

import gew.ftp.Client;

import java.io.IOException;

/**
 * Example
 * @author Jason/GeW
 */
public class Main
{
    private static final String FTP_URL = "localhost";
    private static final int FTP_PORT = 21;
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";

    public static void main(String[] args) {
        ftpUploadExample("files/tracking.jpg", "/picture/tracking.jpg");
        ftpDownloadExample("files/log.txt", "/xyz-log.txt");
    }

    private static void ftpUploadExample(final String localPath, final String remotePath) {
        Client ftp = new Client(FTP_URL, FTP_PORT, USERNAME, PASSWORD);
        try {
            ftp.connectAndLogin();
            boolean status = ftp.upload(localPath, remotePath);
            System.out.println("File Upload -> " + status);
//            ftp.upload("files/pic2.jpg", "pic2.jpg");
//            System.out.println("File Upload -> " + status);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        } finally {
            ftp.disconnect();
        }
    }

    private static void ftpDownloadExample(final String localPath, final String remotePath) {
        Client ftp = new Client(FTP_URL, FTP_PORT, USERNAME, PASSWORD);
        boolean status;
        try {
            ftp.connect();
            ftp.login();
            status = ftp.download(localPath, remotePath);
            System.out.println("File Download -> " + status);

        } catch (IOException e) {
            System.err.println(e.getMessage());
        } finally {
            ftp.disconnect();
        }
    }
}
