package ntnu.karolisw.sockets.web;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.logging.Logger;

import com.sun.net.httpserver.HttpServer;
import org.apache.commons.text.StringEscapeUtils;


/**
 * Due to interface HttpHandler, the method handle must be overridden
 */
public class MyHttpHandler implements HttpHandler {
    private final Logger logger = Logger.getLogger(String.valueOf(WebServer.class));

    /**
     * @param exchange encapsulates an HTTP request received and a response to be generated in one exchange.
     *                 exchange does this through method representing the lifecycle of a http request
     * @throws IOException if exchange is null
     */
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String requestParamValue=null;

        // getRequestMethod() is one (the first) of the lifecycle methods of a http request
        // getRequestMethod() returns the request method of the http header (exchange) it is reading (from the client)
        if("GET".equals(exchange.getRequestMethod())) {
            // handleGetRequest() is a method that does what it sounds like --> method defined below
            requestParamValue = handleGetRequest(exchange);
            logger.info("Get request received with request parameter: " + requestParamValue + "\n");
        }
        //todo implement exception handling
        handleResponse(exchange,requestParamValue);
        // Once the delivery is complete, we close the server...
        logger.info("Closing server");
        WebServer.closeServer();
    }

    /**
     * Method handles get - requests
     * Method "getRequestURI()" is an HttpExchange-class method that extracts
             * -the request parameter value contained in the URI
     * URI = Uniform Resource Identifiers --> ex: www.ntnu.no/dataingeniør
     *
     * The regex looks like this due to the syntax i have set up for the URI
     * @param httpExchange
     * @return
     */
    private String handleGetRequest(HttpExchange httpExchange) {
        return httpExchange.
        getRequestURI()
                .toString()
                .split("\\?")[1];
               // .split("=")[1];
    }

    /**
     * This method handles sending the response back to the client (the browser)
     * We get the output stream by calling method getResponseBody()
     * Later, we use this output stream to write HTML content back to the client
     * @param httpExchange
     * @param requestParamValue
     * @throws IOException
     */
    private void handleResponse(HttpExchange httpExchange, String requestParamValue)  throws  IOException {
        OutputStream outputStream = httpExchange.getResponseBody();
        StringBuilder htmlBuilder = new StringBuilder();
        /**
        htmlBuilder.append("HTTP/1.0 200 OK\n" + "\n")
                .append("Content-Type: text/html; charset=utf-8); \n")
         */
        htmlBuilder.append("<html>")
                .append("<body>")
                .append("<h1>")
                .append("Hello ")

                .append(requestParamValue)

                .append("</h1>")
                .append("<h3>")
                .append("This is a test page that could have been a little cooler...")
                .append("</h3>")
                .append("</body>")
                .append("</html>");

        // encode HTML content --> this is where the stringbuilder with HTML is converted to actual text on the webpage
        String htmlResponse = StringEscapeUtils.unescapeHtml4(htmlBuilder.toString());

        // this line is a must
        httpExchange.sendResponseHeaders(200, htmlResponse.length());
        System.out.println("testing\n");
        System.out.println(httpExchange.getProtocol());

        outputStream.write(htmlResponse.getBytes());
        outputStream.flush();
        outputStream.close();
    }
}
