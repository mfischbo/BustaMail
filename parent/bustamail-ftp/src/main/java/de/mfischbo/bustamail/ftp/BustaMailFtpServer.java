package de.mfischbo.bustamail.ftp;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.listener.ListenerFactory;
import org.springframework.stereotype.Component;

@Component
public class BustaMailFtpServer {

	@Inject private BustaMailUserManager	ftpUserManager;
	
	@Inject private BustaMailFileSystemFactory ftpFileSystemFactory;
	
	@PostConstruct
	public void initialize() throws FtpException {
		FtpServerFactory serverFactory = new FtpServerFactory();
		ListenerFactory lFact = new ListenerFactory();
		lFact.setPort(2221);
		lFact.setServerAddress("127.0.0.1");
		serverFactory.addListener("default", lFact.createListener());
		serverFactory.setUserManager(ftpUserManager);
		serverFactory.setFileSystem(ftpFileSystemFactory);
		
		//Map<String, Ftplet> ftpmap = Collections.singletonMap("default", new TemplatePackFtplet());
		//serverFactory.setFtplets(ftpmap);
		
		FtpServer server = serverFactory.createServer();
		server.start();
	}
}
