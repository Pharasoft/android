package com.chochon.chochonandroid.DBObjects;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

public class BusinessInfo {
	private String _id;
	private String name;
	private String email;
	private String address;
	private String phone;
	private String website;
	private Bitmap image ;
	private Drawable imageDrawabe;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public Bitmap getImage() {
		return image;
	}
	public void setImage(Bitmap image) {
		this.image = image;
	}
	public Drawable getImageDrawabe() {
		return imageDrawabe;
	}
	public void setImageDrawabe(Drawable imageDrawabe) {
		this.imageDrawabe = imageDrawabe;
	}
	public String getWebsite() {
		return website;
	}
	public void setWebsite(String website) {
		this.website = website;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String get_id() {
		return _id;
	}
	public void set_id(String _id) {
		this._id = _id;
	}
}
