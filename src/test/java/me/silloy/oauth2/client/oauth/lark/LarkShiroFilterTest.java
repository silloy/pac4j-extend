package me.silloy.oauth2.client.oauth.lark;

import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;

class LarkShiroFilterTest {

    @Test
    public void url() throws MalformedURLException {
        String url = "https://x.baidu.com?ac=bc";
        URL u = new URL(url);
        System.out.println(u.getPath());
    }

}