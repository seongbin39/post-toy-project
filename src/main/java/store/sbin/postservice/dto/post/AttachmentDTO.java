package store.sbin.postservice.dto.post;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AttachmentDTO {
    private Long id;
    private String fileName;
    private String fileOriName;
    private String fileUrl;
    private String fileType;
    private Long fileSize;
    private Long postId;
}
