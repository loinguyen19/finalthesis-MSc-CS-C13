package com.nbloi.cqrses.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

@Component
public class DeSerializeObject {

    public Object deserializeEvent(String event) throws JsonProcessingException {
        try{
            return new ObjectMapper().readValue(event, Object.class);
        }catch (Exception e){
            throw new RuntimeException("Failed to deserialize event", e);
        }
    }
}
