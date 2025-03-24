package client;
import model.AuthData;
import com.google.gson.Gson;


import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class ServerFacade {

    private final String serverUrl;
    private final Gson gson = new Gson();

    public ServerFacade(int port) {
        this.serverUrl = "http://localhost:" + port;
    }

    public AuthData register(String username, String password, String email) throws Exception {

}
