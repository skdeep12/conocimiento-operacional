import org.jenkinsci.plugins.github.config.GitHubPluginConfig
import org.jenkinsci.plugins.github.config.GitHubServerConfig

def github = jenkins.model.Jenkins.instance.getExtensionList(GitHubPluginConfig.class)[0]


def github_credentials_id = "GITHUB_SERVICE_TOKEN"
def env = System.getenv()
def created = false
for (e in env) { 
    if(e.key.contains(github_credentials_id) && e.value.trim() != null){
        def config = new GitHubServerConfig(github_credentials_id)
        config.setName("OrgName")
        config.setApiUrl("<github_api_url>")
        config.setManageHooks(false)

        github.setConfigs([config])
        github.save()
        created = true;
    }
}

if (!created){
    println "Github plugin is not configured."
}
