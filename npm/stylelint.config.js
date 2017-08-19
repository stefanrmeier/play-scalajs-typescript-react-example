module.exports = {
  extends: "./airbnb-stylelint.config.js",
  rules: {
    'rule-non-nested-empty-line-before': null,
    'selector-list-comma-newline-after': null,
    'declaration-property-value-blacklist': null,
    'max-nesting-depth': 6,

    'scss/dollar-variable-pattern': null,

    // Vendor Prefix
    'value-no-vendor-prefix': true,
    'property-no-vendor-prefix': true,
    'selector-no-vendor-prefix': true,
    'media-feature-name-no-vendor-prefix': true,
    'at-rule-no-vendor-prefix': true
  },
  ignoreFiles: [
    'local_modules/**/*.scss'
  ]
};
