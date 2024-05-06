package store.sbin.postservice.service.mail;

import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import store.sbin.postservice.dto.MailDto;

@Service
@Slf4j
public class MailService {
    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    public MailService(JavaMailSender mailSender, SpringTemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    /**
     * 메일 전송
     *
     * @param mail 메일 정보
     */
    public void sendMail(MailDto mail) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();

        // 필수 입력 체크
        validationMail(mail);
        try {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");

            messageHelper.setTo(mail.getTo());
            messageHelper.setSubject(mail.getSubject());
            messageHelper.setText(mail.getContent());
            if (mail.getCc() != null) {
                messageHelper.setCc(mail.getCc());
            }
            if (mail.getBcc() != null) {
                messageHelper.setBcc(mail.getBcc());
            }
            mailSender.send(mimeMessage);

            log.info("========== Success send mail ==========  ");

        }
        catch (Exception e) {
            log.info("========== Fail send mail ==========, {}", e.getMessage());
        }

    }

    /**
     * 메일 정보 유효성 체크
     *
     * @param mail 메일 정보
     */
    public void validationMail(MailDto mail) {
        if (mail.getTo() == null || mail.getSubject() == null || mail.getContent() == null){
            throw new IllegalArgumentException("받는사람, 제목, 내용은 필수 입력 사항 입니다.");
        }
        if (mail.getTo().isBlank() || mail.getSubject().isBlank() || mail.getContent().isBlank()){
            throw new IllegalArgumentException("받는사람, 제목, 내용은 필수 입력 사항 입니다.");
        }
    }

    public String setContext(String message) {
        Context context = new Context();
        context.setVariable("message", message);
        return templateEngine.process(message, context);
    }
}
