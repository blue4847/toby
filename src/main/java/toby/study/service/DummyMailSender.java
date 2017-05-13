package toby.study.service;

import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

/**
 * Created by blue4 on 2017-05-11.
 */
public class DummyMailSender implements MailSender {

    @Override
    public void send(SimpleMailMessage mailMessage) throws MailException {
    }

    @Override
    public void send(SimpleMailMessage... mailMessages) throws MailException {
    }
}
