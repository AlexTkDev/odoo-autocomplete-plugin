# Odoo Autocompletion Support — Documentation (v1.0.2)

## Overview
Odoo Autocompletion Support is a modern, production-ready plugin for PyCharm Community Edition and IntelliJ IDEA, designed to supercharge Odoo development. It provides:
- **Intelligent autocompletion** for Odoo models, fields, XML-IDs, manifests, and more in Python and XML.
- **Go to declaration** and navigation for models, fields, records, and references across Python and XML.
- **Inspections and quickfixes** for missing models, records, and manifest errors.
- **Odoo-specific code generation** and documentation popups.
- Works out of the box with PyCharm Community Edition 2025.1+ and Odoo 12.0–17.0.
- No Ultimate dependencies, no license checks, fully open source.

## Features
- **Autocompletion & Navigation**: Models, fields, XML-IDs, manifests, security, and more.
- **Inspections & Quickfixes**: For missing models, records, manifest errors, and Odoo-specific issues.
- **Code Generation**: Fast creation of Odoo models, fields, and records.
- **Documentation Popups**: Inline Odoo docs for models and fields.
- **Manifest & Security Support**: Navigation and validation for manifest and security files.
- **Marketplace-ready**: All code and UI in English, ready for JetBrains Marketplace.

## Compatibility
- **PyCharm Community Edition 2025.1+** (build 251+)
- **Odoo 12.0–17.0**
- No Ultimate/Pro dependencies
- No until-build restrictions (plugin.xml is always patched automatically)

## Installation
1. Download the latest release (`odoo_plugin-1.0.2.zip`) from [GitHub Releases](https://github.com/AlexTkDev/odoo-autocomplete-plugin/releases) or build from source:
   ```bash
   ./gradlew clean build --no-build-cache --refresh-dependencies
   ```
2. In PyCharm: Preferences → Plugins → Install plugin from disk → select `odoo_plugin-1.0.2.zip`.
3. Restart PyCharm.

## Build & Release
- The build process is fully automated. No manual patching required.
- After build, the distribution zip is always compatible with the latest PyCharm Community Edition.
- To publish: upload `odoo_plugin-1.0.2.zip` to JetBrains Marketplace or GitHub Releases.

## Support
- Issues: [GitHub Issues](https://github.com/AlexTkDev/odoo-autocomplete-plugin/issues)
- Author: AlexTkDev

---
*This documentation is up to date for version 1.0.2 and the 2025.1+ JetBrains platform.*

## Autocompletion Scenarios
- **Python:** Autocompletes models, fields, `env["model.name"]`, and `http.request.env[...]`.
- **XML:** Autocompletes models, XML IDs, actions, views, menus, groups, and reports.
- **CSV:** Autocompletes and navigates record XML IDs in `id`, `res_id`, and `model` columns.
- **JavaScript:** Autocompletes and navigates JS module names in `odoo.define` and `require`.
- **PO/POT:** Autocompletes and navigates record XML IDs, models, and fields referenced in translations.

## Go To Declaration
- Navigate directly to the definition of models, records, actions, views, menus, groups, reports, JS modules, and translations.
- Navigate to QWeb templates via `t-name`.

## Usage Examples
- **Python:** `self.env["res.partner"]` — Autocompletes and navigates to the model definition.
- **XML:** `<field name="model">res.partner</field>` — Autocompletes model names.
- **XML:** `<field name="view_id" ref="my_module.my_view"/>` — Autocompletes and navigates to the view.
- **CSV:** `res_id` column — Autocompletes and navigates to the record by its XML ID.
- **JavaScript:** `odoo.define('my_module.my_widget', ...)` — Autocompletes and navigates to the JS file.
- **PO:** `msgid "my_module.my_record"` — Autocompletes and navigates to the record XML ID.

## FAQ
**Q:** Which Odoo versions are supported?
**A:** All versions, including 17.0 and newer. The plugin is designed to be adaptable to future Odoo releases.

**Q:** Can I use multiple Odoo versions in one project?
**A:** Yes, the plugin fully supports multi-version projects.

**Q:** How can I add support for a new entity?
**A:** Extend the relevant `Matcher`/`Contributor` in the source code. The architecture is designed to be extensible.

## License
GNU General Public License v3.0. Copyright (c) 2024 AlexTkDev.
Based on the original work by wt-io-it/odoo-pycharm-plugin (https://github.com/wt-io-it/odoo-pycharm-plugin). 