const path = require('path');
const StyleLintPlugin = require('stylelint-webpack-plugin');

module.exports = new StyleLintPlugin({
  configFile: path.resolve(__dirname, '../../stylelint.config.js'),
  files: '**/*.scss',
  failOnError: false,
  syntax: 'scss'
});
