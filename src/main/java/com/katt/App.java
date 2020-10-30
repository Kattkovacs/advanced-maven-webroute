package com.katt;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;


public class App {
    private static final Map<String, Method> routesHashMap = new HashMap<>();
    private static final Routes routes = new Routes();

    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        for (Method m : Routes.class.getMethods()) {
            if (m.isAnnotationPresent(WebRoute.class)) {
                WebRoute annotation = m.getAnnotation(WebRoute.class);
                m.setAccessible(true);
                String path = annotation.path();
                routesHashMap.put(path, m);
                server.createContext(path, new MyHandler());
            }
        }
        server.setExecutor(null);
        server.start();
    }

    static class MyHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            String response = "response";
            try {
                Method method = routesHashMap.get(t.getHttpContext().getPath());
                if (method != null) {
                    method.setAccessible(true);
                    Object methodObject = method.invoke(routes);
                    response = methodObject.toString();
                }
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
}
