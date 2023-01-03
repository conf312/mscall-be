package com.admin.util;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

@RequiredArgsConstructor
@Component
public class MailUtil {
    private final JavaMailSender javaMailSender;

    public void sendMail(String to) {
        // 수신 대상을 담을 ArrayList 생성
        ArrayList<String> toUserList = new ArrayList<>();
        // 수신 대상 추가
        toUserList.add(to);
        // 수신 대상 개수
        int toUserSize = toUserList.size();
        // SimpleMailMessage (단순 텍스트 구성 메일 메시지 생성할 때 이용)
        SimpleMailMessage simpleMessage = new SimpleMailMessage();
        // 수신자 설정
        simpleMessage.setTo((String[]) toUserList.toArray(new String[toUserSize]));
        // 메일 제목
        simpleMessage.setSubject("Subject Sample");
        // 메일 내용
        simpleMessage.setText("Text Sample");
        // 메일 발송
        javaMailSender.send(simpleMessage);
    }

    public void sendMailHtml(String to, String subject, String text) {
        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            message.setSubject(subject);
            message.setText(text, "UTF-8", "html");
            message.addRecipient(MimeMessage.RecipientType.TO, new InternetAddress(to, "관리자", "UTF-8"));
            javaMailSender.send(message);
        } catch (MessagingException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
