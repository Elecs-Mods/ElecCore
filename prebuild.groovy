import jenkins.model.*
import hudson.model.*

jenkins_instance = jenkins.model.Jenkins.instance

job_name = "ElecCore"
allItems = jenkins_instance.allItems
validItems = allItems.findAll{ job ->  job instanceof FreeStyleProject && job.name.contains(job_name) }
validItems.remove(build.project)

build_number = validItems.collect{ it -> it.nextBuildNumber }.max()
sync_bnr = build.project.nextBuildNumber

if(build_number >= sync_bnr) {
    sync_bnr = build_number
}

for(proj in validItems){
    proj.updateNextBuildNumber sync_bnr
}

if(build_number >= build.project.nextBuildNumber) {
    build.project.updateNextBuildNumber sync_bnr
    build.doStop()
}

return ""