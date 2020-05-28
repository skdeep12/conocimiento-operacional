import hudson.model.*;
import jenkins.model.*;
import hudson.tools.*;
import hudson.util.Secret;

// Check if enabled
def env = System.getenv()

// Variables
def SMTPPort = env['SMTP_PORT']
def SMTPHost = env['SMTP_HOST']
def SystemAdminMailAddress = env['POD_NAMESPACE'].split('-')[0]

// Constants
def instance = Jenkins.getInstance()
def mailServer = instance.getDescriptor("hudson.tasks.Mailer")
def extmailServer = instance.getDescriptor("hudson.plugins.emailext.ExtendedEmailPublisher")
def jenkinsLocationConfiguration = JenkinsLocationConfiguration.get()

//Jenkins Location
println "--> Configuring JenkinsLocation"
jenkinsLocationConfiguration.setAdminAddress("jenkins@"+SystemAdminMailAddress+".example.com")
jenkinsLocationConfiguration.save()

//E-mail Server
mailServer.setSmtpHost(SMTPHost)
mailServer.setSmtpPort(SMTPPort)

//Extended-Email
extmailServer.smtpHost=SMTPHost
extmailServer.smtpPort=SMTPPort

// Save the state
instance.save()
