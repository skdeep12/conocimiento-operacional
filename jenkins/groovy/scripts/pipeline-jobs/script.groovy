if(defaultDir != ""){
                pipelineJob(appName + "-ci-pipeline") {//pr triggered
                    definition {
                        properties{
                            githubProjectUrl(repoURL)
                        }
                        triggers {
                            githubPullRequests{
                                branchRestriction {
                                    spec("")
                                    targetBranch(gitConfig.get("branch"))//on which branch prs will be raised
                                }
                                events{
                                    Open()
                                    commitChanged()
                                }
                                repoProviders {
                                    githubPlugin {
                                        // Old trigger behaviour when connection resolved first from global settings and then used locally.
                                        // Allow disable registering hooks even if it specified in global settings.
                                        manageHooks(false)
                                        // ADMIN, PUSH or PULL repository permission required for choosing connection from `GitHub Plugin` `GitHub Server Configs`.
                                        repoPermission("PUSH")
                                    }
                                }
                                triggerMode("HEAVY_HOOKS")
                                skipFirstRun(false)
                            }
                        }
                        cpsScmFlowDefinition {
                            scm {
                                gitSCM {
                                    extensions {
                                        cleanBeforeCheckout()
                                    }
                                    branches {
                                        branchSpec {
                                            name(cicdBranch)
                                        }
                                    }
                                    userRemoteConfigs {
                                        userRemoteConfig {
                                            url(repoURL)
                                            // ID of the repository, such as origin, to uniquely identify this repository among other remote repositories.
                                            name("")
                                            // A refspec controls the remote refs to be retrieved and how they map to local refs.
                                            refspec("")
                                            credentialsId(credentials)
                                        }
                                    }
                                    /* doGenerateSubmoduleConfigurations, gitTool, browser
                                    * Three above mentioned fields are not required for the job configuration,
                                    * but the pipeline job creation will fail if you don't specify these fields.
                                    */
                                    doGenerateSubmoduleConfigurations(false)
                                    gitTool(git_tool)
                                    browser {
                                        gitWeb {
                                            repoUrl(repoURL)
                                        }
                                    }
                                }
                            }
                            scriptPath(defaultDir+"/ci.jenkinsfile")
                        }
                    }
                }
                pipelineJob(appName + "-cd-pipeline") {// push triggeres
                    definition {
                        properties{
                            githubProjectUrl(repoURL)
                        }
                        triggers{
                            GenericTrigger{
                                genericVariables {
                                    genericVariable{
                                        key("ref")
                                        value("\$.ref")
                                        expressionType("JSONPath")
                                    }
                                    genericVariable{
                                        key("repo")
                                        value("\$.repository.name")
                                        expressionType("JSONPath")
                                    }
                                }
                                token("123")
                                regexpFilterText("\$ref \$repo")
                                regexpFilterExpression("^refs/heads/" + gitConfig.get("branch") + "\\s" + gitConfig.get("repo") + "\$")
                            }
                        }
                        cpsScmFlowDefinition {
                            scm {
                                gitSCM {
                                    extensions {
                                        cleanBeforeCheckout()
                                    }
                                    branches {
                                        branchSpec {
                                            name(cicdBranch)
                                        }
                                    }
                                    userRemoteConfigs {
                                        userRemoteConfig {
                                            url(repoURL)
                                            // ID of the repository, such as origin, to uniquely identify this repository among other remote repositories.
                                            name("")
                                            // A refspec controls the remote refs to be retrieved and how they map to local refs.
                                            refspec("")
                                            credentialsId(credentials)
                                        }
                                    }
                                    /* doGenerateSubmoduleConfigurations, gitTool, browser
                                    * Three above mentioned fields are not required for the job configuration,
                                    * but the pipeline job creation will fail if you don't specify these fields.
                                    */
                                    doGenerateSubmoduleConfigurations(false)
                                    gitTool(git_tool)
                                    browser {
                                        gitWeb {
                                            repoUrl(repoURL)
                                        }
                                    }
                                }
                            }
                            scriptPath(defaultDir+"/cd.jenkinsfile")
                        }
                    }
                }
                pipelineJob(appName + "-release-pipeline") {// release pipeline
                    definition {
                        properties{
                            githubProjectUrl()
                        }
                        cpsScmFlowDefinition {
                            scm {
                                gitSCM {
                                    extensions {
                                        cleanBeforeCheckout()
                                    }
                                    branches {
                                        branchSpec {
                                            name(cicdBranch)
                                        }
                                    }
                                    userRemoteConfigs {
                                        userRemoteConfig {
                                            url(repoURL)
                                            // ID of the repository, such as origin, to uniquely identify this repository among other remote repositories.
                                            name(id)
                                            // A refspec controls the remote refs to be retrieved and how they map to local refs.
                                            refspec(defaultRefspec)
                                            credentialsId(credentials)
                                        }
                                    }
                                    /* doGenerateSubmoduleConfigurations, gitTool, browser
                                    * Three above mentioned fields are not required for the job configuration,
                                    * but the pipeline job creation will fail if you don't specify these fields.
                                    */
                                    doGenerateSubmoduleConfigurations(false)
                                    gitTool(git_tool)
                                    browser {
                                        gitWeb {
                                            repoUrl(repoURL)
                                        }
                                    }
                                }
                            }
                            scriptPath(defaultDir+"/release.jenkinsfile")
                        }
                    }
                }
                if ( coverity == true ) {
                    pipelineJob(appName + "-coverity-pipeline") {// release pipeline
                        definition {
                            properties{
                                githubProjectUrl()
                            }
                            cpsScmFlowDefinition {
                                scm {
                                    gitSCM {
                                        extensions {
                                            cleanBeforeCheckout()
                                        }
                                        branches {
                                            branchSpec {
                                                name(cicdBranch)
                                            }
                                        }
                                        userRemoteConfigs {
                                            userRemoteConfig {
                                                url(repoURL)
                                                // ID of the repository, such as origin, to uniquely identify this repository among other remote repositories.
                                                name(id)
                                                // A refspec controls the remote refs to be retrieved and how they map to local refs.
                                                refspec(defaultRefspec)
                                                credentialsId(credentials)
                                            }
                                        }
                                        /* doGenerateSubmoduleConfigurations, gitTool, browser
                                        * Three above mentioned fields are not required for the job configuration,
                                        * but the pipeline job creation will fail if you don't specify these fields.
                                        */
                                        doGenerateSubmoduleConfigurations(false)
                                        gitTool(git_tool)
                                        browser {
                                            gitWeb {
                                                repoUrl(repoURL)
                                            }
                                        }
                                    }
                                }
                                scriptPath(defaultDir+"/coverity.jenkinsfile")
                            }
                        }
                    }
                } else {
                    println "Coverity not configured for " + app
                }
            }
            if(customDir != "" && filepaths != null){
                filepaths.each { filepath ->
                    println filepath
                    pipelineJob(appName + "-" + filepath.tokenize('.')[0].replace("/", "") + "-pipeline"){ // test report pipeline
                         definition {
                              properties{
                                    githubProjectUrl(repoURL)
                              }
                              cpsScmFlowDefinition {
                                   scm {
                                       gitSCM {
                                            extensions {
                                                cleanBeforeCheckout()
                                            }
                                            branches {
                                                branchSpec {
                                                    name(cicdBranch)
                                                }
                                            }
                                            userRemoteConfigs {
                                                userRemoteConfig {
                                                    url(repoURL)
                                                    // ID of the repository, such as origin, to uniquely identify this repository among other remote repositories.
                                                    name(id)
                                                    // A refspec controls the remote refs to be retrieved and how they map to local refs.
                                                    refspec(defaultRefspec)
                                                    credentialsId(credentials)
                                                }
                                                 /* doGenerateSubmoduleConfigurations, gitTool, browser
                                                 * Three above mentioned fields are not required for the job configuration,
                                                 * but the pipeline job creation will fail if you don't specify these fields.
                                                 */
                                            }
                                            doGenerateSubmoduleConfigurations(false)
                                            gitTool(git_tool)
                                            browser {
                                                gitWeb {
                                                    repoUrl(repoURL)
                                                }
                                            }
                                       }
                                   }
                                   scriptPath(customDir+filepath)
                              }

                         }
                    }
                }
            }
