package com.qmatica.arsen.aglistview;

import java.util.ArrayList;
import java.util.Locale;

public class SendMailMessage {
    private String subject;
    private String message;

    private String sendTo;
    private String copyTo;
    private String username;
    private String password;

    private UserProfile profile;

    public SendMailMessage(UserProfile profile)
    {
        this.sendTo   = profile.getSendTo();
        this.copyTo   = profile.getCopyTo();
        this.username = profile.getUsername();
        this.password = profile.getPassword();
        this.profile  = profile;
    }

    public void create(long orderNo, ArrayList<Order> orderList) {

        subject = profile.getCompany() + " Order " + String.format(Locale.US, "%05d", orderNo);
        message = "";

        message += "<table style=\"width:100%\">" + "\n";

        for(Order order:orderList) {
            if(order.getType() == A.TYPE_SECTION)
            {
                message += "<tr>" + "<th>" + "</th>" + "</th>" + "</tr>" + "\n";
                message += "<tr>" + "\n";
                message += "<th align=\"left\">" + order.getProductName() + "</th>" + "\n";
                message += "<th align=\"left\">" + " " + "</th>" + "\n";
                message += "</tr>" + "\n";

            }
            else {
                if(order.getQuantity() > 0)
                {
                    message += "<tr>" + "\n";
                    message += "<td align=\"left\">" + order.getProductName() + " " + order.getUnit() + "</td>" + "\n";
                    message += "<td>" + String.valueOf(order.getQuantity()) + "</td>" + "\n";
                    message += "</tr>" + "\n";
                }
            }
        }

        message += "</table>" + "\n";
        message += "<p>Submitted by " + profile.getName() + "</p>" + "\n";

    }

    public String getSendTo()
    {
        return sendTo;
    }
    public String getCopyTo()
    {
        return copyTo;
    }
    public String getSubject()
    {
        return subject;
    }
    public String getMessage()
    {
        return message;
    }
    public String getUsername()
    {
        return username;
    }
    public String getPassword()
    {
        return password;
    }
}
