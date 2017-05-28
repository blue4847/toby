package toby.study.dao;

import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * MailSender의 Mock구현체
 * 메일 송신 여부를 기록한다.
 */
public class MockMailSender implements MailSender{
    
	/**
	 * 메일 발송이 일어나면 requests에 그 결과가 저장된다.
	 */
    private List<String> requests = new ArrayList<>();

    public List<String> getRequests(){
        return requests;
    }

    @Override
    public void send(SimpleMailMessage mailMessage) throws MailException{
        requests.add(mailMessage.getTo()[0]);
    }

    @Override
    public void send(SimpleMailMessage... simpleMailMessages) throws MailException {
    }
}
