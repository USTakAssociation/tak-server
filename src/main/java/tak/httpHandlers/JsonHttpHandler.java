package tak.httpHandlers;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public abstract class JsonHttpHandler implements HttpHandler {
  final int METHOD_NOT_ALLOWED = 405;

  protected Logger logger = Logger.getLogger(this.getClass().getName());
  protected ObjectMapper jsonMapper = new ObjectMapper()
      .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
      .setVisibility(PropertyAccessor.FIELD, Visibility.DEFAULT);

  protected Object GET(HttpExchange httpExchange) throws Exception {
    throw new UnsupportedOperationException("GET is not supported on this endpoint");
  }

  protected Object POST(HttpExchange httpExchange) throws Exception {
    throw new UnsupportedOperationException("POST is not supported on this endpoint");
  }

  protected Object PUT(HttpExchange httpExchange) throws Exception {
    throw new UnsupportedOperationException("PUT is not supported on this endpoint");
  }

  protected Object DELETE(HttpExchange httpExchange) throws Exception {
    throw new UnsupportedOperationException("DELETE is not supported on this endpoint");
  }

  private Object deriveResult(String httpMethod, HttpExchange httpExchange)
      throws UnsupportedOperationException, Exception {
    switch (httpMethod.toUpperCase()) {
      case "PUT":
        return PUT(httpExchange);
      case "POST":
        return POST(httpExchange);
      case "GET":
        return GET(httpExchange);
      case "DELETE":
        return DELETE(httpExchange);
      default:
        throw new UnsupportedOperationException(String.format("Unsupported HTTP method '%s'", httpMethod));
    }
  }

  @Override
  public final void handle(HttpExchange httpExchange) throws IOException {
    Object result;
    try {
      result = deriveResult(httpExchange.getRequestMethod(), httpExchange);
    } catch (UnsupportedOperationException ex) {
      logger.log(Level.WARNING, "Failed to handle request", ex);
      send(httpExchange, METHOD_NOT_ALLOWED, "Operation not supported: " + ex.getMessage());
      return;
    } catch (Exception ex) {
      logger.log(Level.WARNING, "Failed to handle request", ex);
      send(httpExchange, HttpURLConnection.HTTP_INTERNAL_ERROR, "Failed to handle request: " + ex.getMessage());
      return;
    }

    try {
      String response = jsonMapper.writeValueAsString(result);
      send(httpExchange, HttpURLConnection.HTTP_OK, response);
    } catch (Exception ex) {
      logger.log(Level.WARNING, "Failed serialize the response data", ex);
      send(httpExchange, HttpURLConnection.HTTP_INTERNAL_ERROR, "Failed to serialize response");
    }
  }

  private void send(HttpExchange httpExchange, int httpStatusCode, String response) throws IOException {
    httpExchange.sendResponseHeaders(httpStatusCode, response.length());
    var headers = httpExchange.getResponseHeaders();
    headers.add("content-type", "application/json");

    OutputStream os = httpExchange.getResponseBody();
    os.write(response.getBytes());
    os.close();
  }
}