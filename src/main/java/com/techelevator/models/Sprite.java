package com.techelevator.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Sprite {
    @JsonProperty("back_default")
    private String backDefault;
    @JsonProperty("front_default")
    private String frontDefault;

    public String getBackDefault() {
        return backDefault;
    }

    public void setBackDefault(String backDefault) {
        this.backDefault = backDefault;
    }

    public String getFrontDefault() {
        return frontDefault;
    }

    public void setFrontDefault(String frontDefault) {
        this.frontDefault = frontDefault;
    }

    @Override
    public String toString() {
        return "  -  Image Urls: \n\t" +
                "backDefault= '" + backDefault + '\'' +
                ",\n\tfrontDefault='" + frontDefault + '\'' +
                '}';
    }
}
