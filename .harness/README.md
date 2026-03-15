# Harness Configuration Directory

This directory contains all Harness entities managed via Git Experience.

## Directory Structure

- `pipelines/` - Pipeline definitions for app-alpha and app-beta
- `templates/` - Reusable pipeline templates (progressive canary)
- `services/` - Service definitions with artifact and manifest configs
- `environments/` - Environment definitions (dev, qa, prod)
- `infrastructures/` - Infrastructure definitions for k8s clusters
- `connectors/` - Connector configurations (GitHub, Docker, k8s)

## Git Experience

All entities in this directory are synced with Harness via Git Experience.

- Changes pushed to Git will be reflected in Harness
- Changes made in Harness UI can be committed back to Git
- Use bi-directional sync for collaborative development

## Entity References

Entities reference each other using identifiers:

- Pipelines reference templates via `templateRef`
- Services reference connectors for artifacts and manifests
- Environments contain infrastructure definitions
- Infrastructures reference k8s connectors

## Modifying Entities

### Via Git
1. Edit YAML files in this directory
2. Commit and push changes
3. Harness auto-syncs within minutes

### Via Harness UI
1. Make changes in Harness
2. Click "Save to Git"
3. Provide commit message
4. Pull changes locally

## Best Practices

1. Keep separate branches for development
2. Use pull requests for infrastructure changes
3. Review pipeline changes before merging
4. Tag releases for rollback capability
5. Document custom variables and inputs

## Learn More

- [Harness Git Experience](https://developer.harness.io/docs/platform/git-experience/git-experience-overview/)
- [Pipeline as Code](https://developer.harness.io/docs/platform/pipelines/harness-yaml-quickstart/)
