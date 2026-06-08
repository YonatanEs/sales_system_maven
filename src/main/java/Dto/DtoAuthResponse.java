package Dto;

import Objects.Usuario_Ob;

public class DtoAuthResponse {
    
    private String token;
    private String id;
    private Usuario_Ob userAuth;

    public DtoAuthResponse(String token, String id, Usuario_Ob userAuth) {
        this.token = token;
        this.id = id;
        this.userAuth = userAuth;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Usuario_Ob getUserAuth() {
        return userAuth;
    }

    public void setUserAuth(Usuario_Ob userAuth) {
        this.userAuth = userAuth;
    }
    
}
