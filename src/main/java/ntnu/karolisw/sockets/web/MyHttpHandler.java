package ntnu.karolisw.sockets.web;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.logging.Logger;
import java.util.regex.PatternSyntaxException;

import org.apache.commons.text.StringEscapeUtils;


/**
 * Due to interface HttpHandler, the method handle must be overridden
 */
public class MyHttpHandler implements HttpHandler {
    private final Logger logger = Logger.getLogger(String.valueOf(WebServer.class));
    Boolean error = false;

    /**
     * @param exchange encapsulates an HTTP request received and a response to be generated in one exchange.
     *                 exchange does this through method representing the lifecycle of a http request
     * @throws IOException if exchange is null
     */
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String requestParamValue = null;

        // getRequestMethod() is one (the first) of the lifecycle methods of a http request
        // getRequestMethod() returns the request method of the http header (exchange) it is reading (from the client)
        if("GET".equals(exchange.getRequestMethod())) {
            // handleGetRequest() is a method that does what it sounds like --> method defined below

            browserGetRequest(exchange);

            requestParamValue = handleGetRequest(exchange);
            logger.info("Get request received with request parameter: " + requestParamValue + "\n");
        }
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
        if(!httpExchange.getRequestURI().toString().contains("?")) {
            error = true;
            return "There was an error! ";
        }
         else{
            try{
                return httpExchange.
                        getRequestURI()
                        .toString()
                        .split("\\?")[1];
                // .split("=")[1];
            } catch (PatternSyntaxException e) {
                error = true;
                e.printStackTrace();
                return "Regex error upon rendering of page";
            }

        }

    }

    private void browserGetRequest(HttpExchange exchange){
        System.out.println("Headers: " + exchange.getRequestHeaders());
        System.out.println("BROWSER GET REQUEST");
        System.out.println("GET /" + exchange.getRequestURI().toString() + " HTTP/1.1");
        System.out.println("Host: " + exchange.getLocalAddress());
        System.out.println("User-Agent: Mozilla/5.0 (Windows; U; Windows NT 6.1; en-US; rv:1.9.1.5) Gecko/20091102 Firefox/3.5.5 (.NET CLR 3.5.30729)");
        System.out.println("Accept: text/html,application/xhtml+xml,application/xml;q=0.9,;q=0.8");
        System.out.println("Accept-Language: en-us,en;q=0.5\n");
        System.out.println("Accept-Charset: ISO-8859-1,utf-8;q=0.7,*;q=0.7");
        System.out.println("Keep-Alive: 300\n");
        System.out.println("Cookie: PHPSESSID=r2t5uvjq435r4q7ib3vtdjq120");
        System.out.println("Pragma: no-cache");
        System.out.println("PHPSESSID=r2t5uvjq435r4q7ib3vtdjq120");
        System.out.println("Cache-Control: no-cache\n");
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
        if(!error){
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

            // encode HTML content --> this is where the stringBuilder with HTML is converted to actual text on the webpage
            String htmlResponse = StringEscapeUtils.unescapeHtml4(htmlBuilder.toString());

            // this line is a must
            httpExchange.sendResponseHeaders(200, htmlResponse.length());
            System.out.println("HEADER: \n");
            System.out.println(httpExchange.getProtocol() + "200 OK");
            System.out.println(("Content-Type: text/html; charset=utf-8;"));
            logger.info("Content-length: " + htmlResponse.getBytes().length * 8);

            System.out.println(httpExchange.getProtocol());

            outputStream.write(htmlResponse.getBytes());
            outputStream.flush();
            outputStream.close();
        }
        else {
            htmlBuilder.append("<html>")
                    .append("<body>")
                    .append("<h1>")
                    .append("Dear user. ")

                    .append(requestParamValue)

                    .append("</h1>")
                    .append("<h3>")
                    .append("The correct URI format is http://localhost:80/index.html?''optional_input''")
                    .append("</h3>")
                    .append("</body>")
                    .append("</html>");

            // encode HTML content --> this is where the stringbuilder with HTML is converted to actual text on the webpage
            String htmlResponse = StringEscapeUtils.unescapeHtml4(htmlBuilder.toString());

            // this line is a must
            httpExchange.sendResponseHeaders(200, htmlResponse.length());

            System.out.println("HEADER: \n");
            System.out.println(httpExchange.getProtocol() + "404 ERROR");
            System.out.println(("Content-Type: text/html; charset=utf-8;"));
            System.out.println(LocalDateTime.now());
            logger.info("Content-length: " + htmlResponse.getBytes().length * 8);

            outputStream.write(htmlResponse.getBytes());
            outputStream.flush();
            outputStream.close();
        }
    }
}
