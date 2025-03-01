package model;

public record AuthData(String authToken, String username) {
    public String getToken() {
        return new String("The Token");
    }
}
