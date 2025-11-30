package com.dieti.dietiestatesbackend.service.emails;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.dieti.dietiestatesbackend.dto.response.AgentVisitDTO;
import com.dieti.dietiestatesbackend.entities.Offer;
import com.dieti.dietiestatesbackend.entities.User;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;

@Service
public class EmailService {

    @Value("${SENDGRID_API_KEY}")
    private String sendGridApiKey;
    private static final String BOTTOM = "\n\nCordiali saluti,\nIl team di Dieti Estates";
    private static final String CIAO = "Ciao ";
    private static final String VISITOR = "\n- Visitatore: ";

    private void sendEmail(String to, String subject, String contentText) {
        Email from = new Email("25dietiestates@gmail.com");
        Email toEmail = new Email(to);
        Content content = new Content("text/plain", contentText);
        Mail mail = new Mail(from, subject, toEmail, content);

        SendGrid sg = new SendGrid(sendGridApiKey);
        Request request = new Request();

        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");
        try {
            request.setBody(mail.build());
            sg.api(request);
        } catch (IOException ex) {
            // fail silently, email sending errors should not block main flow
            System.err.println("Errore durante l'invio dell'email: " + ex.getMessage());
        }
    }


    public void welcomeMessage(User user) throws IOException {
        sendEmail(user.getEmail(), "Benvenuto in Dieti Estates", CIAO + user.getFirstName() + ",\n\nBenvenuto in Dieti Estates! Siamo felici di averti con noi." + BOTTOM);
    }

    public void sendOfferCreatedEmail(Offer offer) throws IOException {
        final String subject = "Nuova offerta creata per la proprietà";
        if (offer.getUser() == null) {
            return;
        }
        String contentText = "Ciao,\n\nÈ stata creata una nuova offerta per la tua proprietà in \"" + offer.getProperty().getAddress() + "\".\n\nDettagli dell'offerta:\n- Importo: " + offer.getPrice() + "\n- Proposto da: " + offer.getUser().getUsername() + BOTTOM;
        sendEmail(offer.getProperty().getAgent().getEmail(), subject, contentText);
    }

    public void sendOfferWithdrawnEmail(Offer offer) throws IOException {
        final String subject = "Offerta ritirata per la proprietà";
        if (offer.getUser() == null) {
            return;
        }
        String contentText = "Ciao,\n\nL'offerta per la tua proprietà in \"" + offer.getProperty().getAddress() + "\" è stata ritirata.\n\nDettagli dell'offerta ritirata:\n- Importo: " + offer.getPrice() + "\n- Proposto da: " + offer.getUser().getUsername() + BOTTOM;
        sendEmail(offer.getProperty().getAgent().getEmail(), subject, contentText);
    }

    public void sendOfferAcceptedEmail(Offer offer) throws IOException {
        final String subject = "Offerta accettata per la proprietà";
        if (offer.getUser() == null) {
            return;
        }
        String contentText = CIAO + offer.getUser().getFirstName() + ",\n\nCongratulazioni! La tua offerta per la proprietà in \"" + offer.getProperty().getAddress() + "\" è stata accettata.\n\nDettagli dell'offerta accettata:\n- Importo: " + offer.getPrice() + BOTTOM;
        sendEmail(offer.getUser().getEmail(), subject, contentText);
    }

    public void sendOfferRejectedEmail(Offer offer) throws IOException {
        final String subject = "Offerta rifiutata per la proprietà";
        if (offer.getUser() == null) {
            return;
        }
        String contentText = CIAO + offer.getUser().getFirstName() + ",\n\nSiamo spiacenti di informarti che la tua offerta per la proprietà in \"" + offer.getProperty().getAddress() + "\" è stata rifiutata.\n\nDettagli dell'offerta rifiutata:\n- Importo: " + offer.getPrice() + BOTTOM;
        sendEmail(offer.getUser().getEmail(), subject, contentText);
    }

    public void sendOfferCountered(Offer offer) throws IOException {
        final String subject = "Offerta controproposta per la proprietà";
        if (offer.getUser() == null) {
            return;
        }
        String contentText = CIAO + offer.getUser().getFirstName() + ",\n\nLa tua offerta per la proprietà in \"" + offer.getProperty().getAddress() + "\" ha ricevuto una controproposta.\n\nDettagli della controproposta:\n- Nuovo importo: " + offer.getPrice() + BOTTOM;
        sendEmail(offer.getUser().getEmail(), subject, contentText);
    }

    public void sendVisitScheduledEmail(AgentVisitDTO visit) throws IOException {
        final String subject = "Visita programmata";
        String contentText = "Ciao,\n\nUna nuova visita è stata programmata per la proprietà in \"" + visit.getAddress() + "\".\n\nDettagli della visita:\n- Data e ora: " + visit.getVisit().getStartTime() + "-" + visit.getVisit().getEndTime() + VISITOR + visit.getVisit().getUser().getUsername() + BOTTOM;
        sendEmail(visit.getVisit().getAgent().getEmail(), subject, contentText);
    }

    public void sendVisitStatusUpdatedEmail(AgentVisitDTO visit) throws IOException {
        final String subject = "Aggiornamento stato visita";
        String contentText = "Ciao,\n\nLa visita per la proprietà in \"" + visit.getAddress() + "\" è ora " + visit.getVisit().getStatus() + ".\n\nDettagli della visita:\n- Data e ora: " + visit.getVisit().getStartTime() + "-" + visit.getVisit().getEndTime() + VISITOR + visit.getVisit().getUser().getUsername() + "\n - Agente: " + visit.getVisit().getAgent().getUsername() + BOTTOM;
        sendEmail(visit.getVisit().getAgent().getEmail(), subject, contentText);
        sendEmail(visit.getVisit().getUser().getEmail(), subject, contentText);
    }

    public void sendVisitCancelledEmail(AgentVisitDTO visit) throws IOException {
        final String subject = "Visita annullata";
        String contentText = "Ciao,\n\nCi dispiace informarla che la visita per la proprietà in \"" + visit.getAddress() + "\" è stata annullata dal nostro agente.\n\nDettagli della visita:\n- Data e ora: " + visit.getVisit().getStartTime() + "-" + visit.getVisit().getEndTime()  + BOTTOM;
        sendEmail(visit.getVisit().getUser().getEmail(), subject, contentText);
    }

    public void sendAgentAccountCreatedEmail(String email, String password) {
        final String subject = "Account agente creato";
        String contentText = "Ciao,\n\nIl tuo account agente su Dieti Estates è stato creato con successo. Benvenuto a bordo! Usa questa password per accedere: " + password + "." + BOTTOM;
        sendEmail(email, subject, contentText);
    }


    public void sendManagerAccountCreatedEmail(String email, String password) {
        final String subject = "Account manager creato";
        String contentText = "Ciao,\n\nIl tuo account manager su Dieti Estates è stato creato con successo. Benvenuto a bordo! Usa questa password per accedere: " + password + "." + BOTTOM;
        sendEmail(email, subject, contentText);
    }
}