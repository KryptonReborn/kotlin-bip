# Release Process

This document outlines the steps required to release a new version of our project. The process is semi-automated, relying on both manual steps and automated CI/CD pipelines.

## Release Steps

1. **Create a Release Branch**: Start by creating a new branch from `main` (or your default branch) named `releases/vX.X.X`, where `X.X.X` is the new version number.

    ```bash
    git checkout main
    git pull origin main
    git checkout -b releases/vX.X.X
    ```

2. **Update Version and Changelog**: Update the version number in your project's libraries and the `CHANGELOG.md` file to reflect the new version and summarize the changes. Use the labels and PR titles to guide your changelog entries.

    ```bash
    # Update version in relevant files
    # Update CHANGELOG.md
    ```

3. **Push the Release Branch**: Push the newly created release branch to the remote repository.

    ```bash
    git push origin releases/vX.X.X
    ```

4. **Create a Release Tag**: Once the release branch is pushed and all CI checks pass, create a tag for the release.

    ```bash
    git tag vX.X.X
    ```

5. **Push the Tag**: Push the tag to the remote repository. This action can trigger CI/CD pipelines designed to deploy/release the new version.

    ```bash
    git push origin vX.X.X
    ```

6. **Trigger CI**: If your CI/CD pipeline does not automatically trigger on tag push, manually trigger the pipeline to build, test, and deploy/release the new version as configured.

## Post-Release

- Ensure that the release is correctly published to all intended distribution channels (e.g., Github packages).
- Announce the release on relevant platforms (e.g., GitHub Releases, project website, social media) with a summary of the changes and links to the detailed changelog.

## Troubleshooting

If issues arise during the release process, address them as follows:

- **Failed CI Checks**: Investigate and fix any failing CI checks. This may involve additional commits to the release branch to resolve issues.
- **Problems with Release Tagging**: If a tag is created incorrectly, it can be deleted and recreated both locally and remotely using `git tag -d vX.X.X` and `git push --delete origin vX.X.X`, respectively. Ensure you coordinate with your team before deleting tags to avoid confusion.