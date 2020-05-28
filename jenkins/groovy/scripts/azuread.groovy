    import jenkins.model.*
import hudson.model.*
import jenkins.security.*
import jenkins.security.apitoken.*

import com.microsoft.jenkins.azuread.AzureSecurityRealm;
import com.microsoft.jenkins.azuread.AzureAdGroup;
import com.microsoft.jenkins.azuread.AzureAdGroup.AzureAdMatrixAuthorizationStrategy;
import com.microsoft.jenkins.azuread.AzureAdGroup;
import com.microsoft.jenkins.azuread.AzureAdMatrixAuthorizationStrategy;
import hudson.security.Permission;
import com.cloudbees.plugins.credentials.CredentialsProvider;
import hudson.scm.SCM;

import hudson.security.csrf.DefaultCrumbIssuer
import jenkins.security.s2m.AdminWhitelistRule

def userPermissions = [Jenkins.READ,Item.BUILD, Item.CANCEL, Item.READ, Item.WORKSPACE, View.READ]
def additionalAdminPermissions = [Item.CONFIGURE, Item.CREATE, Jenkins.READ, CredentialsProvider.VIEW, Computer.BUILD, Computer.CONNECT, Computer.DISCONNECT, SCM.TAG]
def superUserPermissions = [Item.DELETE, Item.DISCOVER, Hudson.ADMINISTER, Run.DELETE, Run.UPDATE]

def env = System.getenv()
def jenkins = Jenkins.getInstance()
//println env["AzureTenant"]
realm = new AzureSecurityRealm()

realm.setTenant(env["AzureTenant"].trim())
realm.setClientId(env["AzureClientID"].trim())
realm.setClientSecret(env["AzureClientSecret"].trim())
jenkins.setSecurityRealm(realm)



println("Security realm set to azure")

authStrategy = new AzureAdMatrixAuthorizationStrategy()
groups = new File("/etc/config/adgroups.txt").text.tokenize('/')

groups.each { line -> 
    temp = line.split(',')// first token will be name of group and second token will be object id
    groupName = temp[0] + " (" + temp[1] + ")"
    println("Adding " + groupName + " to auth matrix")
    userPermissions.each {  permission ->
        authStrategy.add(permission,groupName)
    }
    if(groupName.contains("Admin")) {
        additionalAdminPermissions.each { permission ->
            authStrategy.add(permission,groupName)
        }
    }
    if( groupName.contains("Contributors")){
        superUserPermissions.each{ permission ->
            authStrategy.add(permission,groupName)
        }
    }
}

jenkins.setAuthorizationStrategy(authStrategy)


if(jenkins.getCrumbIssuer() == null) {
    jenkins.setCrumbIssuer(new DefaultCrumbIssuer(true))
}

jenkins.injector.getInstance(AdminWhitelistRule.class)
    .setMasterKillSwitch(false);

jenkins.save()



