package org.ocpteam.component;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.ocpteam.core.Container;
import org.ocpteam.core.IContainer;
import org.ocpteam.misc.JLG;
import org.ocpteam.misc.LOG;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

@SuppressWarnings("restriction")
public class HTTPServerHandler extends Container<IContainer> implements HttpHandler {

	private String root;
	private Map<String, String> extMap;

	@Override
	public void init() {
		this.root = getRoot().getProperty("http.htdocs", ".");
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
				LOG.info("asking path: " + path);
				LOG.info("asking file: " + file.getAbsolutePath());
				responseBody.write(JLG.getBinaryFile(file));
			} catch (Exception e) {
				responseBody.write(("Error...\n").getBytes());
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
