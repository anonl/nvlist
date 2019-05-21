
# Release procedure

- Set version number in build.gradle.
- Update the [changelog](CHANGELOG.md)
- Merge changes to upstream master branch.
- Wait for the [CI build](https://travis-ci.org/anonl/nvlist) to finish successfully.
- Tag the commit with the version number (i.e. `v1.2.3`)
- Upload the release to bintray: `./gradlew bintrayUpload`
- Create a distribution of nvlist-buildgui: `./gradlew assembleDist`
- Upload the buildgui distribution as a GitHub release 
