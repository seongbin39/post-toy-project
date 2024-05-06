package store.sbin.postservice.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import store.sbin.postservice.domain.Attachment;

@Getter
@Setter
@Builder
public class FilesDto {

    private Long id;
    private String fileName;
    private String fileOriName;
    private String fileUrl;
    private String fileType;
    private Long fileSize;

    public Attachment toEntity() {
        return Attachment.builder()
                .id(id)
                .fileName(fileName)
                .fileOriName(fileOriName)
                .fileUrl(fileUrl)
                .fileType(fileType)
                .fileSize(fileSize)
                .build();
    }

}
