import java.lang.reflect.Field
import jenkins.model.*
import org.jenkinsci.plugins.ghprb.*

def descriptor = Jenkins.instance.getDescriptorByType(org.jenkinsci.plugins.ghprb.GhprbTrigger.DescriptorImpl.class)

Field auth = descriptor.class.getDeclaredField("githubAuth")    

auth.setAccessible(true)

githubAuth = new ArrayList<GhprbGitHubAuth>()
githubAuth.add(new GhprbGitHubAuth("<github_api_url>", "", "git-credentials", "", null, null))

auth.set(descriptor, githubAuth)


descriptor.save()
