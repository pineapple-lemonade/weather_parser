package ru.itis.ruzavin.servlets;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.itis.ruzavin.helpers.JsonHelper;

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

		Map<String, String> json1 = JsonHelper.parseJson(content.toString());

		String json = new ObjectMapper().writeValueAsString(json1);

		resp.setContentType("application/json");
		PrintWriter out = resp.getWriter();
		out.print(json);
		out.flush();
	}
}
