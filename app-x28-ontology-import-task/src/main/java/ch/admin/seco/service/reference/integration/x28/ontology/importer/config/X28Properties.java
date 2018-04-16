package ch.admin.seco.service.reference.integration.x28.ontology.importer.config;

import javax.validation.constraints.NotBlank;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "jobroom.x28.sftp")
@Validated
public class X28Properties {

    @NotBlank
    private String host;

    @NotBlank
    private String username;

    @NotBlank
    private String password;

    private Integer port;

    private Boolean allowUnknownKeys = true;

    @NotBlank
    private String localDirectory;

    @NotBlank
    private String remoteDirectory;

    @NotBlank
    private String fileNamePattern;

    public Boolean getAllowUnknownKeys() {
        return allowUnknownKeys;
    }

    public void setAllowUnknownKeys(Boolean allowUnknownKeys) {
        this.allowUnknownKeys = allowUnknownKeys;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLocalDirectory() {
        return localDirectory;
    }

    public void setLocalDirectory(String localDirectory) {
        this.localDirectory = localDirectory;
    }

    public String getRemoteDirectory() {
        return remoteDirectory;
    }

    public void setRemoteDirectory(String remoteDirectory) {
        this.remoteDirectory = remoteDirectory;
    }

    public String getFileNamePattern() {
        return fileNamePattern;
    }

    public void setFileNamePattern(String fileNamePattern) {
        this.fileNamePattern = fileNamePattern;
    }
}
