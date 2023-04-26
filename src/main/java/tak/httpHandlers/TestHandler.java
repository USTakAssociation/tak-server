package tak.httpHandlers;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import com.sun.net.httpserver.HttpExchange;

/**
 * This endpoint exists to test for connectivity and returns 
 * "ok;", the HTTP Method, the requested URL and body.
 */
public class TestHandler extends JsonHttpHandler {
	@Override public String POST(HttpExchange t) throws IOException{
		return GET(t);
	}

	@Override public String PUT(HttpExchange t) throws IOException {
		return GET(t);
	}

	@Override public String DELETE(HttpExchange t) throws IOException {
		return GET(t);
	}

	@Override
	public String GET(HttpExchange t) throws IOException {
		String body = new String(t.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
		logger.info("Handling " + t.getRequestMethod() + " " + t.getRequestURI() + " body=" + body);
		return "ok;method="+ t.getRequestMethod() +";url=" + t.getRequestURI() + ";body=" + body;
	}
}
