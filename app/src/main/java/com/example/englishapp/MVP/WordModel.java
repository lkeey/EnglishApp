package com.example.englishapp.MVP;

import java.io.Serializable;

public class WordModel implements Serializable {

    private String id;
    private String textRu;
    private String textEn;

    public WordModel () {}

    public WordModel(String id, String textRu, String textEn) {
        this.id = id;
        this.textRu = textRu;
        this.textEn = textEn;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTextRu() {
        return textRu;
    }

    public void setTextRu(String textRu) {
        this.textRu = textRu;
    }

    public String getTextEn() {
        return textEn;
    }

    public void setTextEn(String textEn) {
        this.textEn = textEn;
    }
}
