package gew.zookeeper.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gew.zookeeper.model.NodeInfo;

import java.io.IOException;


public class JSONMapper {


    public static String serialize(Object object) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(object);
    }

    public static NodeInfo deserialize(byte[] data) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(data, NodeInfo.class);
    }

    public static NodeInfo deserialize(String data) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(data, NodeInfo.class);
    }
}
