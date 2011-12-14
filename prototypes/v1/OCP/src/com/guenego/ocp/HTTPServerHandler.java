package com.guenego.ocp;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import com.guenego.misc.JLG;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class HTTPServerHandler implements HttpHandler {

	private Agent agent;
	private String root;
	private Map<String, String> extMap;

	public HTTPServerHandler(Agent agent) {
		this.agent = agent;
		this.root = agent.p.getProperty("http.htdocs", ".");
		extMap = new HashMap<String, String>();
		extMap.put("txt", "text/plain");
		extMap.put("html", "text/html");
		extMap.put("htm", "text/html");
	}

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		String requestMethod = exchange.getRequestMethod();
		URI uri = exchange.getRequestURI();
		String path = uri.getPath();
		path = adjust(path);
		if (requestMethod.equalsIgnoreCase("GET")) {

			Headers responseHeaders = exchange.getResponseHeaders();
			setContentType(responseHeaders, getExtension(path));

			exchange.sendResponseHeaders(200, 0);

			OutputStream responseBody = exchange.getResponseBody();
			try {
				File file = new File(root + path);
				JLG.debug("asking path: " + path);
				JLG.debug("asking file: " + file.getAbsolutePath());
				responseBody.write(JLG.getBinaryFile(file));
			} catch (Exception e) {
				responseBody.write(("Error...\n" + agent.id).getBytes());
			}
			responseBody.close();
		}
	}

	private String adjust(String path) {
		if (path.endsWith("/")) {
			// it's a directory
			path += "index.html";
		}
		return path;
	}

	private void setContentType(Headers responseHeaders, String ext) {
		String contentType = extMap.get(ext);
		if (contentType == null) {
			contentType = "text/plain";
		}
		responseHeaders.set("Content-Type", contentType);

	}

	private String getExtension(String path) {
		return path.substring(path.lastIndexOf(".") + 1);
	}

}
