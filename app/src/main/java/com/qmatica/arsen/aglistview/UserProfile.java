package com.qmatica.arsen.aglistview;

public class UserProfile {
    private int id;
    private String name;
    private String company;
    private String sendTo;
    private String copyTo;
    private String username;
    private String password;
    private int    mode;

    UserProfile() {
        this.id         = 0;
        this.name       = "";
        this.company    = "";
        this.sendTo     = "";
        this.copyTo     = "";
        this.username   = "";
        this.password   = "";
        this.mode       = A.INTERNAL_MODE;
    }
    UserProfile(int id, String name, String company, String sendTo, String copyTo, String username, String password, int mode) {
        this.id         = id;
        this.name       = name;
        this.company    = company;
        this.sendTo     = sendTo;
        this.copyTo     = copyTo;
        this.username   = username;
        this.password   = password;
        this.mode       = mode;
    }

    public int getID()
    {
        return id;
    }
    public String getName()
    {
        return this.name;
    }
    public void setName(String name)
    {
        this.name = name;
    }
    public String getCompany()
    {
        return this.company;
    }
    public void setCompany(String company)
    {
        this.company = company;
    }
    public String getSendTo()
    {
        return this.sendTo;
    }
    public void setSendTo(String sendTo)
    {
        this.sendTo = sendTo;
    }
    public String getCopyTo()
    {
        return this.copyTo;
    }
    public void setCopyTo(String copyTo)
    {
        this.copyTo = copyTo;
    }
    public String getUsername()
    {
        return this.username;
    }
    public void setUsername(String username)
    {
        this.username = username;
    }
    public String getPassword()
    {
        return this.password;
    }
    public void setPassword(String password)
    {
        this.password = password;
    }
    public int getMode()
    {
        return this.mode;
    }
    public void setMode(int mode)
    {
        this.mode = mode;
    }
}
