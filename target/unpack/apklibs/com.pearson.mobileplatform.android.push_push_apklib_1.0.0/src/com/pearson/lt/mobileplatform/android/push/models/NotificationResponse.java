package com.pearson.lt.mobileplatform.android.push.models;

import java.util.HashMap;
import java.util.Map;

import com.pearson.lt.mobileplatform.android.shaded.fasterxml.jackson.annotation.JsonAnyGetter;
import com.pearson.lt.mobileplatform.android.shaded.fasterxml.jackson.annotation.JsonAnySetter;
import com.pearson.lt.mobileplatform.android.shaded.fasterxml.jackson.annotation.JsonIgnore;
import com.pearson.lt.mobileplatform.android.shaded.fasterxml.jackson.databind.JsonNode;

public class NotificationResponse {
	
	/**
	 * Sample JSON:
		{
		    "ticker": "displayed when the notification is received and shown in the system status bar",
		    "contentTitle": "title of the notification when viewing the notification",
		    "contentText": "text associated with the notification",
		    "number": 6, // the number of events that this notification represents
		    "sound": null, // the uri of the sound to play
		    "sender": "Ryan Chavez", // optional 
		    "when": 0, optional timestamp that is displayed when viewing the notification.
		}
	 */
	
	private long when;
	private String ticker;
	private String contentTitle;
	private String contentText;
	private int number;
	private String sound;
	private String sender;
	
	@JsonIgnore
	private String payload;
	
	protected Map<String, JsonNode> properties = new HashMap<String, JsonNode>();

	public NotificationResponse() {
	}

	/**
	 * @return the when
	 */
	public long getWhen() {
		return when;
	}

	/**
	 * @param when the when to set
	 */
	public void setWhen(long when) {
		this.when = when;
	}

	/**
	 * @return the ticker
	 */
	public String getTicker() {
		return ticker;
	}

	/**
	 * @param ticker the ticker to set
	 */
	public void setTicker(String ticker) {
		this.ticker = ticker;
	}

	/**
	 * @return the contentTitle
	 */
	public String getContentTitle() {
		return contentTitle;
	}

	/**
	 * @param contentTitle the contentTitle to set
	 */
	public void setContentTitle(String contentTitle) {
		this.contentTitle = contentTitle;
	}

	/**
	 * @return the contentText
	 */
	public String getContentText() {
		return contentText;
	}

	/**
	 * @param contentText the contentText to set
	 */
	public void setContentText(String contentText) {
		this.contentText = contentText;
	}

	/**
	 * @return the number
	 */
	public int getNumber() {
		return number;
	}

	/**
	 * @param number the number to set
	 */
	public void setNumber(int number) {
		this.number = number;
	}

	/**
	 * @return the sound
	 */
	public String getSound() {
		return sound;
	}

	/**
	 * @param sound the sound to set
	 */
	public void setSound(String sound) {
		this.sound = sound;
	}

	/**
	 * @return the sender
	 */
	public String getSender() {
		return sender;
	}

	/**
	 * @param sender the sender to set
	 */
	public void setSender(String sender) {
		this.sender = sender;
	}
	
    @JsonAnyGetter
    public Map<String, JsonNode> getProperties() {
        return properties;
    }

    @JsonAnySetter
    public void setProperty(String name, JsonNode value) {
        if (value == null) {
            properties.remove(name);
        } else {
            properties.put(name, value);
        }
    }

	/**
	 * @return the payload
	 */
    @JsonIgnore
	public String getPayload() {
		return payload;
	}

	/**
	 * @param payload the payload to set
	 */
    @JsonIgnore
	public void setPayload(String payload) {
		this.payload = payload;
	}

}
