package com.cision.cisionassetmanager.service.impl;


import com.cision.cisionassetmanager.service.CisionSFTPService;
import com.jcraft.jsch.*;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@Slf4j
public class CisionSFTPServiceImp implements CisionSFTPService {

    @Value("${sftp.host}")
    private String host;

    @Value("${sftp.port}")
    private Integer port;

    @Value("${sftp.username}")
    private String username;

    @Value("${sftp.password}")
    private String password;

    @Value("${sftp.sessionTimeout}")
    private Integer sessionTimeout;

    @Value("${sftp.channelTimeout}")
    private Integer channelTimeout;

    public void readAssets() {
        ChannelSftp channelSftp = createChannelSftp();
        try {
            List<ChannelSftp.LsEntry> files = channelSftp.ls("upload");
            files.forEach( CisionSFTPServiceImp::logFileStats);
        } catch(SftpException   ex) {
            log.error("Error download file", ex);
        } finally {
            disconnectChannelSftp(channelSftp);
        }

    }

    private ChannelSftp createChannelSftp() {
        try {
            JSch jSch = new JSch();
            Session session = jSch.getSession(username, host, port);
            session.setConfig("StrictHostKeyChecking", "no");
            session.setPassword(password);
            session.connect(sessionTimeout);
            Channel channel = session.openChannel("sftp");
            channel.connect(channelTimeout);
            return (ChannelSftp) channel;
        } catch(JSchException ex) {
            log.error("Create ChannelSftp error", ex);
        }

        return null;
    }

    private void disconnectChannelSftp(ChannelSftp channelSftp) {
        try {
            if( channelSftp == null)
                return;

            if(channelSftp.isConnected())
                channelSftp.disconnect();

            if(channelSftp.getSession() != null)
                channelSftp.getSession().disconnect();

        } catch(Exception ex) {
            log.error("SFTP disconnect error", ex);
        }
    }

    private static void logFileStats(ChannelSftp.LsEntry fileEntry) {
        try {
            if (fileEntry.getFilename().toLowerCase().endsWith(".pdf") || fileEntry.getFilename().toLowerCase().endsWith(".xml")) {
                long fileSizeInMegaBytes = ((fileEntry.getAttrs().getSize()) / 1024) / 1024;
                if (fileSizeInMegaBytes < 10)
                    log.info("File name: {}, File size: {} MB", fileEntry.getFilename(), fileSizeInMegaBytes);
                else
                    throw new Exception("File size exceeds more then 10 MB");
            } else {
                throw new Exception("FileFormat not supported");
            }
        } catch (Exception e) {
            log.error("File name: {}, errorMessage: {} ", fileEntry.getFilename(), e.getMessage());
        }
    }
}
