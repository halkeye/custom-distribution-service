package com.org.jenkins.custom.jenkins.distribution.service.services;

import com.org.jenkins.custom.jenkins.distribution.service.util.Util;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Logger;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

@Service
public class UpdateCenterService {

    private final transient Util util = new Util();

    private transient final static Logger LOGGER = Logger.getLogger(UpdateCenterService.class.getName());
    private transient static final String UPDATE_CENTER_URL = "https://updates.jenkins.io/current/update-center.actual.json";
    private transient int updateFlag;
    private transient String responseString;
    private transient String updateCenterPath = "";

    public JSONObject downloadUpdateCenterJSON() throws Exception {
        /*
        * Check if updateFlag has been set if not then it is the first time the application
        * is being run so we need to download the update-center
        */
        LOGGER.info("Update flag is " + updateFlag);
        LOGGER.info("Update Center Path " + updateCenterPath);

        if(updateFlag == 0) {
            final File updateCenterFile = File.createTempFile("update-center", ".json");
            updateCenterPath = updateCenterFile.getPath();
            LOGGER.info("Creating a new file" + updateCenterFile.getPath());
            LOGGER.info("Executing Request at " + UPDATE_CENTER_URL);
            try (CloseableHttpClient httpClient = HttpClients.createDefault();
                 CloseableHttpResponse response = httpClient.execute(new HttpGet(UPDATE_CENTER_URL))) {
                responseString = EntityUtils.toString(response.getEntity());}
            final byte[] buf = responseString.getBytes();
            Files.write(updateCenterFile.toPath(), buf);
            updateFlag = 1;


        } else {
            responseString = readFileAsString(updateCenterPath);
        }
        LOGGER.info("Returning Response");
        return util.convertPayloadToJSON(responseString);
    }

    private static String readFileAsString(final String fileName) throws Exception {
        String data;
        data = new String(Files.readAllBytes(Paths.get(fileName)));
        return data;
    }
}
