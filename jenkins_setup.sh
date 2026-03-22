#!/bin/bash
# ============================================================
#   Jenkins Server Setup Script for AWS EC2 (Amazon Linux 2)
#   Installs: Git, Java 21, Maven, Jenkins
#   Sets up: Environment Variables, Jenkins Service
# ============================================================

set -e  # Exit on any error

echo "============================================"
echo "  Jenkins Server Setup - Starting..."
echo "============================================"

# ----------------------------
# STEP 1: Install Git
# ----------------------------
echo ""
echo "[1/6] Installing Git..."
yum install git -y
echo "Git installed: $(git --version)"

# ----------------------------
# STEP 2: Install Java 21
# ----------------------------
echo ""
echo "[2/6] Installing Java 21..."
yum install java-21* -y

# Find the Java home path (3rd line of find output)
JAVA_HOME_PATH=$(find /usr/lib/jvm/java-21* -maxdepth 0 | sed -n '3p')
echo "Java installed at: $JAVA_HOME_PATH"
echo "Java version: $(java -version 2>&1 | head -n 1)"

# ----------------------------
# STEP 3: Install Maven
# ----------------------------
echo ""
echo "[3/6] Installing Apache Maven..."

MAVEN_VERSION="3.9.14"
MAVEN_URL="https://dlcdn.apache.org/maven/maven-3/${MAVEN_VERSION}/binaries/apache-maven-${MAVEN_VERSION}-bin.tar.gz"
MAVEN_TAR="apache-maven-${MAVEN_VERSION}-bin.tar.gz"

cd /opt

echo "Downloading Maven ${MAVEN_VERSION}..."
wget -q "$MAVEN_URL" -O "$MAVEN_TAR"

echo "Extracting Maven..."
tar -xvzf "$MAVEN_TAR"

echo "Cleaning up tar file..."
rm -rf "$MAVEN_TAR"

echo "Renaming folder to 'maven'..."
mv "apache-maven-${MAVEN_VERSION}" maven

echo "Maven installed at: /opt/maven"

# ----------------------------
# STEP 4: Set Environment Variables
# ----------------------------
echo ""
echo "[4/6] Setting up Environment Variables..."

# Write to /etc/profile.d for system-wide availability
cat >> /etc/profile.d/jenkins_env.sh << EOF

# ---- Jenkins Server Environment Variables ----
export JAVA_HOME=${JAVA_HOME_PATH}
export M2_HOME=/opt/maven
export M2=/opt/maven/bin
export PATH=\$PATH:\$JAVA_HOME/bin:\$M2:\$M2_HOME
EOF

# Source it immediately for this session
source /etc/profile.d/jenkins_env.sh

echo "Environment variables set:"
echo "  JAVA_HOME = $JAVA_HOME"
echo "  M2_HOME   = $M2_HOME"
echo "  M2        = $M2"

# Verify Maven
echo "Maven version: $(mvn --version | head -n 1)"

# ----------------------------
# STEP 5: Install Jenkins
# ----------------------------
echo ""
echo "[5/6] Installing Jenkins..."

# Add Jenkins repo (official)
wget -O /etc/yum.repos.d/jenkins.repo https://pkg.jenkins.io/redhat-stable/jenkins.repo
rpm --import https://pkg.jenkins.io/redhat-stable/jenkins.io-2023.key

# Install Jenkins
yum install jenkins -y

echo "Jenkins installed."

# ----------------------------
# STEP 6: Start & Enable Jenkins
# ----------------------------
echo ""
echo "[6/6] Starting and Enabling Jenkins Service..."

systemctl daemon-reload
service jenkins start
systemctl enable jenkins

echo ""
echo "============================================"
echo "  Setup Complete!"
echo "============================================"
echo ""
echo "Jenkins Status:"
systemctl status jenkins --no-pager | head -5

echo ""
echo "============================================"
echo "  NEXT STEPS:"
echo "============================================"
echo ""
echo "1. Open Jenkins in browser:  http://<YOUR-EC2-PUBLIC-IP>:8080"
echo ""
echo "2. Get the initial admin password:"
echo "   cat /var/lib/jenkins/secrets/initialAdminPassword"
echo ""
echo "3. Install these plugins in Jenkins:"
echo "   - GitHub"
echo "   - Maven Integration"
echo "   - Deploy to Container"
echo "   - Publish Over SSH"
echo ""
echo "4. Configure Global Tool Configuration:"
echo "   - Java:   $JAVA_HOME"
echo "   - Git:    $(which git)"
echo "   - Maven:  /opt/maven"
echo ""
echo "  NOTE: Make sure port 8080 is open in your EC2 Security Group!"
echo "============================================"
