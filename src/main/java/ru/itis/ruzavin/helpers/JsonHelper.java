package ru.itis.ruzavin.helpers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

import java.util.HashMap;
import java.util.Map;

public class JsonHelper {

	@SneakyThrows
	public static Map<String, String> parseJson(String json){
		Map<String, String> map = new HashMap<>();
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode jsonNode = objectMapper.readTree(json);

		int temp = (int) (jsonNode.get("main").get("temp").asDouble() - 273.15);
		int feelsLike = (int) (jsonNode.get("main").get("feels_like").asDouble() - 273.15);
		int tempMin = (int) (jsonNode.get("main").get("temp_min").asDouble() - 273.15);
		int tempMax = (int) (jsonNode.get("main").get("temp_max").asDouble() - 273.15);

		map.put("description", jsonNode.get("weather").get(0).get("description").asText());
		map.put("temp", String.valueOf(temp));
		map.put("feels_like", String.valueOf(feelsLike));
		map.put("temp_min", String.valueOf(tempMin));
		map.put("temp_max", String.valueOf(tempMax));
		map.put("pressure", jsonNode.get("main").get("pressure").asText());
		map.put("humidity", jsonNode.get("main").get("humidity").asText());
		map.put("wind_speed", jsonNode.get("wind").get("speed").asText());
		map.put("name", jsonNode.get("name").asText());

		return map;
	}
}
