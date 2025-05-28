package barbatos_rex1.client;

import barbatos_rex1.http.server.handle.ResponseEntity;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Http {
    public static void main(String[] args) throws IOException {
        try {
            // Change this path to test different responses
            URL url = new URL("http://127.0.0.1:8080/");// try also /not-found

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            connection.connect();

            ObjectInputStream ois = new ObjectInputStream(connection.getInputStream());
            ResponseEntity<String> response = (ResponseEntity<String>) ois.readObject();
            System.out.println("Response: "+response.entity());
            connection.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
