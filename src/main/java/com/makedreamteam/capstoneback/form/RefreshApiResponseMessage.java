package com.makedreamteam.capstoneback.form;

import java.util.HashMap;
import java.util.Map;

public class RefreshApiResponseMessage {
    private Map<String, String> map=new HashMap<>();
    public RefreshApiResponseMessage(Map<String, String> map) {
        this.map=map;
    }
}
