# Odoo Autocomplete Plugin — Documentation

## Features
- **Autocompletion & Go To Declaration** for:
  - Odoo Models (Python, XML, CSV)
  - Record XML IDs (in XML, CSV, PO/POT)
  - Actions (`ir.actions.*`)
  - Views (`ir.ui.view`)
  - Menus (`ir.ui.menu`)
  - Groups (`res.groups`)
  - Reports (`ir.actions.report`)
  - JS Modules (`odoo.define`)
  - Translations (`msgid` in PO/POT files)
- **Multi-Version Support:** Works with all Odoo versions, including 17.0+, and supports multiple versions in the same project.
- **Language Support:** Python, XML, CSV, JavaScript, and PO/POT files.
- **Template Navigation:** Navigate to QWeb templates (`t-name`).
- **Inspections:** Detects missing models, records, JS modules, and `msgid`s.
- **High Performance:** All features are powered by indexing for a fast and smooth experience.

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
- **Python:** `self.env["res.partner"]` - Autocompletes and navigates to the model definition.
- **XML:** `<field name="model">res.partner</field>` - Autocompletes model names.
- **XML:** `<field name="view_id" ref="my_module.my_view"/>` - Autocompletes and navigates to the view.
- **CSV:** `res_id` column - Autocompletes and navigates to the record by its XML ID.
- **JavaScript:** `odoo.define('my_module.my_widget', ...)` - Autocompletes and navigates to the JS file.
- **PO:** `msgid "my_module.my_record"` - Autocompletes and navigates to the record XML ID.

## FAQ
**Q:** Which Odoo versions are supported?
**A:** All versions, including 17.0 and newer. The plugin is designed to be adaptable to future Odoo releases.

**Q:** Can I use multiple Odoo versions in one project?
**A:** Yes, the plugin fully supports multi-version projects.

**Q:** How can I add support for a new entity?
**A:** Extend the relevant `Matcher`/`Contributor` in the source code. The architecture is designed to be extensible.

## Authorship and License
MIT License. Copyright (c) 2024 AlexTkDev.
Based on the original work by wt-io-it/odoo-pycharm-plugin (https://github.com/wt-io-it/odoo-pycharm-plugin). 