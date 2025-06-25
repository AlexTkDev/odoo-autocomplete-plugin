# 🚀 Odoo Autocompletion Support (v1.0.2)

A modern, production-ready plugin for PyCharm Community Edition and IntelliJ IDEA, designed to supercharge Odoo development.

---

## ✨ Features
- **Autocompletion:** Models, fields, XML-IDs, manifests, and more in Python and XML.
- **Go to Declaration:** Instantly navigate to Odoo entities.
- **Inspections & Quickfixes:** For missing models, records, and manifest errors.
- **Odoo-specific Code Generation:** Fast creation of Odoo models and records.
- **Documentation Popups:** Inline Odoo docs for models and fields.
- **Manifest & Security Support:** Navigation and validation for manifest and security files.
- **No Ultimate dependencies, no license checks, fully open source.**

---

## 📦 Installation
- Download the latest release from the [GitHub Releases](https://github.com/AlexTkDev/odoo-autocomplete-plugin/releases) section of this repository.
- In PyCharm: Preferences → Plugins → Install plugin from disk → select the built `.zip` file.
- Restart PyCharm.

---

## 📚 Documentation & Support
- **This plugin is available and maintained on [GitHub](https://github.com/AlexTkDev/odoo-autocomplete-plugin).**
- For full documentation, usage examples, and FAQ, see [documentation/README.md](documentation/README.md).
- For issues and feedback, use [GitHub Issues](https://github.com/AlexTkDev/odoo-autocomplete-plugin/issues).

---

## 📝 License
MIT License. Copyright (c) 2024 AlexTkDev.

---
Odoo® is a registered trademark of Odoo S.A. This plugin is an independent open-source project and is not affiliated with Odoo S.A.

*This README is up to date for version 1.0.2 and the 2025.1+ JetBrains platform.*

---

## ⚙️ Technical Notes for Developers

- **Architecture:**
  - The plugin is built using the IntelliJ Platform SDK (Java), targeting PyCharm Community Edition 2025.1+.
  - Main logic is in `src/main/java/at/wtioit/intellij/plugins/odoo/` and subpackages (`models`, `records`, `modules`, etc).
  - Uses contributors, matchers, and indexers for autocompletion, navigation, and inspections.
  - Extensible via new `CompletionContributor`, `GoToDeclarationHandler`, and `LocalInspectionTool` classes.

- **Odoo Version Support:**
  - Supports Odoo 12.0–17.0+ out of the box.
  - For new Odoo versions, check for changes in model/manifest structure and update matchers if needed.
  - No hardcoded version checks; plugin is designed to be forward-compatible.

- **Environment & Build:**
  - Requires JDK 17+ and Gradle 8+.
  - Build with `./gradlew clean build --no-build-cache --refresh-dependencies`.
  - Plugin descriptor (`plugin.xml`) is patched automatically for compatibility.
  - No `until-build` restrictions for JetBrains IDEs.

- **Testing & CI:**
  - Unit and integration tests are in `src/test/java/at/wtioit/intellij/plugins/odoo/`.
  - GitHub Actions workflow: `.github/workflows/build.yml` (build, verify, runPluginVerifier).
  - Always run tests and plugin verifier before release.

- **Publishing:**
  - Tag releases as `vX.Y.Z` and push to GitHub.
  - Distribute via GitHub Releases (preferred) or JetBrains Marketplace (optional).

- **User Support:**
  - Use GitHub Issues for bug reports and feature requests.
  - Keep documentation up to date in `documentation/README.md`.

- **Extending the Plugin:**
  - To add new Odoo entities or features, implement new contributors or inspections in the relevant package.
  - Follow the existing code style and architecture for consistency.
  - Update tests and documentation for any new features.

- **Maintenance Recommendations:**
  - Monitor changes in Odoo and JetBrains Platform APIs.
  - Regularly update dependencies and test with new PyCharm versions.
  - Respond to user feedback and issues on GitHub.
  - Keep the plugin open source and community-driven.