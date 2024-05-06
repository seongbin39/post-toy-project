package store.sbin.postservice.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class MailDto {

    // 받는 사람
    private String to;
    // 보내는 사람
    private String from;
    // 제목
    private String subject;
    // 내용
    private String content;
    // 참조
    private String cc;
    // 숨은 참조
    private String bcc;

    @Builder
    public MailDto(String to, String from, String subject, String content, String cc, String bcc) {
        this.to = to;
        this.from = from;
        this.subject = subject;
        this.content = content;
        this.cc = cc;
        this.bcc = bcc;
    }
}
