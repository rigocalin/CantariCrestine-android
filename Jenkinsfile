stage 'Unit test'
    node('android') {
        checkout scm
        withEnv(['GRADLE_HOME=/var/jenkins_home/tools/gradle', 'GRADLE_OPTS="-Dorg.gradle.daemon=true -Xmx1024m -Xms512m -XX:MaxPermSize=2048m"', 'ANDROID_HOME=/var/jenkins_home/tools/android-sdk']) {
           try {
               sh '$GRADLE_HOME/bin/gradle clean testDebug'
           } finally {
               step([$class: 'JUnitResultArchiver', testResults: '**/*.xml'])
           }
        }
    }

stage 'Package'
  node('android') {
      checkout scm
      withEnv(['GRADLE_HOME=/var/jenkins_home/tools/gradle', 'GRADLE_OPTS="-Dorg.gradle.daemon=true -Xmx1024m -Xms512m -XX:MaxPermSize=2048m"', 'ANDROID_HOME=/var/jenkins_home/tools/android-sdk']) {
           try {
               sh './bundle-db.sh'
               sh '$GRADLE_HOME/bin/gradle clean assembleDebug'
           } finally {
               archive includes:'**/*.apk'
           }
      }
  }
