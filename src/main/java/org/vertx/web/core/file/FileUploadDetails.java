package org.vertx.web.core.file;

import java.util.Arrays;

/**
 * @author yangcong
 *
 * 文件上传详情
 */
public class FileUploadDetails {

    /**
     * 文件名(不包含后缀)
     */
    private String filename;

    /**
     * 后缀名 例如:  .txt .jpg 等
     */
    private String suffix;

    /**
     * 文件数据
     */
    private byte[] bytes;

    public String getFilename() {
        return filename;
    }

    public FileUploadDetails setFilename(String filename) {
        this.filename = filename;
        return this;
    }

    public String getSuffix() {
        return suffix;
    }

    public FileUploadDetails setSuffix(String suffix) {
        this.suffix = suffix;
        return this;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public FileUploadDetails setBytes(byte[] bytes) {
        this.bytes = bytes;
        return this;
    }

    @Override
    public String toString() {
        return "FileUploadDetails{" +
                "filename='" + filename + '\'' +
                ", suffix='" + suffix + '\'' +
                ", bytes=" + Arrays.toString(bytes) +
                '}';
    }
}
