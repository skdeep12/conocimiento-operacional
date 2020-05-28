import hudson.model.*
import jenkins.model.*
import org.csanchez.jenkins.plugins.kubernetes.*
import org.csanchez.jenkins.plugins.kubernetes.volumes.workspace.EmptyDirWorkspaceVolume
import org.csanchez.jenkins.plugins.kubernetes.volumes.HostPathVolume
import org.csanchez.jenkins.plugins.kubernetes.volumes.SecretVolume
import org.csanchez.jenkins.plugins.kubernetes.pod.retention.*;
//since kubernetes-1.0
//import org.csanchez.jenkins.plugins.kubernetes.model.KeyValueEnvVar
import org.csanchez.jenkins.plugins.kubernetes.PodEnvVar
 
def env = System.getenv()
//change after testing
JENKINS_REF = "/usr/share/jenkins/ref"
ConfigObject conf = new ConfigSlurper().parse(new File( JENKINS_REF + '/kubernetes.txt').text)
def jenkins = Jenkins.getInstance()

try {
    println("Configuring k8s")
    kc = new KubernetesCloud(conf.kubernetes.name)
 
    kc.setContainerCapStr(conf.kubernetes.containerCapStr)
    kc.setServerUrl(env["KUBERNETES_URL"].trim())
    kc.setSkipTlsVerify(conf.kubernetes.skipTlsVerify)
    kc.setNamespace(env["POD_NAMESPACE"].trim())
    kc.setJenkinsUrl(env["JENKINS_URL"].trim())
    kc.setCredentialsId(conf.kubernetes.credentialsId)
    kc.setRetentionTimeout(conf.kubernetes.retentionTimeout)
    PodRetention never = new Never()
    kc.setPodRetention(never)
    kc.setReadTimeout(conf.kubernetes.readTimeout)

    println "set templates"
    kc.templates.clear()
 
    conf.kubernetes.podTemplates.each { podTemplateConfig ->

        def podTemplate = new PodTemplate()
        

        podTemplate.setLabel(podTemplateConfig.label)
        podTemplate.setName(podTemplateConfig.name)
        podTemplate.setNamespace(env["POD_NAMESPACE"].trim())
        

        if (podTemplateConfig.inheritFrom) podTemplate.setInheritFrom(podTemplateConfig.inheritFrom)
        if (podTemplateConfig.slaveConnectTimeout) podTemplate.setSlaveConnectTimeout(podTemplateConfig.slaveConnectTimeout)
        
        //pod retention settings
        podTemplate.setPodRetention(never)
        if (podTemplateConfig.idleMinutes) podTemplate.setIdleMinutes(podTemplateConfig.idleMinutes)
        
        if (podTemplateConfig.nodeSelector) podTemplate.setNodeSelector(podTemplateConfig.nodeSelector)
        //since kubernetes-1.0
        //if (podTemplateConfig.nodeUsageMode) podTemplate.setNodeUsageMode(podTemplateConfig.nodeUsageMode)
        if (podTemplateConfig.customWorkspaceVolumeEnabled) podTemplate.setCustomWorkspaceVolumeEnabled(podTemplateConfig.customWorkspaceVolumeEnabled)
 
        if (podTemplateConfig.workspaceVolume) {
            if (podTemplateConfig.workspaceVolume.type == 'EmptyDirWorkspaceVolume') {
                podTemplate.setWorkspaceVolume(new EmptyDirWorkspaceVolume(podTemplateConfig.workspaceVolume.memory))
            }
        }
 
        if (podTemplateConfig.volumes) {
            def volumes = []
            podTemplateConfig.volumes.each { volume ->
                if (volume.type == 'HostPathVolume') {
                    volumes << new HostPathVolume(volume.hostPath, volume.mountPath) 
                } else if(volume.type == 'SecretVolume') {
                    volumes << new SecretVolume(volume.mountPath, volume.secretName)
                }

            } 
            podTemplate.setVolumes(volumes) 
        } 
        if (podTemplateConfig.keyValueEnvVar) { 
            def envVars = [] 
            podTemplateConfig.keyValueEnvVar.each { keyValueEnvVar ->
 
                //since kubernetes-1.0
//                envVars << new KeyValueEnvVar(keyValueEnvVar.key, keyValueEnvVar.value)
                envVars << new PodEnvVar(keyValueEnvVar.key, keyValueEnvVar.value)
            }
            podTemplate.setEnvVars(envVars)
        }
 
 
        if (podTemplateConfig.containerTemplate) {
            println "containerTemplate: ${podTemplateConfig.containerTemplate}"
 
            ContainerTemplate ct = new ContainerTemplate(
                    podTemplateConfig.containerTemplate.name ?: conf.kubernetes.containerTemplateDefaults.name,
                    podTemplateConfig.containerTemplate.image)
 
            ct.setAlwaysPullImage(podTemplateConfig.containerTemplate.alwaysPullImage ?: conf.kubernetes.containerTemplateDefaults.alwaysPullImage)
            ct.setPrivileged(podTemplateConfig.containerTemplate.privileged ?: conf.kubernetes.containerTemplateDefaults.privileged)
            ct.setTtyEnabled(podTemplateConfig.containerTemplate.ttyEnabled ?: conf.kubernetes.containerTemplateDefaults.ttyEnabled)
            //ct.setWorkingDir(podTemplateConfig.containerTemplate.workingDir ?: conf.kubernetes.containerTemplateDefaults.workingDir)
            ct.setArgs(podTemplateConfig.containerTemplate.args ?: conf.kubernetes.containerTemplateDefaults.args)
            ct.setResourceRequestCpu(podTemplateConfig.containerTemplate.resourceRequestCpu ?: conf.kubernetes.containerTemplateDefaults.resourceRequestCpu)
            ct.setResourceLimitCpu(podTemplateConfig.containerTemplate.resourceLimitCpu ?: conf.kubernetes.containerTemplateDefaults.resourceLimitCpu)
            ct.setResourceRequestMemory(podTemplateConfig.containerTemplate.resourceRequestMemory ?: conf.kubernetes.containerTemplateDefaults.resourceRequestMemory)
            ct.setResourceLimitMemory(podTemplateConfig.containerTemplate.resourceLimitMemory ?: conf.kubernetes.containerTemplateDefaults.resourceLimitMemory)
            ct.setCommand(podTemplateConfig.containerTemplate.command ?: conf.kubernetes.containerTemplateDefaults.command)
            podTemplate.setContainers([ct])
        }


        println "adding ${podTemplateConfig.name}"
        kc.templates << podTemplate
 
    }
    jenkins.clouds.replace(kc)
    kc = null
    println("Configuring k8s completed")
}
finally {
    //if we don't null kc, jenkins will try to serialise k8s objects and that will fail, so we won't see actual error
    kc = null
}
jenkins.save()
