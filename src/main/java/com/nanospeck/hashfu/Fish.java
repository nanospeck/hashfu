package com.nanospeck.hashfu;

import org.bson.Document;

public class Fish {
    
    private String id;
    private String text;
 
    

    public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Document getFishAsDocument() {
        Document fishDocument = new Document("id_str", getId())
                .append("name", getText());
        return fishDocument;
    }

	@Override
	public String toString() {
		return "Fish [id=" + id + ", text=" + text + "]";
	};
 
}