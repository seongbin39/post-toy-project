package store.sbin.postservice.service.files;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.tika.Tika;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import store.sbin.postservice.config.exception.FileStorageException;
import store.sbin.postservice.domain.Attachment;
import store.sbin.postservice.repository.post.FileRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
public class FileService {
    private final Environment env;
    private final Path fileStorageLocation;
    private final FileRepository fileRepository;
    private static final Tika tika = new Tika();

    public FileService(Environment env, FileRepository fileRepository) {
        this.env = env;
        this.fileStorageLocation = Paths.get(Objects.requireNonNull(env.getProperty("file.path"))).toAbsolutePath().normalize();
        this.fileRepository = fileRepository;
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (IOException e) {
            throw new FileStorageException("파일 저장 경로 생성 실패", e);
        }
    }

    /**
     * 첨부 파일 삭제
     *
     * @param files 첨부 파일 정보
     * @throws FileStorageException 파일 삭제 실패 시 예외 발생
     */
    public void deleteFile(Attachment files) {
        Path path = Paths.get(files.getFileUrl());
        if (Files.exists(path)) {
            try {
                Files.delete(path);
                log.info("파일 삭제 성공 : {}, {}", files.getFileOriName(), files.getFileUrl());
            } catch (IOException e) {
                throw new FileStorageException("파일 삭제 실패", e);
            }
        }
    }

    public void deleteFile(String filePath) {
        Path path = Paths.get(filePath);
        if (Files.exists(path)) {
            try {
                Files.delete(path);
                log.info("파일 삭제 성공 : {}", filePath);
            } catch (IOException e) {
                throw new FileStorageException("파일 삭제 실패", e);
            }
        }
    }

    /**
     * 파일 업로드
     *
     * @param file MultipartFile
     * @return 파일 정보
     */
    public Map<String, String> storeFile(MultipartFile file) {
        // 파일 검증
        try {
            validateFile(file);
        } catch (IOException e) {
            throw new FileStorageException("파일 저장 실패 : " + file.getOriginalFilename(), e);
        }

        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));

        String filePath = Optional.ofNullable(env.getProperty("file.path")).orElse("/upload");
        // 파일 저장 경로
        Path directory = Paths.get(filePath, datePath.split("/"));
        // 파일 확장자
        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        // 새로운 파일 이름
        String newFileName = UUID.randomUUID() + "." + extension;
        // 파일 경로
        String newFileUrl = directory + File.separator + newFileName;

        try {
            // 파일 저장 경로 생성
            Files.createDirectories(directory);
            Path targetPath = directory.resolve(newFileName).normalize();
            // 파일 저장
            file.transferTo(targetPath);
            log.info("파일 저장 성공 : {}", newFileUrl);
        } catch (IOException e) {
            log.error("파일 저장 실패 : {}", newFileUrl);
            throw new FileStorageException("파일 저장 실패 : " + file.getOriginalFilename(), e);
        }

        return Map.of(
                "fileName", newFileName,
                "fileUrl", newFileUrl
        );
    }

    /**
     * MultipartFile 리스트를 Files 리스트로 변환
     *
     * @param files MultipartFile 리스트
     * @return Files 리스트
     */
    public List<Attachment> createFileList(List<MultipartFile> files) {
        return files.stream().map(file -> {
            Map<String, String> fileInfo = storeFile(file);

            return Attachment.builder()
                    .fileName(fileInfo.get("fileName"))
                    .fileOriName(file.getOriginalFilename())
                    .fileType(file.getContentType())
                    .fileUrl(fileInfo.get("fileUrl"))
                    .fileSize(file.getSize())
                    .build();
        }).toList();
    }

    /**
     * 첨부파일 검증
     *
     * @param file MultipartFile 파일
     */
    public void validateFile(MultipartFile file) throws IOException {
        // 파일 이름 검증
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        if (fileName.contains("..")) {
            throw new FileStorageException("파일 이름에 부적절한 문자가 포함되어 있습니다. : {}", fileName);
        }

        // 파일 크기 검증
        long fileSize = file.getSize();
        long maxFileSize = 10485760; // 10MB
        if (fileSize > maxFileSize) {
            throw new FileStorageException("파일 크기 초과 : {}, {}, {}", fileName, fileSize, maxFileSize);
        }

        // 파일 타입 검증 (MIME 타입)
        String mimeType = tika.detect(file.getResource().getInputStream());

        List<String> allowedContentTypes = Arrays.asList(
                "application/pdf", "application/zip", "text/plain",
                "image/jpeg", "image/png", "image/gif", "image/bmp", "image/svg+xml",
                "application/msword", "application/vnd.ms-powerpoint", "application/vnd.ms-excel",
                "application/x-hwp"
                ); // 허용된 MIME 타입 리스트
        if (!allowedContentTypes.contains(mimeType)) {
            throw new FileStorageException("허용되지 않는 파일 타입 입니다. " + fileName + " : " + mimeType);
        }
    }

}
