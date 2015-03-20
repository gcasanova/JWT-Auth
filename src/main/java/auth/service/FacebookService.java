package auth.service;

import java.io.IOException;
import java.util.Date;

import org.json.JSONException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import auth.domain.entities.FacebookResponse;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

@Component
public class FacebookService {
	
	@Value("${facebook.app.id}")
	private long facebookAppId;
	
	@Value("${facebook.app.secret}")
	private String facebookAppSecret;

    public boolean isTokenValid(long facebookId, String facebookToken) throws JSONException, UnirestException, JsonParseException, JsonMappingException, IOException {
    	HttpResponse<JsonNode> jsonNode = Unirest.get("https://graph.facebook.com/v2.2/debug_token")
    			  .header("accept", "application/json")
    			  .queryString("input_token", facebookToken)
    			  .queryString("access_token", facebookAppId + "|" + facebookAppSecret)
    			  .asJson();
    	
    	FacebookResponse facebookResponse = new ObjectMapper().readValue(jsonNode.getRawBody(), FacebookResponse.class);
		if (jsonNode.getStatus() == HttpStatus.OK.value()) {
	        if (facebookResponse.isValid() && facebookResponse.getUserId().equals(String.valueOf(facebookId)) && 
	        		facebookResponse.getAppId().equals(String.valueOf(facebookAppId)) && 
	        		new Date(facebookResponse.getExpiresAt().getTime() * 1000).after(new Date())) {
	            return true;
	        }
		}
        return false;
    }
}
