// ─────────────────────────────────────────────────────────────────────────
// GPN depends on 2 other local, unpublished Maven artifacts (autoFrameX's
// autoframex-selenium + AlfaDOCK) with no shared Nexus/Artifactory in this
// environment (same v1 simplification as GPN/Dockerfile — see its header
// comment). This pipeline checks out each dependency's own git repo into a
// sibling directory of the Jenkins workspace and builds them in order,
// mirroring the exact directory layout GPN/Dockerfile already uses.
// ─────────────────────────────────────────────────────────────────────────
pipeline {
    agent any

    parameters {
        choice(
            name: 'BROWSER',
            choices: ['chrome', 'firefox', 'edge'],
            description: 'Browser to use for UI test execution'
        )
        choice(
            name: 'ENVIRONMENT',
            choices: ['dev', 'qa', 'prod'],
            description: 'Target environment'
        )
        booleanParam(
            name: 'HEADLESS',
            defaultValue: true,
            description: 'Run browser in headless mode (recommended for CI)'
        )
        string(
            name: 'SUITE_FILE',
            defaultValue: 'gpn.xml',
            description: 'TestNG suite XML file to execute (relative to the GPN/ directory)'
        )
        string(
            name: 'THREAD_COUNT',
            defaultValue: '4',
            description: 'Parallel thread count for Surefire (parallel="instances")'
        )
        string(
            name: 'AUTOFRAMEX_GIT_URL',
            defaultValue: 'https://github.com/Rajeshluffy/autoFrameX.git',
            description: 'autoFrameX repo URL'
        )
        string(
            name: 'AUTOFRAMEX_GIT_BRANCH',
            defaultValue: 'framework-3.1',
            description: 'autoFrameX branch to build against'
        )
        string(
            name: 'ALFADOCK_GIT_URL',
            defaultValue: 'https://github.com/Rajeshluffy/alfaDock.git',
            description: 'AlfaDOCK repo URL'
        )
        string(
            name: 'ALFADOCK_GIT_BRANCH',
            defaultValue: 'main',
            description: 'AlfaDOCK branch to build against'
        )
    }

    options {
        timestamps()
        timeout(time: 60, unit: 'MINUTES')
        buildDiscarder(logRotator(numToKeepStr: '10'))
    }

    stages {

        stage('Validate parameters') {
            steps {
                script {
                    if (!params.ALFADOCK_GIT_URL?.trim()) {
                        error("ALFADOCK_GIT_URL is required — AlfaDOCK has no default remote configured yet. " +
                              "Push AlfaDOCK to a git repo and pass its URL via 'Build with Parameters'.")
                    }
                }
            }
        }

        stage('Checkout GPN') {
            steps {
                dir('GPN') {
                    checkout scm
                }
            }
        }

        stage('Checkout dependencies') {
            parallel {
                stage('autoFrameX') {
                    steps {
                        dir('autoFrameX') {
                            git url: params.AUTOFRAMEX_GIT_URL, branch: params.AUTOFRAMEX_GIT_BRANCH
                        }
                    }
                }
                stage('AlfaDOCK') {
                    steps {
                        dir('alfaDock') {
                            git url: params.ALFADOCK_GIT_URL, branch: params.ALFADOCK_GIT_BRANCH
                        }
                    }
                }
            }
        }

        stage('Build & Install dependencies') {
            steps {
                // Install order matters: autoFrameX reactor -> AlfaDOCK -> GPN.
                script {
                    def cmds = [
                        'mvn -f autoFrameX/pom.xml clean install -DskipTests -Djacoco.skip=true -q',
                        'mvn -f alfaDock/pom.xml clean install -DskipTests -q'
                    ]
                    cmds.each { cmd -> if (isUnix()) { sh cmd } else { bat cmd } }
                }
            }
        }

        stage('Inject GPN account data') {
            steps {
                // "GPN Account.xlsx"/"GPN Account - ERP.xlsx" hold real customer
                // credentials (DATAPROVIDER mode) — never committed (see
                // GPN/.gitignore). Supplied here as Jenkins file credentials.
                withCredentials([
                    file(credentialsId: 'gpn-account-xlsx',     variable: 'GPN_ACCOUNT_XLSX'),
                    file(credentialsId: 'gpn-account-erp-xlsx', variable: 'GPN_ACCOUNT_ERP_XLSX')
                ]) {
                    script {
                        if (isUnix()) {
                            sh '''
                                cp "$GPN_ACCOUNT_XLSX"     "GPN/src/test/resources/GPN Account.xlsx"
                                cp "$GPN_ACCOUNT_ERP_XLSX" "GPN/src/test/resources/GPN Account - ERP.xlsx"
                            '''
                        } else {
                            bat '''
                                copy /Y "%GPN_ACCOUNT_XLSX%"     "GPN\\src\\test\\resources\\GPN Account.xlsx"
                                copy /Y "%GPN_ACCOUNT_ERP_XLSX%" "GPN\\src\\test\\resources\\GPN Account - ERP.xlsx"
                            '''
                        }
                    }
                }
            }
        }

        stage('Build GPN') {
            steps {
                script {
                    def cmd = 'mvn -f GPN/pom.xml clean install -DskipTests -q'
                    if (isUnix()) { sh cmd } else { bat cmd }
                }
            }
        }

        stage('Run GPN suite') {
            steps {
                // STANDARD-mode suites (gpn-standard.xml) resolve their account
                // via AlfaDockBaseTest's ConfigResolver fallback — ALFADOCK_*_USER/PASS
                // supplied here as env vars, matching gpn-standard.xml's own
                // documented resolution order. DATAPROVIDER-mode suites (gpn.xml,
                // the default) read accounts from the Excel fixtures injected above
                // instead and don't need these credentials.
                withCredentials([
                    string(credentialsId: 'alfadock-company-user', variable: 'ALFADOCK_COMPANY_USER'),
                    string(credentialsId: 'alfadock-company-pass', variable: 'ALFADOCK_COMPANY_PASS'),
                    string(credentialsId: 'alfadock-user-user',    variable: 'ALFADOCK_USER_USER'),
                    string(credentialsId: 'alfadock-user-pass',    variable: 'ALFADOCK_USER_PASS')
                ]) {
                    script {
                        def cmd = "mvn -f GPN/pom.xml test" +
                            " -DsuiteXmlFile=${params.SUITE_FILE}" +
                            " -Dbrowser=${params.BROWSER}" +
                            " -Denv=${params.ENVIRONMENT}" +
                            " -Dheadless=${params.HEADLESS}" +
                            " -Dsurefire.threadCount=${params.THREAD_COUNT}" +
                            " -Dsurefire.reportNameSuffix=gpn"
                        if (isUnix()) { sh cmd } else { bat cmd }
                    }
                }
            }
        }
    }

    post {
        always {
            script {
                def summary = junit(
                    testResults: 'GPN/**/surefire-reports/*.xml',
                    allowEmptyResults: true
                )

                // Quality gate — no RetryTest-style intentional failures in GPN,
                // so this is a real gate, not just informational (unlike autoFrameX's
                // own 50% threshold, which exists only to accommodate its retry-engine
                // self-test).
                if (summary.totalCount > 0) {
                    def failureRate = (summary.failCount * 100.0) / summary.totalCount
                    echo "Quality gate: ${summary.failCount}/${summary.totalCount} failed (${String.format('%.1f', failureRate)}%)"
                    if (failureRate > 10) {
                        currentBuild.result = 'FAILURE'
                        error("Quality gate failed: ${String.format('%.1f', failureRate)}% failure rate exceeds 10% threshold")
                    }
                }
            }

            archiveArtifacts(
                artifacts: 'GPN/reports/**/*.html, GPN/logs/test-events.json, GPN/**/target/surefire-reports/**',
                allowEmptyArchive: true
            )
        }

        success {
            echo 'GPN suite executed successfully.'
        }

        failure {
            echo 'GPN suite failed. Check archived reports.'
        }

        unstable {
            echo 'Build is unstable — some tests were skipped or reported as flaky.'
        }
    }
}
