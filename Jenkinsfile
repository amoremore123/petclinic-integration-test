pipeline {

    agent any

    tools {
        maven 'Maven'
        jdk 'Java'
        // git 'Git'
    }

    stages {

        stage('Checkout') {
            steps {
                echo '📥 Getting code...'
                checkout scm
            }
        }

        stage('Build') {
            steps {
                echo '🔨 Building...'
                bat 'mvn clean compile'
            }
        }

        stage('Test') {
            steps {
                echo '🧪 Testing...'
                bat 'mvn test'
            }
        }

        stage('Package') {
            steps {
                echo '📦 Creating JAR...'
                bat 'mvn package -DskipTests'
            }
        }
    }
}
