## Publishing to maven central

Github action will automatically publish the package to maven central when a new release is created.
Maven central: https://central.sonatype.com

### GPG
In order to publish to maven central, artifacts needs to be signed with PGP.
Use gpg for that:
```bash
gpg --gen-key
gpg --keyserver keyserver.ubuntu.com --send-keys <fingerprint
gpg --armor --export-secret-keys <fingerprint>
```

Upload the private key and passphase to github secrets 
