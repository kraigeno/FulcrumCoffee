package com.qmatica.arsen.aglistview;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Properties;

import javax.mail.AuthenticationFailedException;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

//Class is extending AsyncTask because this class is going to perform a networking operation
public class SendMail extends AsyncTask<Void,Void,Void> {

    private Context context;

    private DBManager dbManager;
    private long orderNo;
    private ArrayList<Order> orderList;
    private ArrayList<Order> cleanList;
    private OrderAdapter adapter;

    private SendMailMessage smm;

    private String username;
    private String password;
    private String sendTo;
    private String copyTo;
    private String subject;
    private String message;

    private int    error;
    private String eMessage;

    private ProgressDialog progressDialog;

    public SendMail(Context context, SendMailMessage smm) {
        this.context    = context;
        this.smm        = smm;
        this.error      = A.NO_ERROR;
        this.eMessage   = A.SUCCESS_MESSAGE;
    }

    public void setForRollBack(DBManager dbManager, OrderAdapter adapter, ArrayList<Order> orderList, ArrayList<Order> cleanList) {
        this.dbManager  = dbManager;
        this.adapter    = adapter;
        this.orderList  = orderList;
        this.cleanList  = cleanList;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        username = smm.getUsername();
        password = smm.getPassword();
        sendTo   = smm.getSendTo();
        copyTo   = smm.getCopyTo();
        subject  = smm.getSubject();
        message  = smm.getMessage();
        progressDialog = ProgressDialog.show(context,"Sending order","Please wait...",false,false);     // TODO strings
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        progressDialog.dismiss();
        if(error == A.NO_ERROR)
        {
            Toast.makeText(context, eMessage, Toast.LENGTH_LONG).show();
            saveOrder();
        }
        else
        {
            rollback();
        }
    }

    @Override
    protected Void doInBackground(Void... params) {

        Properties props = new Properties();

        // Configuring properties for gmail
        // If you are not using gmail you may need to change the values
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");

        //Creating a new session
        Session session = Session.getDefaultInstance(props,
                new javax.mail.Authenticator() {
                    //Authenticating the password
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });
        try {
            MimeMessage mm = new MimeMessage(session);

            mm.setFrom(new InternetAddress(username));
            mm.addRecipient(Message.RecipientType.TO, new InternetAddress(sendTo));
            mm.addRecipient(Message.RecipientType.CC, new InternetAddress(copyTo));
            mm.setSubject(subject);

//          mm.setText(message);                                    // TODO select plain vs html
            mm.setContent(message, "text/html; charset=utf-8");
            //Sending email
            Transport.send(mm);

        } catch (AddressException e) {

            this.error    = A.ADDRESS_ERROR;
            this.eMessage = A.ADDRESS_ERROR_MESSAGE + (sendTo.isEmpty() ? "email address is empty" : sendTo);

        } catch (AuthenticationFailedException e) {

            this.error    = A.AUTH_ERROR;
            this.eMessage = A.AUTH_ERROR_MESSAGE;

        } catch (SendFailedException e) {

            this.error    = A.SEND_ERROR;
            this.eMessage = A.SEND_ERROR_MESSAGE;
//            this.eMessage = e.getMessage() + "\n\n" + e.toString();
//            e.printStackTrace();

        } catch (MessagingException e) {

            this.error    = A.MESSAGE_ERROR;
            this.eMessage = e.getMessage() + "\n\n" + e.toString();
//            e.printStackTrace();

        } catch (Exception e) {

            this.error    = A.UNKNOWN_ERROR;
            this.eMessage = e.getMessage() + "\n\n" + e.toString();
//            e.printStackTrace();
        }
        return null;
    }

    private void saveOrder() {
        rollQuantity(orderList);                  // adapter is attached to orderList
        adapter.notifyDataSetChanged();
        dbManager.deleteAllOrders();
        dbManager.insertAllOrders(cleanList);
    }


    private void rollQuantity(ArrayList<Order> list) {
        for(Order order : list) {
            order.roll();
            order.setID(-1);         // TODO not 0 to change color
        }
    }

    private void rollback() {
        dbManager.deleteHistoryAndDetail(orderNo);      // rollback history table
        AlertDialog.Builder alertError;
        alertError = new AlertDialog.Builder(context);
        alertError.setTitle("Error sending mail: correct and resubmit the order");      // TODO strings
        alertError.setMessage(this.eMessage);
        alertError.setPositiveButton("CLOSE",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // do nothing
                    }
                });

        alertError.show();
    }
}




