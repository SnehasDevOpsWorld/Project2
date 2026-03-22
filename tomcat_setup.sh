#!/bin/bash
# ============================================================
#   Tomcat Server Setup Script for AWS EC2 (Amazon Linux 2)
#   Installs: Java 21, Apache Tomcat
#   Configures: Manager App, Users, Context.xml
# ============================================================

set -e  # Exit on any error

echo "============================================"
echo "  Tomcat Server Setup - Starting..."
echo "============================================"

# ----------------------------
# STEP 1: Install Java 21
# ----------------------------
echo ""
echo "[1/6] Installing Java 21..."
yum install java-21* -y
echo "Java installed: $(java -version 2>&1 | head -n 1)"

# ----------------------------
# STEP 2: Download & Install Tomcat
# ----------------------------
echo ""
echo "[2/6] Downloading Apache Tomcat..."

TOMCAT_VERSION="11.0.20"
TOMCAT_URL="https://dlcdn.apache.org/tomcat/tomcat-11/v${TOMCAT_VERSION}/bin/apache-tomcat-${TOMCAT_VERSION}.tar.gz"
TOMCAT_TAR="apache-tomcat-${TOMCAT_VERSION}.tar.gz"

cd /opt

echo "Downloading Tomcat ${TOMCAT_VERSION}..."
wget -q "$TOMCAT_URL" -O "$TOMCAT_TAR"

echo "Extracting Tomcat..."
tar -xvzf "$TOMCAT_TAR"

echo "Removing tar file..."
rm -rf "$TOMCAT_TAR"

echo "Renaming folder to 'tomcat'..."
mv "apache-tomcat-${TOMCAT_VERSION}" tomcat

echo "Tomcat installed at: /opt/tomcat"

# ----------------------------
# STEP 3: Fix context.xml files
# (Comment out Valve line to allow external browser access)
# ----------------------------
echo ""
echo "[3/6] Fixing context.xml files for external access..."

# Fix manager context.xml
MANAGER_CONTEXT="/opt/tomcat/webapps/manager/META-INF/context.xml"
if [ -f "$MANAGER_CONTEXT" ]; then
    sed -i 's|<Valve className="org.apache.catalina.valves.RemoteCIDRValve"|<!--<Valve className="org.apache.catalina.valves.RemoteCIDRValve"|g' "$MANAGER_CONTEXT"
    sed -i 's|allow="127.0.0.0/8,::1/128" />|allow="127.0.0.0/8,::1/128" />-->|g' "$MANAGER_CONTEXT"
    echo "Manager context.xml updated ✅"
else
    echo "Manager context.xml not found - will be available after first start"
fi

# Fix host-manager context.xml
HOSTMANAGER_CONTEXT="/opt/tomcat/webapps/host-manager/META-INF/context.xml"
if [ -f "$HOSTMANAGER_CONTEXT" ]; then
    sed -i 's|<Valve className="org.apache.catalina.valves.RemoteCIDRValve"|<!--<Valve className="org.apache.catalina.valves.RemoteCIDRValve"|g' "$HOSTMANAGER_CONTEXT"
    sed -i 's|allow="127.0.0.0/8,::1/128" />|allow="127.0.0.0/8,::1/128" />-->|g' "$HOSTMANAGER_CONTEXT"
    echo "Host-Manager context.xml updated ✅"
else
    echo "Host-Manager context.xml not found - will be available after first start"
fi

# ----------------------------
# STEP 4: Setup Tomcat Users
# ----------------------------
echo ""
echo "[4/6] Setting up Tomcat users..."

TOMCAT_USERS_FILE="/opt/tomcat/conf/tomcat-users.xml"

# Add users before closing tag
sed -i 's|</tomcat-users>||g' "$TOMCAT_USERS_FILE"

cat >> "$TOMCAT_USERS_FILE" << EOF

  <role rolename="manager-gui"/>
  <role rolename="manager-script"/>
  <role rolename="manager-jmx"/>
  <role rolename="manager-status"/>
  <user username="admin" password="admin" roles="manager-gui,manager-script,manager-jmx,manager-status"/>
  <user username="deployer" password="deployer" roles="manager-script"/>
  <user username="tomcat" password="s3cret" roles="manager-gui"/>

</tomcat-users>
EOF

echo "Tomcat users configured ✅"

# ----------------------------
# STEP 5: Set permissions
# ----------------------------
echo ""
echo "[5/6] Setting permissions on Tomcat folder..."
chmod -R 755 /opt/tomcat
echo "Permissions set ✅"

# ----------------------------
# STEP 6: Start Tomcat
# ----------------------------
echo ""
echo "[6/6] Starting Tomcat..."
cd /opt/tomcat/bin
./startup.sh

echo ""
echo "============================================"
echo "  Tomcat Setup Complete!"
echo "============================================"
echo ""
echo "  NEXT STEPS:"
echo "============================================"
echo ""
echo "1. Access Tomcat:          http://<ELASTIC-IP>:8080"
echo "2. Access Manager App:     http://<ELASTIC-IP>:8080/manager"
echo "3. Manager Login:          admin / admin"
echo ""
echo "4. Connect to Jenkins:"
echo "   - Manage Jenkins → Credentials → Add"
echo "   - Username: admin  Password: admin"
echo "   - Use in Post Build Action of your Jenkins job"
echo ""
echo "5. WAR files deploy to:    /opt/tomcat/webapps/"
echo ""
echo "  NOTE: Make sure port 8080 is open in EC2 Security Group!"
echo "============================================"
