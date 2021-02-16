
# Release procedure

- Set version number in `build.gradle`
- Update version number in `template/build-res/build.properties`
- Update version number in `EngineVersion.java`
- Update the [changelog](CHANGELOG.md)
- Merge changes to upstream master branch.
- Wait for the [CI build](https://github.com/anonl/nvlist/actions) to finish successfully.
- Tag the commit with the version number (i.e. `v1.2.3`)
- Upload the release to OSSRH (staging repo): `./gradlew publish`
- Publish the release, see https://central.sonatype.org/pages/releasing-the-deployment.html
- Create a distribution of nvlist-buildgui: `./gradlew :nvlist-buildgui:assembleDist`
- Upload the buildgui distributions (`buildgui/build/release`) as a GitHub release.
