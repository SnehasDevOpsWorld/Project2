🧩 Problem 1: Folder visible on GitHub but not opening

You said:

“I can see formfillapp folder in Project2 repo, but can’t open it”

👉 This usually means:

The folder was pushed as a Git submodule, not actual files.
💥 Why this happens

You cloned one repo (formfillapp) and then copied it into another repo (Project2) without removing its .git folder.

So Git treated it as a nested repository, not normal files.
🔍 How to confirm

On your server, run:

cd Project2/formfillapp
ls -a

If you see:

.git

👉 That’s the issue.
✅ Fix it (important)

Run this inside Project2:

rm -rf formfillapp/.git
git add .
git commit -m "Fix: remove nested git repo"
git push origin main
|
----------------------------------------------------------------------------------------------------------
----------------------------------------------------------------------------------------------------------

Problem 2 : 🔍 Why Git says “nothing to commit”

Even after deleting .git inside formfillapp, Git is still not detecting changes because:

👉 The folder was already tracked as a submodule (pointer) earlier
👉 Removing .git alone doesn’t convert it back to normal files

So Git still thinks:

“formfillapp is just a reference, not actual content”

✅ Proper fix (you need to re-add the folder correctly)

Run these steps carefully inside your Project2 repo:

1. Remove old submodule reference
git rm --cached formfillapp
👉 This removes the “pointer” tracking (not your files)

2. Add files again as normal folder
git add formfillapp
3. Commit changes
git commit -m "Convert formfillapp from submodule to normal folder"
4. Push

----------------------------------------------------------------------------------------
----------------------------------------------------------------------------------------

Problem 3 :
📌 Using sed to get JAVA path (Learned about sed command , apart from head and tail command)

While creating jenkins_setup.sh, we used:

find /usr/lib/jvm/java-21* -maxdepth 0 | sed -n '3p'

This command prints the 3rd line from the output, which gives a specific Java installation path.

It helps to automatically set JAVA_HOME in the script without manually checking or editing environment variables.

JAVA_HOME=$(find /usr/lib/jvm/java-21* -maxdepth 0 | sed -n '3p')
Now refresh GitHub — the folder will open normally.
