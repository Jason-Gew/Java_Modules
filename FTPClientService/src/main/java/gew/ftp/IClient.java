package gew.ftp;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Basic FTP Client, support basic (small size) file upload and download.
 * Based on Apache Commons-Net FTP Library.
 * @author Jason/GeW
 */
public interface IClient
{
    boolean connect() throws IOException;

    boolean login();

    boolean connectAndLogin();

    void disconnect();

    void enablePassiveMode();

    void enableActiveMode();

    List<String> listDirectory() throws IOException;

    List<String> listDirectory(final String path) throws IOException;

    List<String> listFile() throws IOException;

    List<String> listFile(final String path) throws IOException;

    boolean mkdir(final String dir);

    boolean delete(final String remoteFile);

    boolean upload(final String localFile, final String remoteFile) throws IOException;

    boolean upload(InputStream source, final String remoteFile) throws IOException;

    boolean download(final String localFile, final String remoteFile) throws IOException;
}
