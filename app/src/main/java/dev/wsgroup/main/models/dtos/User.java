package dev.wsgroup.main.models.dtos;

import org.json.JSONObject;

import java.io.Serializable;

public class User implements Serializable {

    private String userId, googleId, accountId, username, phoneNumber, password,
            firstName, lastName, mail, avatarLink, walletCode, walletSecret, token;

    private boolean status;

    public User() {
    }

    public User(String phoneNumber, String password) {
        this.phoneNumber = phoneNumber;
        this.password = password;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getGoogleId() {
        return googleId;
    }

    public void setGoogleId(String googleId) {
        this.googleId = googleId;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getDisplayName() {
        String name = "";
        if (firstName != null) {
            name += firstName + " ";
        }
        if (lastName != null) {
            name += lastName;
        }
        return name;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getAvatarLink() {
        return avatarLink;
    }

    public void setAvatarLink(String avatarLink) {
        this.avatarLink = avatarLink;
    }

    public String getWalletCode() {
        return walletCode;
    }

    public void setWalletCode(String walletCode) {
        this.walletCode = walletCode;
    }

    public String getWalletSecret() {
        return walletSecret;
    }

    public void setWalletSecret(String walletSecret) {
        this.walletSecret = walletSecret;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean setStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public static User getAccountFromJSON(JSONObject account, JSONObject profile) throws Exception {
        User user = new User();
        user.setUserId(profile.getString("id"));
        user.setAccountId(account.getString("id"));
        user.setGoogleId(account.getString("googleid"));
        user.setUsername(account.getString("username"));
        user.setFirstName(profile.getString("firstname"));
        user.setLastName(profile.getString("lastname"));
        user.setPhoneNumber(account.getString("phone"));
        user.setMail(profile.getString("email"));
        user.setAvatarLink(profile.getString("avt"));
        user.setStatus(profile.getBoolean("isdeleted"));
        user.setWalletCode(profile.getString("eWalletCode"));
        user.setWalletSecret(profile.getString("eWalletSecret"));
        return user;
    }

    public static User getObjectFromJSON(JSONObject jsonObject) throws Exception {
        User user = new User();
        user.setUserId(jsonObject.getString("id"));
        user.setAccountId(jsonObject.getString("accountid"));
        user.setFirstName(jsonObject.getString("firstname"));
        user.setLastName(jsonObject.getString("lastname"));
        user.setMail(jsonObject.getString("email"));
        user.setAvatarLink(jsonObject.getString("avt"));
        user.setStatus(jsonObject.getBoolean("isdeleted"));
        user.setGoogleId(jsonObject.getString("googleid"));
        user.setUsername(jsonObject.getString("username"));
        user.setPhoneNumber(jsonObject.getString("phone"));
        user.setWalletCode(jsonObject.getString("eWalletCode"));
        user.setWalletSecret(jsonObject.getString("eWalletSecret"));
        return user;
    }
}
