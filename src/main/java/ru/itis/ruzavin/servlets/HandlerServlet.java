package ru.itis.ruzavin.servlets;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

@WebServlet(urlPatterns = "/handleWeather")
public class HandlerServlet extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		BufferedReader reader = req.getReader();
		StringBuilder buffer = new StringBuilder();

		String line;

		while ((line = reader.readLine()) != null){
			buffer.append(line);
		}

		String data = buffer.toString();

		Map<String, String> jsonMap = null;

		if (!data.isEmpty()){
			ObjectMapper mapper = new ObjectMapper();
			jsonMap = mapper.readValue(data, Map.class);
		}

		URL getUrl = new URL("https://api.openweathermap.org/data/2.5/weather?q=" + jsonMap.get("city") + "&appid=d64cde12deca23990a6e956bf65b16aa");

		HttpURLConnection connection = (HttpURLConnection) getUrl.openConnection();

		connection.setRequestMethod("GET");

		StringBuilder content;
		try(BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()))){
			content = new StringBuilder();
			String input;
			while ((input = bufferedReader.readLine()) != null){
				content.append(input);
			}
		}

		connection.disconnect();

		Map<String, String> json1 = parseJson(content.toString());

		String json = new ObjectMapper().writeValueAsString(json1);

		resp.setContentType("application/json");
		PrintWriter out = resp.getWriter();
		out.print(json);
		out.flush();
	}

	public Map<String, String> parseJson(String json) throws JsonProcessingException {
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
