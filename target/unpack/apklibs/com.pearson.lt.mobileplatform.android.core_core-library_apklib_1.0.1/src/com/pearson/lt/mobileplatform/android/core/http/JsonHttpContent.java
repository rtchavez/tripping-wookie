package com.pearson.lt.mobileplatform.android.core.http;

import com.pearson.lt.mobileplatform.android.shaded.google.api.client.http.HttpContent;
import com.pearson.lt.mobileplatform.android.core.json.JsonUtils;

import java.io.IOException;
import java.io.OutputStream;

public class JsonHttpContent implements HttpContent {

	private String content;

	public JsonHttpContent(String content) {
		this.content = content;
	}

	public JsonHttpContent(Object content) {
		this.content = JsonUtils.toJsonString(content);
	}

	@Override
	public long getLength() throws IOException {
		return content.length();
	}

	@Override
	public String getType() {
		return "application/json";
	}

	@Override
	public void writeTo(OutputStream out) throws IOException {
		out.write(content.getBytes());

	}

	@Override
	public boolean retrySupported() {
		return true;
	}

    public String getContent() {
        return content;
    }

}
