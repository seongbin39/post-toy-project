package store.sbin.postservice.oauth2;

import java.util.Map;

public abstract class OAuth2UserInfo {

    protected Map<String, Object> attributes;
    public OAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }
    public abstract String getId();
    public abstract String getEmail();
    public abstract String getUsername();
    public abstract String getImageUrl();
}
