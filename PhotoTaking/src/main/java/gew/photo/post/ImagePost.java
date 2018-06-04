package gew.photo.post;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class ImagePost {

    private OkHttpClient httpClient;
    private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
    private static final MediaType MEDIA_TYPE_JPG = MediaType.parse("image/jpg");

    private static final Logger logger = LoggerFactory.getLogger(ImagePost.class);

    public ImagePost() {
        httpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build();
    }

    public JSONObject post(final String path, final String restApi, final String location) {

        File imageFile = new File(path);
        JSONObject responseObj = new JSONObject();
        if (imageFile.exists()) {
            RequestBody fileBody = RequestBody.create(MEDIA_TYPE_PNG, imageFile);
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("image", imageFile.getName(), fileBody)
                    .addFormDataPart("location", location)
                    .build();
            Request request = new Request.Builder()
                    .url(restApi)
                    .post(requestBody)
                    .build();

            try {
                Response response = httpClient.newCall(request).execute();
                if (response.isSuccessful()) {
                    String body = response.body().string();
                    if (body != null) {
                        responseObj = new JSONObject(body);
                    }
                } else {
                    logger.warn(String.valueOf(response.code()));
                }

            } catch (IOException e) {
                logger.error("POST Image to Server Failed: {}", e.getMessage());
            }

        } else {

            logger.error("Image File Does Not Exist...");
        }
        return responseObj;
    }


}
