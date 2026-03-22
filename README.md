# 🚀 Project 2 — Jenkins CI/CD Pipeline with Tomcat on AWS

> A complete DevOps project demonstrating automated build and deployment using Jenkins, Maven, and Apache Tomcat on AWS EC2.

---

## 📚 Table of Contents

- [📌 Project Overview](#-project-overview)
- [🏗️ Architecture](#️-architecture)
- [🛠️ Tools & Technologies](#️-tools--technologies)
- [⚙️ Jenkins Server Setup](#️-jenkins-server-setup)
- [🐱 Tomcat Server Setup](#-tomcat-server-setup)
- [🔗 Jenkins Job Configuration](#-jenkins-job-configuration)
- [⚠️ Problems Faced](#️-problems-faced)
- [🔒 Drawbacks — Security, Accessibility & Reliability](#-drawbacks--security-accessibility--reliability)
- [✅ When to Use This Architecture & How to Improve It](#-when-to-use-this-architecture--how-to-improve-it)
- [📁 Repository Structure](#-repository-structure)
- [👩‍💻 Author](#-author)

---

## 📌 Project Overview

This project sets up a **CI/CD pipeline** where:
- Developer pushes code to **GitHub**
- **Jenkins** automatically pulls the code, builds a WAR file using Maven
- Jenkins deploys the WAR file to **Apache Tomcat**
- The application becomes accessible via browser

---

## 🏗️ Architecture

```
👨‍💻 Developer
      |
      | git push
      ↓
🐙 GitHub Repository (Project2)
      |
      | webhook / manual trigger
      ↓
⚙️  Jenkins Server (EC2 + Elastic IP + Port 8080)
      |  - Pulls code from GitHub
      |  - Runs: mvn clean install
      |  - Builds WAR file
      ↓
🐱 Tomcat Server (EC2 + Elastic IP + Port 8080)
      |  - Receives WAR from Jenkins
      |  - Deploys to /opt/tomcat/webapps/
      ↓
🌐 Browser → ElasticIP:8080/FormFillApp
```

### 🖥️ Servers Used

| Server | Type | Purpose |
|--------|------|---------|
| Jenkins Server | EC2 t2.medium | Build & CI/CD automation |
| Tomcat Server | EC2 t2.micro | Application deployment |
| Git Server | EC2 (optional) | Used during setup for pushing scripts |

---

## 🛠️ Tools & Technologies

| Tool | Version | Purpose |
|------|---------|---------|
| ☕ Java | 21 (Amazon Corretto) | Runtime for Jenkins & builds |
| 📦 Maven | 3.9.x | Build & dependency management |
| ⚙️ Jenkins | Latest | CI/CD automation |
| 🐱 Tomcat | 10.1.x | Application server |
| 🔧 Git | 2.x | Source code management |
| ☁️ AWS EC2 | Amazon Linux 2 | Cloud infrastructure |

---

## ⚙️ Jenkins Server Setup

> 📄 Automated script available: `jenkins_setup.sh`

### Manual Steps

**1. Install Git & Java**
```bash
yum install git -y
yum install java-21-amazon-corretto -y
```

**2. Install Maven**
```bash
cd /opt
wget <maven-tar.gz-url>
tar -xvzf apache-maven-*.tar.gz
rm -rf apache-maven-*.tar.gz
mv apache-maven-* maven
```

**3. Find Java Path**
```bash
find /usr/lib/jvm/java-21* -maxdepth 0 | sed -n '3p'
```

**4. Set Environment Variables**
```bash
vi /etc/profile.d/jenkins_env.sh
```
```bash
export JAVA_HOME=/usr/lib/jvm/java-21-amazon-corretto.x86_64
export M2_HOME=/opt/maven
export M2=/opt/maven/bin
export PATH=$PATH:$JAVA_HOME/bin:$M2:$M2_HOME
```
```bash
source /etc/profile.d/jenkins_env.sh
```

**5. Install Jenkins**
```bash
wget -O /etc/yum.repos.d/jenkins.repo https://pkg.jenkins.io/redhat-stable/jenkins.repo
rpm --import https://pkg.jenkins.io/redhat-stable/jenkins.io-2023.key
yum install jenkins -y
systemctl daemon-reload
service jenkins start
systemctl enable jenkins
```

**6. Access Jenkins**
```
http://<JenkinsElasticIP>:8080
Initial password: cat /var/lib/jenkins/secrets/initialAdminPassword
```

**7. Install Plugins**
- ✅ GitHub
- ✅ Maven Integration
- ✅ Deploy to Container
- ✅ Publish Over SSH

**8. Global Tool Configuration**

`Manage Jenkins → Global Tool Configuration`

| Tool | Name | Path |
|------|------|------|
| JDK | Java21 | `/usr/lib/jvm/java-21-amazon-corretto.x86_64` |
| Git | Git | `/usr/bin/git` |
| Maven | Maven | `/opt/maven` |

---

## 🐱 Tomcat Server Setup

> 📄 Automated script available: `tomcat_setup.sh`

### Manual Steps

**1. Install Java**
```bash
yum install java-21-amazon-corretto -y
```

**2. Install Tomcat**
```bash
cd /opt
wget <tomcat-tar.gz-url>
tar -xvzf apache-tomcat-*.tar.gz
rm -rf apache-tomcat-*.tar.gz
mv apache-tomcat-* tomcat
```

**3. Fix context.xml (Allow External Browser Access)**
```bash
vi /opt/tomcat/webapps/manager/META-INF/context.xml
vi /opt/tomcat/webapps/host-manager/META-INF/context.xml
```
Comment out the Valve line in both files:
```xml
<!-- <Valve className="org.apache.catalina.valves.RemoteCIDRValve"
       allow="127.0.0.0/8,::1/128" /> -->
```

**4. Add Tomcat Users**
```bash
vi /opt/tomcat/conf/tomcat-users.xml
```
Add before `</tomcat-users>`:
```xml
<role rolename="manager-gui"/>
<role rolename="manager-script"/>
<role rolename="manager-jmx"/>
<role rolename="manager-status"/>
<user username="admin" password="admin" roles="manager-gui,manager-script,manager-jmx,manager-status"/>
<user username="deployer" password="deployer" roles="manager-script"/>
<user username="tomcat" password="s3cret" roles="manager-gui"/>
```

**5. Start Tomcat**
```bash
cd /opt/tomcat/bin
./startup.sh
```

**6. Access Tomcat**
```
http://<TomcatElasticIP>:8080
Manager: http://<TomcatElasticIP>:8080/manager
Login: admin / admin
```

---

## 🔗 Jenkins Job Configuration

**1. Create New Maven Job**

`New Item → Maven Project → Give name → OK`

**2. Source Code Management**
```
Git → Repository URL: https://github.com/SnehasDevOpsWorld/Project2.git
Branch: */main
```

**3. Build**
```
Root POM: FormFillApp/pom.xml
Goals: clean install
```

**4. Add Tomcat Credentials in Jenkins**

`Manage Jenkins → Credentials → Global → Add Credentials`
```
Kind: Username with password
Username: admin
Password: admin
ID: tomcat_credentials
```

**5. Post Build Action**
```
Deploy WAR/EAR to container
WAR file: **/*.war
Container: Tomcat 10.x
Credentials: tomcat_credentials
Tomcat URL: http://<TomcatElasticIP>:8080
```

---

## ⚠️ Problems Faced

| # | Problem | Cause | Solution |
|---|---------|-------|----------|
| 1 | `mvn not found` after script ran | Environment variables not loaded in current session | Ran `source /etc/profile.d/jenkins_env.sh` |
| 2 | `JAVA_HOME` path was incomplete (`java-21` instead of full path) | `head -n 1` was picking symlink not real path | Changed to `sed -n '3p'` to get exact path |
| 3 | Jenkins build failed — `pom.xml not found` | GitHub repo not linked to Jenkins job | Added GitHub URL in Source Code Management |
| 4 | Build failed — `Source option 7 not supported` | pom.xml had Java 7 as source/target, Java 21 doesn't support it | Changed source and target from 7 to 8 in pom.xml |
| 5 | Tomcat Manager not accessible from browser | Valve line in context.xml was blocking external IPs | Commented out Valve line in both context.xml files |
| 6 | `context.xml` sed fix didn't work in script | Script expected `RemoteAddrValve` but Tomcat 10 uses `RemoteCIDRValve` | Updated sed command with correct class name |
| 7 | `Deploy to Container` not visible in Post Build Actions | Plugin was not installed | Installed `Deploy to Container` plugin from Plugin Manager |
| 8 | `git push` rejected with non-fast-forward error | Remote repo had newer commits from another server | Ran `git pull origin main` first then pushed |

---

## 🔒 Drawbacks — Security, Accessibility & Reliability

### 🔴 Security Issues

| Issue | Problem |
|-------|---------|
| **Weak credentials** | Using `admin/admin` for Tomcat is extremely insecure |
| **No HTTPS** | All communication over plain HTTP — data can be intercepted |
| **Port 8080 open to world** | EC2 Security Group allows all IPs to access port 8080 |
| **Running as root** | All setup done as root user — gives too much power |
| **Plain text passwords** | Passwords stored in `tomcat-users.xml` without encryption |
| **No firewall rules** | No restriction on who can access Jenkins or Tomcat |

### 🟡 Accessibility Issues

| Issue | Problem |
|-------|---------|
| **Elastic IP can change** | If instance is stopped/started, IP may change without Elastic IP |
| **No domain name** | Users must remember IP:8080 — not user friendly |
| **No load balancer** | Single server — if it goes down, app is inaccessible |
| **Manual Tomcat start** | Tomcat doesn't auto-start on server reboot — must run startup.sh manually |

### 🟠 Reliability Issues

| Issue | Problem |
|-------|---------|
| **No backups** | No automated backups of Jenkins jobs or Tomcat deployments |
| **Single point of failure** | One Jenkins + one Tomcat — if either crashes, pipeline breaks |
| **No monitoring** | No alerts if server goes down or build fails |
| **t2.micro for Tomcat** | Very limited CPU/RAM — may struggle under load |
| **No rollback** | If deployment fails, no automatic way to go back to previous version |

---

## ✅ When to Use This Architecture & How to Improve It

This type of setup is a great **starting point** for small teams and learning projects. Here is how to use it properly in real scenarios:

### 👍 Good For
- Personal learning projects ✅
- Small internal tools with limited users ✅
- Practice and proof-of-concept projects ✅

### 🏢 For Real Production Projects — Improvements Needed

| Problem | Real World Solution |
|---------|-------------------|
| Weak passwords | Use **AWS Secrets Manager** or **Jenkins Credentials Vault** |
| No HTTPS | Add **SSL certificate** via AWS Certificate Manager + Load Balancer |
| Port open to all | Restrict Security Group to **specific IPs** only |
| Running as root | Create dedicated users with **minimum permissions** |
| No auto-start | Create Tomcat as a **systemd service** so it starts on reboot |
| Single server | Use **Auto Scaling Groups** + **Load Balancer** for high availability |
| No monitoring | Add **AWS CloudWatch** or **Prometheus + Grafana** for alerts |
| No rollback | Use **Blue-Green deployment** or **versioned WAR files** |
| No domain | Use **Route 53** for proper domain name |
| Manual pipeline | Add **GitHub Webhooks** to auto-trigger Jenkins on every push |

---

## 📁 Repository Structure

```
Project2/
│
├── jenkins_setup.sh          # Jenkins server setup automation script
├── tomcat_setup.sh           # Tomcat server setup automation script
├── README.md                 # This file
│
└── FormFillApp/              # Java Maven Web Application
    ├── pom.xml               # Maven build configuration
    ├── server/               # Server module
    └── webapp/               # Web application module
```

---

## 👩‍💻 Author

**Sneha** — DevOps Learning Project
- GitHub: [@SnehasDevOpsWorld](https://github.com/SnehasDevOpsWorld)

---

> 💡 *This project was built for learning purposes. For production use, please address all security, accessibility and reliability issues mentioned above.*
