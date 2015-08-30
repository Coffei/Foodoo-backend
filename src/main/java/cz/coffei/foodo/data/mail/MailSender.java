package cz.coffei.foodo.data.mail;

import cz.coffei.foodo.data.entities.Order;
import cz.coffei.foodo.data.entities.PriceConstant;
import cz.coffei.foodo.data.enums.OrderStatus;
import cz.coffei.foodo.data.util.Properties;

import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.mail.*;
import javax.mail.internet.*;
import javax.xml.soap.Text;
import java.util.logging.Logger;

/**
 * Created by jtrantin on 29.8.15.
 */
@Stateless
public class MailSender {

    @Inject
    Session emailSession;

    @Inject
    private Logger log;

    @Asynchronous
    public void sendNewOrderEmail(Order order, PriceConstant takeawayConstant) throws MessagingException {
        if(order.getCustomeremail()!=null && order.getCustomeremail().length() > 0) {
            log.info("Sending email for new order " + order.getId());
            String subjectHeader = Properties.getInstance().get("emailSubjectHeader", "[Foodoo]");
            String subject = subjectHeader + " New order created";
            String text = Templates.newOrderTextTemplate(order, takeawayConstant);
            String htmlContent = Templates.newOrderHTMLTemplate(order, takeawayConstant);

            this.sendMail(subject, order.getCustomeremail(), text, htmlContent);
        }
    }

    @Asynchronous
    public void sendStatusChangedEmail(Order order, PriceConstant takeawayConstant, boolean userInitiated) throws MessagingException {
        if(order.getCustomeremail()!=null && order.getCustomeremail().length() > 0) {
            log.info("Sending email for order status change: " + order.getId() + " " + order.getStatus().toString());
            if(order.getStatus()== OrderStatus.CANCELLED) this.sendCancelMail(order, takeawayConstant, userInitiated);
            else if (order.getStatus()==OrderStatus.FINISHED) this.sendFinishedMail(order, takeawayConstant);
        }
    }

    private void sendCancelMail(Order order, PriceConstant takeawayConstant, boolean userInitiated) throws MessagingException {
        String subjectHeader = Properties.getInstance().get("emailSubjectHeader", "[Foodoo]");
        String subject = subjectHeader + " Order #" + order.getId() + " is now cancelled";
        String text = Templates.cancelledOrderTextTemplate(order, takeawayConstant, userInitiated);
        String html = Templates.cancelledOrderHTMLTemplate(order, takeawayConstant, userInitiated);

        this.sendMail(subject, order.getCustomeremail(), text, html);
    }

    private void sendFinishedMail(Order order, PriceConstant takeawayConstant) throws MessagingException {
        String subjectHeader = Properties.getInstance().get("emailSubjectHeader", "[Foodoo]");
        String subject = subjectHeader + " Order #" + order.getId() + " is now finished";
        String text = Templates.finishedOrderTextTemplate(order, takeawayConstant);
        String html = Templates.finishedOrderHTMLTemplate(order, takeawayConstant);

        this.sendMail(subject, order.getCustomeremail(), text, html);
    }



    private void sendMail(String subject, String to, String text, String html) throws MessagingException {
        Message msg = new MimeMessage(emailSession);
        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        msg.setSubject(subject);

        MimeBodyPart textPart = new MimeBodyPart();
        textPart.setContent(text, "text/plain; charset=utf-8");
        MimeBodyPart htmlPart = new MimeBodyPart();
        htmlPart.setContent(html, "text/html; charset=utf-8");
        Multipart multipart = new MimeMultipart("alternative");
        multipart.addBodyPart(textPart);
        multipart.addBodyPart(htmlPart);

        msg.setContent(multipart);
        Transport.send(msg);
    }
}
