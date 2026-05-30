# Troubleshooting Guide

## Problem 1: Folder Visible on GitHub but Not Opening

### Issue

The `formfillapp` folder was visible inside the `Project2` repository but could not be opened on GitHub.

### Cause

This usually happens when a folder is added as a **Git submodule** instead of normal project files.

This can occur if you:

* Clone a repository (`formfillapp`)
* Copy it into another repository (`Project2`)
* Forget to remove the internal `.git` directory

Git then treats the folder as a **nested repository** rather than regular files.

### Verify the Issue

Navigate into the folder:

```bash
cd Project2/formfillapp
ls -a
```

If you see:

```bash
.git
```

the folder is being treated as a nested Git repository.

### Fix

Remove the internal Git metadata:

```bash
rm -rf formfillapp/.git
git add .
git commit -m "Fix: remove nested git repository"
git push origin main
```

---

## Problem 2: Git Shows "Nothing to Commit"

### Issue

After deleting the `.git` folder inside `formfillapp`, Git still reported:

```bash
nothing to commit
```

### Cause

Removing `.git` alone does not fully solve the issue.

The folder had already been tracked as a **Git submodule reference** (pointer).

Git still considered `formfillapp` a linked repository instead of normal project content.

### Fix

#### Step 1: Remove the Existing Submodule Reference

```bash
git rm --cached formfillapp
```

This removes Git’s stored submodule tracking reference.

#### Step 2: Add the Folder Again as Normal Files

```bash
git add formfillapp
```

#### Step 3: Commit Changes

```bash
git commit -m "Convert formfillapp from submodule to normal folder"
```

#### Step 4: Push Changes

```bash
git push origin main
```

The folder should now behave like a standard directory in GitHub.

---

## Problem 3: Using `sed` to Extract JAVA_HOME Path

### Context

While creating `jenkins_setup.sh`, the following command was used:

```bash
find /usr/lib/jvm/java-21* -maxdepth 0 | sed -n '3p'
```

### Explanation

* `find` searches for Java 21 installation directories.
* `sed -n '3p'` prints the **third line** from the output.

This helps dynamically retrieve a Java installation path without manually checking the system.

Example:

```bash
JAVA_HOME=$(find /usr/lib/jvm/java-21* -maxdepth 0 | sed -n '3p')
```

This value can then be assigned automatically to `JAVA_HOME`.

---


This improves compatibility with the installed Java 21 environment.
