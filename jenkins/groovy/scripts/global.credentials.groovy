import com.cloudbees.plugins.credentials.impl.*;
import com.cloudbees.plugins.credentials.*;
import com.cloudbees.plugins.credentials.domains.*;
import org.jenkinsci.plugins.plaincredentials.impl.*;
import hudson.util.Secret;

def env = System.getenv()

def credsList = []
println "Creating credentials..."
for (e in env) {
    if (e.key.contains("ACR") || 
        e.key.contains("SUBSCRIPTION") || 
        e.key.contains("SP_CLIENT") || 
        e.key.contains("NEXUS") ||
        e.key.contains("TOKEN") ||
        e.key.contains("Tenant")){
        println "found "+ e.key
        credsList << (Credentials) new StringCredentialsImpl(CredentialsScope.GLOBAL,e.key, e.key, Secret.fromString(e.value.trim()));
    }
}



if ( env["GITHUB_USER"]?.trim() == null || env["GITHUB_PASSWORD"]?.trim() == null ) {
    println "Github credentials not present."
} else {
    println "found github credentials" 
    credsList << (Credentials) new UsernamePasswordCredentialsImpl(CredentialsScope.GLOBAL,"git-credentials", "git creds", env["GITHUB_USER"].trim(),env["GITHUB_PASSWORD"].trim())
}

//TODO: Add coverity and sonar secrets.
for (c in credsList) {
    SystemCredentialsProvider.getInstance().getStore().addCredentials(Domain.global(), c)
}

println "Credentials Created."
